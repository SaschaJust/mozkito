/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
/**
 * 
 */
package org.mozkito.issues.exceptions;

/**
 * The Class InvalidParameterException.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class InvalidParameterException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3009789262305389991L;
	
	/**
	 * Instantiates a new invalid parameter exception.
	 */
	public InvalidParameterException() {
		
	}
	
	/**
	 * Instantiates a new invalid parameter exception.
	 *
	 * @param arg0 the arg0
	 */
	public InvalidParameterException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * Instantiates a new invalid parameter exception.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 */
	public InvalidParameterException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * Instantiates a new invalid parameter exception.
	 *
	 * @param arg0 the arg0
	 */
	public InvalidParameterException(final Throwable arg0) {
		super(arg0);
	}
	
}
