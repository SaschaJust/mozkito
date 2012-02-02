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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class AndamaArgumentSet<T> implements AndamaArgumentInterface<T> {
	
	private final HashMap<String, AndamaArgumentInterface<?>> arguments   = new HashMap<String, AndamaArgumentInterface<?>>();
	private final Set<AndamaArgumentInterface<?>>             dependees   = new HashSet<AndamaArgumentInterface<?>>();
	private final String                                      name;
	private final String                                      description;
	private final boolean                                     required;
	private final AndamaSettings                              settings;
	private boolean                                           init        = false;
	private T                                                 cachedValue = null;
	
	/**
	 * @param name
	 * @param description
	 */
	private AndamaArgumentSet(final AndamaArgumentSet<?> argumentSet, final String description) {
		this(argumentSet, description, false);
	}
	
	/**
	 * @param name
	 * @param description
	 * @param dependee
	 */
	public AndamaArgumentSet(final AndamaArgumentSet<?> argumentSet, final String description,
	        final AndamaArgumentInterface<?> dependee) {
		this(argumentSet, description);
		getDependees().add(dependee);
	}
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 */
	public AndamaArgumentSet(final AndamaArgumentSet<?> argumentSet, final String description, final boolean required) {
		this.name = getHandle();
		this.description = description;
		this.settings = argumentSet.getSettings();
		argumentSet.addArgument(this);
		this.required = required;
	}
	
	/**
	 * @param name
	 * @param description
	 * @param dependees
	 */
	public AndamaArgumentSet(final AndamaArgumentSet<?> argumentSet, final String description,
	        final Set<AndamaArgumentInterface<?>> dependees) {
		this(argumentSet, description);
		getDependees().addAll(dependees);
	}
	
	/**
	 * @param settings
	 * @param description
	 */
	AndamaArgumentSet(final AndamaSettings settings, final String description) {
		this.settings = settings;
		this.description = description;
		this.name = getHandle();
		this.required = false;
		getSettings().addArgument(this);
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware
	 * that you have to set all arguments before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @return <code>true</code> if the argument could be added.
	 *         <code>false</code> otherwise.
	 */
	public final boolean addArgument(final AndamaArgumentInterface<?> argument) {
		if (this.arguments.containsKey(argument.getName())) {
			return false;
		}
		
		this.arguments.put(argument.getName(), argument);
		
		return true;
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
	
	/**
	 * @param name
	 * @return
	 */
	public final AndamaArgumentInterface<?> getArgument(final String name) {
		return getArguments().get(name);
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return
	 */
	public Map<String, AndamaArgumentInterface<?>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * @return
	 */
	protected final T getCachedValue() {
		return this.cachedValue;
	}
	
	/**
	 * @return the dependees
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
		return getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getName()
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/**
	 * @return the settings
	 */
	@Override
	public final AndamaSettings getSettings() {
		return this.settings;
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
	
	/**
	 * @return
	 */
	protected abstract boolean init();
	
	/**
	 * @return the init
	 */
	protected final boolean isInitialized() {
		return this.init;
	}
	
	/**
	 * @return the required
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
	
}
