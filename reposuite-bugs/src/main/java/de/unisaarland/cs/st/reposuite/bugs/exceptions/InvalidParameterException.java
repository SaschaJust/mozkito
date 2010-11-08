/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class InvalidParameterException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -3009789262305389991L;
	
	/**
	 * 
	 */
	public InvalidParameterException() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public InvalidParameterException(final String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidParameterException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public InvalidParameterException(final Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
}
