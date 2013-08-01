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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

/**
 * The Interface ChangeGenealogy.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class ChangeGenealogy<T> {
	
	/** The Constant NODE_ID. */
	public static final String        NODE_ID       = "change_op_id";
	
	/** The Constant EDGE_TYPE. */
	public static final String        EDGE_TYPE     = "edge_type";
	
	/** The Constant ROOT_VERTICES. */
	public static final String        ROOT_VERTICES = "root_vertices";
	
	/** The graph. */
	protected final KeyIndexableGraph graph;
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param graph
	 *            the graph
	 */
	public ChangeGenealogy(@NotNull final KeyIndexableGraph graph) {
		this.graph = graph;
		graph.createKeyIndex("VertexIndex", Vertex.class);
		graph.createKeyIndex("EdgeIndex", Edge.class);
	}
	
	/**
	 * Adds the edge.
	 * 
	 * @param dependent
	 *            the dependent
	 * @param target
	 *            the target
	 * @param edgeType
	 *            the edge type
	 * @return true, if successful
	 */
	public boolean addEdge(final T dependent,
	                       final T target,
	                       final GenealogyEdgeType edgeType) {
		
		// add both vertices
		if (!containsVertex(dependent)) {
			addVertex(dependent);
		}
		if (!containsVertex(target)) {
			addVertex(target);
		}
		
		// we know that they have to exist
		final Vertex from = getNodeForVertex(dependent);
		final Vertex to = getNodeForVertex(target);
		
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
			final Edge relationship = from.addEdge(edgeType.name(), to);
			if (relationship == null) {
				
				if (this.graph instanceof TransactionalGraph) {
					((TransactionalGraph) this.graph).rollback();
				}
				if (Logger.logWarn()) {
					Logger.warn("Relationship %s->%s already exists!", from, to);
				}
				return false;
			}
			relationship.setProperty(EDGE_TYPE, edgeType);
			if (this.graph instanceof TransactionalGraph) {
				((TransactionalGraph) this.graph).commit();
			}
			if (isRoot(to)) {
				to.removeProperty(ROOT_VERTICES);
			}
			
		}
		return true;
	}
	
	/**
	 * Adds a vertex to the genealogy that is associated with the specified JavaChangeOperation. This method also checks
	 * if such a vertex exists already.
	 * 
	 * @param Id
	 *            the id
	 * @param v
	 *            the JavaChangeOperation to add
	 * @return true if the new vertex was successfully added. False otherwise (this may include that the vertex existed
	 *         already).
	 */
	@NoneNull
	public final boolean addVertex(@NotNull final Object Id,
	                               @NotNull final T v) {
		if (hasVertex(v)) {
			if (Logger.logWarn()) {
				Logger.warn("JavaChangeOperations with id `" + Id + "` already exists");
			}
			return false;
		}
		final Vertex node = this.graph.addVertex(Id);
		if (node == null) {
			if (this.graph instanceof TransactionalGraph) {
				((TransactionalGraph) this.graph).rollback();
			}
			return false;
		}
		node.setProperty(ChangeGenealogy.NODE_ID, Id);
		node.setProperty(ChangeGenealogy.ROOT_VERTICES, true);
		if (this.graph instanceof TransactionalGraph) {
			((TransactionalGraph) this.graph).commit();
		}
		
		return true;
	}
	
	/**
	 * Adds the vertex.
	 * 
	 * @param v
	 *            the v
	 * @return true, if successful
	 */
	public abstract boolean addVertex(@NotNull final T v);
	
	/**
	 * Must be called to ensure the Graph DB to be shut down properly! This will be taken care of by a separate
	 * ShutdownHook. So make sure to call this method only when you are know what you are doing!
	 */
	public final void close() {
		this.graph.shutdown();
	}
	
	/**
	 * Contains edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if successful
	 */
	public boolean containsEdge(final T from,
	                            final T to) {
		final GenealogyEdgeType result = getEdge(from, to);
		return result != null;
	}
	
	/**
	 * Contains vertex.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return true, if successful
	 */
	public boolean containsVertex(final T vertex) {
		return hasVertex(vertex);
	}
	
	/**
	 * Edge size.
	 * 
	 * @return the int
	 */
	public int edgeSize() {
		int result = 0;
		for (@SuppressWarnings ("unused")
		final Edge edge : this.graph.getEdges()) {
			++result;
		}
		return result;
	}
	
	/**
	 * Returns a collection containing nodes that depend on node <code>node</code> (incoming edges).
	 * 
	 * @param operation
	 *            the operation
	 * @return all dependents
	 */
	public final Collection<T> getAllDependents(final T operation) {
		return getDependents(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
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
	protected final Collection<Vertex> getAllDependents(final Vertex node) {
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
	public final Collection<T> getAllParents(final T operation) {
		return getParents(operation, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                  GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                  GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                  GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Gets the core.
	 * 
	 * @return the core
	 */
	public abstract CoreChangeGenealogy getCore();
	
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
	@NoneNull
	public Collection<T> getDependents(final T operation,
	                                   final GenealogyEdgeType... edgeTypes) {
		final Vertex node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<T>();
		}
		final Collection<Vertex> dependentNodes = getDependents(node, edgeTypes);
		final Set<T> parentOperations = new HashSet<T>();
		for (final Vertex dependentNode : dependentNodes) {
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
	private final Collection<Vertex> getDependents(final Vertex node,
	                                               final GenealogyEdgeType... edgeTypes) {
		final Iterable<Edge> relationships = node.getEdges(Direction.IN, GenealogyEdgeType.asStringArray(edgeTypes));
		final Set<Vertex> parents = new HashSet<>();
		for (final Edge rel : relationships) {
			parents.add(rel.getVertex(Direction.OUT));
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
	public final GenealogyEdgeType getEdge(final T from,
	                                       final T to) {
		final Vertex fromNode = getNodeForVertex(from);
		final Vertex toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		return getEdge(fromNode, toNode);
	}
	
	/**
	 * Gets the edge.
	 * 
	 * @param fromNode
	 *            the from node
	 * @param toNode
	 *            the to node
	 * @return the edge
	 */
	@NoneNull
	public final GenealogyEdgeType getEdge(final Vertex fromNode,
	                                       final Vertex toNode) {
		final Iterable<Edge> relationships = fromNode.getEdges(Direction.OUT,
		                                                       GenealogyEdgeType.CallOnDefinition.name(),
		                                                       GenealogyEdgeType.DefinitionOnDefinition.name(),
		                                                       GenealogyEdgeType.DefinitionOnDeletedDefinition.name(),
		                                                       GenealogyEdgeType.DeletedCallOnCall.name(),
		                                                       GenealogyEdgeType.DeletedCallOnDeletedDefinition.name(),
		                                                       GenealogyEdgeType.DeletedDefinitionOnDefinition.name());
		
		for (final Edge rel : relationships) {
			if (rel.getVertex(Direction.IN).equals(toNode)) {
				final String relationshipType = rel.getLabel();
				return GenealogyEdgeType.valueOf(relationshipType.toString());
			}
		}
		return null;
	}
	
	/**
	 * Gets the edges.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the edges
	 */
	public Collection<GenealogyEdgeType> getEdges(final T from,
	                                              final T to) {
		final Vertex fromNode = getNodeForVertex(from);
		final Vertex toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		return getEdges(fromNode, toNode);
	}
	
	/**
	 * Gets the edges.
	 * 
	 * @param fromNode
	 *            the from node
	 * @param toNode
	 *            the to node
	 * @return the edges
	 */
	@NoneNull
	protected final Collection<GenealogyEdgeType> getEdges(final Vertex fromNode,
	                                                       final Vertex toNode) {
		
		final Iterable<Edge> relationships = fromNode.getEdges(Direction.OUT,
		                                                       GenealogyEdgeType.CallOnDefinition.name(),
		                                                       GenealogyEdgeType.DefinitionOnDefinition.name(),
		                                                       GenealogyEdgeType.DefinitionOnDeletedDefinition.name(),
		                                                       GenealogyEdgeType.DeletedCallOnCall.name(),
		                                                       GenealogyEdgeType.DeletedCallOnDeletedDefinition.name(),
		                                                       GenealogyEdgeType.DeletedDefinitionOnDefinition.name());
		
		final Collection<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		for (final Edge rel : relationships) {
			if (rel.getVertex(Direction.IN).equals(toNode)) {
				final String label = rel.getLabel();
				result.add(GenealogyEdgeType.valueOf(label));
			}
		}
		return result;
	}
	
	/**
	 * Gets the existing edge types.
	 * 
	 * @return the existing edge types
	 */
	public final Set<GenealogyEdgeType> getExistingEdgeTypes() {
		
		final Set<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		for (final GenealogyEdgeType t : GenealogyEdgeType.values()) {
			final Iterable<Edge> edges = this.graph.getEdges(EDGE_TYPE, t);
			if (edges.iterator().hasNext()) {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Gets the graph db dir.
	 * 
	 * @return the graph db dir
	 */
	public final KeyIndexableGraph getGraphDB() {
		return this.graph;
	}
	
	/**
	 * Gets the node for vertex.
	 * 
	 * @param op
	 *            the op
	 * @return the node for vertex
	 */
	protected final Vertex getNodeForVertex(final T op) {
		return this.graph.getVertex(op);
	}
	
	/**
	 * Gets the id of the node represented within this change genealogy.
	 * 
	 * @param t
	 *            the t
	 * @return the node id if node within this genealogy vertex. Returns null otherwise.
	 */
	public abstract String getNodeId(T t);
	
	/**
	 * Returns a collection containing nodes connected though outgoing edges.
	 * 
	 * @param operation
	 *            the operation
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	public Collection<T> getParents(final T operation,
	                                final GenealogyEdgeType... edgeTypes) {
		final Vertex node = getNodeForVertex(operation);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<T>();
		}
		final Collection<Vertex> dependentNodes = getParents(node, edgeTypes);
		final Set<T> parentOperations = new HashSet<>();
		for (final Vertex dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
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
	protected final Collection<Vertex> getParents(final Vertex node,
	                                              final GenealogyEdgeType... edgeTypes) {
		final Iterable<Edge> relationships = node.getEdges(Direction.OUT, GenealogyEdgeType.asStringArray(edgeTypes));
		final Set<Vertex> parents = new HashSet<>();
		for (final Edge rel : relationships) {
			parents.add(rel.getVertex(Direction.IN));
		}
		return parents;
	}
	
	/**
	 * Gets the roots.
	 * 
	 * @return the roots
	 */
	public Collection<T> getRoots() {
		final Collection<T> result = new HashSet<>();
		for (final Vertex op : this.graph.getVertices(ROOT_VERTICES, true)) {
			result.add(getVertexForNode(op));
		}
		return result;
	}
	
	/**
	 * Gets the vertex for node.
	 * 
	 * @param dependentNode
	 *            the dependent node
	 * @return the vertex for node
	 */
	protected abstract T getVertexForNode(final Vertex dependentNode);
	
	/**
	 * Checks for vertex.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return true, if successful
	 */
	public final boolean hasVertex(final T vertex) {
		return (getNodeForVertex(vertex) != null);
	}
	
	/**
	 * In degree.
	 * 
	 * @param op
	 *            the op
	 * @return the int
	 */
	public final int inDegree(final T op) {
		return inDegree(op, GenealogyEdgeType.values());
	}
	
	/**
	 * In degree.
	 * 
	 * @param op
	 *            the op
	 * @param edgeTypes
	 *            the edge types
	 * @return the int
	 */
	public int inDegree(final T op,
	                    final GenealogyEdgeType... edgeTypes) {
		final Vertex node = getNodeForVertex(op);
		final Iterable<Edge> relationships = node.getEdges(Direction.IN, GenealogyEdgeType.asStringArray(edgeTypes));
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Edge r : relationships) {
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
	protected final boolean isRoot(final Vertex node) {
		return node.getProperty(ROOT_VERTICES);
	}
	
	/**
	 * Out degree.
	 * 
	 * @param op
	 *            the op
	 * @return the int
	 */
	public final int outDegree(final T op) {
		return outDegree(op, GenealogyEdgeType.values());
	}
	
	/**
	 * Out degree.
	 * 
	 * @param op
	 *            the op
	 * @param edgeTypes
	 *            the edge types
	 * @return the int
	 */
	public int outDegree(final T op,
	                     final GenealogyEdgeType... edgeTypes) {
		final Vertex node = getNodeForVertex(op);
		final Iterable<Edge> relationships = node.getEdges(Direction.OUT, GenealogyEdgeType.asStringArray(edgeTypes));
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Edge r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/**
	 * Vertex iterator.
	 * 
	 * @return the iterator
	 */
	public final Iterator<T> vertexIterator() {
		final Set<Long> operations = new HashSet<Long>();
		for (final Vertex node : this.graph.getVertices()) {
			final Object property = node.getProperty(ChangeGenealogy.NODE_ID);
			if (property == null) {
				throw new UnrecoverableError("Error while loading node from GraphDB!");
			}
			operations.add((Long) node.getProperty(ChangeGenealogy.NODE_ID));
		}
		return new Iterator<T>() {
			
			private final Iterator<Long> idIter = operations.iterator();
			
			@Override
			public boolean hasNext() {
				return this.idIter.hasNext();
			}
			
			@Override
			public T next() {
				final Vertex v = ChangeGenealogy.this.graph.getVertex(this.idIter.next());
				return getVertexForNode(v);
			}
			
			@Override
			public void remove() {
				this.idIter.remove();
			}
		};
	}
	
	/**
	 * Vertex set.
	 * 
	 * @return the iterator
	 */
	public Iterable<T> vertexSet() {
		return new Iterable<T>() {
			
			@Override
			public Iterator<T> iterator() {
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
	public int vertexSize() {
		int result = 0;
		final Iterator<Vertex> iterator = this.graph.getVertices().iterator();
		while (iterator.hasNext()) {
			++result;
			iterator.next();
		}
		return result;
	}
}
