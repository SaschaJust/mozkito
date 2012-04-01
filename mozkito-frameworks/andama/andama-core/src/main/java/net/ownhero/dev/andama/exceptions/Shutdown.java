/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

/**
 * The Class Shutdown.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Shutdown extends Error {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3886313778786996803L;
	
	/**
	 * Instantiates a new shutdown.
	 */
	public Shutdown() {
		super();
	}
	
	/**
	 * Instantiates a new shutdown.
	 *
	 * @param arg0 the arg0
	 */
	public Shutdown(final String arg0) {
		super(arg0);
	}
	
	/**
	 * Instantiates a new shutdown.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 */
	public Shutdown(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * Instantiates a new shutdown.
	 *
	 * @param arg0 the arg0
	 */
	public Shutdown(final Throwable arg0) {
		super(arg0);
	}
	
}
