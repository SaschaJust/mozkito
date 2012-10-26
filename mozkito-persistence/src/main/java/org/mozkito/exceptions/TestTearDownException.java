/**
 * 
 */
package org.mozkito.exceptions;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class TestTearDownException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -6191017560383466751L;
	
	/**
	 * 
	 */
	public TestTearDownException() {
	}
	
	/**
	 * @param message
	 */
	public TestTearDownException(final String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public TestTearDownException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param cause
	 */
	public TestTearDownException(final Throwable cause) {
		super(cause);
	}
	
}
