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
package net.ownhero.dev.kisa;

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger.TerminalColor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Highlighter {
	
	private final LogLevel min;
	private final LogLevel max;
	
	public Highlighter(final LogLevel min, final LogLevel max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * @return the max
	 */
	public final LogLevel getMax() {
		// PRECONDITIONS
		
		try {
			return this.max;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.max, "Field '%s' in '%s'.", "max", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the min
	 */
	public final LogLevel getMin() {
		// PRECONDITIONS
		
		try {
			return this.min;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.min, "Field '%s' in '%s'.", "min", getClass().getSimpleName());
		}
	}
	
	public String highlight(final String message) {
		return String.format("%s%s%s%s", TerminalColor.MAGENTA.getTag(), TerminalColor.UNDERLINE.getTag(), message,
		                     TerminalColor.NONE.getTag());
	}
	
	public abstract boolean matches(String message,
	                                LogLevel level,
	                                String prefix);
	
}
