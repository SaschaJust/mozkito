/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MIMETypeDeterminationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7912987273535001426L;
	
	/**
	 * 
	 */
	public MIMETypeDeterminationException() {
	}
	
	/**
	 * @param arg0
	 */
	public MIMETypeDeterminationException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public MIMETypeDeterminationException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public MIMETypeDeterminationException(final Throwable arg0) {
		super(arg0);
	}
	
}
