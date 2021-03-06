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

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.strategies.Strategy;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class StrategyOptions extends ArgumentSetOptions<Set<Strategy>, ArgumentSet<Set<Strategy>, StrategyOptions>> {
	
	/** The enabled strategies option. */
	private SetArgument.Options                                                             enabledStrategiesOption;
	
	/** The engine options. */
	private final Map<Class<? extends Strategy>, ArgumentSetOptions<? extends Strategy, ?>> strategyOptions = new HashMap<>();
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public StrategyOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, Strategy.TAG, Strategy.DESCRIPTION, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public Set<Strategy> init() {
		// PRECONDITIONS
		final Set<Strategy> set = new HashSet<>();
		
		try {
			
			final SetArgument argument = getSettings().getArgument(this.enabledStrategiesOption);
			final HashSet<String> value = argument.getValue();
			
			for (final String name : value) {
				Class<? extends Strategy> clazz;
				try {
					clazz = (Class<? extends Strategy>) Class.forName(Strategy.class.getPackage().getName() + '.'
					        + name);
				} catch (final ClassNotFoundException e) {
					throw new UnrecoverableError(Messages.getString("Strategy.loadingFailure", name)); //$NON-NLS-1$
					
				}
				
				final ArgumentSetOptions<? extends Strategy, ?> options = this.strategyOptions.get(clazz);
				if (options == null) {
					if (Logger.logWarn()) {
						Logger.warn(Messages.getString("Strategy.laggingConfigClass", //$NON-NLS-1$
						                               clazz.getSimpleName(), ArgumentSetOptions.class.getSimpleName(),
						                               clazz.getSimpleName(), ArgumentSet.class.getSimpleName(),
						                               clazz.getSimpleName()));
					}
					
				} else {
					final ArgumentSet<? extends Strategy, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
					set.add(argumentSet.getValue());
				}
			}
			
			return set;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
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
				final Collection<Class<? extends Strategy>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
				                                                                                              Strategy.class,
				                                                                                              Modifier.ABSTRACT
				                                                                                                      | Modifier.INTERFACE
				                                                                                                      | Modifier.PRIVATE
				                                                                                                      | Modifier.PROTECTED);
				
				// first compute the default value for the strategy enabler option, i.e., all implemented strategies
				for (final Class<? extends Strategy> strategyClass : collection) {
					defaultSet.add(strategyClass.getSimpleName());
				}
				
				// now create the enabler for the strategies
				this.enabledStrategiesOption = new SetArgument.Options(
				                                                       set,
				                                                       "enabled", Messages.getString("MappingStrategy.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$
				                                                       defaultSet, Requirement.required);
				
				// iterate over the strategy classes to process dependencies
				for (final Class<? extends Strategy> strategyClass : collection) {
					
					if (strategyClass.getSuperclass() == Strategy.class) {
						
						final Class<?>[] declaredClasses = strategyClass.getDeclaredClasses();
						
						boolean foundOptionClass = false;
						for (final Class<?> strategyOptionClass : declaredClasses) {
							
							if (ArgumentSetOptions.class.isAssignableFrom(strategyOptionClass)) {
								foundOptionClass = true;
								//
								@SuppressWarnings ("unchecked")
								final Constructor<ArgumentSetOptions<? extends Strategy, ?>> constructor = (Constructor<ArgumentSetOptions<? extends Strategy, ?>>) strategyOptionClass.getDeclaredConstructor(ArgumentSet.class,
								                                                                                                                                                                               Requirement.class);
								
								final ArgumentSetOptions<? extends Strategy, ?> strategyOption = constructor.newInstance(set,
								                                                                                         Requirement.contains(this.enabledStrategiesOption,
								                                                                                                              strategyClass.getSimpleName()));
								
								if (Logger.logDebug()) {
									Logger.debug("Adding new mapping strategies dependency '%s' with list activator '%s'", //$NON-NLS-1$
									             strategyOption.getTag(), this.enabledStrategiesOption.getTag());
								}
								
								this.strategyOptions.put(strategyClass, strategyOption);
								if (strategyOption.required()) {
									map.put(strategyOption.getName(), strategyOption);
								}
							}
						}
						
						if (!foundOptionClass) {
							throw new Shutdown(Messages.getString("Strategy.noInternalConfigurator", //$NON-NLS-1$
							                                      strategyClass.getSimpleName()));
						}
					} else {
						if (Logger.logInfo()) {
							Logger.info(Messages.getString("Strategy.noDirectExtension", //$NON-NLS-1$
							                               strategyClass.getSimpleName(),
							                               Strategy.class.getSimpleName(),
							                               strategyClass.getSuperclass().getSimpleName()));
						}
					}
				}
			} catch (final ClassNotFoundException | WrongClassSearchMethodException | IOException
			        | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			        | NoSuchMethodException | SecurityException | InstantiationException e) {
				throw new UnrecoverableError(e);
			}
			
			map.put(this.enabledStrategiesOption.getName(), this.enabledStrategiesOption);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
