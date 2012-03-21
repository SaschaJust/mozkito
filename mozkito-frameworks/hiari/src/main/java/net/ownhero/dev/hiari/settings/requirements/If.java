/**
 * 
 */
package net.ownhero.dev.hiari.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class IsSet.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class If extends Requirement {
	
	/** The argument. */
	private final IOptions<?, ?> option;
	
	/**
	 * Instantiates a new checks if is set.
	 * 
	 * @param option
	 *            the argument
	 */
	public If(@NotNull final IOptions<?, ?> option) {
		try {
			this.option = option;
		} finally {
			Condition.notNull(this.option, "The referring argument in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * Gets the argument.
	 * 
	 * @return the argument
	 */
	public final IOptions<?, ?> getArgumentSet() {
		return this.option;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<IOptions<?, ?>> getDependencies() {
		final HashSet<IOptions<?, ?>> dependencies = new HashSet<IOptions<?, ?>>();
		
		try {
			dependencies.add(this.option);
			
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
		return required()
		                 ? null
		                 : new LinkedList<Requirement>() {
			                 
			                 private static final long serialVersionUID = 1L;
			                 
			                 {
				                 add(If.this);
			                 }
		                 };
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		return this.option.getArgumentSet().getSettings().getProperty(this.option.getTag()) != null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(ISSET: " + this.option.getName() + ")";
	}
	
}
