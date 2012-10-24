/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package de.unisaarland.cs.st.mozkito.genealogies.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import scala.actors.threadpool.Arrays;
import de.unisaarland.cs.st.mozkito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.core.GenealogyEdgeType;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents a ChangeGenealogy as a Jung2 graph. This graph cannot be modified. This class should only be used for
 * representation, drawing or algorithm purposes.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JungGenealogyGraph<T> implements DirectedGraph<T, JungGenealogyGraph.Edge<T>> {
	
	/**
	 * The Class Edge.
	 * 
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	public static class Edge<T> {
		
		/** The from. */
		public T                 from;
		
		/** The to. */
		public T                 to;
		
		/** The type. */
		public GenealogyEdgeType type;
		
		/**
		 * Instantiates a new edge.
		 * 
		 * @param from
		 *            the from
		 * @param to
		 *            the to
		 * @param type
		 *            the type
		 */
		public Edge(T from, T to, GenealogyEdgeType type) {
			this.from = from;
			this.to = to;
			this.type = type;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			@SuppressWarnings ("rawtypes")
			Edge other = (Edge) obj;
			if (this.from == null) {
				if (other.from != null) {
					return false;
				}
			} else if (!this.from.equals(other.from)) {
				return false;
			}
			if (this.to == null) {
				if (other.to != null) {
					return false;
				}
			} else if (!this.to.equals(other.to)) {
				return false;
			}
			if (this.type != other.type) {
				return false;
			}
			return true;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((this.from == null)
			                                           ? 0
			                                           : this.from.hashCode());
			result = (prime * result) + ((this.to == null)
			                                         ? 0
			                                         : this.to.hashCode());
			result = (prime * result) + ((this.type == null)
			                                           ? 0
			                                           : this.type.hashCode());
			return result;
		}
		
	}
	
	private int                           edgeCount = -1;
	
	private ChangeGenealogy<T>            genealogy;
	private Collection<GenealogyEdgeType> edgeTypeFilter;
	
	@SuppressWarnings ("unchecked")
	public JungGenealogyGraph(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		this.edgeTypeFilter = Arrays.asList(GenealogyEdgeType.values());
	}
	
	public JungGenealogyGraph(ChangeGenealogy<T> genealogy, Collection<GenealogyEdgeType> edgeTypeFilter) {
		this.genealogy = genealogy;
		this.edgeTypeFilter = edgeTypeFilter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> edge,
	                       Collection<? extends T> vertices) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection,
	 * edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> edge,
	                       Collection<? extends T> vertices,
	                       EdgeType edge_type) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> e,
	                       T v1,
	                       T v2) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object,
	 * edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> e,
	                       T v1,
	                       T v2,
	                       EdgeType edgeType) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
	 */
	@Override
	@Deprecated
	public boolean addVertex(T vertex) {
		return false;
	}
	
	@Override
	public boolean containsEdge(Edge<T> edge) {
		return this.genealogy.containsEdge(edge.from, edge.to);
	}
	
	@Override
	public boolean containsVertex(T vertex) {
		return this.genealogy.containsVertex(vertex);
	}
	
	@Override
	public int degree(T vertex) {
		return this.genealogy.inDegree(vertex) + this.genealogy.outDegree(vertex);
	}
	
	@Override
	public Edge<T> findEdge(T v1,
	                        T v2) {
		Collection<GenealogyEdgeType> edges = this.genealogy.getEdges(v1, v2);
		for (GenealogyEdgeType type : edges) {
			if (this.edgeTypeFilter.contains(type)) {
				return new Edge<T>(v1, v2, type);
			}
		}
		return null;
	}
	
	@Override
	public Collection<Edge<T>> findEdgeSet(T v1,
	                                       T v2) {
		Collection<GenealogyEdgeType> edges = this.genealogy.getEdges(v1, v2);
		Set<Edge<T>> result = new HashSet<Edge<T>>();
		for (GenealogyEdgeType t : edges) {
			if (this.edgeTypeFilter.contains(t)) {
				result.add(new Edge<T>(v1, v2, t));
			}
		}
		return result;
	}
	
	@Override
	public EdgeType getDefaultEdgeType() {
		return EdgeType.DIRECTED;
	}
	
	@Override
	public T getDest(Edge<T> directed_edge) {
		return directed_edge.to;
	}
	
	@Override
	public int getEdgeCount() {
		if (this.edgeCount < 0) {
			this.edgeCount = 0;
			for (T vertex : this.genealogy.vertexSet()) {
				this.edgeCount += this.genealogy.getDependants(vertex,
				                                     this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))
				                      .size();
			}
		}
		return this.edgeCount;
	}
	
	@Override
	public int getEdgeCount(EdgeType edge_type) {
		if (edge_type.equals(EdgeType.UNDIRECTED)) {
			return 0;
		}
		return getEdgeCount();
	}
	
	@Override
	public Collection<Edge<T>> getEdges() {
		Collection<Edge<T>> result = new HashSet<Edge<T>>();
		for (T v : getVertices()) {
			for (T to : this.genealogy.getDependants(v, this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))) {
				result.addAll(this.findEdgeSet(v, to));
			}
		}
		return result;
	}
	
	@Override
	public Collection<Edge<T>> getEdges(EdgeType edge_type) {
		if (edge_type.equals(EdgeType.UNDIRECTED)) {
			return new ArrayList<Edge<T>>(0);
		}
		return getEdges();
	}
	
	@Override
	public EdgeType getEdgeType(Edge<T> edge) {
		return EdgeType.DIRECTED;
	}
	
	@Override
	public Pair<T> getEndpoints(Edge<T> edge) {
		return new Pair<T>(edge.from, edge.to);
	}
	
	public ChangeGenealogy<T> getGenealogy() {
		return this.genealogy;
	}
	
	@Override
	public int getIncidentCount(Edge<T> edge) {
		return 2;
	}
	
	@Override
	public Collection<Edge<T>> getIncidentEdges(T vertex) {
		Collection<Edge<T>> edges = new HashSet<Edge<T>>();
		edges.addAll(getInEdges(vertex));
		edges.addAll(getOutEdges(vertex));
		return edges;
	}
	
	@Override
	public Collection<T> getIncidentVertices(Edge<T> edge) {
		Collection<T> result = new ArrayList<T>(2);
		result.add(edge.from);
		result.add(edge.to);
		return result;
	}
	
	@Override
	public Collection<Edge<T>> getInEdges(T vertex) {
		Collection<Edge<T>> edges = new HashSet<Edge<T>>();
		for (T dependant : this.genealogy.getDependants(vertex,
		                                           this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))) {
			edges.addAll(findEdgeSet(dependant, vertex));
		}
		return edges;
	}
	
	@Override
	public int getNeighborCount(T vertex) {
		int count = 0;
		count += getPredecessorCount(vertex);
		count += getSuccessorCount(vertex);
		return count;
	}
	
	@Override
	public Collection<T> getNeighbors(T vertex) {
		Collection<T> neighbors = new HashSet<T>();
		for (T dependant : this.genealogy.getDependants(vertex,
		                                           this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))) {
			neighbors.add(dependant);
		}
		for (T parent : this.genealogy.getParents(vertex,
		                                     this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))) {
			neighbors.add(parent);
		}
		return neighbors;
	}
	
	@Override
	public T getOpposite(T vertex,
	                     Edge<T> edge) {
		if (edge.from.equals(vertex)) {
			return edge.to;
		} else if (edge.to.equals(vertex)) {
			return edge.from;
		}
		return null;
	}
	
	@Override
	public Collection<Edge<T>> getOutEdges(T vertex) {
		Collection<Edge<T>> edges = new HashSet<Edge<T>>();
		for (T parent : this.genealogy.getParents(vertex,
		                                     this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))) {
			edges.addAll(findEdgeSet(vertex, parent));
		}
		return edges;
		
	}
	
	@Override
	public int getPredecessorCount(T vertex) {
		int count = 0;
		count += this.genealogy.getParents(vertex, this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))
		                  .size();
		return count;
	}
	
	@Override
	public Collection<T> getPredecessors(T vertex) {
		return this.genealogy.getParents(vertex, this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]));
	}
	
	@Override
	public T getSource(Edge<T> directed_edge) {
		return directed_edge.from;
	}
	
	@Override
	public int getSuccessorCount(T vertex) {
		int count = 0;
		count += this.genealogy.getDependants(vertex, this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]))
		                  .size();
		return count;
	}
	
	@Override
	public Collection<T> getSuccessors(T vertex) {
		return this.genealogy.getDependants(vertex, this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]));
	}
	
	@Override
	public int getVertexCount() {
		return this.genealogy.vertexSize();
	}
	
	@Override
	public Collection<T> getVertices() {
		Set<T> vertices = new HashSet<T>();
		for (T vertex : this.genealogy.vertexSet()) {
			vertices.add(vertex);
		}
		return vertices;
	}
	
	@Override
	public int inDegree(T vertex) {
		return this.genealogy.inDegree(vertex, this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]));
	}
	
	@Override
	public boolean isDest(T vertex,
	                      Edge<T> edge) {
		return edge.to.equals(vertex);
	}
	
	@Override
	public boolean isIncident(T vertex,
	                          Edge<T> edge) {
		return edge.from.equals(vertex) || edge.to.equals(vertex);
	}
	
	@Override
	public boolean isNeighbor(T v1,
	                          T v2) {
		return getNeighbors(v1).contains(v2);
	}
	
	@Override
	public boolean isPredecessor(T v1,
	                             T v2) {
		return findEdge(v2, v1) != null;
	}
	
	@Override
	public boolean isSource(T vertex,
	                        Edge<T> edge) {
		return edge.from.equals(vertex);
	}
	
	@Override
	public boolean isSuccessor(T v1,
	                           T v2) {
		return findEdge(v1, v2) != null;
	}
	
	@Override
	public int outDegree(T vertex) {
		return this.genealogy.outDegree(vertex, this.edgeTypeFilter.toArray(new GenealogyEdgeType[this.edgeTypeFilter.size()]));
	}
	
	@Override
	@Deprecated
	public boolean removeEdge(Edge<T> edge) {
		return false;
	}
	
	@Override
	@Deprecated
	public boolean removeVertex(T vertex) {
		return false;
	}
	
}
