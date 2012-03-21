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
package de.unisaarland.cs.st.moskito.callgraph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * The Class Tarjan.
 *
 * @param <V> the value type
 * @param <E> the element type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Tarjan<V, E> {
	
	/**
	 * The Class Node.
	 *
	 * @param <V> the value type
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	private static class Node<V> {
		
		/** The inner. */
		V   inner;
		
		/** The index. */
		int index   = -1;
		
		/** The lowlink. */
		int lowlink = 0;
		
		/**
		 * Instantiates a new node.
		 *
		 * @param v the v
		 */
		public Node(final V v) {
			this.inner = v;
		}
	}
	
	/** The nodes. */
	private final Map<Object, Node<V>> nodes = new HashMap<Object, Node<V>>();
	
	/** The index. */
	private int                        index = 0;
	
	/** The stack. */
	private final ArrayList<Node<V>>   stack = new ArrayList<Node<V>>();
	
	/** The SCC. */
	private final Set<Set<V>>          SCC   = new HashSet<Set<V>>();
	
	/**
	 * Clear.
	 */
	private void clear() {
		this.nodes.clear();
		this.index = 0;
		this.stack.clear();
		this.SCC.clear();
		
	}
	
	/**
	 * Gets the node.
	 *
	 * @param v the v
	 * @return the node
	 */
	private Node<V> getNode(final V v) {
		if (!this.nodes.containsKey(v)) {
			this.nodes.put(v, new Node<V>(v));
		}
		return this.nodes.get(v);
	}
	
	/**
	 * Gets the strongly connected components.
	 *
	 * @param graph the graph
	 * @return the strongly connected components
	 */
	public Set<Set<V>> getStronglyConnectedComponents(final DirectedGraph<V, E> graph) {
		Set<Set<V>> result = new HashSet<Set<V>>();
		
		for (V v : graph.getVertices()) {
			Set<Set<V>> tmpResult = tarjan(getNode(v), graph);
			result.addAll(tmpResult);
		}
		clear();
		return result;
	}
	
	/**
	 * Tarjan.
	 *
	 * @param v the v
	 * @param graph the graph
	 * @return the sets the
	 */
	public Set<Set<V>> tarjan(final Node<V> v,
	                          final DirectedGraph<V, E> graph) {
		
		v.index = this.index;
		v.lowlink = this.index;
		this.index++;
		this.stack.add(0, v);
		
		for (V vPrime : graph.getSuccessors(v.inner)) {
			Node<V> n = getNode(vPrime);
			if (n.index == -1) {
				tarjan(n, graph);
				v.lowlink = Math.min(v.lowlink, n.lowlink);
			} else if (this.stack.contains(n)) {
				v.lowlink = Math.min(v.lowlink, n.index);
			}
		}
		
		if (v.lowlink == v.index) {
			Node<V> n;
			Set<V> component = new HashSet<V>();
			do {
				n = this.stack.remove(0);
				component.add(n.inner);
			} while (n != v);
			if (component.size() > 1) {
				this.SCC.add(component);
			}
		}
		return this.SCC;
	}
	
}
