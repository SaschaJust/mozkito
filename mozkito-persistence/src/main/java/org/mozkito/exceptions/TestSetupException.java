/**
 * 
 */
package org.mozkito.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TestSetupException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -6191017560383466751L;
	
	/**
	 * 
	 */
	public TestSetupException() {
	}
	
	/**
	 * @param message
	 */
	public TestSetupException(final String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public TestSetupException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param cause
	 */
	public TestSetupException(final Throwable cause) {
		super(cause);
	}
	
}
