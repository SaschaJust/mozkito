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

package org.mozkito.genealogies.metrics.utils;

import org.joda.time.Days;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class DaysBetweenUtils.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class DaysBetweenUtils {
	
	/**
	 * Gets the days between.
	 * 
	 * @param p1
	 *            the p1
	 * @param p2
	 *            the p2
	 * @return the days between
	 */
	public static int getDaysBetween(final ChangeGenealogyLayerNode p1,
	                                 final ChangeGenealogyLayerNode p2) {
		final int diff = Math.abs(Days.daysBetween(p1.getLatestTimestamp(), p2.getEarliestTimestamp()).getDays());
		final int diff2 = Math.abs(Days.daysBetween(p2.getLatestTimestamp(), p1.getEarliestTimestamp()).getDays());
		return Math.min(diff, diff2);
	}
	
	/**
	 * Gets the days between.
	 * 
	 * @param op1
	 *            the op1
	 * @param op2
	 *            the op2
	 * @return the days between
	 */
	public static int getDaysBetween(final JavaChangeOperation op1,
	                                 final JavaChangeOperation op2) {
		return Math.abs(Days.daysBetween(op1.getRevision().getChangeSet().getTimestamp(),
		                                 op2.getRevision().getChangeSet().getTimestamp()).getDays());
	}
	
	/**
	 * Gets the days between.
	 * 
	 * @param t1
	 *            the t1
	 * @param t2
	 *            the t2
	 * @return the days between
	 */
	public static int getDaysBetween(final ChangeSet t1,
	                                 final ChangeSet t2) {
		return Math.abs(Days.daysBetween(t1.getTimestamp(), t2.getTimestamp()).getDays());
	}
	
}
