/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UninitializedDatabaseException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -2020013706740319545L;
	
	/**
	 * 
	 */
	public UninitializedDatabaseException() {
	}
	
	/**
	 * @param arg0
	 */
	public UninitializedDatabaseException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public UninitializedDatabaseException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public UninitializedDatabaseException(final Throwable arg0) {
		super(arg0);
	}
	
}
