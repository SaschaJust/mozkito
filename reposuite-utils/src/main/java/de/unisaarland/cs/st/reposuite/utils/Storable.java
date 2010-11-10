/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.io.Serializable;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Storable extends Serializable {
	
	public String getFilename();
}
