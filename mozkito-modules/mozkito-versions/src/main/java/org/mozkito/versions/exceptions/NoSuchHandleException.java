/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
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
package org.mozkito.versions.exceptions;

/**
 * The Class NoSuchHandleException.
 */
public class NoSuchHandleException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7902755533383802066L;
	
	/**
	 * Create exception using format string.
	 * 
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the no such handle exception
	 */
	public static NoSuchHandleException format(final String formatString,
	                                           final Object... args) {
		return new NoSuchHandleException(String.format(formatString, args));
	}
	
	/**
	 * Create exception using format string.
	 * 
	 * @param cause
	 *            the cause
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the no such handle exception
	 */
	public static NoSuchHandleException format(final Throwable cause,
	                                           final String formatString,
	                                           final Object... args) {
		return new NoSuchHandleException(String.format(formatString, args), cause);
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 */
	public NoSuchHandleException() {
		super();
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 * 
	 * @param message
	 *            the message
	 */
	public NoSuchHandleException(final String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public NoSuchHandleException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Instantiates a new no such handle exception.
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
	public NoSuchHandleException(final String message, final Throwable cause, final boolean enableSuppression,
	        final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/**
	 * Instantiates a new no such handle exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public NoSuchHandleException(final Throwable cause) {
		super(cause);
	}
	
}
