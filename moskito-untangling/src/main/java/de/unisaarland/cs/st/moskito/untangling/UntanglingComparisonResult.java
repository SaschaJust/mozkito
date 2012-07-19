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
package de.unisaarland.cs.st.moskito.untangling;

import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class UntanglingComparisonResult {
	
	private final int    diff;
	
	private final double minJaccarIndex;
	
	private final int    numCorrectPartition;
	
	private final int    numFalsePartition;
	
	private final int    blobSize;
	
	public UntanglingComparisonResult(final int diff, final double minJaccarIndex, final int numCorrectPartition,
	        final int numFalsePartition, final int blobSize) {
		this.diff = diff;
		this.minJaccarIndex = minJaccarIndex;
		this.numCorrectPartition = numCorrectPartition;
		this.numFalsePartition = numFalsePartition;
		this.blobSize = blobSize;
	}
	
	public int getBlobSize() {
		// PRECONDITIONS
		
		try {
			return this.blobSize;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.blobSize, "Field '%s' in '%s'.", "blobSize", getClass().getSimpleName());
		}
	}
	
	public int getDiff() {
		// PRECONDITIONS
		
		try {
			return this.diff;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.diff, "Field '%s' in '%s'.", "diff", getClass().getSimpleName());
		}
	}
	
	public double getMinJaccarIndex() {
		// PRECONDITIONS
		
		try {
			return this.minJaccarIndex;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.minJaccarIndex, "Field '%s' in '%s'.", "minJaccarIndex", getClass().getSimpleName());
		}
	}
	
	public int getNumCorrectPartition() {
		// PRECONDITIONS
		
		try {
			return this.numCorrectPartition;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.numCorrectPartition, "Field '%s' in '%s'.", "numCorrectPartition",
			                  getClass().getSimpleName());
		}
	}
	
	public int getNumFalsePartition() {
		// PRECONDITIONS
		
		try {
			return this.numFalsePartition;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.numFalsePartition, "Field '%s' in '%s'.", "numFalsePartition",
			                  getClass().getSimpleName());
		}
	}
	
	public double getPrecision() {
		final double TP = getNumCorrectPartition();
		final double FP = getNumFalsePartition();
		return (TP / (TP + FP));
	}
	
	/**
	 * @return
	 */
	public double getRelativeDiff() {
		// PRECONDITIONS
		
		try {
			return ((double) getDiff() / (double) getBlobSize());
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
