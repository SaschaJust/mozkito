/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Collection;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Annotated {
	
	public Collection<Annotated> saveFirst();
}
