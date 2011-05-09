/**
 * 
 */
package net.ownhero.dev.ioda.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UnsupportedProtocolException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -5086626803073351561L;
	
	/**
	 * 
	 */
	public UnsupportedProtocolException() {
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
