/**
 * 
 */
package net.ownhero.dev.andama.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.IArgument;
import net.ownhero.dev.andama.settings.arguments.ListArgument;
import net.ownhero.dev.andama.settings.arguments.SetArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Contains extends Requirement {
	
	private ListArgument listArgument = null;
	
	private SetArgument  setArgument  = null;
	private IArgument<?> depender     = null;
	private String       value        = null;
	
	public Contains(@NotNull final ListArgument argument, @NotNull final IArgument<?> depender) {
		try {
			this.listArgument = argument;
			this.depender = depender;
		} finally {
			Condition.notNull(this.listArgument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	public Contains(@NotNull final ListArgument argument, @NotNull final String value) {
		try {
			this.listArgument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.listArgument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	public Contains(@NotNull final SetArgument argument, @NotNull final IArgument<?> depender) {
		try {
			this.setArgument = argument;
			this.depender = depender;
		} finally {
			Condition.notNull(this.listArgument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	public Contains(@NotNull final SetArgument argument, @NotNull final String value) {
		try {
			this.setArgument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.listArgument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<IArgument<?>> getDependencies() {
		final HashSet<IArgument<?>> dependencies = new HashSet<IArgument<?>>();
		try {
			dependencies.add(this.listArgument != null
			                                          ? this.listArgument
			                                          : this.setArgument);
			
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
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		if (this.listArgument != null) {
			return this.listArgument.isInitialized()
			        && this.listArgument.getValue().contains(this.depender != null
			                                                                      ? this.depender.getName()
			                                                                      : this.value);
		} else if (this.setArgument != null) {
			return this.setArgument.isInitialized()
			        && this.setArgument.getValue().contains(this.depender != null
			                                                                     ? this.depender.getName()
			                                                                     : this.value);
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ("(∈ " + this.listArgument) != null
		                                          ? this.listArgument.getName()
		                                          : this.setArgument.getName() + ".value() )";
	};
	
}
