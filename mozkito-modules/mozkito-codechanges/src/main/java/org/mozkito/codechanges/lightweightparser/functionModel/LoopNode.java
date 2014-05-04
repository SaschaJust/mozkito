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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mozkito.codechanges.lightweightparser.constraints.Constraint;

/**
 * The Class LoopNode.
 */
public class LoopNode extends Node {
	
	/** The loop incoming edges. */
	List<Edge> loopIncomingEdges;
	
	/** The loop out edges. */
	List<Edge> loopOutEdges;
	
	/** The built hash. */
	boolean    builtHash;
	
	/**
	 * Instantiates a new loop node.
	 */
	public LoopNode() {
		super();
		this.loopIncomingEdges = new LinkedList<Edge>();
		this.loopOutEdges = new LinkedList<Edge>();
		this.builtHash = false;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.functionModel.Node#addInEdge(org.mozkito.codechanges.lightweightparser.functionModel.Edge)
	 */
	@Override
	public void addInEdge(final Edge e) {
		this.loopIncomingEdges.add(e);
		e.setTo(this);
	}
	
	/**
	 * Adds the normal in edge.
	 * 
	 * @param e
	 *            the e
	 */
	public void addNormalInEdge(final Edge e) {
		e.setTo(this);
		this.incomingEdges.add(e);
	}
	
	/**
	 * Adds the normal out edge.
	 * 
	 * @param e
	 *            the e
	 */
	public void addNormalOutEdge(final Edge e) {
		e.setTo(this);
		this.outgoingEdges.add(e);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.functionModel.Node#addOutEdge(org.mozkito.codechanges.lightweightparser.functionModel.Edge)
	 */
	@Override
	public void addOutEdge(final Edge e) {
		this.loopOutEdges.add(e);
		e.setFrom(this);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.functionModel.Node#collectConstraints()
	 */
	@Override
	public Set<Constraint> collectConstraints() {// Set<Constraint> constraintSet){
	
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
		
		if (!this.builtHash) {
			this.partOfLoop = 1;
			final HashSet<Edge> temp = new HashSet<Edge>();
			for (final Edge edge : this.loopIncomingEdges) {
				final LinkedList<LoopNode> list = edge.from.markLoopNodes(1);
				for (final LoopNode n : list) {
					if (!n.builtHash) {
						for (final Edge ed : n.loopIncomingEdges) {
							ed.from.setHashMap_Loop(this.prevEvents, ed, n, temp, n.partOfLoop);
						}
						n.builtHash = true;
					}
				}
			}
			
			for (final Edge edge : this.loopIncomingEdges) {
				edge.from.setHashMap_Loop(this.prevEvents, edge, this, temp, 1);
			}
			this.builtHash = true;
		}
		
		constraintSet.addAll(super.collectConstraints());
		
		for (final Edge e : this.loopIncomingEdges) {
			
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
		return constraintSet;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.functionModel.Node#getIncomingEdges()
	 */
	@Override
	public List<Edge> getIncomingEdges() {
		final List<Edge> both = new LinkedList<Edge>();
		both.addAll(this.incomingEdges);
		both.addAll(this.loopIncomingEdges);
		return both;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.functionModel.Node#getOutgoingEdges()
	 */
	@Override
	public List<Edge> getOutgoingEdges() {
		final List<Edge> both = new LinkedList<Edge>();
		both.addAll(this.outgoingEdges);
		both.addAll(this.loopOutEdges);
		return both;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.functionModel.Node#markLoopNodes(int)
	 */
	@Override
	public LinkedList<LoopNode> markLoopNodes(final int i) {
		final LinkedList<LoopNode> list = new LinkedList<LoopNode>();
		// System.out.println("Mark: " + this.id + " with " + (i+1));
		this.partOfLoop = i + 1;
		list.add(this);
		for (final Edge edge : this.incomingEdges) {
			if (edge.from.partOfLoop < i) {
				list.addAll(0, edge.from.markLoopNodes(i));
			}
		}
		for (final Edge edge : this.loopIncomingEdges) {
			if (edge.from.partOfLoop < (i + 1)) {
				list.addAll(0, edge.from.markLoopNodes(i + 1));
			}
		}
		return list;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.functionModel.Node#toString()
	 */
	@Override
	public String toString() {
		return (super.id + 1000) + "";
	}
	
}
