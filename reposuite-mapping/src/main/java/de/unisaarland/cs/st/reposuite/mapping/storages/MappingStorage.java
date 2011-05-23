/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.storages;

import de.unisaarland.cs.st.reposuite.mapping.register.Registered;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingStorage extends Registered {
	
	/**
	 * @param util
	 */
	public abstract void loadData(final PersistenceUtil util);
	
}
