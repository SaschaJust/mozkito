/**
 * 
 */
package net.ownhero.dev.ioda.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class FetchException extends Exception {
	
	private static final long serialVersionUID = 3871783918581993676L;
	
	/**
	 * 
	 */
	public FetchException() {
	}
	
	/**
	 * @param arg0
	 */
	public FetchException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public FetchException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public FetchException(final Throwable arg0) {
		super(arg0);
	}
	
}
