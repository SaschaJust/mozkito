/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.callgraph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedGraph;

public class Tarjan<V, E> {
	
	private static class Node<V> {
		
		V   inner;
		int index   = -1;
		int lowlink = 0;
		
		public Node(final V v) {
			this.inner = v;
		}
	}
	
	private final Map<Object, Node<V>> nodes = new HashMap<Object, Node<V>>();
	private int                        index = 0;
	private final ArrayList<Node<V>>   stack = new ArrayList<Node<V>>();
	private final Set<Set<V>>          SCC   = new HashSet<Set<V>>();
	
	private void clear() {
		this.nodes.clear();
		this.index = 0;
		this.stack.clear();
		this.SCC.clear();
		
	}
	
	private Node<V> getNode(final V v) {
		if (!this.nodes.containsKey(v)) {
			this.nodes.put(v, new Node<V>(v));
		}
		return this.nodes.get(v);
	}
	
	public Set<Set<V>> getStronglyConnectedComponents(final DirectedGraph<V, E> graph) {
		Set<Set<V>> result = new HashSet<Set<V>>();
		
		for (V v : graph.getVertices()) {
			Set<Set<V>> tmpResult = tarjan(getNode(v), graph);
			result.addAll(tmpResult);
		}
		clear();
		return result;
	}
	
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
