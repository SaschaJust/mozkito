/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

/**
 * The Class UnsupportedThreadTypeException.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class UnsupportedThreadTypeException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7312025881887963041L;
	
	/**
	 * Instantiates a new unsupported thread type exception.
	 */
	public UnsupportedThreadTypeException() {
		super();
	}
	
	/**
	 * Instantiates a new unsupported thread type exception.
	 *
	 * @param arg0 the arg0
	 */
	public UnsupportedThreadTypeException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * Instantiates a new unsupported thread type exception.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 */
	public UnsupportedThreadTypeException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * Instantiates a new unsupported thread type exception.
	 *
	 * @param arg0 the arg0
	 */
	public UnsupportedThreadTypeException(final Throwable arg0) {
		super(arg0);
	}
	
}
