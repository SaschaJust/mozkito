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
package org.mozkito.genealogies.layer;

import java.util.Collection;

import org.joda.time.DateTime;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class TransactionChangeGenealogyNode.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TransactionChangeGenealogyNode extends PartitionChangeGenealogyNode {
	
	/** The r cs transaction. */
	private final ChangeSet changeSet;
	
	/**
	 * Instantiates a new transaction change genealogy node.
	 * 
	 * @param changeSet
	 *            the r cs transaction
	 * @param partition
	 *            the partition
	 */
	public TransactionChangeGenealogyNode(final ChangeSet changeSet,
	        final Collection<JavaChangeOperation> partition) {
		super(changeSet.getId(), partition);
		this.changeSet = changeSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogyLayerNode#getEarliestTimestamp()
	 */
	@Override
	public DateTime getEarliestTimestamp() {
		return this.changeSet.getTimestamp();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogyLayerNode#getLatestTimestamp()
	 */
	@Override
	public DateTime getLatestTimestamp() {
		return this.changeSet.getTimestamp();
	}
}
