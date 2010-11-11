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
	
	public boolean cached();
	
	public String getFilename();
	
	public void setCached(final String filename);
}
