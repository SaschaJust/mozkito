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
package de.unisaarland.cs.st.moskito.untangling.settings;

import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class UntangleInstruction {
	
	private final ChangeSet changeSet;
	private int             numPartitions = 0;
	private double          threshold     = 0.0d;
	
	public UntangleInstruction(final ChangeSet changeSet, final double value) {
		this.changeSet = changeSet;
		if (value >= 1) {
			this.numPartitions = Double.valueOf(value).intValue();
		} else {
			this.threshold = value;
		}
	}
	
	public ChangeSet getChangeSet() {
		return this.changeSet;
	}
	
	public int getNumPartitions() {
		return this.numPartitions;
	}
	
	public double getTreshold() {
		return this.threshold;
	}
	
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
