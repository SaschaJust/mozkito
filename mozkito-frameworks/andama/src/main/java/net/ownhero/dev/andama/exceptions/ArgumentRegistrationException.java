/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ArgumentRegistrationException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = 1258725690431585182L;
	
	/**
	 * 
	 */
	public ArgumentRegistrationException() {
	}
	
	/**
	 * @param message
	 */
	public ArgumentRegistrationException(final String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public ArgumentRegistrationException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param cause
	 */
	public ArgumentRegistrationException(final Throwable cause) {
		super(cause);
	}
	
}
