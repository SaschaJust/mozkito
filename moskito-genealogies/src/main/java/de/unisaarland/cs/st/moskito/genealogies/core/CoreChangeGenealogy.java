package de.unisaarland.cs.st.moskito.genealogies.core;

import java.io.File;
import java.util.ArrayList;
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
import org.neo4j.graphdb.index.IndexManager;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;

/**
 * The Class ChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreChangeGenealogy implements ChangeGenealogy<JavaChangeOperation> {
	
	public static final String         NODE_ID       = "javachangeooeration_id";
	private static final String        ROOT_VERTICES = "root_vertices";
	
	/** The graph. */
	private final GraphDatabaseService graph;
	
	/** The persistence util. */
	private PersistenceUtil            persistenceUtil;
	
	private File                       dbFile;
	
	private IndexManager               indexManager;
	
	private Index<Node>                nodeIndex;
	private Index<Node>                rootIndex;
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param graph
	 *            the graph
	 * @param dbFile
	 */
	@NoneNull
	public CoreChangeGenealogy(final GraphDatabaseService graph, File dbFile, PersistenceUtil persistenceUtil) {
		this.graph = graph;
		this.dbFile = dbFile;
		this.persistenceUtil = persistenceUtil;
		indexManager = graph.index();
		nodeIndex = indexManager.forNodes(NODE_ID);
		rootIndex = indexManager.forNodes(ROOT_VERTICES);
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
		
		ChangeType depChangeType = dependent.getChangeType();
		JavaElement depElement = dependent.getChangedElementLocation().getElement();
		JavaElement targetElement = target.getChangedElementLocation().getElement();
		ChangeType targetChangeType = target.getChangeType();
		switch (edgeType) {
			case DefinitionOnDefinition:
			case DefinitionOnDeletedDefinition:
				if (depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DefinitionOn(Deleted)Definition` edge starting from delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DefinitionOn(Deleted)Definition` edge starting from non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				if (!(targetElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DefinitionOn(Deleted)Definition` edge pointing to non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				
				switch (edgeType) {
					case DefinitionOnDefinition:
						if (targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DefinitionOnDefinition` edge pointing to delete operation. Edge not added.");
							}
							return false;
						}
						break;
					case DefinitionOnDeletedDefinition:
						if (!targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DefinitionOnDefinition` edge pointing to non-delete operation. Edge not added.");
							}
							return false;
						}
						break;
					default:
						if (Logger.logError()) {
							Logger.error("Unhandled situation found: edgeType=" + edgeType.toString() + " dependent="
									+ dependent.toString() + " target=" + target.toString());
						}
						return false;
				}
				break;
				
			case DeletedDefinitionOnDefinition:
				if (!depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge starting from non-delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge starting from non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				if (targetChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge pointing to delete operation. Edge not added.");
					}
					return false;
				}
				if (!(targetElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge pointing to non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				break;
			case CallOnDefinition:
				if (depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge starting from delete operation. Edge not added.");
					}
					return false;
				}
				if (targetChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge starting from delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodCall)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge starting from non JavaMethodCall. Edge not added.");
					}
					return false;
				}
				if (!(targetElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge pointing to non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				break;
			case DeletedCallOnCall:
			case DeletedCallOnDeletedDefinition:
				
				if (!depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedCallOn[DeletedDefinition|Call]` edge starting from non-delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodCall)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedCallOn[DeletedDefinition|Call]` edge starting from non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				switch (edgeType) {
					case DeletedCallOnCall:
						if (targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnCall` edge pointing to delete operation. Edge not added.");
							}
							return false;
						}
						if (!(targetElement instanceof JavaMethodCall)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnCall` edge pointing to non JavaMethodCall. Edge not added.");
							}
							return false;
						}
						break;
					case DeletedCallOnDeletedDefinition:
						if (!targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnDeletedDefinition` edge pointing to non-delete operation. Edge not added.");
							}
							return false;
						}
						if (!(targetElement instanceof JavaMethodDefinition)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnDeletedDefinition` edge pointing to non JavaMethodDefinition. Edge not added.");
							}
							return false;
						}
						break;
					default:
						return false;
				}
				break;
			default:
				if (Logger.logError()) {
					Logger.error("Unhandled situation found: edgeType=" + edgeType.toString() + " dependent="
							+ dependent.toString() + " target=" + target.toString());
				}
				return false;
		}
		
		//add both vertices
		if (!containsVertex(dependent)) {
			addVertex(dependent);
		}
		if (!containsVertex(target)) {
			addVertex(target);
		}
		
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
		
		rootIndex.remove(from, ROOT_VERTICES);
		
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
		if (this.hasVertex(v)) {
			if (Logger.logWarn()) {
				Logger.warn("JavaChangeOperations with id `" + v.getId() + "` already exists");
			}
			return false;
		}
		Transaction tx = this.graph.beginTx();
		Node node = graph.createNode();
		if (node == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		node.setProperty(NODE_ID, v.getId());
		
		nodeIndex.add(node, NODE_ID, node.getProperty(NODE_ID));
		rootIndex.add(node, ROOT_VERTICES, 1);
		
		tx.success();
		tx.finish();
		
		return true;
		
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
	@Override
	public boolean containsEdge(JavaChangeOperation from, JavaChangeOperation to) {
		GenealogyEdgeType result = this.getEdge(from, to);
		return result != null;
	}
	
	@Override
	public boolean containsVertex(JavaChangeOperation vertex) {
		return hasVertex(vertex);
	}
	
	@Override
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
	@Override
	public Collection<JavaChangeOperation> getAllDependants(JavaChangeOperation operation) {
		return getDependants(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
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
	 * Returns a collection containing nodes that  are connected through an outgoing edge.
	 * 
	 * @return all dependents
	 */
	@Override
	public Collection<JavaChangeOperation> getAllParents(JavaChangeOperation operation) {
		return getParents(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	@Override
	public CoreChangeGenealogy getCore() {
		return this;
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
	@Override
	public Collection<JavaChangeOperation> getDependants(JavaChangeOperation operation, GenealogyEdgeType... edgeTypes) {
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
	
	///////////////
	
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
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, edgeTypes);
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
				RelationshipType relationshipType = rel.getType();
				return GenealogyEdgeType.valueOf(relationshipType.toString());
			}
		}
		return null;
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(JavaChangeOperation from, JavaChangeOperation to) {
		Collection<GenealogyEdgeType> result = new ArrayList<GenealogyEdgeType>(1);
		result.add(getEdge(from, to));
		return result;
	}
	
	@Override
	public Set<GenealogyEdgeType> getExistingEdgeTypes() {
		Set<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		List<GenealogyEdgeType> values = Arrays.asList(GenealogyEdgeType.values());
		Iterable<RelationshipType> relationshipTypes = graph.getRelationshipTypes();
		for (RelationshipType type : relationshipTypes) {
			if (values.contains(type)) {
				GenealogyEdgeType edgeType = GenealogyEdgeType.valueOf(type.toString());
				result.add(edgeType);
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
	
	@Override
	public File getGraphDBDir() {
		return this.dbFile;
	}
	
	@Override
	public GraphDatabaseService getGraphDBService(){
		return this.graph;
	}
	
	private Node getNodeForVertex(final JavaChangeOperation op){
		IndexHits<Node> indexHits = nodeIndex.query(NODE_ID, op.getId());
		if(!indexHits.hasNext()){
			return null;
		}
		Node node = indexHits.next();
		indexHits.close();
		return node;
	}
	
	@Override
	public String getNodeId(JavaChangeOperation t) {
		if(this.containsVertex(t)){
			return String.valueOf(t.getId());
		}
		return null;
	}
	
	/**
	 * Returns a collection containing nodes connected though outgoing edges.
	 * 
	 * @param types
	 *            consider only edges of these types
	 * @return the dependents
	 */
	@Override
	public Collection<JavaChangeOperation> getParents(JavaChangeOperation operation, GenealogyEdgeType... edgeTypes) {
		Node node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<JavaChangeOperation>();
		}
		Collection<Node> dependentNodes = getParents(node, edgeTypes);
		Set<JavaChangeOperation> parentOperations = new HashSet<JavaChangeOperation>();
		for (Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	/**
	 * Returns a collection containing nodes connected though outgoing edges.
	 * 
	 * @param types
	 *            consider only edges of these types
	 * @return the dependents
	 */
	private Collection<Node> getParents(Node node, GenealogyEdgeType... edgeTypes) {
		Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, edgeTypes);
		Set<Node> parents = new HashSet<Node>();
		for (Relationship rel : relationships) {
			parents.add(rel.getEndNode());
		}
		return parents;
	}
	
	/**
	 * Gets the PersistenceUtil registered with the ChangeGenealogy.
	 * 
	 * @return the persistence util. Returns <code>null</code> if none set.
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	@Override
	public Collection<JavaChangeOperation> getRoots() {
		Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		IndexHits<Node> indexHits = rootIndex.query(ROOT_VERTICES, 1);
		while (indexHits.hasNext()) {
			result.add(this.getVertexForNode(indexHits.next()));
		}
		return result;
	}
	
	private JavaChangeOperation getVertexForNode(Node dependentNode) {
		Long operationId = (Long) dependentNode.getProperty(NODE_ID);
		return persistenceUtil.loadById(operationId, JavaChangeOperation.class);
	}
	
	public boolean hasVertex(final JavaChangeOperation vertex) {
		return (getNodeForVertex(vertex) != null);
	}
	
	@Override
	public int inDegree(JavaChangeOperation op) {
		Node node = getNodeForVertex(op);
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, GenealogyEdgeType.values());
		int numEdges = 0;
		for (@SuppressWarnings("unused") Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/**
	 * Vertex set.
	 * 
	 * @return the genealogy vertex iterator
	 */
	public IndexHits<Node> nodes() {
		return nodeIndex.query(NODE_ID, "*");
	}
	
	@Override
	public int outDegree(JavaChangeOperation op) {
		Node node = getNodeForVertex(op);
		Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, GenealogyEdgeType.values());
		int numEdges = 0;
		for (@SuppressWarnings("unused") Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/**
	 * 
	 * @return an iterator over all JavaChangeOperations contained by this
	 *         change genealogy
	 */
	public Iterator<JavaChangeOperation> vertexIterator() {
		IndexHits<Node> indexHits = nodeIndex.query(NODE_ID, "*");
		
		Set<Long> operations = new HashSet<Long>();
		for (Node node : indexHits) {
			operations.add((Long) node.getProperty(NODE_ID));
		}
		indexHits.close();
		return new CoreGenealogyVertexIterator(operations, persistenceUtil);
	}
	
	@Override
	public Iterator<JavaChangeOperation> vertexSet() {
		return vertexIterator();
	}
	
	/**
	 * Number of vertices. In most scenarios this number is exact. In some
	 * scenarios this number will be close to accurate.
	 * 
	 * @return the #vertices
	 */
	@Override
	public int vertexSize() {
		IndexHits<Node> indexHits = graph.index().forNodes(NODE_ID).query(NODE_ID, "*");
		int result = indexHits.size();
		indexHits.close();
		return result;
	}
	
}
