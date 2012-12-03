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
 * The Class UntanglingComparisonResult.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UntanglingComparisonResult {
	
	/** The diff. */
	private final int    diff;
	
	/** The max jaccar index. */
	private final double maxJaccarIndex;
	
	/** The num correct partition. */
	private final int    numCorrectPartition;
	
	/** The num false partition. */
	private final int    numFalsePartition;
	
	/** The blob size. */
	private final int    blobSize;
	
	/** The file error. */
	private final double fileError;
	
	/**
	 * Instantiates a new untangling comparison result.
	 * 
	 * @param diff
	 *            the diff
	 * @param minJaccarIndex
	 *            the min jaccar index
	 * @param numCorrectPartition
	 *            the num correct partition
	 * @param numFalsePartition
	 *            the num false partition
	 * @param blobSize
	 *            the blob size
	 * @param fileError
	 *            the file error
	 */
	public UntanglingComparisonResult(final int diff, final double minJaccarIndex, final int numCorrectPartition,
	        final int numFalsePartition, final int blobSize, final double fileError) {
		this.diff = diff;
		this.maxJaccarIndex = minJaccarIndex;
		this.numCorrectPartition = numCorrectPartition;
		this.numFalsePartition = numFalsePartition;
		this.blobSize = blobSize;
		this.fileError = fileError;
	}
	
	/**
	 * Gets the blob size.
	 * 
	 * @return the blob size
	 */
	public int getBlobSize() {
		// PRECONDITIONS
		
		try {
			return this.blobSize;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.blobSize, "Field '%s' in '%s'.", "blobSize", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the diff.
	 * 
	 * @return the diff
	 */
	public int getDiff() {
		// PRECONDITIONS
		
		try {
			return this.diff;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.diff, "Field '%s' in '%s'.", "diff", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the file error.
	 * 
	 * @return the file error
	 */
	public double getFileError() {
		return this.fileError;
	}
	
	/**
	 * Gets the max jaccar index.
	 * 
	 * @return the max jaccar index
	 */
	public double getMaxJaccarIndex() {
		// PRECONDITIONS
		
		try {
			return this.maxJaccarIndex;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.maxJaccarIndex, "Field '%s' in '%s'.", "minJaccarIndex", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the num correct partition.
	 * 
	 * @return the num correct partition
	 */
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
	
	/**
	 * Gets the num false partition.
	 * 
	 * @return the num false partition
	 */
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
	
	/**
	 * Gets the precision.
	 * 
	 * @return the precision
	 */
	public double getPrecision() {
		final double TP = getNumCorrectPartition();
		final double FP = getNumFalsePartition();
		return (TP / (TP + FP));
	}
	
	/**
	 * Gets the relative diff.
	 * 
	 * @return the relative diff
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
