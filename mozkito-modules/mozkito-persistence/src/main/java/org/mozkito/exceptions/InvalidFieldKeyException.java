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

import org.mozkito.persistence.FieldKey;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class InvalidFieldKeyException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -1509064949713116084L;
	
	/**
	 * @param key
	 * 
	 */
	public InvalidFieldKeyException(final FieldKey key) {
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
	 * @param message
	 */
	public InvalidFieldKeyException(final FieldKey key, final String message) {
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
	 * @param message
	 * @param cause
	 */
	public InvalidFieldKeyException(final FieldKey key, final String message, final Throwable cause) {
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
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidFieldKeyException(final FieldKey key, final String message, final Throwable cause,
	        final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
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
	 * @param cause
	 */
	public InvalidFieldKeyException(final FieldKey key, final Throwable cause) {
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
	
}
