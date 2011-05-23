/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.splitters;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.register.Registered;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingSplitter extends Registered {
	
	/**
	 * @return
	 */
	public abstract List<Annotated> process();
	
}
