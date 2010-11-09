/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UnsupportedProtocolException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2560762830938974228L;
	
	/**
	 * 
	 */
	public UnsupportedProtocolException() {
		super();
	}
	
	/**
	 * @param arg0
	 */
	public UnsupportedProtocolException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public UnsupportedProtocolException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public UnsupportedProtocolException(final Throwable arg0) {
		super(arg0);
	}
	
}
