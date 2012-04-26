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

import net.ownhero.dev.hiari.settings.requirements.Optional;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class ArgumentOptions.
 * 
 * @param <T>
 *            the generic type
 * @param <X>
 *            the generic type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class ArgumentOptions<T, X extends Argument<T, ? extends ArgumentOptions<T, ?>>> implements
        IArgumentOptions<T, X> {
	
	/** The name. */
	private final String            name;
	
	/** The description. */
	private final String            description;
	
	/** The requirements. */
	private final Requirement       requirements;
	
	/** The string value. */
	private final String            stringValue;
	
	/** The default value. */
	private final T                 defaultValue;
	
	/** The argument set. */
	private final ArgumentSet<?, ?> argumentSet;
	
	/** The masked. */
	private boolean                 masked;
	
	/**
	 * Instantiates a new argument options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param defaultValue
	 *            the default value
	 * @param requirements
	 *            the requirements
	 */
	public ArgumentOptions(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, final T defaultValue,
	        @NotNull final Requirement requirements) {
		this(argumentSet, name, description, defaultValue, requirements, false);
	}
	
	/**
	 * Instantiates a new argument options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param defaultValue
	 *            the default value
	 * @param requirements
	 *            the requirements
	 * @param mask
	 *            the mask
	 */
	public ArgumentOptions(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, final T defaultValue,
	        @NotNull final Requirement requirements, final boolean mask) {
		// PRECONDITIONS
		
		try {
			this.name = name;
			this.description = description;
			this.requirements = requirements;
			this.stringValue = defaultValue != null
			                                       ? defaultValue.toString()
			                                       : null;
			this.defaultValue = defaultValue;
			this.argumentSet = argumentSet;
			this.masked = mask;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.argumentSet, "Field '%s' in %s.", "argumentSet", getHandle());
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
	public int compareTo(final IOptions<?, ?> arg0) {
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
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getArgumentSet()
	 */
	@Override
	public final ArgumentSet<?, ?> getArgumentSet() {
		return this.argumentSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getDefaultValue()
	 */
	@Override
	public final T getDefaultValue() {
		return this.defaultValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getDescription()
	 */
	@Override
	public final String getDescription() {
		return this.description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getHandle()
	 */
	@Override
	public final String getHandle() {
		return (getClass().getEnclosingClass() != null
		                                              ? getClass().getEnclosingClass().getSimpleName() + '.'
		                                              : "") + getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getHelpString()
	 */
	@Override
	public final String getHelpString(final int keyWidth) {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("-D%s%-").append(keyWidth).append("s%s: %s%s [");
		
		if (getAdditionalHelpString().length() > 0) {
			builder.append("'%s', ");
		}
		
		builder.append("default='%s', required=%s, type=%s]");
		
		if (getAdditionalHelpString().length() > 0) {
			return String.format(builder.toString(),
			                     required()
			                               ? (getDefaultValue() != null)
			                                                            ? Logger.TerminalColor.YELLOW.getTag()
			                                                            : Logger.TerminalColor.RED.getTag()
			                               : "", getTag(), required()
			                                                         ? Logger.TerminalColor.NONE.getTag()
			                                                         : "", getDescription(), required()
			                                                                                           ? " (required!)"
			                                                                                           : "",
			                     getAdditionalHelpString(), getDefaultValue(), getRequirements(), getHandle());
		}
		return String.format(builder.toString(),
		                     required()
		                               ? (getDefaultValue() != null)
		                                                            ? Logger.TerminalColor.YELLOW.getTag()
		                                                            : Logger.TerminalColor.RED.getTag()
		                               : "", getTag(), required()
		                                                         ? Logger.TerminalColor.NONE.getTag()
		                                                         : "", getDescription(), required()
		                                                                                           ? " (required!)"
		                                                                                           : "",
		                     getDefaultValue(), getRequirements(), getHandle());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getHelpString(int)
	 */
	@Override
	public final String getHelpString(final int keyWidth,
	                                  final int indentation) {
		final StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < indentation; ++i) {
			builder.append("| ");
		}
		
		return "|" + getHelpString(keyWidth);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getName()
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getParent()
	 */
	@Override
	public ArgumentSet<?, ?> getParent() {
		// PRECONDITIONS
		
		try {
			return getArgumentSet();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#getRequirements()
	 */
	@Override
	public final Requirement getRequirements() {
		return this.requirements;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.IOptions#getSettings()
	 */
	@Override
	public ISettings getSettings() {
		return getParent().getSettings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getTag()
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
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#isMasked()
	 */
	@Override
	public final boolean isMasked() {
		return this.masked;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgumentOptions#required()
	 */
	@Override
	public final boolean required() {
		return getRequirements().required() && !(getRequirements() instanceof Optional);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getHandle());
		builder.append(" [name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", requirements=");
		builder.append(this.requirements);
		builder.append(", stringValue=");
		builder.append(this.stringValue);
		builder.append(", defaultValue=");
		builder.append(this.defaultValue);
		builder.append(", masked=");
		builder.append(this.masked);
		builder.append("]");
		return builder.toString();
	}
}
