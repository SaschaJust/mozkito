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
 *******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies.metrics.utils;

import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogyLayerNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class DaysBetweenUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class DaysBetweenUtils {
	
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
		return Math.abs(Days.daysBetween(op1.getRevision().getTransaction().getTimestamp(),
		                                 op2.getRevision().getTransaction().getTimestamp()).getDays());
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
	public static int getDaysBetween(final RCSTransaction t1,
	                                 final RCSTransaction t2) {
		return Math.abs(Days.daysBetween(t1.getTimestamp(), t2.getTimestamp()).getDays());
	}
	
}
