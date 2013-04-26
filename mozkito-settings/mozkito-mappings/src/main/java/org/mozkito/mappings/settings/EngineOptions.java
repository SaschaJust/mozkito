/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.mappings.settings;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.engines.Engine;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * The Class Options.
 */
public class EngineOptions extends ArgumentSetOptions<Set<Engine>, ArgumentSet<Set<Engine>, EngineOptions>> {
	
	/** The Constant tag. */
	static final String                                                                 TAG           = "engines";                               //$NON-NLS-1$
	                                                                                                                                              
	/** The Constant DESCRIPTION. */
	static final String                                                                 DESCRIPTION   = Messages.getString("Engine.description"); //$NON-NLS-1$
	                                                                                                                                              
	/** The engines option. */
	private SetArgument.Options                                                         enabledEnginesOption;
	
	/** The engine options. */
	private final Map<Class<? extends Engine>, ArgumentSetOptions<? extends Engine, ?>> engineOptions = new HashMap<>();
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public EngineOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, TAG, DESCRIPTION, requirements);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public Set<Engine> init() {
		// PRECONDITIONS
		final Set<Engine> set = new HashSet<Engine>();
		
		try {
			// request set argument containing enabled engines
			final SetArgument argument = getSettings().getArgument(this.enabledEnginesOption);
			final HashSet<String> value = argument.getValue();
			
			for (final String name : value) {
				Class<? extends Engine> clazz;
				try {
					clazz = (Class<? extends Engine>) Class.forName(Engine.class.getPackage().getName() + '.' + name);
				} catch (final ClassNotFoundException e) {
					throw new Shutdown(String.format(Messages.getString("Engine.loadingFailure"), //$NON-NLS-1$
					                                 name));
					
				}
				
				final ArgumentSetOptions<? extends Engine, ?> options = this.engineOptions.get(clazz);
				if (options == null) {
					if (Logger.logWarn()) {
						Logger.warn(Messages.getString("Engine.laggingConfigClass"), //$NON-NLS-1$
						            clazz.getSimpleName(), ArgumentSetOptions.class.getSimpleName(),
						            clazz.getSimpleName(), ArgumentSet.class.getSimpleName(), clazz.getSimpleName());
					}
					
				} else {
					final ArgumentSet<? extends Engine, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
					set.add(argumentSet.getValue());
				}
			}
			
			return set;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			final HashSet<String> defaultSet = new HashSet<String>();
			
			try {
				// first off, find all implemented engines
				final Collection<Class<? extends Engine>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
				                                                                                            Engine.class,
				                                                                                            Modifier.ABSTRACT
				                                                                                                    | Modifier.INTERFACE
				                                                                                                    | Modifier.PRIVATE
				                                                                                                    | Modifier.PROTECTED);
				
				// first compute the default value for the engine enabler option, i.e., all implemented engines
				for (final Class<? extends Engine> engineClass : collection) {
					defaultSet.add(engineClass.getSimpleName());
				}
				
				// now create the enabler for the engines
				this.enabledEnginesOption = new SetArgument.Options(
				                                                    set,
				                                                    "enabled", Messages.getString("MappingEngine.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$
				                                                    defaultSet, Requirement.required);
				
				// iterate over the engine classes to process dependencies
				for (final Class<? extends Engine> engineClass : collection) {
					// loading of engines is in the responsibility of the direct parent class, thus we only process
					// mapping classes of direct extensions of MappingEngine
					if (engineClass.getSuperclass() == Engine.class) {
						// MappingEngines have to encapsulate their initializer/options. fetch them first.
						final Class<?>[] declaredClasses = engineClass.getDeclaredClasses();
						
						boolean foundOptionClass = false;
						for (final Class<?> engineOptionClass : declaredClasses) {
							// check if we found the options to initialize the engine under suspect
							if (ArgumentSetOptions.class.isAssignableFrom(engineOptionClass)) {
								foundOptionClass = true;
								// found options
								
								// fetch constructor of the options
								@SuppressWarnings ("unchecked")
								final Constructor<ArgumentSetOptions<? extends Engine, ?>> constructor = (Constructor<ArgumentSetOptions<? extends Engine, ?>>) engineOptionClass.getDeclaredConstructor(ArgumentSet.class,
								                                                                                                                                                                         Requirement.class);
								
								// instantiate the options and set to required if enabledEnginesOptions contains the
								// simple classname of the engine under suspect, i.e., c.getSimpleName()
								final ArgumentSetOptions<? extends Engine, ?> engineOption = constructor.newInstance(set,
								                                                                                     Requirement.contains(this.enabledEnginesOption,
								                                                                                                          engineClass.getSimpleName()));
								if (Logger.logDebug()) {
									Logger.debug(Messages.getString("Engine.addingEngineDependency", //$NON-NLS-1$
									                                engineOption.getTag(),
									                                this.enabledEnginesOption.getTag()));
								}
								
								this.engineOptions.put(engineClass, engineOption);
								if (engineOption.required()) {
									map.put(engineOption.getName(), engineOption);
								}
							}
						}
						
						if (!foundOptionClass) {
							throw new Shutdown(String.format(Messages.getString("Engine.noInternalConfigurator"), //$NON-NLS-1$
							                                 engineClass.getSimpleName()));
						}
					} else {
						if (Logger.logInfo()) {
							Logger.info(Messages.getString("Engine.noDirectExtension", //$NON-NLS-1$
							                               engineClass.getSimpleName(), Engine.class.getSimpleName(),
							                               engineClass.getSuperclass().getSimpleName()));
						}
					}
				}
			} catch (final ClassNotFoundException | WrongClassSearchMethodException | IOException
			        | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			        | NoSuchMethodException | SecurityException | InstantiationException e) {
				throw new UnrecoverableError(e);
			}
			
			map.put(this.enabledEnginesOption.getName(), this.enabledEnginesOption);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
}
