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
package org.mozkito.utilities.loading.classpath.exceptions;

/**
 * The Class GenericClassPathException.
 */
public class GenericClassPathException extends RuntimeException {
	
	/**
     * 
     */
	private static final long serialVersionUID = -8441705121804248631L;
	
	/**
	 * Instantiates a new generic class path exception.
	 */
	public GenericClassPathException() {
		
	}
	
	/**
	 * Instantiates a new generic class path exception.
	 * 
	 * @param message
	 *            the message
	 */
	public GenericClassPathException(final String message) {
		super(message);
		
	}
	
	/**
	 * Instantiates a new generic class path exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public GenericClassPathException(final String message, final Throwable cause) {
		super(message, cause);
		
	}
	
	/**
	 * Instantiates a new generic class path exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param enableSuppression
	 *            the enable suppression
	 * @param writableStackTrace
	 *            the writable stack trace
	 */
	public GenericClassPathException(final String message, final Throwable cause, final boolean enableSuppression,
	        final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}
	
	/**
	 * Instantiates a new generic class path exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public GenericClassPathException(final Throwable cause) {
		super(cause);
		
	}
}
