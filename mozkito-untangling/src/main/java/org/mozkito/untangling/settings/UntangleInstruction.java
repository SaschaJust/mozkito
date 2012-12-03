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
package org.mozkito.untangling.settings;

import org.mozkito.untangling.blob.ChangeSet;

/**
 * The Class UntangleInstruction.
 *
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UntangleInstruction {
	
	/** The change set. */
	private final ChangeSet changeSet;
	
	/** The num partitions. */
	private int             numPartitions = 0;
	
	/** The threshold. */
	private double          threshold     = 0.0d;
	
	/**
	 * Instantiates a new untangle instruction.
	 *
	 * @param changeSet the change set
	 * @param value the value
	 */
	public UntangleInstruction(final ChangeSet changeSet, final double value) {
		this.changeSet = changeSet;
		if (value >= 1) {
			this.numPartitions = Double.valueOf(value).intValue();
		} else {
			this.threshold = value;
		}
	}
	
	/**
	 * Gets the change set.
	 *
	 * @return the change set
	 */
	public ChangeSet getChangeSet() {
		return this.changeSet;
	}
	
	/**
	 * Gets the num partitions.
	 *
	 * @return the num partitions
	 */
	public int getNumPartitions() {
		return this.numPartitions;
	}
	
	/**
	 * Gets the treshold.
	 *
	 * @return the treshold
	 */
	public double getTreshold() {
		return this.threshold;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("UntanglingInstruction[transactionid=");
		sb.append(getChangeSet().getTransaction().getId());
		sb.append(", numPartitions=");
		sb.append(getNumPartitions());
		sb.append("]");
		return sb.toString();
	}
}
