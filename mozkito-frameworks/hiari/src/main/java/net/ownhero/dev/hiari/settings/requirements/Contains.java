/**
 * 
 */
package net.ownhero.dev.hiari.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.IArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class Contains.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Contains extends Requirement {
	
	/** The list argument. */
	private IOptions<?, ?>  listOption = null;
	
	/** The set argument. */
	private IOptions<?, ?>  setOption  = null;
	
	/** The depender. */
	private IArgument<?, ?> depender   = null;
	
	/**
	 * Instantiates a new contains.
	 * 
	 * @param argument
	 *            the argument
	 * @param depender
	 *            the depender
	 */
	Contains(@NotNull final ListArgument.Options option, @NotNull final IArgument<?, ?> depender) {
		try {
			this.listOption = option;
			this.depender = depender;
		} finally {
			Condition.notNull(this.listOption, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	/**
	 * Instantiates a new contains.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 */
	Contains(@NotNull final ListArgument.Options option, @NotNull final String value) {
		try {
			this.listOption = option;
		} finally {
			Condition.notNull(this.listOption, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	/**
	 * Instantiates a new contains.
	 * 
	 * @param argument
	 *            the argument
	 * @param depender
	 *            the depender
	 */
	Contains(@NotNull final SetArgument.Options option, @NotNull final IArgument<?, ?> depender) {
		try {
			this.setOption = option;
			this.depender = depender;
		} finally {
			Condition.notNull(this.listOption, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	/**
	 * Instantiates a new contains.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 */
	Contains(@NotNull final SetArgument.Options option, @NotNull final String value) {
		try {
			this.setOption = option;
		} finally {
			Condition.notNull(this.listOption, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<IOptions<?, ?>> getDependencies() {
		final Set<IOptions<?, ?>> dependencies = new HashSet<IOptions<?, ?>>();
		try {
			dependencies.add(this.listOption != null
			                                        ? this.listOption
			                                        : this.setOption);
			
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
		// if (this.listOption != null) {
		// return this.listOption.required()
		// && this.listOption.getValue().contains(this.depender != null
		// ? this.depender.getName()
		// : this.value);
		// } else if (this.setOption != null) {
		// return this.setOption.required()
		// && this.setOption.getValue().contains(this.depender != null
		// ? this.depender.getName()
		// : this.value);
		// } else {
		// return false;
		// }
		// TODO FIXME IMPLEMENT THIS
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ("(∈ " + this.listOption) != null
		                                          ? this.listOption.getName()
		                                          : this.setOption.getName() + ".value() )";
	};
	
}
