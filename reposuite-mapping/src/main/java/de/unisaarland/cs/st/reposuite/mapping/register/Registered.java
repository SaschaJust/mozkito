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
package de.unisaarland.cs.st.reposuite.mapping.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;

/**
 * Classes extending {@link Registered} can dynamically register config options
 * to the tool chain. Additionally there is support to automatically generate
 * config option names following the standard naming convention.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Registered {
	
	/**
	 * @param clazz
	 *            the base class extending {@link Registered}
	 * @return the lowercase part of the name specifies the category of the
	 *         registered class, e.g. "engine" for MappingEngine.
	 */
	private static final String findRegisteredSuper(final Class<? extends Registered> clazz) {
		String[] superTag = new String[] { "" };
		return findRegisteredSuper(clazz, superTag);
	}
	
	/**
	 * @param clazz
	 * @param superTag
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private static String findRegisteredSuper(final Class<? extends Registered> clazz,
	                                          final String[] superTag) {
		if (clazz.getSuperclass() == Registered.class) {
			superTag[0] = clazz.getSimpleName().replace("Mapping", "");
			return superTag[0].toLowerCase();
		} else if (clazz == Registered.class) {
			throw new UnrecoverableError("Instance of ABSTRACT class " + Registered.class.getSimpleName()
			        + " tries to register config option.");
		} else {
			Class<? extends Registered> c = clazz;
			if (Registered.class.isAssignableFrom(c.getSuperclass()) && (c.getSuperclass() != Registered.class)) {
				c = (Class<? extends Registered>) c.getSuperclass();
				String string = findRegisteredSuper(c, superTag);
				String retval = string + "." + clazz.getSimpleName().replace(superTag[0], "").toLowerCase();
				superTag[0] = clazz.getSimpleName();
				return retval;
			}
		}
		throw new UnrecoverableError("Instance of ABSTRACT class " + Registered.class.getSimpleName()
		        + " tries to register config option.");
	}
	
	private MappingSettings                                            settings;
	
	private boolean                                                    registered  = false;
	private boolean                                                    initialized = false;
	private final Map<Class<? extends MappingStorage>, MappingStorage> storages    = new HashMap<Class<? extends MappingStorage>, MappingStorage>();
	
	/**
	 * 
	 */
	public Registered() {
		super();
	}
	
	/**
	 * @return a string describing the task of the registered class
	 */
	public abstract String getDescription();
	
	/**
	 * @return
	 */
	public final String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param option
	 * @return
	 */
	public final String getOptionName(final String option) {
		StringBuilder builder = new StringBuilder();
		builder.append("mapping.");
		builder.append(findRegisteredSuper(this.getClass()).toLowerCase()).append('.');
		builder.append(option);
		return builder.toString();
	}
	
	/**
	 * @return the settings
	 */
	public final MappingSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public final <T extends MappingStorage> T getStorage(final Class<T> key) {
		return (T) this.storages.get(key);
	}
	
	/**
	 * 
	 */
	public void init() {
		Condition.check(isRegistered(), "The " + this.getClass().getSuperclass().getSimpleName()
		        + " has to be registered before it is initialized: %s", this.getClass().getSimpleName());
		setInitialized(true);
	}
	
	/**
	 * @return true, if the {@link Registered} object is enabled
	 */
	public abstract boolean isEnabled();
	
	/**
	 * @param listSetting
	 * @return
	 */
	public final boolean isEnabled(final String listSetting,
	                               final String registered) {
		if (getSettings() != null) {
			ListArgument setting = (ListArgument) getSettings().getSetting(listSetting);
			return setting.getValue().contains(registered);
		} else {
			return true;
		}
	}
	
	/**
	 * @return the initialized
	 */
	public final boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @return the registered
	 */
	public final boolean isRegistered() {
		return this.registered;
	}
	
	/**
	 * @param storage
	 */
	public void provideStorage(final MappingStorage storage) {
		this.storages.put(storage.getClass(), storage);
	}
	
	/**
	 * @param storages
	 */
	public final void provideStorages(final Set<? extends MappingStorage> storages) {
		for (MappingStorage storage : storages) {
			this.storages.put(storage.getClass(), storage);
		}
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param isRequired
	 */
	@NoneNull
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		setSettings(settings);
		setRegistered(true);
	}
	
	/**
	 * @param initialized
	 *            the initialized to set
	 */
	final void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @param registered
	 *            the registered to set
	 */
	final void setRegistered(final boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * @param settings
	 *            the settings to set
	 */
	final public void setSettings(final MappingSettings settings) {
		this.settings = settings;
	}
	
	/**
	 * @return
	 */
	public boolean singleton() {
		return false;
	}
	
	/**
	 * @return
	 */
	public Set<Class<? extends MappingStorage>> storageDependency() {
		return new HashSet<Class<? extends MappingStorage>>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSuperclass().getSimpleName() + " [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append(", dependencies=");
		builder.append(JavaUtils.collectionToString(storageDependency()));
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected final String truncate(final String string) {
		return string.substring(0, Math.min(string.length() - 1, 254));
	}
	
}
