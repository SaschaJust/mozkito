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
package org.mozkito.callgraph.model;

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

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
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
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CallGraph extends AbstractGraph<MethodVertex, CallGraphEdge> implements
        DirectedGraph<MethodVertex, CallGraphEdge>, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5019126219418574465L;
	
	/**
	 * Unserialize.
	 * 
	 * @param file
	 *            the file
	 * @return the call graph
	 */
	public static CallGraph unserialize(final File file) {
		try {
			final ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			final CallGraph graph = (CallGraph) objIn.readObject();
			objIn.close();
			return graph;
		} catch (final FileNotFoundException e) {
			if (Logger.logError()) {
				Logger.error("Cannot unserialize call graph from file " + file.getAbsolutePath()
				        + FileUtils.lineSeparator + e.getMessage());
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error("Cannot unserialize call graph from file " + file.getAbsolutePath()
				        + FileUtils.lineSeparator + e.getMessage());
			}
		} catch (final ClassNotFoundException e) {
			if (Logger.logError()) {
				Logger.error("Cannot unserialize call graph from file " + file.getAbsolutePath()
				        + FileUtils.lineSeparator + e.getMessage());
			}
		}
		return null;
	}
	
	/** The method call graph. */
	private DirectedSparseGraph<MethodVertex, CallGraphEdge> methodCallGraph = new DirectedSparseGraph<MethodVertex, CallGraphEdge>();
	
	/** The class call graph. */
	private DirectedSparseGraph<ClassVertex, CallGraphEdge>  classCallGraph  = new DirectedSparseGraph<ClassVertex, CallGraphEdge>();
	
	/**
	 * Instantiates a new call graph.
	 */
	public CallGraph() {
		
	}
	
	/**
	 * Instantiates a new call graph.
	 * 
	 * @param other
	 *            the other
	 */
	public CallGraph(final CallGraph other) {
		this.methodCallGraph = other.methodCallGraph;
		this.classCallGraph = other.classCallGraph;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object, edu.uci.ics.jung.graph.util.Pair)
	 * @deprecated
	 */
	@Override
	@Deprecated
	public boolean addEdge(final CallGraphEdge edge,
	                       final Pair<? extends MethodVertex> endpoints) {
		if (!this.methodCallGraph.addEdge(edge, endpoints)) {
			edge.addOccurrence();
		}
		final ClassVertex from = VertexFactory.createClassVertex(endpoints.getFirst());
		final ClassVertex to = VertexFactory.createClassVertex(endpoints.getSecond());
		
		if (!this.classCallGraph.containsVertex(from)) {
			this.classCallGraph.addVertex(from);
		}
		if (!this.classCallGraph.containsVertex(to)) {
			this.classCallGraph.addVertex(to);
		}
		
		final CallGraphEdge classEdge = this.classCallGraph.findEdge(from, to);
		if (classEdge != null) {
			classEdge.addOccurrence();
		} else {
			this.classCallGraph.addEdge(new CallGraphEdge(), new Pair<ClassVertex>(from, to));
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object, edu.uci.ics.jung.graph.util.Pair,
	 *      edu.uci.ics.jung.graph.util.EdgeType)
	 * @deprecated
	 */
	@Override
	@Deprecated
	public boolean addEdge(final CallGraphEdge edge,
	                       final Pair<? extends MethodVertex> endpoints,
	                       final EdgeType edgeType) {
		if (!edgeType.equals(EdgeType.DIRECTED)) {
			return false;
		}
		return this.addEdge(edge, endpoints);
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
	public boolean addEdge(final MethodVertex from,
	                       final MethodVertex to) {
		final CallGraphEdge edge = this.methodCallGraph.findEdge(from, to);
		if (edge != null) {
			return this.addEdge(edge, new Pair<MethodVertex>(from, to));
		}
		return this.addEdge(new CallGraphEdge(), new Pair<MethodVertex>(from, to));
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
		
		final ClassVertex classVertex = VertexFactory.createClassVertex(vertex);
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CallGraph other = (CallGraph) obj;
		if (this.classCallGraph == null) {
			if (other.classCallGraph != null) {
				return false;
			}
		} else if (this.methodCallGraph == null) {
			if (other.methodCallGraph != null) {
				return false;
			}
		} else {
			boolean equal = true;
			final DirectedSparseGraph<ClassVertex, CallGraphEdge> otherClassCallGraph = other.getClassCallGraph();
			equal &= this.classCallGraph.getVertices().containsAll(otherClassCallGraph.getVertices());
			
			for (final ClassVertex v : this.classCallGraph.getVertices()) {
				for (final CallGraphEdge e : this.classCallGraph.getOutEdges(v)) {
					final ClassVertex n = this.classCallGraph.getDest(e);
					equal &= (otherClassCallGraph.findEdge(v, n) != null);
				}
			}
			
			equal &= this.methodCallGraph.getVertices().containsAll(other.getVertices());
			
			for (final MethodVertex v : getVertices()) {
				for (final CallGraphEdge e : getOutEdges(v)) {
					final MethodVertex n = getDest(e);
					equal &= (other.findEdge(v, n) != null);
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
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount(edu.uci.ics.jung.graph .util.EdgeType)
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
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdges(edu.uci.ics.jung.graph.util .EdgeType)
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.classCallGraph == null)
		                                                          ? 0
		                                                          : this.classCallGraph.hashCode());
		result = (prime * result) + ((this.methodCallGraph == null)
		                                                           ? 0
		                                                           : this.methodCallGraph.hashCode());
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isDest(final MethodVertex vertex,
	                      final CallGraphEdge edge) {
		return this.methodCallGraph.isDest(vertex, edge);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isSource(final MethodVertex vertex,
	                        final CallGraphEdge edge) {
		return this.methodCallGraph.isSource(vertex, edge);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
	 * @deprecated
	 */
	@Override
	@Deprecated
	public boolean removeEdge(final CallGraphEdge edge) {
		if (!containsEdge(edge)) {
			return false;
		}
		final ClassVertex source = VertexFactory.createClassVertex(getSource(edge));
		final ClassVertex dest = VertexFactory.createClassVertex(getDest(edge));
		if (edge.getOccurrence() > 1) {
			edge.addOccurrence(-1);
		} else {
			this.methodCallGraph.removeEdge(edge);
		}
		
		final CallGraphEdge classEdge = this.classCallGraph.findEdge(source, dest);
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
	public boolean removeEdge(final MethodVertex from,
	                          final MethodVertex to) {
		if ((!containsVertex(from)) || (!containsVertex(to))) {
			return false;
		}
		final CallGraphEdge e = findEdge(from, to);
		if (e != null) {
			return this.removeEdge(e);
		}
		return false;
	}
	
	/**
	 * Removes the recursive.
	 * 
	 * @param vertex
	 *            the vertex
	 */
	public void removeRecursive(final ClassVertex vertex) {
		Collection<CallGraphEdge> outEdges = this.classCallGraph.getOutEdges(vertex);
		if (outEdges == null) {
			return;
		}
		outEdges = new HashSet<CallGraphEdge>(outEdges);
		for (final CallGraphEdge e : outEdges) {
			this.classCallGraph.removeEdge(e);
		}
		for (final MethodVertex v : vertex.getChildren()) {
			Collection<CallGraphEdge> childOutEdges = this.methodCallGraph.getOutEdges(v);
			if (childOutEdges == null) {
				continue;
			}
			childOutEdges = new HashSet<CallGraphEdge>(this.methodCallGraph.getOutEdges(v));
			for (final CallGraphEdge e : childOutEdges) {
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
		final Collection<MethodVertex> successors = this.methodCallGraph.getSuccessors(vertex);
		if (!this.methodCallGraph.removeVertex(vertex)) {
			return false;
		}
		
		final ClassVertex toRemove = VertexFactory.createClassVertex(vertex);
		for (final MethodVertex successor : successors) {
			final ClassVertex classVertex = VertexFactory.createClassVertex(successor);
			final CallGraphEdge edge = this.classCallGraph.findEdge(toRemove, classVertex);
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
	
	/**
	 * Serialize.
	 * 
	 * @param file
	 *            the file
	 */
	public void serialize(final File file) {
		try {
			final ObjectOutputStream objOut = new ObjectOutputStream(
			                                                         new BufferedOutputStream(
			                                                                                  new FileOutputStream(file)));
			objOut.writeObject(this);
			objOut.close();
		} catch (final FileNotFoundException e) {
			throw new UnrecoverableError(e);
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
