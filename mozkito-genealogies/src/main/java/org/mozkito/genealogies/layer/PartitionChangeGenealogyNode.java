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
package org.mozkito.genealogies.layer;

import java.util.Collection;

import org.mozkito.codeanalysis.model.JavaChangeOperation;

/**
 * The Class PartitionChangeGenealogyNode.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartitionChangeGenealogyNode extends ChangeGenealogyLayerNode {
	
	/** The partition id. */
	private final String partitionId;
	
	/**
	 * Instantiates a new partition change genealogy node.
	 * 
	 * @param partitionId
	 *            the partition id
	 * @param partition
	 *            the partition
	 */
	public PartitionChangeGenealogyNode(final String partitionId, final Collection<JavaChangeOperation> partition) {
		super(partition);
		this.partitionId = partitionId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogyLayerNode#getNodeId()
	 */
	@Override
	public String getNodeId() {
		return this.partitionId;
	}
	
}
