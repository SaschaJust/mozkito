/**
 * 
 */
package net.ownhero.dev.ioda.interfaces;

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
