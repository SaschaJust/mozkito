/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.hiari.settings;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Optional;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class ArgumentSetConfigurator.
 * 
 * @param <T>
 *            the generic type
 * @param <X>
 *            the generic type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class ArgumentSetOptions<T, X extends ArgumentSet<T, ? extends ArgumentSetOptions<T, ?>>> implements
        IArgumentSetOptions<T, X> {
	
	/** The name. */
	private final String      name;
	
	/** The description. */
	private final String      description;
	
	/** The requirements. */
	private final Requirement requirements;
	/** The set. */
	private ArgumentSet<?, ?> set;
	
	/**
	 * Instantiates a new argument set configurator.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param requirements
	 *            the requirements
	 */
	@NoneNull
	public ArgumentSetOptions(final ArgumentSet<?, ?> argumentSet, final String name, final String description,
	        final Requirement requirements) {
		// PRECONDITIONS
		
		try {
			this.set = argumentSet;
			this.name = name;
			this.description = description;
			this.requirements = requirements;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.set, "Field '%s' in %s.", "argumentSet", getHandle());
			Condition.notNull(this.name, "Field '%s' in %s.", "name", getHandle());
			Condition.notNull(this.description, "Field '%s' in %s.", "description", getHandle());
			Condition.notNull(this.requirements, "Field '%s' in %s.", "requirements", getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(final IOptions<?, ?> arg0) {
		// PRECONDITIONS
		
		if (this == arg0) {
			return 0;
		} else if (equals(arg0)) {
			return 0;
		}
		
		return getTag().compareTo(arg0.getTag());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IOptions#getAdditionalHelpString()
	 */
	@Override
	public String getAdditionalHelpString() {
		// PRECONDITIONS
		
		try {
			return "";
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the argument set.
	 * 
	 * @return the set
	 */
	@Override
	public final ArgumentSet<?, ?> getArgumentSet() {
		// PRECONDITIONS
		
		try {
			return this.set;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.set, "Field '%s' in '%s'.", "set", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public final String getDescription() {
		// PRECONDITIONS
		
		try {
			return this.description;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.description, "Field '%s' in '%s'.", "description", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	@Override
	public final String getHandle() {
		return (getClass().getEnclosingClass() != null
		                                              ? getClass().getEnclosingClass().getSimpleName() + '.'
		                                              : "") + getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String getHelpString(final int keyWidth) {
		return getHelpString(keyWidth, 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public String getHelpString(final int keyWidth,
	                            final int indentation) {
		final StringBuilder builder = new StringBuilder();
		
		final StringBuilder indent = new StringBuilder();
		final StringBuilder header = new StringBuilder();
		
		for (int i = 0, j = 0; i < indentation; ++i, ++j) {
			indent.append("| ");
			if ((j + 1) < indentation) {
				header.append(" ");
			}
		}
		
		builder.append(header).append("[ ").append(getName()).append(" ] ").append(getDescription());
		builder.append(required()
		                         ? " (Required, due to " + getRequirements() + ")"
		                         : "");
		
		return builder.toString();
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Override
	public final String getName() {
		// PRECONDITIONS
		
		try {
			return this.name;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.name, "Field '%s' in '%s'.", "name", getClass().getSimpleName());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IOptions#getParent()
	 */
	@Override
	public ArgumentSet<?, ?> getParent() {
		// PRECONDITIONS
		
		try {
			return this.set;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.set, "Field '%s'.", "set");
		}
	}
	
	/**
	 * Gets the requirements.
	 * 
	 * @return the requirements
	 */
	@Override
	public final Requirement getRequirements() {
		// PRECONDITIONS
		
		try {
			return this.requirements;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.requirements, "Field '%s' in '%s'.", "requirements", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	@Override
	public ISettings getSettings() {
		return this.set.getSettings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IOptions#getTag()
	 */
	@Override
	public String getTag() {
		final StringBuilder builder = new StringBuilder();
		final LinkedList<String> list = new LinkedList<String>();
		IArgument<?, ?> parent = getParent();
		
		do {
			if (!(parent instanceof Settings.RootArgumentSet)) {
				list.add(parent.getName());
			}
		} while ((parent = parent.getParent()) != null);
		
		final Iterator<String> iterator = list.descendingIterator();
		while (iterator.hasNext()) {
			if (builder.length() > 0) {
				builder.append('.');
			}
			
			builder.append(iterator.next());
		}
		
		if (builder.length() > 0) {
			builder.append('.');
		}
		builder.append(getName());
		
		return builder.toString();
	}
	
	/**
	 * Inits the.
	 * 
	 * @return the t
	 */
	@Override
	public abstract T init();
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IOptions#required()
	 */
	@Override
	public boolean required() {
		// PRECONDITIONS
		
		try {
			final ArgumentSet<?, ?> parent = getParent();
			boolean required = false;
			
			// parent required status
			if (parent != null) {
				required = parent.required();
			} else {
				required = true;
			}
			
			// local required status
			final boolean check = this.requirements.check();
			
			required &= check;
			
			required &= !(this.requirements instanceof Optional);
			
			return required;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Requirements.
	 * 
	 * @param argumentSet
	 *            the set
	 * @return the map
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	@Override
	public abstract Map<String, IOptions<?, ?>> requirements(ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                       SettingsParseError;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getHandle());
		builder.append(" [name=");
		builder.append(getName());
		builder.append(", description=");
		builder.append(getDescription());
		builder.append(", requirements=");
		builder.append(getRequirements());
		builder.append(", parent=");
		builder.append(getArgumentSet().getName());
		builder.append("]");
		return builder.toString();
	}
}
