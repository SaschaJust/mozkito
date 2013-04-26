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

package org.mozkito.infozilla.model.stacktrace;

import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class StacktraceEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class StacktraceEntry {
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(StacktraceEntry.class);
	}
	
	/**
	 * Gets the class name.
	 * 
	 * @return the class name
	 */
	public abstract String getEntryClassName();
	
	/**
	 * Gets the file name.
	 * 
	 * @return the file name
	 */
	public abstract String getFileName();
	
	/**
	 * Gets the line number.
	 * 
	 * @return the line number
	 */
	public abstract String getLineNumber();
	
	/**
	 * Gets the method name.
	 * 
	 * @return the method name
	 */
	public abstract String getMethodName();
}
