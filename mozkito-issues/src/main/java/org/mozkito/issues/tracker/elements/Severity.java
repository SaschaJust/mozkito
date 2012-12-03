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
package org.mozkito.issues.tracker.elements;

/**
 * The Enum Severity.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum Severity {
	
	/** The UNKNOWN. */
	UNKNOWN,
	/** The ENHANCEMENT. */
	ENHANCEMENT,
	/** The TRIVIAL. */
	TRIVIAL,
	/** The MINOR. */
	MINOR,
	/** The NORMAL. */
	NORMAL,
	/** The MAJOR. */
	MAJOR,
	/** The CRITICAL. */
	CRITICAL,
	/** The BLOCKER. */
	BLOCKER;
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
