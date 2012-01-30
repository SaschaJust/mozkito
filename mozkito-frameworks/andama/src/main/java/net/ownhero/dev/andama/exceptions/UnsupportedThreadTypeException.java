/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UnsupportedThreadTypeException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7312025881887963041L;
	
	/**
	 * 
	 */
	public UnsupportedThreadTypeException() {
		super();
	}
	
	/**
	 * @param arg0
	 */
	public UnsupportedThreadTypeException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public UnsupportedThreadTypeException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public UnsupportedThreadTypeException(final Throwable arg0) {
		super(arg0);
	}
	
}
