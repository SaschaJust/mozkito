/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class EncodingDeterminationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8649305536003056411L;
	
	/**
	 * 
	 */
	public EncodingDeterminationException() {
	}
	
	/**
	 * @param arg0
	 */
	public EncodingDeterminationException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public EncodingDeterminationException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public EncodingDeterminationException(final Throwable arg0) {
		super(arg0);
	}
	
}
