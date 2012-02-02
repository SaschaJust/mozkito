/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class AndamaArgument<T> implements AndamaArgumentInterface<T> {
	
	private final String                          defaultValue;
	private final String                          description;
	private final boolean                         required;
	private final String                          name;
	private final Set<AndamaArgumentInterface<?>> dependees = new HashSet<AndamaArgumentInterface<?>>();
	private final AndamaSettings                  settings;
	
	private String                                stringValue;
	private boolean                               wasSet;
	private boolean                               init      = false;
	private T                                     cachedValue;
	
	/**
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param dependee
	 */
	@SuppressWarnings ("serial")
	public AndamaArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final AndamaArgument<?> dependee) {
		this(argumentSet, name, description, defaultValue, new HashSet<AndamaArgumentInterface<?>>() {
			
			{
				add(dependee);
			}
		});
	}
	
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
	 */
	public AndamaArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final boolean required) {
		this.name = name;
		this.description = description;
		this.required = required;
		this.stringValue = defaultValue;
		this.defaultValue = defaultValue;
		
		this.settings = argumentSet.getSettings();
		argumentSet.addArgument(this);
	}
	
	/**
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param dependees
	 */
	public AndamaArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final Set<AndamaArgumentInterface<?>> dependees) {
		this(argumentSet, name, description, defaultValue, false);
		getDependees().addAll(dependees);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#compareTo(net
	 * .ownhero.dev.andama.settings.AndamaArgumentInterface)
	 */
	@Override
	public final int compareTo(final AndamaArgumentInterface<T> arg0) {
		return getName().compareTo(arg0.getName());
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
		
		final AndamaArgument<?> other = (AndamaArgument<?>) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDependees()
	 */
	@Override
	public final Set<AndamaArgumentInterface<?>> getDependees() {
		return this.dependees;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDescription()
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
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getName()
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#getSettings()
	 */
	@Override
	public final AndamaSettings getSettings() {
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
			throw new UnrecoverableError("Calling getValue() on " + this.getClass().getSimpleName() + " and instance "
			        + getName() + " before calling init() is not allowed! Please fix your code.");
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
	
	protected abstract boolean init();
	
	/**
	 * @return the init
	 */
	protected final boolean isInitialized() {
		return this.init;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#required()
	 */
	@Override
	public final boolean required() {
		return this.required;
	}
	
	/**
	 * @param cachedValue
	 */
	protected final void setCachedValue(final T cachedValue) {
		this.init = true;
		this.cachedValue = cachedValue;
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
	public String toString() {
		return getHandle() + " [name=" + getName() + ", required=" + required() + ", default=" + getDefaultValue()
		        + ", value=" + getStringValue() + ", description=" + getDescription() + "]";
	}
	
	/**
	 * @return
	 */
	public final boolean wasSet() {
		return this.wasSet;
	}
	
}
