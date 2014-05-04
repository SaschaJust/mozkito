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
 * The Class TypeMismatchException.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TypeMismatchException extends DatabaseException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8916535497954789354L;
	
	/**
	 * Instantiates a new type mismatch exception.
	 */
	public TypeMismatchException() {
		super();
		
	}
	
	/**
	 * Instantiates a new type mismatch exception.
	 * 
	 * @param message
	 *            the message
	 */
	public TypeMismatchException(final String message) {
		super(message);
		
	}
	
	/**
	 * Instantiates a new type mismatch exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TypeMismatchException(final String message, final Throwable cause) {
		super(message, cause);
		
	}
	
	/**
	 * Instantiates a new type mismatch exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param previous
	 *            the previous
	 */
	public TypeMismatchException(final String message, final Throwable cause, final Throwable... previous) {
		super(message, cause, previous);
		
	}
	
	/**
	 * Instantiates a new type mismatch exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public TypeMismatchException(final Throwable cause) {
		super(cause);
		
	}
	
}
