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
 * The Class TestTearDownException.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TestTearDownException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6191017560383466751L;
	
	/**
	 * Instantiates a new test tear down exception.
	 */
	public TestTearDownException() {
	}
	
	/**
	 * Instantiates a new test tear down exception.
	 * 
	 * @param message
	 *            the message
	 */
	public TestTearDownException(final String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new test tear down exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TestTearDownException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Instantiates a new test tear down exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public TestTearDownException(final Throwable cause) {
		super(cause);
	}
	
}
