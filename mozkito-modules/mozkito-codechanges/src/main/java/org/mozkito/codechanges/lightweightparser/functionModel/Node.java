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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mozkito.codechanges.lightweightparser.constraints.Constraint;

/**
 * The Class Node.
 */
public class Node {
	
	/** The id given to the most recently created node. */
	public static int            count = 0;
	
	/** The incoming edges. */
	List<Edge>                   incomingEdges;
	
	/** The outgoing edges. */
	List<Edge>                   outgoingEdges;
	
	/** The node id. */
	int                          id;
	
	/**
	 * Used to indicate if sequential constraints for all incoming edges have been traversed when mining seq.
	 * constraints
	 */
	boolean                      done;
	
	/** An integer used when extracting sequential constraints from nested loops. */
	int                          partOfLoop;
	
	/**
	 * A mapping from strings that represent objects to a set of strings representing all function calls associated with
	 * the object that precede the current node in the function model. an example mapping would be: "obj" ->
	 * ["toString(0)@0", "copy(1)@1"]
	 * */
	HashMap<String, Set<String>> prevEvents;
	
	/**
	 * Instantiates a new node.
	 */
	public Node() {
		this.incomingEdges = new ArrayList<Edge>();
		this.outgoingEdges = new ArrayList<Edge>();
		this.id = Node.count;
		Node.count++;
		this.done = false;
		this.partOfLoop = 0;
	}
	
	/**
	 * Adds the an edge to the list of incoming edges. The "to" node of the edge is set to "this".
	 * 
	 * @param e
	 *            the e
	 */
	public void addInEdge(final Edge e) {
		this.incomingEdges.add(e);
		e.setTo(this);
	}
	
	/**
	 * Adds the an edge to the list of outgoing edges. The "from" node of the edge is set to "this".
	 * 
	 * @param e
	 *            the e
	 */
	public void addOutEdge(final Edge e) {
		this.outgoingEdges.add(e);
		e.setFrom(this);
	}
	
	/**
	 * Creates sequential constraints for this node and next nodes.
	 * 
	 * @return the set< constraint>
	 */
	public Set<Constraint> collectConstraints() {
		final Set<Constraint> constraintSet = new HashSet<Constraint>();
		
		if (this.done) {
			return constraintSet;
		}
		
		for (final Edge e : this.incomingEdges) {
			if (!e.getFrom().done) {
				return constraintSet;
			}
		}
		
		if (this.prevEvents == null) {
			this.prevEvents = new HashMap<String, Set<String>>();
		}
		
		for (final Edge e : this.incomingEdges) {
			
			final HashMap<String, Set<String>> prev = e.getFrom().getPrevEvents();
			for (final String s : prev.keySet()) {
				if (!this.prevEvents.containsKey(s)) {
					this.prevEvents.put(s, new TreeSet<String>(prev.get(s)));
				} else {
					this.prevEvents.get(s).addAll(prev.get(s));
				}
			}
		}
		
		for (final Edge e : this.incomingEdges) {
			final Event event = e.getEvent();
			final HashMap<String, Set<String>> prev = e.getFrom().getPrevEvents();
			
			for (final Obj o : event.getObjects()) {
				if (prev.containsKey(o.getName())) {
					for (final String s : prev.get(o.getName())) {
						// make constraint s < o
						final Constraint c = new Constraint(s, o.funName + "@" + o.position);
						if (c != null) {
							constraintSet.add(c);
						}
						
					}
					
				}
				
				if (!this.prevEvents.containsKey(o.getName())) {
					final Set<String> set = new TreeSet<String>();
					set.add(o.funName + "@" + o.position);
					this.prevEvents.put(o.getName(), set);
				} else {
					this.prevEvents.get(o.getName()).add(o.funName + "@" + o.position);
				}
				
			}
		}
		
		this.done = true;
		
		for (final Edge e : getOutgoingEdges()) {
			// e.getTo().collectConstraints(constraintSet);
			constraintSet.addAll(e.getTo().collectConstraints());
		}
		return constraintSet;
	}
	
