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
 * The Enum Status.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum Status {
	
	/** The UNKNOWN. */
	UNKNOWN,
	/** The UNCONFIRMED. */
	UNCONFIRMED,
	/** The NEW. */
	NEW,
	/** The ASSIGNED. */
	ASSIGNED,
	/** The I n_ progress. */
	IN_PROGRESS,
	/** The FEEDBACK. */
	FEEDBACK,
	/** The REOPENED. */
	REOPENED,
	/** The REVIEWPENDING. */
	REVIEWPENDING,
	/** The VERIFIED. */
	VERIFIED,
	/** The CLOSED. */
	CLOSED,
	/** The ACKNOWLEDGED. */
	ACKNOWLEDGED;
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
	}
}
