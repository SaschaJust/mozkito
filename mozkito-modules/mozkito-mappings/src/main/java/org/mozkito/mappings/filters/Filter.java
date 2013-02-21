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
package org.mozkito.mappings.filters;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.register.Node;
import org.mozkito.mappings.requirements.Expression;

/**
 * The Class MappingFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Filter extends Node {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends ArgumentSetOptions<Set<Filter>, ArgumentSet<Set<Filter>, Options>> {
		
		/** The enabled filters option. */
		private SetArgument.Options                                                         enabledFiltersOption;
		
		/** The engine options. */
		private final Map<Class<? extends Filter>, ArgumentSetOptions<? extends Filter, ?>> engineOptions = new HashMap<>();
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, Filter.TAG, Filter.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@SuppressWarnings ({ "rawtypes", "unchecked" })
		@Override
		public Set<Filter> init() {
			// PRECONDITIONS
			final Set<Filter> set = new HashSet<Filter>();
			
			try {
				
				final SetArgument argument = getSettings().getArgument(this.enabledFiltersOption);
				final HashSet<String> value = argument.getValue();
				
				for (final String name : value) {
					Class<? extends Filter> clazz;
					try {
						clazz = (Class<? extends Filter>) Class.forName(Filter.class.getPackage().getName() + '.'
						        + name);
					} catch (final ClassNotFoundException e) {
						throw new UnrecoverableError(Messages.getString("Filter.loadingFailure")); //$NON-NLS-1$
						
					}
					
					final ArgumentSetOptions<? extends Filter, ?> options = this.engineOptions.get(clazz);
					if (options == null) {
						if (Logger.logWarn()) {
							Logger.warn(Messages.getString("Filter.laggingConfigClass"), //$NON-NLS-1$
							            clazz.getSimpleName(), ArgumentSetOptions.class.getSimpleName(),
							            clazz.getSimpleName(), ArgumentSet.class.getSimpleName(), clazz.getSimpleName());
						}
						
					} else {
						final ArgumentSet<? extends Filter, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
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
					final Collection<Class<? extends Filter>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
					                                                                                            Filter.class,
					                                                                                            Modifier.ABSTRACT
					                                                                                                    | Modifier.INTERFACE
					                                                                                                    | Modifier.PRIVATE
					                                                                                                    | Modifier.PROTECTED);
					for (final Class<? extends Filter> c : collection) {
						if (c.getSuperclass() == Filter.class) {
							final Class<?>[] declaredClasses = c.getDeclaredClasses();
							for (final Class<?> dC : declaredClasses) {
								if (ArgumentSetOptions.class.isAssignableFrom(dC)) {
									// found options
									@SuppressWarnings ("unchecked")
									final Constructor<ArgumentSetOptions<? extends Filter, ?>> constructor = (Constructor<ArgumentSetOptions<? extends Filter, ?>>) dC.getDeclaredConstructor(ArgumentSet.class,
									                                                                                                                                                          Requirement.class);
									final ArgumentSetOptions<? extends Filter, ?> instance = constructor.newInstance(set,
									                                                                                 Requirement.required);
									this.engineOptions.put(c, instance);
									map.put(instance.getName(), instance);
								}
							}
						} else {
							if (Logger.logInfo()) {
								Logger.info(Messages.getString("Filter.noDirectExtension"), //$NON-NLS-1$
								            c.getSimpleName(), Filter.class.getSimpleName(), c.getSuperclass()
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
				
				this.enabledFiltersOption = new SetArgument.Options(
				                                                    set,
				                                                    "enabled", Messages.getString("Filter.optionEnabled"), //$NON-NLS-1$ //$NON-NLS-2$
				                                                    defaultSet, Requirement.required);
				
				map.put(this.enabledFiltersOption.getName(), this.enabledFiltersOption);
				
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = Messages.getString("Filter.description"); //$NON-NLS-1$
	/** The Constant TAG. */
	private static final String TAG         = "filters";                               //$NON-NLS-1$
	                                                                                    
	/**
	 * Gets the options.
	 * 
	 * @param set
	 *            the set
	 * @return the options
	 */
	public static final Options getOptions(@NotNull final ArgumentSet<?, ?> set) {
		
		return new Filter.Options(set, Requirement.required);
	}
	
	/**
	 * Instantiates a new mapping filter.
	 */
	public Filter() {
		
	}
	
	/**
	 * Filter.
	 * 
	 * @param mapping
	 *            the mapping
	 * @return the mapping
	 */
	@NoneNull
	public abstract Mapping filter(final Mapping mapping);
	
	/**
	 * Supported.
	 * 
	 * @return the expression
	 */
	public abstract Expression supported();
	
}
