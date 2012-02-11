/**
 * 
 */
package net.ownhero.dev.andama.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.Argument;
import net.ownhero.dev.andama.settings.IArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IsRequired extends Requirement {
	
	private final Argument<?> argument;
	
	/**
	 * @param argument
	 */
	public IsRequired(@NotNull final Argument<?> argument) {
		try {
			this.argument = argument;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		return this.argument.getRequirements().required();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<IArgument<?>> getDependencies() {
		HashSet<IArgument<?>> dependencies = new HashSet<IArgument<?>>();
		try {
			dependencies.add(this.argument);
			
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
	public List<Requirement> getMissingRequirements() {
		return required()
		              ? null
		              : new LinkedList<Requirement>() {
			              
			              private static final long serialVersionUID = 1L;
			              
			              {
				              add(IsRequired.this);
			              }
		              };
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(ISSET: " + this.argument.getName() + ")";
	}
	
}
