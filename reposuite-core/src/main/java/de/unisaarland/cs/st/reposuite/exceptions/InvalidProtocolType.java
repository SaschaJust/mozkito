/**
 * 
 */
package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class InvalidProtocolType extends Exception {
	
	/**
     * 
     */
	private static final long serialVersionUID = -3141953030979198993L;
	private final String      message;
	
	public InvalidProtocolType(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
	
}
