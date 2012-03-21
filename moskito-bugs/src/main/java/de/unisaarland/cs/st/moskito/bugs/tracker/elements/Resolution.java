/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.elements;

/**
 * The Enum Resolution.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public enum Resolution {
	
	/** The UNKNOWN. */
	UNKNOWN, 
 /** The UNRESOLVED. */
 UNRESOLVED, 
 /** The DUPLICATE. */
 DUPLICATE, 
 /** The RESOLVED. */
 RESOLVED, 
 /** The INVALID. */
 INVALID, 
 /** The WON t_ fix. */
 WONT_FIX, 
 /** The WORK s_ fo r_ me. */
 WORKS_FOR_ME;
	
	/**
	 * Gets the handle.
	 *
	 * @return the handle
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
