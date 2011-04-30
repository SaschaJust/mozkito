/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class PreparedQuery<T> {
	
	public abstract Criteria<T> buildCriteria(Object... args);
	
	public abstract String getIdentifier();
}
