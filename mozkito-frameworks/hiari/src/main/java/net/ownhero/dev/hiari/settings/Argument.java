/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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

import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Optional;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class Argument.
 * 
 * @param <T>
 *            the generic type
 * @param <X>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public abstract class Argument<T, X extends ArgumentOptions<T, ? extends Argument<T, ?>>> implements IArgument<T, X> {
	
	/** The string value. */
	private String              stringValue;
	
	/** The cached value. */
	private T                   cachedValue;
	
	/** The options. */
	private X                   options;
	
	/** The Constant maskString. */
	private final static String maskString = "******** (masked)";
	
	/**
	 * Instantiates a new argument.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	@SuppressWarnings ("unchecked")
	Argument(@NotNull final X options) throws ArgumentRegistrationException {
		
		try {
			this.options = options;
			this.stringValue = options.getDefaultValue() != null
			                                                    ? options.getDefaultValue().toString()
			                                                    : null;
			if (!options.getArgumentSet().addArgument((Argument<?, ? extends IOptions<?, IArgument<?, ?>>>) this)) {
				throw new ArgumentRegistrationException(String.format("Could not register argument '%s':%s.",
				                                                      getName(), getHandle()), this, options);
			}
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.options, "Field '%s' in %s.", "options", getHandle());
		}
	}
	
	/**
	 * __init post condition.
	 * 
	 * @param retval
	 *            the retval
	 */
	protected final void __initPostCondition(final boolean retval) {
		if (retval) {
			if (required()) {
				Condition.notNull(getCachedValue(),
				                  "%s has been successful initialized with config value '%s' but the stored data is null.",
				                  getHandle(), getStringValue());
			}
		}
	}
	
	/**
	 * Compare to.
	 * 
	 * @param arg0
	 *            the arg0
	 * @return the int
	 */
	@Override
	public final int compareTo(final IArgument<?, ?> arg0) {
		if (this == arg0) {
			return 0;
		} else if (equals(arg0)) {
			return 0;
		}
		
		final Set<IOptions<?, ?>> dependencies = getDependencies();
		
		if (dependencies.contains(arg0)) {
			return 1;
		} else if (dependencies.contains(this)) {
			return 0;
		} else {
			int ret = -1;
			
			for (final IOptions<?, ?> argX : dependencies) {
				ret = argX.compareTo(arg0.getOptions());
				if (ret != 0) {
					return ret;
				}
			}
			
			return ret;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final Argument<?, ?> other = (Argument<?, ?>) obj;
		
		if (this.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.getName().equals(other.getName())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the cached value.
	 * 
	 * @return the cached value
	 */
	protected final T getCachedValue() {
		return this.cachedValue;
	}
	
	/**
	 * Gets the default value.
	 * 
	 * @return the default value
	 */
	public final T getDefaultValue() {
		return this.options.getDefaultValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getDependencies()
	 */
	@Override
	public final Set<IOptions<?, ?>> getDependencies() {
		return getRequirements().getDependencies();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDescription()
	 */
	@Override
	public final String getDescription() {
		return this.options.getDescription();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getHandle()
	 */
	@Override
	public final String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getHelpString()
	 */
	@Override
	public String getHelpString() {
		// PRECONDITIONS
		
		try {
			return getHelpString(this.options.getTag().length(), 0);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getHelpString(int, int)
	 */
	@Override
	public String getHelpString(final int keyWidth,
	                            final int indentation) {
		// PRECONDITIONS
		
		try {
			return this.options.getHelpString(keyWidth, indentation);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getKeyValueSpan()
	 */
	@Override
	public final Tuple<Integer, Integer> getKeyValueSpan() {
		return new Tuple<Integer, Integer>(getTag().length(),
		                                   getStringValue() == null
		                                                           ? "(unset)".length()
		                                                           : this.options.isMasked()
		                                                                                    ? maskString.length()
		                                                                                    : getStringValue().length());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getName()
	 */
	@Override
	public final String getName() {
		return this.options.getName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getOptions()
	 */
	@Override
	public X getOptions() {
		// PRECONDITIONS
		
		try {
			return this.options;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getParent()
	 */
	@Override
	public ArgumentSet<?, ?> getParent() {
		// PRECONDITIONS
		
		try {
			return this.options.getParent();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getRequirements()
	 */
	@Override
	public final Requirement getRequirements() {
		return this.options.getRequirements();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getSettings()
	 */
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	@Override
	public final ISettings getSettings() {
		return this.options.getArgumentSet().getSettings();
	}
	
	/**
	 * Gets the string value.
	 * 
	 * @return the stringValue
	 */
	protected final String getStringValue() {
		return this.stringValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getTag()
	 */
	@Override
	public String getTag() {
		// PRECONDITIONS
		
		try {
			return this.options.getTag();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getValue()
	 */
	@Override
	public final T getValue() {
		return this.getCachedValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getName() == null)
		                                                ? 0
		                                                : getName().hashCode());
		return result;
	}
	
	/**
	 * Initializes the argument.
	 * 
	 * @return true, if successful
	 */
	protected abstract boolean init();
	
	/**
	 * Checks if is masked.
	 * 
	 * @return true, if is masked
	 */
	public boolean isMasked() {
		return this.options.isMasked();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#parse()
	 */
	@Override
	public final void parse() throws SettingsParseError {
		final String value = getSettings().getProperty(getName());
		
		if (value != null) {
			setStringValue(value);
		}
		
		if (!init()) {
			throw new SettingsParseError("Could not initialize " + getHandle() + " instance '" + getName() + "'.", this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#required()
	 */
	@Override
	public boolean required() {
		// PRECONDITIONS
		
		try {
			return getRequirements().required() && !(getRequirements() instanceof Optional);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the cached value.
	 * 
	 * @param cachedValue
	 *            the new cached value
	 */
	protected final void setCachedValue(final T cachedValue) {
		this.cachedValue = cachedValue;
	}
	
	/**
	 * Sets the string value for the argument.
	 * 
	 * @param value
	 *            the new string value
	 */
	protected final void setStringValue(final String value) {
		this.stringValue = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		final Tuple<Integer, Integer> span = getKeyValueSpan();
		return toString(span.getFirst(), span.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public final String toString(final int keyWidth,
	                             final int valueWidth) {
		final StringBuilder builder = new StringBuilder();
		builder.append("%-").append(keyWidth).append("s = %-").append(valueWidth).append("s\t%s");
		
		return String.format(builder.toString(), getTag(), getStringValue() == null
		                                                                           ? "(unset)"
		                                                                           : isMasked()
		                                                                                       ? maskString
		                                                                                       : getStringValue(),
		                     getOptions().getHelpString(keyWidth + 1));
	}
	
	/**
	 * Valid string value.
	 * 
	 * @return true, if the set stringValue is not null and not empty
	 */
	protected final boolean validStringValue() {
		return (getStringValue() != null) && !getStringValue().trim().isEmpty();
	}
}
