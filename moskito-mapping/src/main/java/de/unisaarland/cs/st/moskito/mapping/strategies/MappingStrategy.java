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
package de.unisaarland.cs.st.moskito.mapping.strategies;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.Messages;
import de.unisaarland.cs.st.moskito.mapping.model.Composite;
import de.unisaarland.cs.st.moskito.mapping.register.Node;

/**
 * 
 * A strategy determines the way reposuite decides whether a mapping is valid or not. In a TotalAgreement strategy all
 * engines have to agree on a valid mapping.
 * 
 * Reposuite relies on a strategy to be used when computing the actual mappings. In this step, reposuite fetches all
 * MapScores from the previous step from the persistence provider and evaluates the feature vector according to the
 * selected strategy. E.g. if a TotalConfidence strategy is used, reposuite will only consider mappings as valid, if and
 * only if the total confidence (the sum of all individual scores from the engines) yields a positive result. In a veto
 * strategies, all mappings that have at least one negative value in the feature vector are dropped. Certain strategies
 * rely on storages. E.g. the SVM strategy uses a model that has been build beforehand by having a support vector
 * machine train on already mapped and verified data. If a mapping has passed the strategy checks it is persisted in the
 * database.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingStrategy extends Node {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<Set<MappingStrategy>, ArgumentSet<Set<MappingStrategy>, Options>> {
		
		/** The Constant TAG. */
		private static final String                                                                           TAG           = "strategies";   //$NON-NLS-1$
		                                                                                                                                       
		/** The Constant DESCRIPTION. */
		private static final String                                                                           DESCRIPTION   = "...";
		
		/** The enabled strategies option. */
		private SetArgument.Options                                                                           enabledStrategiesOption;
		
		/** The engine options. */
		private final Map<Class<? extends MappingStrategy>, ArgumentSetOptions<? extends MappingStrategy, ?>> engineOptions = new HashMap<>();
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TAG, DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@SuppressWarnings ({ "unchecked", "rawtypes" })
		@Override
		public Set<MappingStrategy> init() {
			// PRECONDITIONS
			final Set<MappingStrategy> set = new HashSet<>();
			
			try {
				
				final SetArgument argument = getSettings().getArgument(this.enabledStrategiesOption);
				System.err.println(argument);
				final HashSet<String> value = argument.getValue();
				
				for (final String name : value) {
					Class<? extends MappingStrategy> clazz;
					try {
						clazz = (Class<? extends MappingStrategy>) Class.forName(MappingStrategy.class.getPackage()
						                                                                              .getName()
						        + '.'
						        + name);
					} catch (final ClassNotFoundException e) {
						throw new UnrecoverableError("Could not load strategy '%s'. Does probably not exist. Aborting.");
						
					}
					
					final ArgumentSetOptions<? extends MappingStrategy, ?> options = this.engineOptions.get(clazz);
					if (options == null) {
						if (Logger.logWarn()) {
							Logger.warn("Engine '%s' is lagging a configuration class. Make sure there is an internal class 'public static final Options extends %s<%s, %s<%s, Options>>' ",
							            clazz.getSimpleName(), ArgumentSetOptions.class.getSimpleName(),
							            clazz.getSimpleName(), ArgumentSet.class.getSimpleName(), clazz.getSimpleName());
						}
						
					} else {
						final ArgumentSet<? extends MappingStrategy, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
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
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
		                                                                            SettingsParseError {
			// PRECONDITIONS
			
			try {
				final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
				
				final HashSet<String> defaultSet = new HashSet<String>();
				
				try {
					final Collection<Class<? extends MappingStrategy>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
					                                                                                                     MappingStrategy.class,
					                                                                                                     Modifier.ABSTRACT
					                                                                                                             | Modifier.INTERFACE
					                                                                                                             | Modifier.PRIVATE
					                                                                                                             | Modifier.PROTECTED);
					for (final Class<? extends MappingStrategy> c : collection) {
						if (c.getSuperclass() == MappingStrategy.class) {
							final Class<?>[] declaredClasses = c.getDeclaredClasses();
							for (final Class<?> dC : declaredClasses) {
								if (ArgumentSetOptions.class.isAssignableFrom(dC)) {
									// found options
									@SuppressWarnings ("unchecked")
									final Constructor<ArgumentSetOptions<? extends MappingStrategy, ?>> constructor = (Constructor<ArgumentSetOptions<? extends MappingStrategy, ?>>) dC.getDeclaredConstructor(ArgumentSet.class,
									                                                                                                                                                                            Requirement.class);
									final ArgumentSetOptions<? extends MappingStrategy, ?> instance = constructor.newInstance(set,
									                                                                                          Requirement.required);
									this.engineOptions.put(c, instance);
									map.put(instance.getName(), instance);
								}
							}
						} else {
							if (Logger.logInfo()) {
								Logger.info("The class '%s' is not a direct extension of '%s' and has to be loaded by its parent '%s'.",
								            c.getSimpleName(), MappingStrategy.class.getSimpleName(), c.getSuperclass()
								                                                                       .getSimpleName());
							}
						}
						
						defaultSet.add(c.getSimpleName());
						
					}
				} catch (final ClassNotFoundException | WrongClassSearchMethodException | IOException
				        | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				        | NoSuchMethodException | SecurityException | InstantiationException e) {
					throw new UnrecoverableError(e);
				}
				
				this.enabledStrategiesOption = new SetArgument.Options(
				                                                       set,
				                                                       "enabled", Messages.getString("MappingStrategy.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$
				                                                       defaultSet, Requirement.required);
				
				map.put(this.enabledStrategiesOption.getName(), this.enabledStrategiesOption);
				
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * Gets the options.
	 * 
	 * @param set
	 *            the set
	 * @return the options
	 */
	public static final Options getOptions(@NotNull final ArgumentSet<?, ?> set) {
		return new MappingStrategy.Options(set, Requirement.required);
	}
	
	/**
	 * Map.
	 * 
	 * @param mapping
	 *            the mapping
	 * @return the i mapping
	 */
	public abstract Composite map(Composite mapping);
	
}
