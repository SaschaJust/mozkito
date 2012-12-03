/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.exceptions;

/**
 * The Class InvalidRepositoryURI.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class InvalidRepositoryURI extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1215614419586597882L;
	
	/**
	 * Instantiates a new invalid repository uri.
	 * 
	 * @param message
	 *            the message
	 */
	public InvalidRepositoryURI(final String message) {
		super(message);
	}
	
}
