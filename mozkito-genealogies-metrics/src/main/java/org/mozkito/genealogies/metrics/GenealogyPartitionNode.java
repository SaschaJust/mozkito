/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/

package org.mozkito.genealogies.metrics;

import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;

/**
 * The Class GenealogyPartitionNode.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GenealogyPartitionNode {
	
	/** The t. */
	private final ChangeGenealogyLayerNode t;
	
	/** The last node. */
	private final boolean                  lastNode;
	
	/**
	 * Instantiates a new genealogy partition node.
	 * 
	 * @param t
	 *            the t
	 * @param nodeId
	 *            the node id
	 */
	public GenealogyPartitionNode(final ChangeGenealogyLayerNode t) {
		this.t = t;
		this.lastNode = false;
	}
	
	/**
	 * Instantiates a new genealogy partition node.
	 * 
	 * @param t
	 *            the t
	 * @param nodeId
	 *            the node id
	 * @param lastNode
	 *            the last node
	 */
	public GenealogyPartitionNode(final ChangeGenealogyLayerNode t, final boolean lastNode) {
		this.t = t;
		this.lastNode = lastNode;
	}
	
	/**
	 * Gets the node.
	 * 
	 * @return the node
	 */
	public ChangeGenealogyLayerNode getNode() {
		return this.t;
	}
	
	/**
	 * Gets the node id.
	 * 
	 * @return the node id
	 */
	public String getNodeId() {
		return this.t.getNodeId();
	}
	
	/**
	 * Checks if is last.
	 * 
	 * @return true, if is last
	 */
	public boolean isLast() {
		return this.lastNode;
	}
	
}
