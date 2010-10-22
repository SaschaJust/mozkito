/**
 * 
 */
package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UnsupportedProtocolType extends Exception {
	
	private final String      message;
	
	/**
     * 
     */
	private static final long serialVersionUID = 4200014637263024209L;
	
	public UnsupportedProtocolType(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
	
}
