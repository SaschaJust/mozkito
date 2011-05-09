/**
 * 
 */
package net.ownhero.dev.ioda.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class StoringException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -7315406109072825305L;
	
	/**
	 * 
	 */
	public StoringException() {
	}
	
	/**
	 * @param arg0
	 */
	public StoringException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public StoringException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public StoringException(final Throwable arg0) {
		super(arg0);
	}
	
}
