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

package org.mozkito.mappings.utils;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class ItemizationEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ItemizationEntry {
	
	/** The start. */
	private int start;
	
	/** The end. */
	private int end;
	
	/**
	 * Instantiates a new itemization entry.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public ItemizationEntry(final int start, final int end) {
		// PRECONDITIONS
		
		try {
			this.start = start;
			this.end = end;
		} finally {
			// POSTCONDITIONS
			CompareCondition.positive(this.end, "Field '%s' in '%s'.", "end", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public int getEnd() {
		// PRECONDITIONS
		
		try {
			return this.end;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.end, "Field '%s' in '%s'.", "end", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(ItemizationEntry.class);
	}
	
	/**
	 * Gets the start.
	 * 
	 * @return the start
	 */
	public int getStart() {
		// PRECONDITIONS
		
		try {
			return this.start;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.start, "Field '%s' in '%s'.", "start", getClass().getSimpleName());
		}
	}
}
