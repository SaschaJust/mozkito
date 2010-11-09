/**
 * 
 */
package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PoolLimitExceededException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1373800773427935706L;
	
	/**
	 * 
	 */
	public PoolLimitExceededException() {
	}
	
	/**
	 * @param arg0
	 */
	public PoolLimitExceededException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public PoolLimitExceededException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public PoolLimitExceededException(final Throwable arg0) {
		super(arg0);
	}
	
}
