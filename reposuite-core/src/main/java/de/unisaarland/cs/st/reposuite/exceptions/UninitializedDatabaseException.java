/**
 * 
 */
package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UninitializedDatabaseException extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -2020013706740319545L;
	
	/**
	 * 
	 */
	public UninitializedDatabaseException() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public UninitializedDatabaseException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public UninitializedDatabaseException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public UninitializedDatabaseException(final Throwable arg0) {
		super(arg0);
	}
	
}
