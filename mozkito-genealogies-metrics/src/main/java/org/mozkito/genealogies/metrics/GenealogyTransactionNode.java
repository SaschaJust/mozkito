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

import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class GenealogyTransactionNode.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GenealogyTransactionNode {
	
	/** The t. */
	private final RCSTransaction t;
	
	/** The node id. */
	private final String         nodeId;
	
	/** The last node. */
	private final boolean        lastNode;
	
	/**
	 * Instantiates a new genealogy transaction node.
	 * 
	 * @param t
	 *            the t
	 * @param nodeId
	 *            the node id
	 */
	public GenealogyTransactionNode(final RCSTransaction t, final String nodeId) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = false;
	}
	
	/**
	 * Instantiates a new genealogy transaction node.
	 * 
	 * @param t
	 *            the t
	 * @param nodeId
	 *            the node id
	 * @param lastNode
	 *            the last node
	 */
	public GenealogyTransactionNode(final RCSTransaction t, final String nodeId, final boolean lastNode) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = lastNode;
	}
	
	/**
	 * Gets the node.
	 * 
	 * @return the node
	 */
	public RCSTransaction getNode() {
		return this.t;
	}
	
	/**
	 * Gets the node id.
	 * 
	 * @return the node id
	 */
	public String getNodeId() {
		return this.nodeId;
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
