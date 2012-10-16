/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.genealogies.core;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
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
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.neo4j.Neo4jRootCache;
import de.unisaarland.cs.st.moskito.genealogies.neo4j.Neo4jVertexCache;
import de.unisaarland.cs.st.moskito.genealogies.persistence.JavaChangeOperationCache;
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
	
	public static final String               NODE_ID       = "javachangeooeration_id";
	public static final String               ROOT_VERTICES = "root_vertices";
	
	/** The graph. */
	private final GraphDatabaseService       graph;
	
	/** The persistence util. */
	private final PersistenceUtil            persistenceUtil;
	
	private final File                       dbFile;
	
	private final IndexManager               indexManager;
	
	private final Index<Node>                nodeIndex;
	private final Neo4jVertexCache           vertexCache;
	private final JavaChangeOperationCache   operationCache;
	
	private final TransactionChangeGenealogy transactionGenealogy;
	private final Neo4jRootCache             rootCache;
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param graph
	 *            the graph
	 * @param dbFile
	 */
	public CoreChangeGenealogy(@NotNull final GraphDatabaseService graph, @NotNull final File dbFile,
	        final PersistenceUtil persistenceUtil) {
		this.graph = graph;
		this.dbFile = dbFile;
		this.persistenceUtil = persistenceUtil;
		this.indexManager = graph.index();
		this.nodeIndex = this.indexManager.forNodes(NODE_ID);
		
		this.rootCache = new Neo4jRootCache(graph);
		this.operationCache = new JavaChangeOperationCache(persistenceUtil);
		this.vertexCache = new Neo4jVertexCache(this.nodeIndex);
		final File transactionDbFile = new File(dbFile.getAbsolutePath() + FileUtils.fileSeparator + "transactionLayer");
		
		final GraphDatabaseService transactionGraphService = new EmbeddedGraphDatabase(
		                                                                               transactionDbFile.getAbsolutePath());
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				transactionGraphService.shutdown();
			}
		});
		
		this.transactionGenealogy = new TransactionChangeGenealogy(transactionGraphService, transactionDbFile,
		                                                           persistenceUtil, this);
		
	}
	
	/**
	 * Adds a directed edge between target <--type-- dependent of type edgeType. Adds missing vertices before adding
	 * edge, if necessary.
	 * 
	 * @param dependant
	 *            The collection of JavaChangeOperations that represent the edge source vertex.
	 * @param target
	 *            The collection of JavaChangeOperations that represent the edge target vertex.
	 * @param edgeType
	 *            the GenealogyEdgeType of the edge to be added
	 * @return true, if successful
	 */
	@NoneNull
	public boolean addEdge(final JavaChangeOperation dependent,
	                       final JavaChangeOperation target,
	                       final GenealogyEdgeType edgeType) {
		
		final ChangeType depChangeType = dependent.getChangeType();
		final JavaElement depElement = dependent.getChangedElementLocation().getElement();
		final JavaElement targetElement = target.getChangedElementLocation().getElement();
		final ChangeType targetChangeType = target.getChangeType();
		
		switch (edgeType) {
			case DefinitionOnDefinition:
			case DefinitionOnDeletedDefinition:
			case ModifiedDefinitionOnDefinition:
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
					case ModifiedDefinitionOnDefinition:
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
			if (Logger.logError()) {
				Logger.error("One or more change genealogy structure nodes are NULL: from=%s to=%s", from, to);
			}
			return false;
		}
		
		boolean edgeAlreadyExists = false;
		for (final GenealogyEdgeType existingEdgeType : getEdges(from, to)) {
			if (existingEdgeType.equals(edgeType)) {
				edgeAlreadyExists = true;
				break;
			}
		}
		if (!edgeAlreadyExists) {
			final Transaction tx = this.graph.beginTx();
			final Relationship relationship = from.createRelationshipTo(to, edgeType);
			if (relationship == null) {
				tx.failure();
				tx.finish();
				if (Logger.logWarn()) {
					Logger.warn("Relationship %s->%s already exists!", from, to);
				}
				return false;
			}
			tx.success();
			tx.finish();
			if (isRoot(to)) {
				this.rootCache.remove(to);
			}
			
		}
		
		if (!dependent.getRevision().getTransaction().getId().equals(target.getRevision().getTransaction().getId())) {
			this.transactionGenealogy.addEdge(dependent.getRevision().getTransaction(), target.getRevision()
			                                                                                  .getTransaction(),
			                                  edgeType);
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
	public boolean addVertex(@NotNull final JavaChangeOperation v) {
		if (hasVertex(v)) {
			if (Logger.logWarn()) {
				Logger.warn("JavaChangeOperations with id `" + v.getId() + "` already exists");
			}
			return false;
		}
		final Transaction tx = this.graph.beginTx();
		final Node node = this.graph.createNode();
		if (node == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		node.setProperty(NODE_ID, v.getId());
		
		this.nodeIndex.add(node, NODE_ID, node.getProperty(NODE_ID));
		this.rootCache.add(node);
		
		tx.success();
		tx.finish();
		
		this.transactionGenealogy.addVertex(v.getRevision().getTransaction());
		
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
	public boolean containsEdge(final JavaChangeOperation from,
	                            final JavaChangeOperation to) {
		final GenealogyEdgeType result = getEdge(from, to);
		return result != null;
	}
	
	@Override
	public boolean containsVertex(final JavaChangeOperation vertex) {
		return hasVertex(vertex);
	}
	
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
	 * @return all dependents
	 */
	@Override
	public Collection<JavaChangeOperation> getAllDependants(final JavaChangeOperation operation) {
		return getDependants(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                     GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                     GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Returns a collection containing nodes that depend on node <code>node</code> (incoming edges).
	 * 
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
	 * @return all dependents
	 */
	@Override
	public Collection<JavaChangeOperation> getAllParents(final JavaChangeOperation operation) {
		return getParents(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                  GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                  GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                  GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	@Override
	public CoreChangeGenealogy getCore() {
		return this;
	}
	
	/**
	 * Returns a collection containing nodes depending on node <code>node</code> via an edge of a type is contained
	 * within the specified edge type array. (incoming edges)
	 * 
	 * @param types
	 *            consider only edges of these types
	 * @return the dependents
	 */
	@Override
	@NoneNull
	public Collection<JavaChangeOperation> getDependants(final JavaChangeOperation operation,
	                                                     final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<JavaChangeOperation>();
		}
		final Collection<Node> dependentNodes = getDependents(node, edgeTypes);
		final Set<JavaChangeOperation> parentOperations = new HashSet<JavaChangeOperation>();
		for (final Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	/**
	 * Returns a collection containing nodes depending on node <code>node</code> via an edge of a type is contained
	 * within the specified edge type array. (incoming edges)
	 * 
	 * @param types
	 *            consider only edges of these types
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
	public GenealogyEdgeType getEdge(final JavaChangeOperation from,
	                                 final JavaChangeOperation to) {
		final Node fromNode = getNodeForVertex(from);
		final Node toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		return getEdge(fromNode, toNode);
	}
	
	@NoneNull
	public GenealogyEdgeType getEdge(final Node fromNode,
	                                 final Node toNode) {
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
	
	// /////////////
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(final JavaChangeOperation from,
	                                              final JavaChangeOperation to) {
		final Node fromNode = getNodeForVertex(from);
		final Node toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		return getEdges(fromNode, toNode);
	}
	
	@NoneNull
	private Collection<GenealogyEdgeType> getEdges(final Node fromNode,
	                                               final Node toNode) {
		
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
	
	@Override
	public File getGraphDBDir() {
		return this.dbFile;
	}
	
	@Override
	public GraphDatabaseService getGraphDBService() {
		return this.graph;
	}
	
	private Node getNodeForVertex(final JavaChangeOperation op) {
		return this.vertexCache.getNode(op);
	}
	
	@Override
	public String getNodeId(final JavaChangeOperation t) {
		if (containsVertex(t)) {
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
	public Collection<JavaChangeOperation> getParents(final JavaChangeOperation operation,
	                                                  final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<JavaChangeOperation>();
		}
		final Collection<Node> dependentNodes = getParents(node, edgeTypes);
		final Set<JavaChangeOperation> parentOperations = new HashSet<JavaChangeOperation>();
		for (final Node dependentNode : dependentNodes) {
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
	 * Gets the PersistenceUtil registered with the ChangeGenealogy.
	 * 
	 * @return the persistence util. Returns <code>null</code> if none set.
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	@Override
	public Collection<JavaChangeOperation> getRoots() {
		final Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (final Node op : this.rootCache) {
			result.add(getVertexForNode(op));
		}
		return result;
	}
	
	public TransactionChangeGenealogy getTransactionLayer() {
		return this.transactionGenealogy;
	}
	
	private JavaChangeOperation getVertexForNode(final Node dependentNode) {
		final Long operationId = (Long) dependentNode.getProperty(NODE_ID);
		return this.operationCache.loadById(operationId);
	}
	
	public boolean hasVertex(final JavaChangeOperation vertex) {
		return (getNodeForVertex(vertex) != null);
	}
	
	@Override
	public int inDegree(final JavaChangeOperation op) {
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
	
	@Override
	public int inDegree(final JavaChangeOperation op,
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
	
	private boolean isRoot(final Node node) {
		return this.rootCache.isRoot(node);
	}
	
	/**
	 * Vertex set.
	 * 
	 * @return the genealogy vertex iterator
	 */
	protected IndexHits<Node> nodes() {
		return this.nodeIndex.query(NODE_ID, "*");
	}
	
	@Override
	public int outDegree(final JavaChangeOperation op) {
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
	
	@Override
	public int outDegree(final JavaChangeOperation op,
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
	
	public Iterator<JavaChangeOperation> vertexIterator() {
		final IndexHits<Node> indexHits = this.nodeIndex.query(NODE_ID, "*");
		
		final Set<Long> operations = new HashSet<Long>();
		for (final Node node : indexHits) {
			operations.add((Long) node.getProperty(NODE_ID));
		}
		indexHits.close();
		return new Iterator<JavaChangeOperation>() {
			
			private final Iterator<Long> idIter = operations.iterator();
			
			@Override
			public boolean hasNext() {
				return this.idIter.hasNext();
			}
			
			@Override
			public JavaChangeOperation next() {
				return CoreChangeGenealogy.this.operationCache.loadById(this.idIter.next());
			}
			
			@Override
			public void remove() {
				this.idIter.remove();
			}
		};
	}
	
	@Override
	public Iterable<JavaChangeOperation> vertexSet() {
		return new Iterable<JavaChangeOperation>() {
			
			@Override
			public Iterator<JavaChangeOperation> iterator() {
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
		final IndexHits<Node> indexHits = this.nodeIndex.query(NODE_ID, "*");
		final int result = indexHits.size();
		indexHits.close();
		return result;
	}
	
}
