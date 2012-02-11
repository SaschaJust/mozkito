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
package net.ownhero.dev.andama.settings;

import java.util.Set;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class Argument<T> implements IArgument<T> {
	
	private final String        defaultValue;
	private final String        description;
	private String              name;
	private final Settings      settings;
	private final Requirement   requirements;
	
	private String              stringValue;
	private boolean             wasSet;
	private boolean             init       = false;
	private T                   cachedValue;
	private final boolean       masked;
	private final static String maskString = "******** (masked)";
	
	/**
	 * @param settings
	 *            The RepoSuiteSetting instance this argument will register for
	 * @param name
	 *            Name of the Argument
	 * @param description
	 *            The help string description
	 * @param defaultValue
	 *            The default value given as string will be interpreted as path
	 * @param required
	 *            Set to <code>true</code> if this argument will be required
	 * @throws ArgumentRegistrationException
	 */
	public Argument(@NotNull final ArgumentSet<?> argumentSet, @NotNull final String name,
	        @NotNull final String description, final String defaultValue, @NotNull final Requirement requirements)
	        throws ArgumentRegistrationException {
		this(argumentSet, name, description, defaultValue, requirements, false);
	}
	
	public Argument(@NotNull final ArgumentSet<?> argumentSet, @NotNull final String name,
	        @NotNull final String description, final String defaultValue, @NotNull final Requirement requirements,
	        final boolean mask) throws ArgumentRegistrationException {
		
		try {
			this.setName(name);
			this.description = description;
			this.requirements = requirements;
			this.stringValue = defaultValue;
			this.defaultValue = defaultValue;
			this.settings = argumentSet.getSettings();
			this.masked = mask;
			
			if (!argumentSet.addArgument(this)) {
				throw new ArgumentRegistrationException("Could not register argument set " + getHandle() + ".");
			}
		} finally {
			Condition.notNull(this.getName(), "Field '%s' in %s.", "name", getHandle());
			Condition.notNull(this.description, "Field '%s' in %s.", "description", getHandle());
			Condition.notNull(this.requirements, "Field '%s' in %s.", "requirements", getHandle());
			Condition.notNull(this.settings, "Field '%s' in %s.", "settings", getHandle());
		}
	}
	
	/**
	 * @param retval
	 */
	protected void __initPostCondition(final boolean retval) {
		if (retval) {
			Condition.check(isInitialized(), "If init() returns true, the %s has to be set to initialized.",
			                getHandle());
			if (required()) {
				Condition.notNull(getCachedValue(),
				                  "%s has been successful initialized with config value '%s' but the stored data is null.",
				                  getHandle(), getStringValue());
			}
		} else {
			Condition.check(!isInitialized(), "If init() returns false, the %s has to be set to uninitialized.",
			                getHandle());
		}
	}
	
	/**
	 * @param arg0
	 * @return
	 */
	@Override
	public final int compareTo(final IArgument<?> arg0) {
		if (this == arg0) {
			return 0;
		}
		
		final Set<IArgument<?>> dependencies = getDependencies();
		if (dependencies.contains(arg0)) {
			return 1;
		} else if (dependencies.contains(this)) {
			return 0;
		} else {
			int ret = -1;
			for (final IArgument<?> argX : dependencies) {
				ret = argX.compareTo(arg0);
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
		
		final Argument<?> other = (Argument<?>) obj;
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
	 * @return
	 */
	protected final T getCachedValue() {
		return this.cachedValue;
	}
	
	/**
	 * @return
	 */
	public final String getDefaultValue() {
		return this.defaultValue;
	}
	
	@Override
	public Set<IArgument<?>> getDependencies() {
		return getRequirements().getDependencies();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDescription()
	 */
	@Override
	public final String getDescription() {
		return this.description;
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
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getHelpString()
	 */
	@Override
	public String getHelpString() {
		return String.format("%s ['%s', value='%s', default='%s', description='%s', required=%s, required if=%s]",
		                     getHandle(), getName(), getStringValue() == null
		                                                                     ? "(unset)"
		                                                                     : getStringValue(), getDefaultValue(),
		                     getDescription(), required(), getRequirements());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getHelpString (int)
	 */
	@Override
	public String getHelpString(final int indentation) {
		final StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < indentation; ++i) {
			builder.append("| ");
		}
		
		return "|-" + getHelpString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getKeyValueSpan()
	 */
	@Override
	public Tuple<Integer, Integer> getKeyValueSpan() {
		return new Tuple<Integer, Integer>(getName().length(),
		                                   getStringValue() == null
		                                                           ? "(unset)".length()
		                                                           : this.masked
		                                                                        ? maskString.length()
		                                                                        : getStringValue().length());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getName()
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	@Override
	public Requirement getRequirements() {
		return this.requirements;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getSettings()
	 */
	@Override
	public final Settings getSettings() {
		return this.settings;
	}
	
	/**
	 * @return the stringValue
	 */
	public final String getStringValue() {
		return this.stringValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getValue()
	 */
	@Override
	public final T getValue() {
		if (!this.init) {
			throw new UnrecoverableError("Calling getValue() on " + getHandle() + " and instance '" + getName()
			        + "' before calling init() is not allowed! Please fix your code.");
		}
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
	 * @return
	 */
	protected abstract boolean init();
	
	/**
	 * @return the init
	 */
	@Override
	public final boolean isInitialized() {
		return this.init;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#parse()
	 */
	@Override
	public void parse() throws SettingsParseError {
		final String value = (String) getSettings().getProperties().get(getName());
		
		if (value != null) {
			setStringValue(value);
		}
		
		if (!init()) {
			throw new SettingsParseError("Could not initialize " + getHandle() + " instance '" + getName() + "'.", this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#required()
	 */
	@Override
	public boolean required() {
		return getRequirements().required() && !(getRequirements() instanceof Optional);
	}
	
	/**
	 * @param cachedValue
	 */
	protected final void setCachedValue(final T cachedValue) {
		this.init = true;
		this.cachedValue = cachedValue;
	}
	
	/**
	 * @param name
	 */
	void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Sets the string value for the argument.
	 * 
	 * @param value
	 */
	protected final void setStringValue(final String value) {
		this.stringValue = value;
		this.wasSet = true;
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
	public String toString(final int keyWidth,
	                       final int valueWidth) {
		final StringBuilder builder = new StringBuilder();
		builder.append("%-").append(keyWidth).append("s = %-").append(valueWidth).append("s\t%s");
		
		return String.format(builder.toString(), getName(), getStringValue() == null
		                                                                            ? "(unset)"
		                                                                            : this.masked
		                                                                                         ? maskString
		                                                                                         : getStringValue(),
		                     getHelpString());
	}
	
	/**
	 * @return
	 */
	protected final boolean validStringValue() {
		return (getStringValue() != null) && !getStringValue().trim().isEmpty();
	}
	
	/**
	 * @return
	 */
	public final boolean wasSet() {
		return this.wasSet;
	}
	
}
