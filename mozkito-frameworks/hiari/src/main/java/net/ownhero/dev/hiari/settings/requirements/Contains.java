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

import org.apache.commons.lang.ArrayUtils;

/**
 * The Class Contains.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Contains extends Requirement {
	
	/** The list argument. */
	private ListArgument.Options listOption = null;
	
	/** The set argument. */
	private SetArgument.Options  setOption  = null;
	
	/** The depender. */
	private IArgument<?, ?>      depender   = null;
	
	/** The value. */
	private String               value      = null;
	
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
			this.value = value;
		} finally {
			Condition.notNull(this.listOption, "The referring argument in %s may never be null.", getHandle());
			Condition.notNull(this.value, "Field '%s' in '%s'.", "value", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
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
			Condition.notNull(this.setOption, "Field '%s' in '%s'.", "setOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
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
			this.value = value;
		} finally {
			Condition.notNull(this.setOption, "Field '%s' in '%s'.", "setOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.value, "Field '%s' in '%s'.", "value", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
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
		String[] split = null;
		String delimiter = null;
		String property = null;
		
		if (this.listOption != null) {
			delimiter = this.listOption.getDelimiter();
			property = this.listOption.getSettings().getProperty(this.listOption.getTag());
			
		} else {
			Condition.notNull(this.setOption, "Field '%s' in '%s'.", "setOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			delimiter = this.setOption.getDelimiter();
			property = this.setOption.getSettings().getProperty(this.setOption.getTag());
		}
		
		if (property == null) {
			return false;
		}
		
		Condition.notNull(property, "Local variable '%s' in '%s:%s'.", "property", getHandle(), "required"); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(delimiter, "Local variable '%s' in '%s:%s'.", "delimiter", getHandle(), "required"); //$NON-NLS-1$ //$NON-NLS-2$
		
		split = property.split(delimiter);
		
		Condition.notNull(split, "Local variable '%s' in '%s:%s'.", "split", getHandle(), "required"); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (this.depender != null) {
			return ArrayUtils.contains(split, this.depender.getName());
		}
		Condition.notNull(this.value, "Field '%s' in '%s'.", "value", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		return ArrayUtils.contains(split, this.value);
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
