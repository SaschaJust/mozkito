package de.unisaarland.cs.st.moskito.genealogies.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import scala.actors.threadpool.Arrays;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * Represents a ChangeGenealogy as a Jung2 graph. This graph cannot be modified.
 * This class should only be used for representation, drawing or algorithm
 * purposes.
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
		 * 
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
			@SuppressWarnings("rawtypes") Edge other = (Edge) obj;
			if (from == null) {
				if (other.from != null) {
					return false;
				}
			} else if (!from.equals(other.from)) {
				return false;
			}
			if (to == null) {
				if (other.to != null) {
					return false;
				}
			} else if (!to.equals(other.to)) {
				return false;
			}
			if (type != other.type) {
				return false;
			}
			return true;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((from == null) ? 0 : from.hashCode());
			result = (prime * result) + ((to == null) ? 0 : to.hashCode());
			result = (prime * result) + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		
	}
	
	private int                           edgeCount = -1;
	
	private ChangeGenealogy<T> genealogy;
	private Collection<GenealogyEdgeType> edgeTypeFilter;
	
	
	
	@SuppressWarnings("unchecked")
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
	 * 
	 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object,
	 * java.util.Collection)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> edge, Collection<? extends T> vertices) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object,
	 * java.util.Collection, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> edge, Collection<? extends T> vertices, EdgeType edge_type) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object,
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> e, T v1, T v2) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object,
	 * java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	@Deprecated
	public boolean addEdge(Edge<T> e, T v1, T v2, EdgeType edgeType) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
	 */
	@Override
	@Deprecated
	public boolean addVertex(T vertex) {
		return false;
	}
	
	@Override
	public boolean containsEdge(Edge<T> edge) {
		return genealogy.containsEdge(edge.from, edge.to);
	}
	
	@Override
	public boolean containsVertex(T vertex) {
		return this.genealogy.containsVertex(vertex);
	}
	
	@Override
	public int degree(T vertex) {
		return genealogy.inDegree(vertex) + genealogy.outDegree(vertex);
	}
	
	@Override
	public Edge<T> findEdge(T v1, T v2) {
		Collection<GenealogyEdgeType> edges = genealogy.getEdges(v1, v2);
		for (GenealogyEdgeType type : edges) {
			if (this.edgeTypeFilter.contains(type)) {
				return new Edge<T>(v1, v2, type);
			}
		}
		return null;
	}
	
	@Override
	public Collection<Edge<T>> findEdgeSet(T v1, T v2) {
		Collection<GenealogyEdgeType> edges = genealogy.getEdges(v1, v2);
		Set<Edge<T>> result = new HashSet<Edge<T>>();
		for (GenealogyEdgeType t : edges) {
			if (edgeTypeFilter.contains(t)) {
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
		if (edgeCount < 0) {
			edgeCount = 0;
			for (T vertex : genealogy.vertexSet()) {
				edgeCount += genealogy.getDependants(vertex,
						edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()])).size();
			}
		}
		return edgeCount;
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
		for(T v : getVertices()){
			for(T to : genealogy.getDependants(v,
					edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]))){
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
	
	public ChangeGenealogy<T> getGenealogy(){
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
		for (T dependant : genealogy.getDependants(vertex,
				edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]))) {
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
		for (T dependant : genealogy.getDependants(vertex,
				edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]))) {
			neighbors.add(dependant);
		}
		for (T parent : genealogy.getParents(vertex,
				edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]))) {
			neighbors.add(parent);
		}
		return neighbors;
	}
	
	@Override
	public T getOpposite(T vertex, Edge<T> edge) {
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
		for (T parent : genealogy.getParents(vertex,
				edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]))) {
			edges.addAll(findEdgeSet(vertex, parent));
		}
		return edges;
		
	}
	
	@Override
	public int getPredecessorCount(T vertex) {
		int count = 0;
		count += genealogy.getParents(vertex, edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]))
				.size();
		return count;
	}
	
	@Override
	public Collection<T> getPredecessors(T vertex) {
		return genealogy.getParents(vertex, edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]));
	}
	
	@Override
	public T getSource(Edge<T> directed_edge) {
		return directed_edge.from;
	}
	
	@Override
	public int getSuccessorCount(T vertex) {
		int count = 0;
		count += genealogy.getDependants(vertex, edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]))
				.size();
		return count;
	}
	
	@Override
	public Collection<T> getSuccessors(T vertex) {
		return genealogy.getDependants(vertex, edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]));
	}
	
	@Override
	public int getVertexCount() {
		return genealogy.vertexSize();
	}
	
	@Override
	public Collection<T> getVertices() {
		Set<T> vertices = new HashSet<T>();
		for (T vertex : genealogy.vertexSet()) {
			vertices.add(vertex);
		}
		return vertices;
	}
	
	@Override
	public int inDegree(T vertex) {
		return genealogy.inDegree(vertex, edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]));
	}
	
	@Override
	public boolean isDest(T vertex, Edge<T> edge) {
		return edge.to.equals(vertex);
	}
	
	@Override
	public boolean isIncident(T vertex, Edge<T> edge) {
		return edge.from.equals(vertex) || edge.to.equals(vertex);
	}
	
	@Override
	public boolean isNeighbor(T v1, T v2) {
		return getNeighbors(v1).contains(v2);
	}
	
	@Override
	public boolean isPredecessor(T v1, T v2) {
		return findEdge(v2, v1) != null;
	}
	
	@Override
	public boolean isSource(T vertex, Edge<T> edge) {
		return edge.from.equals(vertex);
	}
	
	@Override
	public boolean isSuccessor(T v1, T v2) {
		return findEdge(v1, v2) != null;
	}
	
	@Override
	public int outDegree(T vertex) {
		return genealogy.outDegree(vertex, edgeTypeFilter.toArray(new GenealogyEdgeType[edgeTypeFilter.size()]));
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
