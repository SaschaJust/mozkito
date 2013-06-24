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
 * The Class NotComparableException.
 */
public class NotComparableException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -7615541969242229084L;
	
	/**
	 * Format.
	 * 
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the not comparable exception
	 */
	public static NotComparableException format(final String formatString,
	                                            final Object... args) {
		return new NotComparableException(String.format(formatString, args));
	}
	
	/**
	 * Format.
	 * 
	 * @param throwable
	 *            the throwable
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the not comparable exception
	 */
	public static NotComparableException format(final Throwable throwable,
	                                            final String formatString,
	                                            final Object... args) {
		return new NotComparableException(String.format(formatString, args), throwable);
	}
	
	/**
	 * Instantiates a new not comparable exception.
	 */
	public NotComparableException() {
		super();
	}
	
	/**
	 * Instantiates a new not comparable exception.
	 * 
	 * @param message
	 *            the message
	 */
	public NotComparableException(final String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new not comparable exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public NotComparableException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Instantiates a new not comparable exception.
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
	public NotComparableException(final String message, final Throwable cause, final boolean enableSuppression,
	        final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/**
	 * Instantiates a new not comparable exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public NotComparableException(final Throwable cause) {
		super(cause);
	}
	
}
