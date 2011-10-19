package de.unisaarland.cs.st.reposuite.genealogies;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class ChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreChangeGenealogy implements ChangeGenealogy {
	
	/** The genealogies. */
	private static Map<CoreChangeGenealogy, File> genealogies = new HashMap<CoreChangeGenealogy, File>();
	
	/**
	 * Gets the graph db change operation by id.
	 * 
	 * @param graph
	 *            the graph
	 * @param id
	 *            the id
	 * @return the graphDbChangeOperation by id iff exists. Return
	 *         <code>null</code> otherwise.
	 */
	protected static GraphDBChangeOperation getGraphDBChangeOperationById(final GraphDatabaseService graph,
			final long id) {
		Node hit = graph.index().forNodes(GraphDBChangeOperation.keyName).get(GraphDBChangeOperation.keyName, id)
				.getSingle();
		if (hit == null) {
			return null;
		}
		return new GraphDBChangeOperation(hit);
	}
	
	/**
	 * Creates a ChangeGenealogy using the specified dbFile directory as graphDB
	 * directory. If there exists a graph DB within the dbFile directory, the
	 * ChangeGenealogy will load the ChangeGenealogy from this directory.
	 * Otherwise it will create a new one.
	 * 
	 * @param dbFile
	 *            the db file
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the change genealogy stored within5 the graph DB directory, if
	 *         possible. Otherwise, creates a new ChangeGenealogy using graph DB
	 *         within specified directory.
	 */
	@NoneNull
	public static CoreChangeGenealogy readFromDB(final File dbFile) {
		GraphDatabaseService graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		CoreChangeGenealogy genealogy = new CoreChangeGenealogy(graph, dbFile);
		genealogies.put(genealogy, dbFile);
		return genealogy;
	}
	
	private GenealogyVertex                       root        = null;
	
	//	static {
	//		Runtime.getRuntime().addShutdownHook(new Thread() {
	//
	//			@Override
	//			public void run() {
	//				for (CoreChangeGenealogy genealogy : genealogies.keySet()) {
	//					if (genealogies.get(genealogy).exists()) {
	//						genealogy.close();
	//					}
	//				}
	//			}
	//		});
	//	}
	
	private Map<Node, GenealogyVertex> nodes2Vertices = new HashMap<Node, GenealogyVertex>();
	
	/** The graph. */
	private final GraphDatabaseService graph;
	
	/** The persistence util. */
	private PersistenceUtil            persistenceUtil;
	
	private File                       dbFile;
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param graph
	 *            the graph
	 * @param dbFile
	 */
	@NoneNull
	protected CoreChangeGenealogy(final GraphDatabaseService graph, File dbFile) {
		this.graph = graph;
		this.dbFile = dbFile;
	}
	
	/**
	 * Adds a directed edge between op1 <--type-- op2 of type edgeType. Adds
	 * missing vertices before adding edge, if necessary.
	 * 
	 * @param dependant
	 *            The collection of JavaChangeOperations that represent the edge
	 *            source vertex.
	 * @param target
	 *            The collection of JavaChangeOperations that represent the edge
	 *            target vertex.
	 * @param edgeType
	 *            the GenealogyEdgeType of the edge to be added
	 * @return true, if successful
	 */
	public boolean addEdge(@NotEmpty final Collection<JavaChangeOperation> dependant,
			@NotEmpty final Collection<JavaChangeOperation> target, final GenealogyEdgeType edgeType) {
		
		String transactionId1 = null;
		for (JavaChangeOperation op : dependant) {
			if (transactionId1 == null) {
				transactionId1 = op.getRevision().getTransaction().getId();
			} else {
				Condition
				.check(transactionId1.equals(op.getRevision().getTransaction().getId()),
						"It is prohibited to add collections of change operations as vertices stemming from different transactions.");
			}
		}
		String transactionId2 = null;
		for (JavaChangeOperation op : dependant) {
			if (transactionId2 == null) {
				transactionId2 = op.getRevision().getTransaction().getId();
			} else {
				Condition
				.check(transactionId2.equals(op.getRevision().getTransaction().getId()),
						"It is prohibited to add collections of change operations as vertices stemming from different transactions.");
			}
		}
		Condition.check(transactionId1 != null,
				"Something went wrong. Could not get a valid transaction id from first operation set.");
		Condition.check(transactionId2 != null,
				"Something went wrong. Could not get a valid transaction id from second operation set.");
		
		GenealogyVertex dependantVertex = this.addVertex(dependant);
		GenealogyVertex targetVertex = this.addVertex(target);
		
		return this.addEdge(dependantVertex, targetVertex, edgeType);
	}
	
	@Override
	public boolean addEdge(@NotNull final GenealogyVertex dependantVertex, @NotNull final GenealogyVertex targetVertex,
			final GenealogyEdgeType edgeType) {
		
		if ((!this.containsVertex(dependantVertex)) || (!this.containsVertex(targetVertex))) {
			return false;
		}
		dependantVertex.addDepencyTo(targetVertex, edgeType);
		return true;
	}
	
	/**
	 * Adds a vertex to the genealogy that is associated with the specified
	 * transactionId and the specified javaChangeOPerationIds. This method also
	 * checks if such a vertex exists already.
	 * 
	 * @param operations
	 *            the operations
	 * @return the newly generated genealogy vertex if no such vertex was added
	 *         before. Otherwise, returns the already added genealogy vertex.
	 */
	@NoneNull
	public GenealogyVertex addVertex(@NotEmpty final Collection<JavaChangeOperation> operations) {
		Condition.check(!operations.isEmpty(), "Cannot add Genealogy vertex that would hold ne JavaChangeOPerations");
		String transactionId = null;
		Set<Long> operationIds = new HashSet<Long>();
		for (JavaChangeOperation op : operations) {
			if (transactionId == null) {
				transactionId = op.getRevision().getTransaction().getId();
			} else {
				Condition
				.check(transactionId.equals(op.getRevision().getTransaction().getId()),
						"It is prohibited to add collections of change operations as vertices stemming from different transactions.");
			}
			operationIds.add(op.getId());
		}
		if (transactionId != null) {
			return this.addVertex(transactionId, operationIds);
		} else {
			return null;
		}
		
	}
	
	/**
	 * Adds the vertex.
	 * 
	 * @param transaction_id
	 *            the transaction_id
	 * @param operation_ids
	 *            the operation_ids
	 * @return the genealogy vertex
	 */
	@NoneNull
	protected GenealogyVertex addVertex(final String transaction_id, final Collection<Long> operation_ids) {
		GenealogyVertex existingVertex = getVertex(transaction_id, operation_ids);
		if (existingVertex != null) {
			return existingVertex;
		}
		Transaction tx = this.graph.beginTx();
		
		Node node = graph.createNode();
		node.setProperty(GenealogyVertex.transaction_id, transaction_id);
		Index<Node> index = graph.index().forNodes(GenealogyVertex.transaction_id);
		index.add(node, GenealogyVertex.transaction_id, node.getProperty(GenealogyVertex.transaction_id));
		
		GenealogyVertex vertex = this.getVertexForNode(node);
		
		//generate the vertices referencing the java change operations
		for (Long oId : operation_ids) {
			
			//check if change operation vertex exists already
			GraphDBChangeOperation genOpVertex = getGraphDBChangeOperationById(oId);
			if (genOpVertex == null) {
				genOpVertex = GraphDBChangeOperation.create(graph, oId);
			}
			vertex.addChangeOperation(genOpVertex);
		}
		tx.success();
		tx.finish();
		return vertex;
	}
	
	/**
	 * Must be called to ensure the Graph DB to be shut down properly! This will
	 * be taken care of by a separate ShutdownHook. So make sure to call this
	 * method only when you are know what you are doing!
	 */
	@Override
	public void close() {
		this.graph.shutdown();
	}
	
	@Override
	public boolean containsEdge(GenealogyVertex from, GenealogyVertex to) {
		Collection<GenealogyVertex> allDependents = from.getAllVerticesDependingOn();
		return allDependents.contains(to);
	}
	
	@Override
	public boolean containsVertex(final GenealogyVertex vertex) {
		IndexHits<Node> hits = graph.index().forNodes(GenealogyVertex.transaction_id)
				.get(GenealogyVertex.transaction_id, vertex.getTransactionId());
		boolean result = hits.hasNext();
		hits.close();
		return result;
	}
	
	@Override
	public int edgeSize(){
		int result = 0;
		GenealogyVertexIterator vertexIter = vertexSet();
		for(GenealogyVertex v : vertexIter){
			result += v.getAllDependents().size();
		}
		vertexIter.close();
		return result;
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(GenealogyVertex from, GenealogyVertex to) {
		Collection<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		if (this.containsEdge(from, to)) {
			for (JavaChangeOperation fromOp : getJavaChangeOperationsForVertex(from)) {
				for (JavaChangeOperation toOp : getJavaChangeOperationsForVertex(to)) {
					result.add(GenealogyAnalyzer.getEdgeTypeForDependency(fromOp, toOp));
				}
			}
		}
		return result;
	}
	
	@Override
	public Set<String> getExistingEdgeTypes() {
		Set<String> result = new HashSet<String>();
		List<GenealogyEdgeType> values = Arrays.asList(GenealogyEdgeType.values());
		Iterable<RelationshipType> relationshipTypes = graph.getRelationshipTypes();
		for (RelationshipType type : relationshipTypes) {
			if (values.contains(type)) {
				result.add(type.getClass().getName());
			}
		}
		return result;
	}
	
	/**
	 * Gets the graph db.
	 * 
	 * @return the graph db
	 */
	protected GraphDatabaseService getGraphDB() {
		return this.graph;
	}
	
	/**
	 * Gets the graph db change operation by id.
	 * 
	 * @param oId
	 *            the o id
	 * @return the graph db change operation by id
	 */
	private GraphDBChangeOperation getGraphDBChangeOperationById(final Long oId) {
		return getGraphDBChangeOperationById(graph, oId);
	}
	
	@Override
	public File getGraphDBDir() {
		return this.dbFile;
	}
	
	@Override
	public GraphDatabaseService getGraphDBService(){
		return this.graph;
	}
	
	/**
	 * Gets the java change operations.
	 * 
	 * @return the java change operations
	 */
	@Override
	public Collection<JavaChangeOperation> getJavaChangeOperationsForVertex(final GenealogyVertex v) {
		Condition
		.check(persistenceUtil != null,
				"Cannot perform method operation 'getJavaChangeOperationsForVertex' if persistenceUtil not set. Use method setPersistenceUtil().");
		if(persistenceUtil == null){
			if (Logger.logError()) {
				Logger.error("Cannot perform method operation 'getJavaChangeOperationsForVertex' if persistenceUtil not set. Use method setPersistenceUtil().");
			}
			return new HashSet<JavaChangeOperation>();
		}
		Set<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		Collection<Long> javaOperationIds = v.getJavaChangeOperationIds();
		for (Long id : javaOperationIds) {
			result.add(persistenceUtil.loadById(id, JavaChangeOperation.class));
		}
		return result;
	}
	
	/**
	 * Gets the PersistenceUtil registered with the ChangeGenealogy.
	 * 
	 * @return the persistence util. Returns <code>null</code> if none set.
	 */
	protected PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	@Override
	public GenealogyVertex getRoot(){
		if (root == null) {
			for(GenealogyVertex v : vertexSet()){
				if((root == null) || (v.getTransaction().compareTo(root.getTransaction()) < 0)){
					root = v;
				}
			}
		}
		return root;
	}
	
	@Override
	public RCSTransaction getTransactionForVertex(final GenealogyVertex v) {
		Condition
		.check(persistenceUtil != null,
				"Cannot perform method operation 'getJavaChangeOperationsForVertex' if persistenceUtil not set. Use method setPersistenceUtil().");
		if (persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error("Cannot perform method operation 'getJavaChangeOperationsForVertex' if persistenceUtil not set. Use method setPersistenceUtil().");
			}
			return null;
		}
		Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class).eq("id",
				v.getTransactionId());
		List<RCSTransaction> load = persistenceUtil.load(criteria);
		if (load.isEmpty()) {
			throw new UnrecoverableError(
					"Could not find RCSTransaction for GenealogyNode! This means the GenealogyGraph is corrupt! (transaction_di = "
							+ v.getTransactionId() + ")");
		}
		return load.get(0);
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
	@NoneNull
	public GenealogyVertex getVertex(final String transactionId, final Collection<Long> javaChangeOperationIds) {
		Transaction tx = graph.beginTx();
		IndexHits<Node> hits = graph.index().forNodes(GenealogyVertex.transaction_id)
				.get(GenealogyVertex.transaction_id, transactionId);
		for (Node hit : hits) {
			GenealogyVertex vertex = this.getVertexForNode(hit);
			if (vertex.getJavaChangeOperationIds().containsAll(javaChangeOperationIds)) {
				hits.close();
				tx.success();
				tx.finish();
				return vertex;
			}
		}
		hits.close();
		tx.success();
		tx.finish();
		return null;
	}
	
	protected GenealogyVertex getVertexForNode(Node node) {
		if (!nodes2Vertices.containsKey(node)) {
			nodes2Vertices.put(node, new GenealogyVertex(this, node));
		}
		return nodes2Vertices.get(node);
	}
	
	/**
	 * Sets the persistence util.
	 * 
	 * @param persistenceUtil
	 *            the new persistence util
	 */
	public void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
	
	/**
	 * Vertex set.
	 * 
	 * @return the genealogy vertex iterator
	 */
	@Override
	public GenealogyVertexIterator vertexSet() {
		IndexHits<Node> indexHits = graph.index().forNodes(GenealogyVertex.transaction_id)
				.query(GenealogyVertex.transaction_id, "*");
		return new DefaultGenealogyVertexIterator(indexHits, this);
	}
	
	/**
	 * Number of vertices. In most scenarios this number is exact. In some
	 * scenarios this number will be close to accurate.
	 * 
	 * @return the #vertices
	 */
	@Override
	public int vertexSize() {
		IndexHits<Node> indexHits = graph.index().forNodes(GenealogyVertex.transaction_id)
				.query(GenealogyVertex.transaction_id, "*");
		int result = indexHits.size();
		indexHits.close();
		return result;
	}
	
}
