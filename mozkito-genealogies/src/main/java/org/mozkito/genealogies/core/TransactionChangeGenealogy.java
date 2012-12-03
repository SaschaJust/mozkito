/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.genealogies.core;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.tooling.GlobalGraphOperations;

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;

// import org.neo4j.graphdb.Transaction;

/**
 * The Class TransactionChangeGenealogy.
 */
public class TransactionChangeGenealogy implements ChangeGenealogy<RCSTransaction> {
	
	/** The Constant NODE_ID. */
	public static final String                NODE_ID       = "transaction_id";
	
	/** The Constant ROOT_VERTICES. */
	public static final String                ROOT_VERTICES = "root_vertices";
	
	/** The graph. */
	private final GraphDatabaseService        graph;
	
	/** The persistence util. */
	private final PersistenceUtil             persistenceUtil;
	
	/** The db file. */
	private final java.io.File                dbFile;
	
	/** The index manager. */
	private final IndexManager                indexManager;
	
	/** The node index. */
	private final Index<Node>                 nodeIndex;
	
	/** The root index. */
	private final Index<Node>                 rootIndex;
	
	/** The core. */
	private final CoreChangeGenealogy         core;
	
	/** The node cache. */
	private final Map<String, RCSTransaction> nodeCache     = new HashMap<String, RCSTransaction>();
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param graph
	 *            the graph
	 * @param dbFile
	 *            the db file
	 * @param persistenceUtil
	 *            the persistence util
	 * @param core
	 *            the core
	 */
	public TransactionChangeGenealogy(@NotNull final GraphDatabaseService graph, @NotNull final java.io.File dbFile,
	        final PersistenceUtil persistenceUtil, @NotNull final CoreChangeGenealogy core) {
		this.graph = graph;
		this.dbFile = dbFile;
		this.persistenceUtil = persistenceUtil;
		this.indexManager = graph.index();
		this.nodeIndex = this.indexManager.forNodes(TransactionChangeGenealogy.NODE_ID);
		this.rootIndex = this.indexManager.forNodes(TransactionChangeGenealogy.ROOT_VERTICES);
		this.core = core;
	}
	
	/**
	 * Adds a directed edge between target <--type-- dependent of type edgeType. Adds missing vertices before adding
	 * edge, if necessary.
	 * 
	 * @param dependent
	 *            the dependent
	 * @param target
	 *            The collection of JavaChangeOperations that represent the edge target vertex.
	 * @param edgeType
	 *            the GenealogyEdgeType of the edge to be added
	 * @return true, if successful
	 */
	@NoneNull
	boolean addEdge(final RCSTransaction dependent,
	                final RCSTransaction target,
	                final GenealogyEdgeType edgeType) {
		
		// add both vertices
		if (!containsVertex(dependent)) {
			addVertex(dependent);
		}
		if (!containsVertex(target)) {
			addVertex(target);
		}
		
		// we know that they have to exist
		final Node from = getNodeForVertex(dependent);
		final Node to = getNodeForVertex(target);
		
		if ((from == null) || (to == null)) {
			return false;
		}
		
		boolean edgeAlreadyExists = false;
		for (final GenealogyEdgeType existingEdgeType : getEdges(dependent, target)) {
			if (existingEdgeType.equals(edgeType)) {
				edgeAlreadyExists = true;
				break;
			}
		}
		if (!edgeAlreadyExists) {
			final org.neo4j.graphdb.Transaction tx = this.graph.beginTx();
			final Relationship relationship = from.createRelationshipTo(to, edgeType);
			if (relationship == null) {
				tx.failure();
				tx.finish();
				return false;
			}
			tx.success();
			tx.finish();
			if (isRoot(to)) {
				final org.neo4j.graphdb.Transaction tx2 = this.graph.beginTx();
				this.rootIndex.remove(to, TransactionChangeGenealogy.ROOT_VERTICES);
				tx2.success();
				tx2.finish();
			}
			
		}
		return true;
	}
	
