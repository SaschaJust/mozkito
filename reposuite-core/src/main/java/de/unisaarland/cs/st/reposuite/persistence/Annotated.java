/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Collection;

import javax.persistence.Transient;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Annotated {
	
	@Transient
	public Collection<Annotated> getSaveFirst();
}
