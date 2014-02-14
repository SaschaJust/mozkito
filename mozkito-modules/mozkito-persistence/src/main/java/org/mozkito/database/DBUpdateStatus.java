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

package org.mozkito.database;

import java.sql.SQLException;

/**
 * The Enum DBUpdateStatus.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum DBUpdateStatus {
	
	/** The success. */
	SUCCESS,
	/** The failure. */
	FAILURE;
	
	/**
	 * Failure.
	 * 
	 * @param e
	 *            the e
	 * @return the dB update status
	 */
	public static DBUpdateStatus fromException(final SQLException e) {
		return FAILURE.setException(e);
	}
	
	/** The exception. */
	private Exception exception = null;
	
	/**
	 * Instantiates a new dB update status.
	 */
	private DBUpdateStatus() {
		this.exception = null;
	}
	
	/**
	 * Gets the exception.
	 * 
	 * @return the exception
	 */
	public Exception getException() {
		return this.exception;
	}
	
	/**
	 * Exception.
	 * 
	 * @param e
	 *            the e
	 * @return the dB update status
	 */
	private DBUpdateStatus setException(final Exception e) {
		this.exception = e;
		return this;
	}
}
