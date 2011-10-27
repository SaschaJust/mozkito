package de.unisaarland.cs.st.reposuite.genealogies.core;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kisa.Logger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

/**
 * The Class ChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreChangeGenealogy {
	
	private static final String        NODE_ID = "javachangeooeration_id";
	
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
	public CoreChangeGenealogy(final GraphDatabaseService graph, File dbFile) {
		this.graph = graph;
		this.dbFile = dbFile;
	}
	
	/**
	 * Adds a directed edge between target <--type-- dependent of type edgeType.
	 * Adds missing vertices before adding edge, if necessary.
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
	public boolean addEdge(@NotEmpty final JavaChangeOperation dependent,
			@NotEmpty final JavaChangeOperation target, final GenealogyEdgeType edgeType) {
		
		//add both vertices
		addVertex(dependent);
		addVertex(target);
		
		//we know that they have to exist
		Node from = this.getNodeForVertex(dependent);
		Node to = this.getNodeForVertex(target);
		
		if ((from == null) || (to == null)) {
			return false;
		}
		
		Transaction tx = graph.beginTx();
		Relationship relationship = from.createRelationshipTo(to, edgeType);
		if (relationship == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		tx.success();
		tx.finish();
		
		return true;
	}
	
	/**
	 * Adds a vertex to the genealogy that is associated with the specified
	 * JavaChangeOperation. This method also checks if such a vertex exists
	 * already.
	 * 
	 * @param v
	 *            the JavaChangeOperation to add
	 * @return true if the new vertex was successfully added. False otherwise
	 *         (this may include that the vertex existed already).
	 */
	@NoneNull
	public boolean addVertex(@NotEmpty final JavaChangeOperation v) {
		Transaction tx = this.graph.beginTx();
		if (this.hasVertex(v)) {
			return false;
		}
		Node node = graph.createNode();
		if (node == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		node.setProperty(NODE_ID, v.getId());
		
		Index<Node> index = graph.index().forNodes(NODE_ID);
		index.add(node, NODE_ID, node.getProperty(NODE_ID));
		
		tx.success();
		tx.finish();
		
		return true;
		
	}
	
	/**
	 * Must be called to ensure the Graph DB to be shut down properly! This will
	 * be taken care of by a separate ShutdownHook. So make sure to call this
	 * method only when you are know what you are doing!
	 */
	public void close() {
		this.graph.shutdown();
	}
	
	/**
	 * Checks if there exists a directed dependency from <code>from</code> to
	 * <code>to</code>.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if an edge from <code>from</code> to <code>to</code>
	 *         exists, false otherwise.
	 */
	public boolean containsEdge(JavaChangeOperation from, JavaChangeOperation to) {
		GenealogyEdgeType result = this.getEdge(from, to);
		return result != null;
	}
	
	public int edgeSize(){
		int result = 0;
		IndexHits<Node> nodes = nodes();
		for (Node node : nodes) {
			result += getAllDependents(node).size();
		}
		nodes.close();
		return result;
		
	}
	
	/**
	 * Returns a collection containing nodes that depend on node
	 * <code>node</code> (incoming edges).
	 * 
	 * @return all dependents
	 */
	public Collection<JavaChangeOperation> getAllDependents(JavaChangeOperation operation) {
		return getDependents(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Returns a collection containing nodes that depend on node
	 * <code>node</code> (incoming edges).
	 * 
	 * @return all dependents
	 */
	private Collection<Node> getAllDependents(Node node) {
		return getDependents(node, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Returns a collection containing nodes depending on node <code>node</code>
	 * via an edge of a type is contained within the specified edge type array.
	 * (incoming edges)
	 * 
	 * @param types
	 *            consider only edges of these types
	 * @return the dependents
	 */
	public Collection<JavaChangeOperation> getDependents(JavaChangeOperation operation, GenealogyEdgeType... edgeTypes) {
		Node node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<JavaChangeOperation>();
		}
		Collection<Node> dependentNodes = getDependents(node, edgeTypes);
		Set<JavaChangeOperation> parentOperations = new HashSet<JavaChangeOperation>();
		for (Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	/**
	 * Returns a collection containing nodes depending on node <code>node</code>
	 * via an edge of a type is contained within the specified edge type array.
	 * (incoming edges)
	 * 
	 * @param types
	 *            consider only edges of these types
	 * @return the dependents
	 */
	private Collection<Node> getDependents(Node node, GenealogyEdgeType... edgeTypes) {
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING,
				GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
		Set<Node> parents = new HashSet<Node>();
		for (Relationship rel : relationships) {
			parents.add(rel.getStartNode());
		}
		return parents;
	}
	
	/**
	 * Returns the directed edge type between the two specified vertices.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the edge or <code>null</code> if no such edge exists.
	 */
	public GenealogyEdgeType getEdge(JavaChangeOperation from, JavaChangeOperation to) {
		Node fromNode = getNodeForVertex(from);
		Node toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		Iterable<Relationship> relationships = fromNode.getRelationships(Direction.OUTGOING,
				GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		for (Relationship rel : relationships) {
			if (rel.getEndNode().equals(toNode)) {
				return (GenealogyEdgeType) rel.getType();
			}
		}
		return null;
	}
	
	public Set<GenealogyEdgeType> getExistingEdgeTypes() {
		Set<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		List<GenealogyEdgeType> values = Arrays.asList(GenealogyEdgeType.values());
		Iterable<RelationshipType> relationshipTypes = graph.getRelationshipTypes();
		for (RelationshipType type : relationshipTypes) {
			if (values.contains(type)) {
				result.add((GenealogyEdgeType) type);
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
	
	public File getGraphDBDir() {
		return this.dbFile;
	}
	
	public GraphDatabaseService getGraphDBService(){
		return this.graph;
	}
	
	private Node getNodeForVertex(final JavaChangeOperation op){
		IndexHits<Node> indexHits = graph.index().forNodes(NODE_ID).query(NODE_ID, op.getId());
		if(!indexHits.hasNext()){
			return null;
		}
		Node node = indexHits.next();
		indexHits.close();
		return node;
	}
	
	/**
	 * Gets the PersistenceUtil registered with the ChangeGenealogy.
	 * 
	 * @return the persistence util. Returns <code>null</code> if none set.
	 */
	protected PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	private JavaChangeOperation getVertexForNode(Node dependentNode) {
		Long operationId = (Long) dependentNode.getProperty(NODE_ID);
		return persistenceUtil.loadById(operationId, JavaChangeOperation.class);
	}
	
	public boolean hasVertex(final JavaChangeOperation vertex) {
		return (getNodeForVertex(vertex) != null);
	}
	
	/**
	 * Vertex set.
	 * 
	 * @return the genealogy vertex iterator
	 */
	public IndexHits<Node> nodes() {
		return graph.index().forNodes(NODE_ID).query(NODE_ID, "*");
	}
	
	/**
	 * 
	 * @return an iterator over all JavaChangeOperations contained by this
	 *         change genealogy
	 */
	public Iterator<JavaChangeOperation> vertexIterator() {
		IndexHits<Node> indexHits = graph.index().forNodes(NODE_ID).query(NODE_ID, "*");
		
		Set<Long> operations = new HashSet<Long>();
		for (Node node : indexHits) {
			operations.add((Long) node.getProperty(NODE_ID));
		}
		indexHits.close();
		return new CoreGenealogyVertexIterator(operations, persistenceUtil);
	}
	
	/**
	 * Number of vertices. In most scenarios this number is exact. In some
	 * scenarios this number will be close to accurate.
	 * 
	 * @return the #vertices
	 */
	public int vertexSize() {
		IndexHits<Node> indexHits = graph.index().forNodes(NODE_ID).query(NODE_ID, "*");
		int result = indexHits.size();
		indexHits.close();
		return result;
	}
	
}
