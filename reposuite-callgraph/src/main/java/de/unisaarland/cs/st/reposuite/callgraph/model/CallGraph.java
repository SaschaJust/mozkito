package de.unisaarland.cs.st.reposuite.callgraph.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;



/**
 * The Class MinerCallGraph.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CallGraph extends AbstractGraph<MethodVertex, CallGraphEdge> implements
DirectedGraph<MethodVertex, CallGraphEdge>, Serializable {
	
	
	/**
	 * 
	 */
	private static final long                                serialVersionUID = -5019126219418574465L;
	
	public static CallGraph unserialize(final File file){
		try {
			ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			CallGraph graph = (CallGraph) objIn.readObject();
			objIn.close();
			return graph;
		} catch (FileNotFoundException e) {
			if (Logger.logError()) {
				Logger.error("Cannot unserialize call graph from file " + file.getAbsolutePath()
				             + FileUtils.lineSeparator + e.getMessage());
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error("Cannot unserialize call graph from file " + file.getAbsolutePath()
				             + FileUtils.lineSeparator + e.getMessage());
			}
		} catch (ClassNotFoundException e) {
			if (Logger.logError()) {
				Logger.error("Cannot unserialize call graph from file " + file.getAbsolutePath()
				             + FileUtils.lineSeparator + e.getMessage());
			}
		}
		return null;
	}
	private DirectedSparseGraph<MethodVertex, CallGraphEdge> methodCallGraph  = new DirectedSparseGraph<MethodVertex, CallGraphEdge>();
	
	private DirectedSparseGraph<ClassVertex, CallGraphEdge>  classCallGraph  = new DirectedSparseGraph<ClassVertex, CallGraphEdge>();
	
	public CallGraph() {
		
	}
	public CallGraph(final CallGraph other) {
		this.methodCallGraph = other.methodCallGraph;
		this.classCallGraph = other.classCallGraph;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * edu.uci.ics.jung.graph.util.Pair)
	 */
	@Override
	@Deprecated
	public boolean addEdge(final CallGraphEdge edge,
	                       final Pair<? extends MethodVertex> endpoints) {
		if (!this.methodCallGraph.addEdge(edge, endpoints)) {
			edge.addOccurrence();
		}
		ClassVertex from = VertexFactory.createClassVertex(endpoints.getFirst());
		ClassVertex to = VertexFactory.createClassVertex(endpoints.getSecond());
		
		if (!this.classCallGraph.containsVertex(from)) {
			this.classCallGraph.addVertex(from);
		}
		if (!this.classCallGraph.containsVertex(to)) {
			this.classCallGraph.addVertex(to);
		}
		
		CallGraphEdge classEdge = this.classCallGraph.findEdge(from, to);
		if (classEdge != null) {
			classEdge.addOccurrence();
		} else {
			this.classCallGraph.addEdge(new CallGraphEdge(), new Pair<ClassVertex>(from, to));
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * edu.uci.ics.jung.graph.util.Pair, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	@Deprecated
	public boolean addEdge(final CallGraphEdge edge, final Pair<? extends MethodVertex> endpoints,
	                       final EdgeType edgeType) {
		if (!edgeType.equals(EdgeType.DIRECTED)) {
			return false;
		} else {
			return this.addEdge(edge, endpoints);
		}
	}
	
	/**
	 * Adds the edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if successful
	 */
	public boolean addEdge(final MethodVertex from, final MethodVertex to) {
		CallGraphEdge edge = this.methodCallGraph.findEdge(from, to);
		if (edge != null) {
			return this.addEdge(edge, new Pair<MethodVertex>(from, to));
		} else {
			return this.addEdge(new CallGraphEdge(), new Pair<MethodVertex>(from, to));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
	 */
	@Override
	public boolean addVertex(final MethodVertex vertex) {
		
		if (!this.methodCallGraph.addVertex(vertex)) {
			return false;
		}
		
		ClassVertex classVertex = VertexFactory.createClassVertex(vertex);
		this.classCallGraph.addVertex(classVertex);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#containsEdge(java.lang.Object)
	 */
	@Override
	public boolean containsEdge(final CallGraphEdge edge) {
		return this.methodCallGraph.containsEdge(edge);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#containsVertex(java.lang.Object)
	 */
	@Override
	public boolean containsVertex(final MethodVertex vertex) {
		return this.methodCallGraph.containsVertex(vertex);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CallGraph other = (CallGraph) obj;
		if (classCallGraph == null) {
			if (other.classCallGraph != null) return false;
		} else if (methodCallGraph == null) {
			if (other.methodCallGraph != null) return false;
		} else {
			boolean equal = true;
			DirectedSparseGraph<ClassVertex, CallGraphEdge> otherClassCallGraph = other.getClassCallGraph();
			equal &= classCallGraph.getVertices().containsAll(otherClassCallGraph.getVertices());
			
			for (ClassVertex v : classCallGraph.getVertices()) {
				for (CallGraphEdge e : classCallGraph.getOutEdges(v)) {
					ClassVertex n = classCallGraph.getDest(e);
					equal &= (otherClassCallGraph.findEdge(v, n) != null);
					int i = 1;
					++i;
				}
			}
			
			equal &= methodCallGraph.getVertices().containsAll(other.getVertices());
			
			for (MethodVertex v : getVertices()) {
				for (CallGraphEdge e : getOutEdges(v)) {
					MethodVertex n = getDest(e);
					equal &= (other.findEdge(v, n) != null);
					int i = 1;
					++i;
				}
			}

			return equal;
		}
		return true;
	}
	
	/**
	 * Gets the class call graph.
	 * 
	 * @return the class call graph
	 */
	public DirectedSparseGraph<ClassVertex, CallGraphEdge> getClassCallGraph() {
		return this.classCallGraph;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getDefaultEdgeType()
	 */
	@Override
	public EdgeType getDefaultEdgeType() {
		return EdgeType.DIRECTED;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#getDest(java.lang.Object)
	 */
	@Override
	public MethodVertex getDest(final CallGraphEdge directed_edge) {
		return this.methodCallGraph.getDest(directed_edge);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return this.methodCallGraph.getEdgeCount();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * edu.uci.ics.jung.graph.Hypergraph#getEdgeCount(edu.uci.ics.jung.graph
	 * .util.EdgeType)
	 */
	@Override
	public int getEdgeCount(final EdgeType edge_type) {
		return this.methodCallGraph.getEdgeCount(edge_type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdges()
	 */
	@Override
	public Collection<CallGraphEdge> getEdges() {
		return this.methodCallGraph.getEdges();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * edu.uci.ics.jung.graph.Hypergraph#getEdges(edu.uci.ics.jung.graph.util
	 * .EdgeType)
	 */
	@Override
	public Collection<CallGraphEdge> getEdges(final EdgeType edge_type) {
		return this.methodCallGraph.getEdges(edge_type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeType(java.lang.Object)
	 */
	@Override
	public EdgeType getEdgeType(final CallGraphEdge edge) {
		return this.methodCallGraph.getEdgeType(edge);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#getEndpoints(java.lang.Object)
	 */
	@Override
	public Pair<MethodVertex> getEndpoints(final CallGraphEdge edge) {
		return this.methodCallGraph.getEndpoints(edge);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentEdges(java.lang.Object)
	 */
	@Override
	public Collection<CallGraphEdge> getIncidentEdges(final MethodVertex vertex) {
		return this.methodCallGraph.getIncidentEdges(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#getInEdges(java.lang.Object)
	 */
	@Override
	public Collection<CallGraphEdge> getInEdges(final MethodVertex vertex) {
		return this.methodCallGraph.getInEdges(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighbors(java.lang.Object)
	 */
	@Override
	public Collection<MethodVertex> getNeighbors(final MethodVertex vertex) {
		return this.methodCallGraph.getNeighbors(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#getOutEdges(java.lang.Object)
	 */
	@Override
	public Collection<CallGraphEdge> getOutEdges(final MethodVertex vertex) {
		return this.methodCallGraph.getOutEdges(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#getPredecessors(java.lang.Object)
	 */
	@Override
	public Collection<MethodVertex> getPredecessors(final MethodVertex vertex) {
		return this.methodCallGraph.getPredecessors(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#getSource(java.lang.Object)
	 */
	@Override
	public MethodVertex getSource(final CallGraphEdge directed_edge) {
		return this.methodCallGraph.getSource(directed_edge);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#getSuccessors(java.lang.Object)
	 */
	@Override
	public Collection<MethodVertex> getSuccessors(final MethodVertex vertex) {
		return this.methodCallGraph.getSuccessors(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getVertexCount()
	 */
	@Override
	public int getVertexCount() {
		return this.methodCallGraph.getVertexCount();
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#getVertices()
	 */
	@Override
	public Collection<MethodVertex> getVertices() {
		return this.methodCallGraph.getVertices();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classCallGraph == null)
				? 0
				: classCallGraph.hashCode());
		result = prime * result + ((methodCallGraph == null)
				? 0
				: methodCallGraph.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean isDest(final MethodVertex vertex, final CallGraphEdge edge) {
		return this.methodCallGraph.isDest(vertex, edge);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean isSource(final MethodVertex vertex, final CallGraphEdge edge) {
		return this.methodCallGraph.isSource(vertex, edge);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
	 */
	@Override
	@Deprecated
	public boolean removeEdge(final CallGraphEdge edge) {
		if (!this.containsEdge(edge)) {
			return false;
		}
		ClassVertex source = VertexFactory.createClassVertex(this.getSource(edge));
		ClassVertex dest = VertexFactory.createClassVertex(this.getDest(edge));
		if (edge.getOccurrence() > 1) {
			edge.addOccurrence(-1);
		} else {
			this.methodCallGraph.removeEdge(edge);
		}
		
		CallGraphEdge classEdge = this.classCallGraph.findEdge(source, dest);
		if (classEdge != null) {
			if (classEdge.getOccurrence() > 1) {
				classEdge.addOccurrence(-1);
			} else {
				this.classCallGraph.removeEdge(classEdge);
			}
		}
		return true;
	}
	
	/**
	 * Removes the edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if successful
	 */
	public boolean removeEdge(final MethodVertex from, final MethodVertex to) {
		if ((!this.containsVertex(from)) || (!this.containsVertex(to))) {
			return false;
		}
		CallGraphEdge e = this.findEdge(from, to);
		if (e != null) {
			return this.removeEdge(e);
		} else {
			return false;
		}
	}
	
	public void removeRecursive(final ClassVertex vertex){
		Collection<CallGraphEdge> outEdges = this.classCallGraph.getOutEdges(vertex);
		if(outEdges == null){
			return;
		}
		outEdges = new HashSet<CallGraphEdge>(outEdges);
		for (CallGraphEdge e : outEdges) {
			this.classCallGraph.removeEdge(e);
		}
		for(MethodVertex v : vertex.getChildren()){
			Collection<CallGraphEdge> childOutEdges = this.methodCallGraph.getOutEdges(v);
			if (childOutEdges == null) {
				continue;
			}
			childOutEdges = new HashSet<CallGraphEdge>(this.methodCallGraph.getOutEdges(v));
			for (CallGraphEdge e : childOutEdges) {
				this.methodCallGraph.removeEdge(e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
	 */
	@Override
	public boolean removeVertex(final MethodVertex vertex) {
		Collection<MethodVertex> successors = this.methodCallGraph.getSuccessors(vertex);
		if (!this.methodCallGraph.removeVertex(vertex)) {
			return false;
		}
		
		ClassVertex toRemove = VertexFactory.createClassVertex(vertex);
		for (MethodVertex successor : successors) {
			ClassVertex classVertex = VertexFactory.createClassVertex(successor);
			CallGraphEdge edge = this.classCallGraph.findEdge(toRemove, classVertex);
			if (edge.getOccurrence() > 1) {
				edge.addOccurrence(-1);
			} else {
				this.classCallGraph.removeEdge(edge);
			}
		}
		
		if (this.classCallGraph.getNeighborCount(toRemove) == 0) {
			this.classCallGraph.removeVertex(toRemove);
		}
		
		return true;
	}
	
	public void serialize(final File file) {
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			objOut.writeObject(this);
			objOut.close();
		} catch (FileNotFoundException e) {
			throw new UnrecoverableError(e);
		} catch (IOException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
