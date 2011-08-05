package de.unisaarland.cs.st.reposuite.genealogies;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.CloseableSequence;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.IndexableGraph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.frames.FramesManager;

import de.unisaarland.cs.st.reposuite.genealogies.model.GraphDBChangeOperation;
import de.unisaarland.cs.st.reposuite.genealogies.model.GraphDBVertex;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ChangeGenealogy {
	
	/**
	 * Read from db.
	 * 
	 * @param dbFile
	 *            the db file
	 * @return the change genealogy
	 */
	@NoneNull
	public static ChangeGenealogy readFromDB(final File dbFile, final PersistenceUtil persistenceUtil) {
		Graph graph = new Neo4jGraph(dbFile.getAbsolutePath());
		ChangeGenealogy genealogy = new ChangeGenealogy(graph);
		genealogy.setPersistenceUtil(persistenceUtil);
		return genealogy;
	}
	
	/** The graph. */
	private final Graph                  graph;
	
	private final FramesManager framesManager;
	private final AutomaticIndex<Vertex> transactionIdIndex;
	private final AutomaticIndex<Vertex> javaChangeOperationIndex;
	private PersistenceUtil              persistenceUtil;
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param graph
	 *            the graph
	 * @param persistenceUtil
	 */
	@NoneNull
	protected ChangeGenealogy(final Graph graph) {
		this.graph = graph;
		framesManager = new FramesManager(graph);
		
		//create indices for GenealogyVertex and GenealogyChangeOperation
		Set<String> indexKeys = new HashSet<String>();
		indexKeys.add(GraphDBVertex.keyName);
		transactionIdIndex = ((IndexableGraph) graph)
				.createAutomaticIndex("transactionId-idx", Vertex.class, indexKeys);
		
		Set<String> indexKeys2 = new HashSet<String>();
		indexKeys2.add(GraphDBChangeOperation.keyName);
		javaChangeOperationIndex = ((IndexableGraph) graph).createAutomaticIndex("javachangeoperationId-idx",
				Vertex.class, indexKeys2);
	}
	
	public GenealogyChangeOperation addChangeOperationToVertex(final JavaChangeOperation operation, final GenealogyVertex vertex){
		GraphDBChangeOperation genOp = getGraphDBChangeOperationById(operation.getId());
		if (genOp == null) {
			genOp = addGraphDBChangeOPeration(operation.getId());
		}
		vertex.getNode().addChangeOperations(genOp);
		return new GenealogyChangeOperation(genOp);
	}
	
	@NoneNull
	private GraphDBChangeOperation addGraphDBChangeOPeration(final Long id) {
		Vertex opVertex = graph.addVertex(null);
		GraphDBChangeOperation genOpVertex = framesManager.frame(opVertex, GraphDBChangeOperation.class);
		genOpVertex.setChangeOperationId(id);
		return genOpVertex;
	}
	
	/**
	 * Adds a vertex to the genealogy that is associated with the specified
	 * transactionId and the specified javaChangeOPerationIds. This method also
	 * checks if such a vertex exists already.
	 * 
	 * @param transactionId
	 *            the transaction id
	 * @param javaChangeOperationIds
	 *            the java change operation ids
	 * @return the newly generated genealogy vertex if no such vertex was added
	 *         before. Otherwise, returns the already added genealogy vertex.
	 */
	public GenealogyVertex addVertex(final String transactionId, final Long[] javaChangeOperationIds) {
		
		//check if such a vertex exists already
		GenealogyVertex existingVertex = getVertex(transactionId, javaChangeOperationIds);
		if (existingVertex != null) {
			return existingVertex;
		}
		
		//generate the main vertex
		Vertex vertex = graph.addVertex(null);
		GraphDBVertex gVertex = framesManager.frame(vertex, GraphDBVertex.class);
		gVertex.setTransactionId(transactionId);
		
		//generate the vertices referencing the java change operations
		for (Long oId : javaChangeOperationIds) {
			
			//check if change operation vertex exists already
			GraphDBChangeOperation genOpVertex = getGraphDBChangeOperationById(oId);
			if (genOpVertex == null) {
				genOpVertex = addGraphDBChangeOPeration(oId);
			}
			gVertex.addChangeOperations(genOpVertex);
		}
		return new GenealogyVertex(this, gVertex);
	}
	
	public void close(){
		this.graph.shutdown();
	}
	
	/**
	 * Gets the graph db change operation by id.
	 * 
	 * @param id
	 *            the id
	 * @return the graphDbChangeOperation by id iff exists. Return
	 *         <code>null</code> otherwise.
	 */
	private GraphDBChangeOperation getGraphDBChangeOperationById(final long id){
		CloseableSequence<Vertex> sequence = null;
		if (javaChangeOperationIndex != null) {
			sequence = javaChangeOperationIndex.get(GraphDBChangeOperation.keyName, id);
		}
		if ((sequence != null) && sequence.hasNext()) {
			Vertex opVertex = sequence.next();
			return framesManager.frame(opVertex, GraphDBChangeOperation.class);
		}
		return null;
	}
	
	/**
	 * Gets the PersistenceUtil registered with the ChangeGenealogy.
	 *
	 * @return the persistence util. Returns <code>null</code> if none set.
	 */
	protected PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	/**
	 * Checks if the genealogy contains a vertex with that references all (maybe
	 * more) specified transactionId and that contains the specified
	 * javaChangeOperationIds.
	 * 
	 * @param transactionId
	 *            the transaction id
	 * @param javaChangeOperationIds
	 *            the java change operation ids
	 * @return the vertex if found. Returns <code>Null</code> otherwise.
	 */
	public GenealogyVertex getVertex(final String transactionId, final Long[] javaChangeOperationIds) {
		Set<Long> opIdSet = new HashSet<Long>(Arrays.asList(javaChangeOperationIds));
		Set<Long> foundOpIds = new HashSet<Long>();
		
		CloseableSequence<Vertex> sequence = null;
		if (transactionIdIndex != null) {
			sequence = transactionIdIndex.get(GraphDBVertex.keyName, transactionId);
		}
		if ((sequence != null) && (sequence.hasNext())) {
			Vertex v = sequence.next();
			for (Edge edge : v.getOutEdges(GraphDBVertex.toOperationLabel)) {
				GraphDBChangeOperation operation = this.framesManager.frame(edge.getInVertex(),
						GraphDBChangeOperation.class);
				foundOpIds.add(operation.getChangeOperationId());
			}
			if (foundOpIds.containsAll(opIdSet)) {
				return new GenealogyVertex(this, this.framesManager.frame(v, GraphDBVertex.class));
			}
			
		}
		
		return null;
	}
	
	private void setPersistenceUtil(final PersistenceUtil persistenceUtil2) {
		this.persistenceUtil = persistenceUtil2;
	}
	
}
