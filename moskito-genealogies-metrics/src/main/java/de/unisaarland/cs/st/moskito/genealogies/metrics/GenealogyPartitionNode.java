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
 ******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class GenealogyPartitionNode {
	
	private Collection<JavaChangeOperation> t;
	private String                          nodeId;
	private boolean                         lastNode;
	
	public GenealogyPartitionNode(Collection<JavaChangeOperation> t, String nodeId) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = false;
	}
	
	public GenealogyPartitionNode(Collection<JavaChangeOperation> t, String nodeId, boolean lastNode) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = lastNode;
	}
	
	public Collection<JavaChangeOperation> getNode() {
		return t;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public boolean isLast() {
		return lastNode;
	}
	
}
