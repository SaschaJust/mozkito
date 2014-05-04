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

package org.mozkito.database.exceptions;

/**
 * The Class NoActiveTransactionException.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class NoActiveTransactionException extends DatabaseException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4932167329304627726L;
	
	/**
	 * Instantiates a new no active transaction exception.
	 */
	public NoActiveTransactionException() {
		super();
	}
	
	/**
	 * Instantiates a new no active transaction exception.
	 * 
	 * @param message
	 *            the message
	 */
	public NoActiveTransactionException(final String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new no active transaction exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public NoActiveTransactionException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Instantiates a new no active transaction exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public NoActiveTransactionException(final Throwable cause) {
		super(cause);
	}
	
}
