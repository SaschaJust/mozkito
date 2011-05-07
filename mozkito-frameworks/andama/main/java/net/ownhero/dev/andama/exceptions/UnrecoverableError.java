/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UnrecoverableError extends Error {
	
	private static final long serialVersionUID = -8156028538555027087L;
	
	/**
     * 
     */
	public UnrecoverableError() {
		super();
	}
	
	/**
	 * @param arg0
	 */
	public UnrecoverableError(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public UnrecoverableError(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public UnrecoverableError(final Throwable arg0) {
		super(arg0);
	}
	
}
