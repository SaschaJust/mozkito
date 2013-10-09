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

package org.mozkito.infozilla.settings;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.settings.filters.FilterOptionsPinPoint;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * The Class FilterOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class FilterOptions extends ArgumentSetOptions<Set<Filter<?>>, ArgumentSet<Set<Filter<?>>, FilterOptions>> {
	
	private SetArgument.Options                                                               enabledFiltersOption;
	
	private final Map<Class<? extends Filter<?>>, ArgumentSetOptions<? extends Filter<?>, ?>> filterOptions = new HashMap<>();
	
	/**
	 * Instantiates a new filter options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public FilterOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "filters", "options for the active filters", requirements);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public Set<Filter<?>> init() {
		final Set<Filter<?>> set = new HashSet<>();
		
		try {
			final SetArgument argument = getSettings().getArgument(this.enabledFiltersOption);
			final HashSet<String> value = argument.getValue();
			
			for (final String name : value) {
				// TOD for (final String name : value) {
				Class<? extends Filter<?>> clazz;
				try {
					clazz = (Class<? extends Filter<?>>) Class.forName(Filter.class.getPackage().getName() + '.' + name);
				} catch (final ClassNotFoundException e) {
					throw new Shutdown(String.format("", //$NON-NLS-1$
					                                 name));
					
				}
				
				final ArgumentSetOptions<? extends Filter<?>, ?> options = this.filterOptions.get(clazz);
				if (options == null) {
					if (Logger.logWarn()) {
						Logger.warn("TODO");
					}
					
				} else {
					final ArgumentSet<? extends Filter<?>, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
					set.add(argumentSet.getValue());
				}
			}
			
			return set;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		PRECONDITIONS: {
			if (set == null) {
				throw new NullPointerException();
			}
		}
		
		final HashMap<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
		final HashSet<String> defaultSet = new HashSet<String>();
		
		try {
			// first off, find all implemented engines
			final Collection<Class<? extends Filter>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
			                                                                                            Filter.class,
			                                                                                            Modifier.ABSTRACT
			                                                                                                    | Modifier.INTERFACE
			                                                                                                    | Modifier.PRIVATE
			                                                                                                    | Modifier.PROTECTED);
			
			final Collection<Class<? extends ArgumentSetOptions>> collection2 = ClassFinder.getClassesExtendingClass(FilterOptionsPinPoint.class.getPackage(),
			                                                                                                         ArgumentSetOptions.class,
			                                                                                                         Modifier.ABSTRACT
			                                                                                                                 | Modifier.INTERFACE
			                                                                                                                 | Modifier.PRIVATE
			                                                                                                                 | Modifier.PROTECTED);
			
			// first compute the default value for the engine enabler option, i.e., all implemented engines
			for (final Class<? extends Filter> filterClass : collection) {
				defaultSet.add(filterClass.getSimpleName());
			}
			
			// now create the enabler for the engines
			this.enabledFiltersOption = new SetArgument.Options(set, "enabled", "description", defaultSet,
			                                                    Requirement.required);
			
			// iterate over the engine classes to process dependencies
			FILTER_CLASS: for (final Class<? extends Filter> filterClass : collection) {
				// loading of engines is in the responsibility of the direct parent class, thus we only process
				if (filterClass.getSuperclass() == Filter.class) {
					boolean foundOptionClass = false;
					FILTER_OPTIONS: for (final Class<? extends ArgumentSetOptions> argumentSetOptionsClass : collection2) {
						for (final Method method : argumentSetOptionsClass.getMethods()) {
							if (filterClass.equals(method.getReturnType())) {
								SANITY: {
									assert ArgumentSetOptions.class.isAssignableFrom(argumentSetOptionsClass);
								}
								foundOptionClass = true;
								final Constructor<ArgumentSetOptions<? extends Filter<?>, ?>> constructor = (Constructor<ArgumentSetOptions<? extends Filter<?>, ?>>) argumentSetOptionsClass.getDeclaredConstructor(ArgumentSet.class,
								                                                                                                                                                                                     Requirement.class);
								SANITY: {
									assert constructor != null;
								}
								
								// instantiate the options and set to required if enabledEnginesOptions contains the
								// simple classname of the engine under suspect, i.e., c.getSimpleName()
								final ArgumentSetOptions<? extends Filter<?>, ?> filterOption = constructor.newInstance(set,
								                                                                                        Requirement.contains(this.enabledFiltersOption,
								                                                                                                             filterClass.getSimpleName()));
								this.filterOptions.put((Class<? extends Filter<?>>) filterClass, filterOption);
								
								if (filterOption.required()) {
									map.put(filterOption.getName(), filterOption);
								}
								continue FILTER_CLASS;
							}
						}
					}
					
					if (!foundOptionClass) {
						throw new Shutdown("TODO");
					}
				} else {
					if (Logger.logInfo()) {
						Logger.info("TODO");
					}
				}
			}
		} catch (final ClassNotFoundException | WrongClassSearchMethodException | IOException
		        | IllegalArgumentException | SecurityException | NoSuchMethodException | InstantiationException
		        | IllegalAccessException | InvocationTargetException e) {
			throw new UnrecoverableError(e);
		}
		
		map.put(this.enabledFiltersOption.getName(), this.enabledFiltersOption);
		
		return map;
		
	}
	
}
