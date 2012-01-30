/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Shutdown extends Error {
	
	private static final long serialVersionUID = 3886313778786996803L;
	
	/**
	 * 
	 */
	public Shutdown() {
		super();
	}
	
	/**
	 * @param arg0
	 */
	public Shutdown(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public Shutdown(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public Shutdown(final Throwable arg0) {
		super(arg0);
	}
	
}
