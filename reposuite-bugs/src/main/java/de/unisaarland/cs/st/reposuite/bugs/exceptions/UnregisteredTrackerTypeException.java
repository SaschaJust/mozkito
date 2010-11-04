/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.exceptions;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class UnregisteredTrackerTypeException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5560859882515414482L;
	
	/**
	 * 
	 */
	public UnregisteredTrackerTypeException() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public UnregisteredTrackerTypeException(final String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public UnregisteredTrackerTypeException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public UnregisteredTrackerTypeException(final Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
}
