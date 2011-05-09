/**
 * 
 */
package net.ownhero.dev.ioda.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class FilePermissionException extends Exception {
	
	private static final long serialVersionUID = 5734718527829761034L;
	
	/**
	 * 
	 */
	public FilePermissionException() {
	}
	
	/**
	 * @param arg0
	 */
	public FilePermissionException(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public FilePermissionException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public FilePermissionException(final Throwable arg0) {
		super(arg0);
	}
	
}
