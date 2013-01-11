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
package net.ownhero.dev.ioda.exceptions;

/**
 * The Class MIMETypeDeterminationException.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MIMETypeDeterminationException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7912987273535001426L;
	
	/**
	 * Instantiates a new mIME type determination exception.
	 */
	public MIMETypeDeterminationException() {
	}
	
	/**
	 * Instantiates a new mIME type determination exception.
	 *
	 * @param arg0 the arg0
	 */
	public MIMETypeDeterminationException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * Instantiates a new mIME type determination exception.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 */
	public MIMETypeDeterminationException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * Instantiates a new mIME type determination exception.
	 *
	 * @param arg0 the arg0
	 */
	public MIMETypeDeterminationException(final Throwable arg0) {
		super(arg0);
	}
	
}
