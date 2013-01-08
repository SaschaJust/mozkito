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

import net.ownhero.dev.ioda.JavaUtils;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class RepositoryOperationException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = 2758683900402585036L;
	
	/**
	 * 
	 */
	public RepositoryOperationException() {
		
	}
	
	/**
	 * @param message
	 */
	public RepositoryOperationException(final String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public RepositoryOperationException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RepositoryOperationException(final String message, final Throwable cause, final boolean enableSuppression,
	        final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/**
	 * @param cause
	 */
	public RepositoryOperationException(final Throwable cause) {
		super(cause);
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(RepositoryOperationException.class);
	}
}
