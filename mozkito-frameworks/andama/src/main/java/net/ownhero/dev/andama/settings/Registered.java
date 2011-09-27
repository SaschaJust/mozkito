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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;

public abstract class Registered {
	
	/**
	 * @param clazz
	 * @return
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
		// FIXME this is specific to Mappings and should be rewritten to work in
		// a generic way
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
	
	private AndamaSettings settings;
	
	private boolean        registered  = false;
	private boolean        initialized = false;
	
	/**
	 * 
	 */
	public Registered() {
		super();
	}
	
	/**
	 * @return
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
	public final AndamaSettings getSettings() {
		return this.settings;
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
	 * @param settings
	 * @param arguments
	 * @param isRequired
	 */
	@NoneNull
	public void register(final AndamaSettings settings,
	                     final AndamaArgumentSet arguments,
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
	final public void setSettings(final AndamaSettings settings) {
		this.settings = settings;
	}
	
	/**
	 * @return
	 */
	public boolean singleton() {
		return false;
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
		builder.append("]");
		return builder.toString();
	}
	
}