	/**
	 * {@inheritDoc}
	 * 
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
		final Node other = (Node) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the node id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets the incoming edges.
	 * 
	 * @return the incoming edges
	 */
	public List<Edge> getIncomingEdges() {
		return this.incomingEdges;
	}
	
	/**
	 * Gets the outgoing edges.
	 * 
	 * @return the outgoing edges
	 */
	public List<Edge> getOutgoingEdges() {
		return this.outgoingEdges;
	}
	
	/**
	 * Gets the hash map of all events for all objects that can precede this node via control flow.
	 * 
	 * @return the prev events
	 */
	public HashMap<String, Set<String>> getPrevEvents() {
		return this.prevEvents;
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
		result = (prime * result) + this.id;
		return result;
	}
	
	/**
	 * Marks all nodes that are part of a loop.
	 * 
	 * @param i
	 *            the i
	 * @return the linked list< loop node>
	 */
	public LinkedList<LoopNode> markLoopNodes(final int i) {
		final LinkedList<LoopNode> list = new LinkedList<LoopNode>();
		// System.out.println("Mark: " + this.id + " with " + i);
		// partOfLoop = true;
		if (this.partOfLoop < i) {
			this.partOfLoop = i;
		}
		
		for (final Edge edge : this.incomingEdges) {
			if (edge.from.partOfLoop < i) {
				list.addAll(0, edge.from.markLoopNodes(i));
			}
		}
		return list;
		
	}
	
	/**
	 * Sets the the previous events hash maps for all nodes in a loop.
	 * 
	 * @param map
	 *            the hash map of the previous node
	 * @param e
	 *            the current edge
	 * @param end
	 *            when this node is reached the complete loop has been iterated
	 * @param completedEdges
	 *            the edges that have already been traversed
	 * @param i
	 *            the depth of the current loop
	 */
	public void setHashMap_Loop(final HashMap<String, Set<String>> map,
	                            final Edge e,
	                            final Node end,
	                            final HashSet<Edge> completedEdges,
	                            final int i) {
		
		if (this.partOfLoop < i) {
			return;
		}
		if (this.prevEvents == null) {
			this.prevEvents = new HashMap<String, Set<String>>();
		}
		
		for (final String s : map.keySet()) {
			if (!this.prevEvents.containsKey(s)) {
				this.prevEvents.put(s, new TreeSet<String>(map.get(s)));
			} else {
				this.prevEvents.get(s).addAll(map.get(s));
			}
		}
		
		for (final Obj o : e.getEvent().getObjects()) {
			if (o.getName().startsWith("#") || o.getName().startsWith("%")) {
				continue;
			}
			if (!this.prevEvents.containsKey(o.getName()) || (o.getPosition() == -1)) {
				final Set<String> set = new TreeSet<String>();
				set.add(o.funName + "@" + o.position);
				this.prevEvents.put(o.getName(), set);
			} else {
				this.prevEvents.get(o.getName()).add(o.funName + "@" + o.position);
			}
		}
		
		if (this == end) {
			return;
		}
		
		completedEdges.add(e);
		boolean propagate = true;
		for (final Edge out : this.outgoingEdges) {
			propagate &= completedEdges.contains(out) || (out.to.partOfLoop != i);
		}
		
		if (propagate) {
			this.partOfLoop--;
			for (final Edge edge : getIncomingEdges()) {
				edge.from.setHashMap_Loop(this.prevEvents, edge, end, completedEdges, i);
			}
			
		}
		
	}
	
	/**
	 * Sets the incoming edges.
	 * 
	 * @param incomingEdges
	 *            the new incoming edges
	 */
	public void setIncomingEdges(final List<Edge> incomingEdges) {
		this.incomingEdges = incomingEdges;
	}
	
	/**
	 * Sets the outgoing edges.
	 * 
	 * @param outgoingEdges
	 *            the new outgoing edges
	 */
	public void setOutgoingEdges(final List<Edge> outgoingEdges) {
		this.outgoingEdges = outgoingEdges;
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
		return "" + this.id;
	}
	
}
