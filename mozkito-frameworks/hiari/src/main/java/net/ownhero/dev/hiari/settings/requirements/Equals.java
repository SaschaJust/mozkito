/**
 * 
 */
package net.ownhero.dev.hiari.settings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class Equals.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Equals extends Requirement {
	
	/** The argument. */
	private final IOptions<?, ?> argument;
	
	/** The depender. */
	private IOptions<?, ?>       depender;
	
	/** The value. */
	private Object               value;
	
	/**
	 * Instantiates a new equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 */
	Equals(@NotNull final BooleanArgument.Options argument, @NotNull final boolean value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * Instantiates a new equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 */
	Equals(@NotNull final DoubleArgument.Options argument, @NotNull final double value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * Instantiates a new equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 */
	Equals(@NotNull final EnumArgument.Options<?> argument, @NotNull final Enum<?> value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * Instantiates a new equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 */
	Equals(@NotNull final LongArgument.Options argument, @NotNull final long value) {
		try {
			this.argument = argument;
			this.value = value;
		} finally {
			Condition.notNull(this.argument, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "The value set in the constructor in %s may never be null.", getHandle());
		}
	}
	
	/**
	 * Instantiates a new equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param depender
	 *            the depender
	 */
	Equals(@NotNull final StringArgument.Options argument, @NotNull final IOptions<?, ?> depender) {
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
	 * Instantiates a new equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 */
	Equals(@NotNull final StringArgument.Options argument, @NotNull final String value) {
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
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<IOptions<?, ?>> getDependencies() {
		final Set<IOptions<?, ?>> dependencies = new HashSet<IOptions<?, ?>>();
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
	public List<Requirement> getRequiredDependencies() {
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
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean required() {
		final String property = this.argument.getSettings().getProperty(this.argument.getTag());
		
		if (property == null) {
			return false;
		}
		
		if (this.depender != null) {
			final String compareTo = this.argument.getSettings().getProperty(this.depender.getTag());
			return (compareTo != null) && property.equals(compareTo);
		}
		Condition.notNull(this.value, "Field '%s' in '%s'.", "value", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		return property.equals(this.value);
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
