/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.filters;

import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingFilter extends Registered {
	
	/**
	 * @param transaction
	 * @param report
	 * @param score
	 */
	@NoneNull
	public abstract Set<MappingFilter> filter(final RCSBugMapping mapping,
	                                          Set<MappingFilter> triggeringFilters);
	
}
