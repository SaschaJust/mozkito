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
public class UnsupportedProtocolType extends Exception {
	
	private static final long serialVersionUID = 4200014637263024209L;
	
	/**
	 * 
	 */
	public UnsupportedProtocolType() {
		super();
	}
	
	/**
	 * @param message
	 */
	public UnsupportedProtocolType(final String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public UnsupportedProtocolType(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param cause
	 */
	public UnsupportedProtocolType(final Throwable cause) {
		super(cause);
	}
	
}
