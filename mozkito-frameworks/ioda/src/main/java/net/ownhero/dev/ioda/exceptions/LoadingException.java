/**
 * 
 */
package net.ownhero.dev.ioda.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LoadingException extends Exception {
	
	private static final long serialVersionUID = 2408941187399893482L;
	
	/**
	 * 
	 */
	public LoadingException() {
	}
	
	/**
	 * @param arg0
	 */
	public LoadingException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public LoadingException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public LoadingException(final Throwable arg0) {
		super(arg0);
	}
	
}
