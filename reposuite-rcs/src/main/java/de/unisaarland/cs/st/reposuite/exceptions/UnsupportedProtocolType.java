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
