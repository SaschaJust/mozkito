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
package de.unisaarland.cs.st.moskito.untangling.blob.compare;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * The Class ChangeCouplingCombineOperator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ConsecutiveChangeCombineOperator implements CombineOperator<ChangeSet> {
	
	private final int timeWindow;
	
	public ConsecutiveChangeCombineOperator(final int timeWindow) {
		this.timeWindow = timeWindow;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.untangling.blob.compare.CombineOperator#canBeCombined(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeSet cl1,
	                             final ChangeSet cl2) {
		// PRECONDITIONS
		
		try {
			
			final RCSTransaction cl1T = cl1.getTransaction();
			final RCSTransaction cl2T = cl2.getTransaction();
			
			if (cl1T.getAuthor().equals(cl2T.getAuthor())) {
				// only consider change sets stemming from the same author.
				// FIXME Careful. Make sure persons-merge was performed.
				final DateTime cl1Time = cl1T.getTimestamp();
				final DateTime cl2Time = cl2T.getTimestamp();
				
				if (cl1Time.getDayOfYear() == cl2Time.getDayOfYear()) {
					// both change sets must be applied the same day
					final int hoursDiff = Math.abs(Hours.hoursBetween(cl1Time, cl2Time).getHours());
					if (hoursDiff <= this.timeWindow) {
						return true;
					}
				}
			}
			
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}
