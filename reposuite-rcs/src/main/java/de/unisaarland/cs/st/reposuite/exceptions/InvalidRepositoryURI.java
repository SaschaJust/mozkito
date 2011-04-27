/**
 * 
 */
package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class InvalidRepositoryURI extends Exception {
	
	private static final long serialVersionUID = 1215614419586597882L;
	
	public InvalidRepositoryURI(String message) {
		super(message);
	}
	
}
