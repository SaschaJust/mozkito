/**
 * 
 */
package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class InvalidProtocolType extends Exception {
	
	private static final long serialVersionUID = -3141953030979198993L;
	
	/**
	 * 
	 */
	public InvalidProtocolType() {
		super();
	}
	
	/**
	 * @param arg0
	 */
	public InvalidProtocolType(final String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidProtocolType(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public InvalidProtocolType(final Throwable arg0) {
		super(arg0);
	}
	
}
