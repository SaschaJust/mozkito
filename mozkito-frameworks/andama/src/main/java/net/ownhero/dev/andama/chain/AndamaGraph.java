/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.chain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.threads.AndamaThread;

public class AndamaGraph {
	
	private final LinkedList<AndamaNode> openBranches   = new LinkedList<AndamaNode>();
	private final HashSet<AndamaNode>    closedBranches = new HashSet<AndamaNode>();
	private final LinkedList<AndamaNode> activeNodes    = new LinkedList<AndamaNode>();
	
	/**
	 * @param andamaNode
	 */
	public void addSource(final AndamaNode andamaNode) {
		if (andamaNode.isSource()) {
			this.openBranches.add(andamaNode);
		} else {
			// TODO error
		}
	}
	
	/**
	 * @param node
	 * @param thread
	 */
	public void attach(final AndamaNode node,
	                   final AndamaThread<?, ?> thread) {
		AndamaNode andamaNode = new AndamaNode(thread);
		System.err.println("attaching " + andamaNode + " to " + node);
		
		if (andamaNode.isDemultiplexer()) {
			// close all branches
			for (AndamaNode matchingNode : getMatching(thread)) {
				attachCheckMultiplexer(matchingNode, andamaNode);
				this.openBranches.remove(matchingNode);
			}
			this.openBranches.add(andamaNode);
		} else if (andamaNode.isSink()) {
			attachCheckMultiplexer(node, andamaNode);
			this.openBranches.remove(node);
			if (this.openBranches.isEmpty()) {
				this.getClosedBranches().add(andamaNode);
				System.err.println("Found graph constellation.");
			}
		} else if (andamaNode.isSource()) {
			this.openBranches.add(andamaNode);
		} else {
			attachCheckMultiplexer(node, andamaNode);
			this.openBranches.remove(node);
			this.openBranches.add(andamaNode);
		}
	}
	
	/**
	 * @param target
	 * @param newNode
	 */
	public void attachCheckMultiplexer(final AndamaNode target,
	                                   final AndamaNode newNode) {
		if (target.isMultiplexer()) {
			AndamaNode branch = target.headCopy();
			this.openBranches.add(branch);
		}
		target.connectOutput(newNode);
	}
	
	public AndamaGraph deepCopy() {
		HashMap<AndamaNode, AndamaNode> map = new HashMap<AndamaNode, AndamaNode>();
		
		for (AndamaNode node : this.activeNodes) {
			map.put(node, node.clone());
		}
		return null;
	}
	
	/**
	 * @param thread
	 */
	public void detach(final AndamaThread<?, ?> thread) {
		AndamaNode andamaNode = null;
		AndamaNode compareNode = new AndamaNode(thread);
		System.err.println("detaching " + compareNode);
		
		// find andamaNode
		for (AndamaNode node : this.openBranches) {
			if (node == compareNode) {
				andamaNode = node;
			}
		}
		
		if (andamaNode == null) {
			// node is in the closed branches
			for (AndamaNode node : this.closedBranches) {
				if (node == compareNode) {
					andamaNode = node;
				}
			}
		}
		
		if (andamaNode == null) {
			// node is either unknown or not a head node
			return;
		}
		
		if (andamaNode.isDemultiplexer()) {
			this.openBranches.remove(andamaNode);
			// remove and open all attached branches again
			for (AndamaNode inputNode : andamaNode.getInputs()) {
				inputNode.disconnectOutput(andamaNode);
				this.openBranches.add(inputNode);
			}
		} else if (andamaNode.isSink()) {
			// open branch again
			this.closedBranches.remove(andamaNode);
			AndamaNode inputNode = andamaNode.getInputs().getFirst();
			inputNode.disconnectOutput(andamaNode);
			this.openBranches.add(inputNode);
		} else if (andamaNode.isSource()) {
			// don't remove at all
		} else {
			this.openBranches.remove(andamaNode);
			this.openBranches.add(andamaNode.getInputs().getFirst());
		}
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
		if (!(obj instanceof AndamaGraph)) {
			return false;
		}
		AndamaGraph other = (AndamaGraph) obj;
		if (this.closedBranches == null) {
			if (other.closedBranches != null) {
				return false;
			}
		} else if (this.closedBranches.size() != other.closedBranches.size()) {
			return false;
		} else {
			for (AndamaNode node : this.closedBranches) {
				// find graphs with equal nodes
				
				node.getSources();
				
			}
		}
		return true;
	}
	
	/**
	 * @return the closedBranches
	 */
	public Set<AndamaNode> getClosedBranches() {
		return this.closedBranches;
	}
	
	/**
	 * @param thread
	 * @return
	 */
	public List<AndamaNode> getMatching(final AndamaThread<?, ?> thread) {
		LinkedList<AndamaNode> list = new LinkedList<AndamaNode>();
		AndamaNode target = new AndamaNode(thread);
		
		if (/* make sure to-node has an input connector */thread.hasInputConnector()) {
			for (AndamaNode node : this.openBranches) {
				if (/* make sure from-node has an output connector */node.getThread().hasOutputConnector()
				        && (/*
							 * make sure output type of from-node matches input
							 * type of to-node
							 */node.getOutputType() == target.getInputType())
				        && (/* avoid demux<-> demux connection */!node.isDemultiplexer() || !target.isDemultiplexer())
				        && (/* avoid mux<-> mux connection */!node.isMultiplexer() || !target.isMultiplexer())) {
					list.add(node);
				}
			}
		} else {
			// we got a source node. This should not happen since we require
			// source nodes to be set before actual graph building starts.
			// TODO error
		}
		
		if (target.isDemultiplexer() && (list.size() < 2)) {
			// don't attach demux to a single node
			return new LinkedList<AndamaNode>();
		} else {
			return list;
		}
	}
	
	/**
	 * @return
	 */
	public List<AndamaNode> getOpenBranches() {
		return this.openBranches;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.closedBranches == null)
		                                                          ? 0
		                                                          : this.closedBranches.hashCode());
		return result;
	}
}