	/**
	 * Adds a vertex to the genealogy that is associated with the specified JavaChangeOperation. This method also checks
	 * if such a vertex exists already.
	 * 
	 * @param v
	 *            the JavaChangeOperation to add
	 * @return true if the new vertex was successfully added. False otherwise (this may include that the vertex existed
	 *         already).
	 */
	@NoneNull
	boolean addVertex(@NotNull final RCSTransaction v) {
		if (hasVertex(v)) {
			if (Logger.logTrace()) {
				Logger.trace("Transaction with id `" + v.getId() + "` already exists");
			}
			return false;
		}
		final org.neo4j.graphdb.Transaction tx = this.graph.beginTx();
		final Node node = this.graph.createNode();
		if (node == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		node.setProperty(TransactionChangeGenealogy.NODE_ID, v.getId());
		
		this.nodeIndex.add(node, TransactionChangeGenealogy.NODE_ID,
		                   node.getProperty(TransactionChangeGenealogy.NODE_ID));
		this.rootIndex.add(node, TransactionChangeGenealogy.ROOT_VERTICES, 1);
		
		tx.success();
		tx.finish();
		
		return true;
		
	}
	
	/**
	 * Must be called to ensure the Graph DB to be shut down properly! This will be taken care of by a separate
	 * ShutdownHook. So make sure to call this method only when you are know what you are doing!
	 */
	@Override
	public void close() {
		this.graph.shutdown();
	}
	
	/**
	 * Checks if there exists a directed dependency from <code>from</code> to <code>to</code>.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if an edge from <code>from</code> to <code>to</code> exists, false otherwise.
	 */
	@Override
	public boolean containsEdge(final RCSTransaction from,
	                            final RCSTransaction to) {
		final GenealogyEdgeType result = getEdge(from, to);
		return result != null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#containsVertex(java.lang.Object)
	 */
	@Override
	public boolean containsVertex(final RCSTransaction vertex) {
		return hasVertex(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#edgeSize()
	 */
	@Override
	public int edgeSize() {
		int result = 0;
		final IndexHits<Node> nodes = nodes();
		for (final Node node : nodes) {
			result += getAllDependents(node).size();
		}
		nodes.close();
		return result;
	}
	
	/**
	 * Returns a collection containing nodes that depend on node <code>node</code> (incoming edges).
	 * 
	 * @param operation
	 *            the operation
	 * @return all dependents
	 */
	@Override
	public Collection<RCSTransaction> getAllDependants(final RCSTransaction operation) {
		return getDependants(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                     GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                     GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Returns a collection containing nodes that depend on node <code>node</code> (incoming edges).
	 * 
	 * @param node
	 *            the node
	 * @return all dependents
	 */
	private Collection<Node> getAllDependents(final Node node) {
		return getDependents(node, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                     GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                     GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Returns a collection containing nodes that are connected through an outgoing edge.
	 * 
	 * @param operation
	 *            the operation
	 * @return all dependents
	 */
	@Override
	public Collection<RCSTransaction> getAllParents(final RCSTransaction operation) {
		return getParents(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                  GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                  GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                  GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getCore()
	 */
	@Override
	public CoreChangeGenealogy getCore() {
		return this.core;
	}
	
	/**
	 * Returns a collection containing nodes depending on node <code>node</code> via an edge of a type is contained
	 * within the specified edge type array. (incoming edges)
	 * 
	 * @param operation
	 *            the operation
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	@Override
	@NoneNull
	public Collection<RCSTransaction> getDependants(final RCSTransaction operation,
	                                                final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertices for RCSTrabsaction `"
				        + operation.toString()
				        + " that has no correspondence within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<RCSTransaction>();
		}
		final Collection<Node> dependentNodes = getDependents(node, edgeTypes);
		final Set<RCSTransaction> parentOperations = new HashSet<RCSTransaction>();
		for (final Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	/**
	 * Returns a collection containing nodes depending on node <code>node</code> via an edge of a type is contained
	 * within the specified edge type array. (incoming edges)
	 * 
	 * @param node
	 *            the node
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	@NoneNull
	private Collection<Node> getDependents(final Node node,
	                                       final GenealogyEdgeType... edgeTypes) {
		final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, edgeTypes);
		final Set<Node> parents = new HashSet<Node>();
		for (final Relationship rel : relationships) {
			parents.add(rel.getStartNode());
		}
		return parents;
	}
	
	// /////////////
	
	/**
	 * Returns the directed edge type between the two specified vertices.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the edge or <code>null</code> if no such edge exists.
	 */
	@NoneNull
	public GenealogyEdgeType getEdge(final RCSTransaction from,
	                                 final RCSTransaction to) {
		final Node fromNode = getNodeForVertex(from);
		final Node toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for RCSTransction `" + from.toString() + "` or `"
				        + to.toString()
				        + "` that have no corresponce within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		final Iterable<Relationship> relationships = fromNode.getRelationships(Direction.OUTGOING,
		                                                                       GenealogyEdgeType.CallOnDefinition,
		                                                                       GenealogyEdgeType.DefinitionOnDefinition,
		                                                                       GenealogyEdgeType.DefinitionOnDeletedDefinition,
		                                                                       GenealogyEdgeType.DeletedCallOnCall,
		                                                                       GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                                                                       GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		for (final Relationship rel : relationships) {
			if (rel.getEndNode().equals(toNode)) {
				final RelationshipType relationshipType = rel.getType();
				return GenealogyEdgeType.valueOf(relationshipType.toString());
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getEdges(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Collection<GenealogyEdgeType> getEdges(final RCSTransaction from,
	                                              final RCSTransaction to) {
		final Node fromNode = getNodeForVertex(from);
		final Node toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for RCSTransction `" + from.toString() + "` or `"
				        + to.toString()
				        + "` that have no corresponce within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		final Iterable<Relationship> relationships = fromNode.getRelationships(Direction.OUTGOING,
		                                                                       GenealogyEdgeType.CallOnDefinition,
		                                                                       GenealogyEdgeType.DefinitionOnDefinition,
		                                                                       GenealogyEdgeType.DefinitionOnDeletedDefinition,
		                                                                       GenealogyEdgeType.DeletedCallOnCall,
		                                                                       GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                                                                       GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		final Collection<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		for (final Relationship rel : relationships) {
			if (rel.getEndNode().equals(toNode)) {
				final RelationshipType relationshipType = rel.getType();
				result.add(GenealogyEdgeType.valueOf(relationshipType.toString()));
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getExistingEdgeTypes()
	 */
	@Override
	public Set<GenealogyEdgeType> getExistingEdgeTypes() {
		final Set<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		final List<GenealogyEdgeType> values = Arrays.asList(GenealogyEdgeType.values());
		final Iterable<RelationshipType> relationshipTypes = GlobalGraphOperations.at(getGraphDBService())
		                                                                          .getAllRelationshipTypes();
		for (final RelationshipType type : relationshipTypes) {
			if (values.contains(type)) {
				final GenealogyEdgeType edgeType = GenealogyEdgeType.valueOf(type.toString());
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getGraphDBDir()
	 */
	@Override
	public File getGraphDBDir() {
		return this.dbFile;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getGraphDBService()
	 */
	@Override
	public GraphDatabaseService getGraphDBService() {
		return this.graph;
	}
	
	/**
	 * Gets the node for vertex.
	 * 
	 * @param op
	 *            the op
	 * @return the node for vertex
	 */
	private Node getNodeForVertex(final RCSTransaction op) {
		final IndexHits<Node> indexHits = this.nodeIndex.query(TransactionChangeGenealogy.NODE_ID, op.getId());
		if (!indexHits.hasNext()) {
			indexHits.close();
			return null;
		}
		final Node node = indexHits.next();
		indexHits.close();
		return node;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getNodeId(java.lang.Object)
	 */
	@Override
	public String getNodeId(final RCSTransaction t) {
		if (containsVertex(t)) {
			return t.getId();
		}
		return null;
	}
	
	/**
	 * Returns a collection containing nodes connected though outgoing edges.
	 * 
	 * @param node
	 *            the node
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	private Collection<Node> getParents(final Node node,
	                                    final GenealogyEdgeType... edgeTypes) {
		final Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, edgeTypes);
		final Set<Node> parents = new HashSet<Node>();
		for (final Relationship rel : relationships) {
			parents.add(rel.getEndNode());
		}
		return parents;
	}
	
	/**
	 * Returns a collection containing nodes connected though outgoing edges.
	 * 
	 * @param operation
	 *            the operation
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	@Override
	public Collection<RCSTransaction> getParents(final RCSTransaction operation,
	                                             final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for RCSTransction `" + operation.toString()
				        + "` that has no corresponce within the ChangeGenealogy. Returning empty null.");
			}
			return new HashSet<RCSTransaction>();
		}
		final Collection<Node> dependentNodes = getParents(node, edgeTypes);
		final Set<RCSTransaction> parentOperations = new HashSet<RCSTransaction>();
		for (final Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	/**
	 * Gets the PersistenceUtil registered with the ChangeGenealogy.
	 * 
	 * @return the persistence util. Returns <code>null</code> if none set.
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getRoots()
	 */
	@Override
	public Collection<RCSTransaction> getRoots() {
		final Collection<RCSTransaction> result = new HashSet<RCSTransaction>();
		final IndexHits<Node> indexHits = this.rootIndex.query(TransactionChangeGenealogy.ROOT_VERTICES, 1);
		while (indexHits.hasNext()) {
			result.add(getVertexForNode(indexHits.next()));
		}
		indexHits.close();
		return result;
	}
	
	/**
	 * Gets the vertex for node.
	 * 
	 * @param dependentNode
	 *            the dependent node
	 * @return the vertex for node
	 */
	private RCSTransaction getVertexForNode(final Node dependentNode) {
		final String operationId = (String) dependentNode.getProperty(TransactionChangeGenealogy.NODE_ID);
		return loadById(operationId, RCSTransaction.class);
	}
	
	/**
	 * Checks for vertex.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return true, if successful
	 */
	public boolean hasVertex(final RCSTransaction vertex) {
		return (getNodeForVertex(vertex) != null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#inDegree(java.lang.Object)
	 */
	@Override
	public int inDegree(final RCSTransaction op) {
		final Node node = getNodeForVertex(op);
		final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING,
		                                                                   GenealogyEdgeType.values());
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#inDegree(java.lang.Object,
	 * org.mozkito.genealogies.core.GenealogyEdgeType[])
	 */
	@Override
	public int inDegree(final RCSTransaction op,
	                    final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(op);
		final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, edgeTypes);
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/**
	 * Checks if is root.
	 * 
	 * @param node
	 *            the node
	 * @return true, if is root
	 */
	private boolean isRoot(final Node node) {
		final IndexHits<Node> indexHits = this.rootIndex.query(TransactionChangeGenealogy.ROOT_VERTICES, 1);
		boolean result = false;
		while (indexHits.hasNext()) {
			final Node hit = indexHits.next();
			result |= (hit.getId() == node.getId());
			if (result) {
				break;
			}
		}
		indexHits.close();
		return result;
	}
	
	/**
	 * Load by id.
	 * 
	 * @param id
	 *            the id
	 * @param clazz
	 *            the clazz
	 * @return the rCS transaction
	 */
	public RCSTransaction loadById(final String id,
	                               final Class<? extends RCSTransaction> clazz) {
		if (!this.nodeCache.containsKey(id)) {
			this.nodeCache.put(id, this.persistenceUtil.loadById(id, clazz));
		}
		return this.nodeCache.get(id);
	}
	
	/**
	 * Vertex set.
	 * 
	 * @return the genealogy vertex iterator
	 */
	protected IndexHits<Node> nodes() {
		return this.nodeIndex.query(TransactionChangeGenealogy.NODE_ID, "*");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#outDegree(java.lang.Object)
	 */
	@Override
	public int outDegree(final RCSTransaction op) {
		final Node node = getNodeForVertex(op);
		final Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING,
		                                                                   GenealogyEdgeType.values());
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#outDegree(java.lang.Object,
	 * org.mozkito.genealogies.core.GenealogyEdgeType[])
	 */
	@Override
	public int outDegree(final RCSTransaction op,
	                     final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(op);
		final Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, edgeTypes);
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/**
	 * Vertex iterator.
	 * 
	 * @return the iterator
	 */
	public Iterator<RCSTransaction> vertexIterator() {
		final IndexHits<Node> indexHits = this.nodeIndex.query(TransactionChangeGenealogy.NODE_ID, "*");
		
		final Set<String> ids = new HashSet<String>();
		for (final Node node : indexHits) {
			ids.add((String) node.getProperty(TransactionChangeGenealogy.NODE_ID));
		}
		indexHits.close();
		return new Iterator<RCSTransaction>() {
			
			private final Iterator<String> idIter = ids.iterator();
			
			@Override
			public boolean hasNext() {
				return this.idIter.hasNext();
			}
			
			@Override
			public RCSTransaction next() {
				return loadById(this.idIter.next(), RCSTransaction.class);
			}
			
			@Override
			public void remove() {
				this.idIter.remove();
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#vertexSet()
	 */
	@Override
	public Iterable<RCSTransaction> vertexSet() {
		return new Iterable<RCSTransaction>() {
			
			@Override
			public Iterator<RCSTransaction> iterator() {
				return vertexIterator();
			}
		};
	}
	
	/**
	 * Number of vertices. In most scenarios this number is exact. In some scenarios this number will be close to
	 * accurate.
	 * 
	 * @return the #vertices
	 */
	@Override
	public int vertexSize() {
		final IndexHits<Node> indexHits = this.nodeIndex.query(TransactionChangeGenealogy.NODE_ID, "*");
		final int result = indexHits.size();
		indexHits.close();
		return result;
	}
	
}
