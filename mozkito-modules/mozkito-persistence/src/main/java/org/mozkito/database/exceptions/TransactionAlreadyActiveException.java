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
 * The Class TransactionAlreadyActiveException.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TransactionAlreadyActiveException extends DatabaseException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8242545560692613347L;
	
	/**
	 * Instantiates a new transaction already active exception.
	 */
	public TransactionAlreadyActiveException() {
		super();
	}
	
	/**
	 * Instantiates a new transaction already active exception.
	 * 
	 * @param message
	 *            the message
	 */
	public TransactionAlreadyActiveException(final String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new transaction already active exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TransactionAlreadyActiveException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Instantiates a new transaction already active exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param previous
	 *            the previous
	 */
	public TransactionAlreadyActiveException(final String message, final Throwable cause, final Throwable... previous) {
		super(message, cause, previous);
	}
	
	/**
	 * Instantiates a new transaction already active exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public TransactionAlreadyActiveException(final Throwable cause) {
		super(cause);
	}
	
}
