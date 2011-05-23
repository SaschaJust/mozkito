/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.strategies;

import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingStrategy extends Registered {
	
	/**
	 * @param mapping
	 * @return
	 */
	public abstract RCSBugMapping map(RCSBugMapping mapping);
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.register.Registered#singleton()
	 */
	@Override
	public boolean singleton() {
		return true;
	}
}
