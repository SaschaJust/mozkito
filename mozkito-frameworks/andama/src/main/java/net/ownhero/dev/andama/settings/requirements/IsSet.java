/**
 * 
 */
package net.ownhero.dev.andama.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaArgumentInterface;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IsSet extends Requirement {
	
	private final AndamaArgument<?> argument;
	
	/**
	 * @param argument
	 */
	public IsSet(@NotNull final AndamaArgument<?> argument) {
		try {
			this.argument = argument;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<AndamaArgumentInterface<?>> getDependencies() {
		HashSet<AndamaArgumentInterface<?>> dependencies = new HashSet<AndamaArgumentInterface<?>>();
		try {
			dependencies.add(this.argument);
			
			return dependencies;
		} finally {
			Condition.notNull(dependencies, "Dependency values may never be null.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		return this.argument.isInitialized()
		                                    ? null
		                                    : new LinkedList<Requirement>() {
			                                    
			                                    private static final long serialVersionUID = 1L;
			                                    
			                                    {
				                                    add(IsSet.this);
			                                    }
		                                    };
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		boolean ret = this.argument.isInitialized();
		ret = ret && this.argument.wasSet();
		return ret;
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
