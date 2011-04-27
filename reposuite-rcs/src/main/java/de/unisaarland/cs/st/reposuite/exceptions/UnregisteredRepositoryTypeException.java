package de.unisaarland.cs.st.reposuite.exceptions;

/**
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UnregisteredRepositoryTypeException extends Exception {
	
	private static final long serialVersionUID = -7392389210139073113L;
	
	public UnregisteredRepositoryTypeException(String string) {
		super(string);
	}
	
}
