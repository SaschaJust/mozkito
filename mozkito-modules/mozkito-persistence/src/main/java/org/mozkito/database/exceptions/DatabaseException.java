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
 * The Class DatabaseException.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DatabaseException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1040430524675017365L;
	
	/** The previous. */
	private Throwable[]       previous         = null;
	
	/**
	 * Instantiates a new database exception.
	 */
	public DatabaseException() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Instantiates a new database exception.
	 * 
	 * @param message
	 *            the message
	 */
	public DatabaseException(final String message) {
		super(message);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Instantiates a new database exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public DatabaseException(final String message, final Throwable cause) {
		super(message, cause);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Instantiates a new database exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param previous
	 *            the previous
	 */
	public DatabaseException(final String message, final Throwable cause, final Throwable... previous) {
		super(message, cause);
		this.previous = previous;
	}
	
	/**
	 * Instantiates a new database exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public DatabaseException(final Throwable cause) {
		super(cause);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Previous.
	 * 
	 * @return the throwable[]
	 */
	public Throwable[] previous() {
		return this.previous;
	}
}
