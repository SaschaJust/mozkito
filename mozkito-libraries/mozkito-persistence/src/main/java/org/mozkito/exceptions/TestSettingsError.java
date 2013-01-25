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
package org.mozkito.exceptions;

/**
 * The Class TestSettingsError.
 */
public class TestSettingsError extends AssertionError {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3580812982570823142L;
	
	/**
	 * Instantiates a new test settings error.
	 */
	public TestSettingsError() {
		super();
	}
	
	/**
	 * Instantiates a new test settings error.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public TestSettingsError(final String arg0) {
		super(arg0);
	}
	
	/**
	 * Instantiates a new test settings error.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public TestSettingsError(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * Instantiates a new test settings error.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public TestSettingsError(final Throwable arg0) {
		super(arg0);
	}
	
}
