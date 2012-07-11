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
package de.unisaarland.cs.st.moskito.untangling.blob.combine;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;

import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class BlobWindowSizeCombineOperator implements CombineOperator<ChangeSet> {
	
	private final int blobWindowSize;
	
	public BlobWindowSizeCombineOperator(@NotNegative final int blobWindowSize) {
		this.blobWindowSize = blobWindowSize;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.untangling.blob.combine.CombineOperator#canBeCombined(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeSet t1,
	                             final ChangeSet t2) {
		// PRECONDITIONS
		
		try {
			if (this.blobWindowSize > -1) {
				final int days = Days.daysBetween(t1.getTransaction().getTimestamp(),
				                                  t2.getTransaction().getTimestamp()).getDays();
				if (Math.abs(days) > this.blobWindowSize) {
					return false;
				}
			}
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
