/**
 * 
 */
package net.ownhero.dev.hiari.settings.requirements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentOptions;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ISettings;
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
	If(@NotNull final IOptions<?, ?> option) {
		try {
			this.option = option;
		} finally {
			Condition.notNull(this.option, "The referring argument in %s may never be null.", getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public boolean check() {
		Condition.notNull(this.option, "Field '%s' in '%s'.", "option", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(this.option.getTag(), "Field '%s' in '%s'.", "option.getTag()", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		final ArgumentSet<?, ?> set = this.option.getArgumentSet();
		final ISettings settings = set.getSettings();
		Object defaultValue = null;
		if (this.option instanceof ArgumentOptions) {
			defaultValue = ((ArgumentOptions) this.option).getDefaultValue();
		}
		
		return (settings.getProperty(this.option.getTag()) != null) || (defaultValue != null);
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
	public List<Requirement> getFailedChecks() {
		return check()
		              ? new ArrayList<Requirement>(0)
		              : new LinkedList<Requirement>() {
			              
			              private static final long serialVersionUID = 1L;
			              
			              {
				              add(If.this);
			              }
		              };
	}
	
	/**
	 * Gets the argument.
	 * 
	 * @return the argument
	 */
	public final IOptions<?, ?> getOption() {
		// PRECONDITIONS
		
		try {
			return this.option;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.option, "Field '%s' in '%s'.", "option", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
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
