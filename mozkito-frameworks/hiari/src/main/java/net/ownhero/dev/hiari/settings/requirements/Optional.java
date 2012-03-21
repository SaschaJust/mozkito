/**
 * 
 */
package net.ownhero.dev.hiari.settings.requirements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class Optional.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Optional extends Requirement {
	
	/**
	 * Instantiates a new optional.
	 */
	Optional() {
	};
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<IOptions<?, ?>> getDependencies() {
		final Set<IOptions<?, ?>> dependencies = new HashSet<IOptions<?, ?>>();
		try {
			return dependencies;
		} finally {
			Condition.notNull(dependencies, "Dependency values may never be null.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getRequiredDependencies() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(optional)";
	}
}
