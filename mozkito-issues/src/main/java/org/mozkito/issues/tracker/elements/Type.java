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
 * The Enum Type.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum Type {
	
	/** The BUG. */
	BUG,
	/** The RFE. */
	RFE,
	/** The TASK. */
	TASK,
	/** The TEST. */
	TEST,
	/** The OTHER. */
	OTHER,
	/** The UNKNOWN. */
	UNKNOWN,
	/** The DESIG n_ defect. */
	DESIGN_DEFECT,
	/** The BACKPORT. */
	BACKPORT,
	/** The CLEANUP. */
	CLEANUP,
	/** The IMPROVEMENT. */
	IMPROVEMENT,
	/** The REFACTORING. */
	REFACTORING,
	/** The SPEC. */
	SPEC,
	/** The DOCUMENTATION. */
	DOCUMENTATION,
	/** The BUIL d_ system. */
	BUILD_SYSTEM;
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
	}
}
