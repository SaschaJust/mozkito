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
import net.ownhero.dev.andama.settings.arguments.BooleanArgument;
import net.ownhero.dev.andama.settings.arguments.DoubleArgument;
import net.ownhero.dev.andama.settings.arguments.EnumArgument;
import net.ownhero.dev.andama.settings.arguments.LongArgument;
import net.ownhero.dev.andama.settings.arguments.StringArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Equals extends Requirement {
	
	private final Argument<?>    argument;
	private IArgument<?> depender;
	private Object                     value;
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(@NotNull final BooleanArgument argument, @NotNull final boolean value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(@NotNull final DoubleArgument argument, @NotNull final double value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(@NotNull final EnumArgument<?> argument, @NotNull final Enum<?> value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(@NotNull final LongArgument argument, @NotNull final long value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * @param argument
	 * @param depender
	 */
	public Equals(@NotNull final StringArgument argument, @NotNull final IArgument<?> depender) {
		try {
			this.argument = argument;
			this.depender = depender;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.depender, "The depender argument set in the constructor in %s may never be null.",
			                  getHandle());
		}
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(@NotNull final StringArgument argument, @NotNull final String value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		if ((this.argument != null) && this.argument.isInitialized()) {
			if (this.depender != null) {
				return this.argument.getValue().equals(this.depender.getName());
			} else {
				return this.argument.getValue().equals(this.value);
			}
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
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
	 * @see
	 * net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		return required()
		              ? null
		              : new LinkedList<Requirement>() {
			              
			              private static final long serialVersionUID = 1L;
			              
			              {
				              add(Equals.this);
			              }
		              };
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.argument.getName() + ".value() = " + ((this.depender != null)
		                                                                               ? this.depender.getName()
		                                                                               : this.value) + ")";
	}
	
}
