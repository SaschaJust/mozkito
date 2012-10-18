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

import java.util.Collection;

import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.jsoup.helper.StringUtil;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class DaysBetweenUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
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
	public static int getDaysBetween(@NotNull @NotEmpty final Collection<JavaChangeOperation> p1,
	                                 @NotNull @NotEmpty final Collection<JavaChangeOperation> p2) {
		DateTime p1EarlyTime = null;
		DateTime p1LateTime = null;
		
		DateTime p2EarlyTime = null;
		DateTime p2LateTime = null;
		
		for (final JavaChangeOperation p1Op : p1) {
			final DateTime tmpTime = p1Op.getRevision().getTransaction().getTimestamp();
			if (p1EarlyTime == null) {
				p1EarlyTime = tmpTime;
			} else if (tmpTime.isBefore(p1EarlyTime)) {
				p1EarlyTime = tmpTime;
			}
			if (p1LateTime == null) {
				p1LateTime = tmpTime;
			} else if (tmpTime.isAfter(p1LateTime)) {
				p1LateTime = tmpTime;
			}
		}
		
		for (final JavaChangeOperation p2Op : p2) {
			final DateTime tmpTime = p2Op.getRevision().getTransaction().getTimestamp();
			if (p2EarlyTime == null) {
				p2EarlyTime = tmpTime;
			} else if (tmpTime.isBefore(p2EarlyTime)) {
				p2EarlyTime = tmpTime;
			}
			if (p2LateTime == null) {
				p2LateTime = tmpTime;
			} else if (tmpTime.isAfter(p2LateTime)) {
				p2LateTime = tmpTime;
			}
		}
		
		Condition.notNull(p1EarlyTime, "p1EarlyTime must not be null! Comparing: %s and %s.", StringUtil.join(p1, ","),
		                  StringUtil.join(p2, ","));
		Condition.notNull(p1LateTime, "p1LateTime must not be null! Comparing: %s and %s.", StringUtil.join(p1, ","),
		                  StringUtil.join(p2, ","));
		Condition.notNull(p2EarlyTime, "p2EarlyTime must not be null! Comparing: %s and %s.", StringUtil.join(p1, ","),
		                  StringUtil.join(p2, ","));
		Condition.notNull(p2LateTime, "p2LateTime must not be null! Comparing: %s and %s.", StringUtil.join(p1, ","),
		                  StringUtil.join(p2, ","));
		
		final int diff = Math.abs(Days.daysBetween(p1LateTime, p2EarlyTime).getDays());
		final int diff2 = Math.abs(Days.daysBetween(p2LateTime, p1EarlyTime).getDays());
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
