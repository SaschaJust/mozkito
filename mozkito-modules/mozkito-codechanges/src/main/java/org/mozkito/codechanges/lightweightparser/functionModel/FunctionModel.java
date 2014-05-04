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
package org.mozkito.codechanges.lightweightparser.functionModel;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.mozkito.codechanges.lightweightparser.constraints.Constraint;

/**
 * The Class FunctionModel.
 * 
 */
public class FunctionModel {
	
	/** The start node. */
	private Node start;
	
	/**
	 * The end node (the end node of the last statement that was added when building function model. Not necessarily the
	 * only node with no outgoing edges
	 */
	private Node end;
	
	/**
	 * Instantiates a new function model.
	 */
	public FunctionModel() {
		this.start = new Node();
		this.end = this.start;
	}
	
	/**
	 * Adds an edge to the function model. A new end node is added to the function model and the inserted edge connects
	 * the old end node with the new end node.
	 * 
	 * @param e
	 *            the edge to be added
	 */
	public void addEdge(final Edge e) {
		if ((this.end != this.start) && this.end.getIncomingEdges().isEmpty()) {
			// if the current end node cannot be reached from the start node don't add the edge
			return;
		}
		this.end.addOutEdge(e);
		this.end = new Node();
		this.end.addInEdge(e);
	}
	
	/**
	 * Mines sequential constraints from the function model.
	 * 
	 * @return the set of sequential constraints
	 */
	public Set<Constraint> collectConstraints() {
		return this.start.collectConstraints();
	}
	
	/**
	 * Returns the set of all edges in the function model.
	 * 
	 * @return the edge set
	 */
	public Set<Edge> getEdgeSet() {
		final Set<Edge> edgeSet = new HashSet<Edge>();
		final Stack<Edge> stack = new Stack<Edge>();
		
		for (final Edge e : this.start.getOutgoingEdges()) {
			if (!edgeSet.contains(e)) {
				edgeSet.add(e);
				stack.add(e);
			}
		}
		while (!stack.isEmpty()) {
			final Edge ed = stack.pop();
			for (final Edge e : ed.getTo().getOutgoingEdges()) {
				if (!edgeSet.contains(e)) {
					edgeSet.add(e);
					stack.add(e);
				}
			}
		}
		return edgeSet;
	}
	
	/**
	 * Gets the end node. The end node is the node which was last added to the function model, not necessarily the only
	 * node with no outgoing edges.
	 * 
	 * @return the end node
	 */
	public Node getEnd() {
		return this.end;
	}
	
	/**
	 * Returns the set of all nodes in the function model.
	 * 
	 * @return the node set
	 */
	public Set<Node> getNodeSet() {
		final Set<Node> nodeSet = new HashSet<Node>();
		final Stack<Node> stack = new Stack<Node>();
		
		nodeSet.add(this.start);
		for (final Edge e : this.start.getOutgoingEdges()) {
			if (!nodeSet.contains(e.to)) {
				nodeSet.add(e.to);
				stack.add(e.to);
			}
		}
		while (!stack.isEmpty()) {
			final Node n = stack.pop();
			for (final Edge e : n.getOutgoingEdges()) {
				if (!nodeSet.contains(e.to)) {
					nodeSet.add(e.to);
					stack.add(e.to);
				}
			}
		}
		return nodeSet;
	}
	
	/**
	 * Returns the start node.
	 * 
	 * @return the start
	 */
	public Node getStart() {
		return this.start;
	}
	
	/**
	 * Sets the end node.
	 * 
	 * @param end
	 *            the new end node
	 */
	public void setEnd(final Node end) {
		this.end = end;
	}
	
	/**
	 * Sets the start node.
	 * 
	 * @param start
	 *            the new start node
	 */
	public void setStart(final Node start) {
		this.start = start;
	}
	
	/**
	 * Returns a string which represents the function model in dot format.
	 * 
	 * @return the string
	 */
	public String toDot() {
		String s = "digraph G {\n";
		
		final Set<Edge> edgeList = new HashSet<Edge>();
		final Stack<Edge> stack = new Stack<Edge>();
		
		for (final Edge e : this.start.getOutgoingEdges()) {
			if (!edgeList.contains(e)) {
				edgeList.add(e);
				stack.add(e);
			}
		}
		while (!stack.isEmpty()) {
			final Edge ed = stack.pop();
			s += ed.toDot() + "\n";
			for (final Edge e : ed.getTo().getOutgoingEdges()) {
				if (!edgeList.contains(e)) {
					edgeList.add(e);
					stack.add(e);
				}
			}
		}
		return s + "}";
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Set<Edge> edgeList = new HashSet<Edge>();
		final Stack<Edge> stack = new Stack<Edge>();
		
		for (final Edge e : this.start.getOutgoingEdges()) {
			if (!edgeList.contains(e)) {
				edgeList.add(e);
				stack.add(e);
			}
		}
		
		String s = "Start " + this.start.toString() + "  End " + this.end.toString() + " \n";
		while (!stack.isEmpty()) {
			final Edge ed = stack.pop();
			s += ed.toString() + "\n";
			for (final Edge e : ed.getTo().getOutgoingEdges()) {
				if (!edgeList.contains(e)) {
					edgeList.add(e);
					stack.add(e);
				}
			}
		}
		return s;
	}
	
}
