/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Annotated extends Serializable {
	
	public Collection<Annotated> saveFirst();
}
