/**
 * 
 */
package net.ownhero.dev.andama.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentInterface;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Contains extends Requirement {
	
	private final ListArgument               argument;
	private final AndamaArgumentInterface<?> depender;
	
	public Contains(@NotNull final ListArgument argument, @NotNull final AndamaArgumentInterface<?> depender) {
		try {
			this.argument = argument;
			this.depender = depender;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		if (this.argument != null) {
			return this.argument.isInitialized() && this.argument.getValue().contains(this.depender.getName());
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
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
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		return required()
		              ? null
		              : new LinkedList<Requirement>() {
			              
			              private static final long serialVersionUID = 1L;
			              
			              {
				              add(Contains.this);
			              }
		              };
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(∈ " + this.argument.getName() + ".value() )";
	};
	
}
