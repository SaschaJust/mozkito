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
import java.util.List;

/**
 * The Class Wrapper.
 */
public class Wrapper {
	
	/** The continue nodes. */
	public List<Node> continueNodes;
	
	/** The break nodes. */
	public List<Node> breakNodes;
	
	/**
	 * Instantiates a new wrapper.
	 */
	public Wrapper() {
		this.continueNodes = new ArrayList<Node>();
		this.breakNodes = new ArrayList<Node>();
	}
	
	/**
	 * Instantiates a new wrapper.
	 * 
	 * @param continueNodes
	 *            the continue nodes
	 * @param breakNodes
	 *            the break nodes
	 */
	public Wrapper(final List<Node> continueNodes, final List<Node> breakNodes) {
		super();
		this.continueNodes = continueNodes;
		this.breakNodes = breakNodes;
	}
	
	/**
	 * Adds the.
	 * 
	 * @param w
	 *            the w
	 */
	public void add(final Wrapper w) {
		this.breakNodes.addAll(w.breakNodes);
		this.continueNodes.addAll(w.continueNodes);
		
	}
	
	/**
	 * Adds the break node.
	 * 
	 * @param n
	 *            the n
	 */
	public void addBreakNode(final Node n) {
		this.breakNodes.add(n);
	}
	
	/**
	 * Adds the con node.
	 * 
	 * @param n
	 *            the n
	 */
	public void addConNode(final Node n) {
		this.continueNodes.add(n);
	}
	
}
