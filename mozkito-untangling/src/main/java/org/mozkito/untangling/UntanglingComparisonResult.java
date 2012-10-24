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
package org.mozkito.untangling;

import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class UntanglingComparisonResult {
	
	private final int    diff;
	
	private final double maxJaccarIndex;
	
	private final int    numCorrectPartition;
	
	private final int    numFalsePartition;
	
	private final int    blobSize;
	
	private final double fileError;
	
	public UntanglingComparisonResult(final int diff, final double minJaccarIndex, final int numCorrectPartition,
	        final int numFalsePartition, final int blobSize, final double fileError) {
		this.diff = diff;
		this.maxJaccarIndex = minJaccarIndex;
		this.numCorrectPartition = numCorrectPartition;
		this.numFalsePartition = numFalsePartition;
		this.blobSize = blobSize;
		this.fileError = fileError;
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
	
	public double getFileError() {
		return this.fileError;
	}
	
	public double getMaxJaccarIndex() {
		// PRECONDITIONS
		
		try {
			return this.maxJaccarIndex;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.maxJaccarIndex, "Field '%s' in '%s'.", "minJaccarIndex", getClass().getSimpleName());
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
