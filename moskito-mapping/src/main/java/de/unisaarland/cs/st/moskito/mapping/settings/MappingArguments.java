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
package de.unisaarland.cs.st.moskito.mapping.settings;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.andama.settings.registerable.Registered;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector;
import de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.mapping.training.MappingTrainer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingArguments extends AndamaArgumentSet {
	
	private final Set<Registered> registereds = new HashSet<Registered>();
	
	/**
	 * @param isRequired
	 * @param mappingSettings
	 * 
	 */
	public MappingArguments(final MappingSettings settings, final boolean isRequired) {
		super();
		
		handleRegistered(settings, "engines", MappingEngine.class, isRequired);
		handleRegistered(settings, "filters", MappingFilter.class, isRequired);
		handleRegistered(settings, "selectors", MappingSelector.class, isRequired);
		handleRegistered(settings, "splitters", MappingSplitter.class, isRequired);
		handleRegistered(settings, "strategies", MappingStrategy.class, isRequired);
		handleRegistered(settings, "trainers", MappingTrainer.class, isRequired);
	}
	
	/**
	 * @param registereds
	 * @return
	 */
	private String buildRegisteredList(final Collection<Class<? extends Registered>> registereds) {
		final StringBuilder builder = new StringBuilder();
		builder.append(FileUtils.lineSeparator);
		
		for (final Class<? extends Registered> registered : registereds) {
			try {
				builder.append('\t').append("  ").append(registered.getSimpleName()).append(": ")
				       .append(registered.newInstance().getDescription());
			} catch (final InstantiationException e) {
			} catch (final IllegalAccessException e) {
			}
			
			if (builder.length() != 0) {
				builder.append(FileUtils.lineSeparator);
			}
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public MappingFinder getValue() {
		final MappingFinder finder = new MappingFinder();
		final Map<Class<? extends Registered>, Method> methodMap = new HashMap<Class<? extends Registered>, Method>();
		final Method[] methods = finder.getClass().getMethods();
		
		try {
			for (final Registered registered : this.registereds) {
				if (methodMap.containsKey(registered.getClass())) {
					// method finder.addXXX already known
					methodMap.get(registered.getClass()).invoke(finder, registered);
				} else {
					// look up finder.addXXX method
					boolean found = false;
					Class<?> registeredSubClass = registered.getClass();
					
					while (!found && (registeredSubClass != Object.class)) {
						for (final Method method : methods) {
							if (method.getName().startsWith("add")) {
								if ((method.getParameterTypes().length == 1)
								        && (method.getParameterTypes()[0] == registeredSubClass)) {
									found = true;
									methodMap.put(registered.getClass(), method);
									method.invoke(finder, registered);
									break;
								}
							}
						}
						registeredSubClass = registeredSubClass.getSuperclass();
					}
					if (!found) {
						
						if (Logger.logError()) {
							Logger.error("Could not find 'add' method in " + MappingFinder.class.getSimpleName()
							        + " for type " + registered.getClass().getSimpleName());
						}
						throw new UnrecoverableError("Could not find 'add' method in "
						        + MappingFinder.class.getSimpleName() + " for type "
						        + registered.getClass().getSimpleName());
					}
				}
			}
		} catch (final Exception e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		return finder;
	}
	
	/**
	 * @param settings
	 * @param argumentName
	 * @param superClass
	 * @param isRequired
	 */
	private void handleRegistered(final MappingSettings settings,
	                              final String argumentName,
	                              final Class<? extends Registered> superClass,
	                              final boolean isRequired) {
		final Collection<Class<? extends Registered>> registeredClasses = new LinkedList<Class<? extends Registered>>();
		try {
			registeredClasses.addAll(ClassFinder.getClassesExtendingClass(superClass.getPackage(), superClass,
			                                                              Modifier.ABSTRACT | Modifier.INTERFACE
			                                                                      | Modifier.PRIVATE));
		} catch (final Exception e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		final StringBuilder builder = new StringBuilder();
		
		for (final Class<? extends Registered> clazz : registeredClasses) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(clazz.getSimpleName());
		}
		addArgument(new ListArgument(settings, "mapping." + argumentName, "A list of mapping " + argumentName
		        + " that shall be used: " + buildRegisteredList(registeredClasses), builder.toString(), false));
		
		final String registereds = System.getProperty("mapping." + argumentName);
		final Set<String> registeredNames = new HashSet<String>();
		
		if (registereds != null) {
			for (final String registeredName : registereds.split(",")) {
				registeredNames.add(superClass.getPackage().getName() + "." + registeredName);
			}
			
		}
		
		for (final Class<? extends Registered> klass : registeredClasses) {
			if (registeredNames.isEmpty() || registeredNames.contains(klass.getCanonicalName())) {
				if ((klass.getModifiers() & Modifier.ABSTRACT) == 0) {
					if (Logger.logInfo()) {
						Logger.info("Adding new " + klass.getSuperclass().getSimpleName() + " "
						        + klass.getCanonicalName());
					}
					
					try {
						final Registered instance = klass.newInstance();
						instance.register(settings, this, isRequired);
						instance.init();
						this.registereds.add(instance);
					} catch (final Exception e) {
						
						if (Logger.logWarn()) {
							Logger.warn("Skipping registration of " + klass.getSimpleName() + " due to errors: "
							        + e.getMessage());
						}
					}
				}
			} else {
				if (Logger.logInfo()) {
					Logger.info("Not loading available engine: " + klass.getSimpleName());
				}
			}
		}
	}
	
}
