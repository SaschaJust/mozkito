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
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.selectors.Selector;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class SelectorOptions extends ArgumentSetOptions<Set<Selector>, ArgumentSet<Set<Selector>, SelectorOptions>> {
	
	/** The enabled selectors option. */
	private SetArgument.Options                                                             enabledSelectorsOption;
	
	/** The selector options. */
	private final Map<Class<? extends Selector>, ArgumentSetOptions<? extends Selector, ?>> selectorOptions = new HashMap<>();
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public SelectorOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, Selector.TAG, Selector.DESCRIPTION, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public Set<Selector> init() {
		// PRECONDITIONS
		final Set<Selector> set = new HashSet<Selector>();
		
		try {
			
			final SetArgument argument = getSettings().getArgument(this.enabledSelectorsOption);
			final HashSet<String> value = argument.getValue();
			
			for (final String name : value) {
				if (Logger.logDebug()) {
					Logger.debug("Processing selector enabler '%s'.", name); //$NON-NLS-1$
				}
				
				Class<? extends Selector> clazz;
				try {
					clazz = (Class<? extends Selector>) Class.forName(Selector.class.getPackage().getName() + '.'
					        + name);
				} catch (final ClassNotFoundException e) {
					throw new UnrecoverableError(Messages.getString("loadingFailure", getHandle())); //$NON-NLS-1$
					
				}
				
				final ArgumentSetOptions<? extends Selector, ?> options = this.selectorOptions.get(clazz);
				if (options == null) {
					if (Logger.logWarn()) {
						Logger.warn(Messages.getString("Selector.laggingConfigClass", //$NON-NLS-1$
						                               clazz.getSimpleName(), ArgumentSetOptions.class.getSimpleName(),
						                               clazz.getSimpleName(), ArgumentSet.class.getSimpleName(),
						                               clazz.getSimpleName()));
					}
					
				} else {
					final ArgumentSet<? extends Selector, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
					final Selector selector = argumentSet.getValue();
					if (Logger.logDebug()) {
						Logger.debug("Adding configured selector '%s'.", selector); //$NON-NLS-1$
					}
					set.add(selector);
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
				// first off, find all implemented selectors
				final Collection<Class<? extends Selector>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
				                                                                                              Selector.class,
				                                                                                              Modifier.ABSTRACT
				                                                                                                      | Modifier.INTERFACE
				                                                                                                      | Modifier.PRIVATE
				                                                                                                      | Modifier.PROTECTED);
				
				// first compute the default value for the selector enabler option, i.e., all implemented selectors
				for (final Class<? extends Selector> selectorClass : collection) {
					defaultSet.add(selectorClass.getSimpleName());
				}
				
				// now create the enabler for the selectors
				this.enabledSelectorsOption = new SetArgument.Options(
				                                                      set,
				                                                      "enabled", Messages.getString("MappingSelector.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$
				                                                      defaultSet, Requirement.required);
				
				// iterate over the selector classes to process dependencies
				for (final Class<? extends Selector> selectorClass : collection) {
					// loading of selectors is in the responsibility of the direct parent class, thus we only
					// process
					// mapping classes of direct extensions of MappingSelector
					if (selectorClass.getSuperclass() == Selector.class) {
						// MappingSelectors have to encapsulate their initializer/options. fetch them first.
						final Class<?>[] declaredClasses = selectorClass.getDeclaredClasses();
						
						for (final Class<?> selectorOptionClass : declaredClasses) {
							// check if we found the options to initialize the selector under suspect
							if (ArgumentSetOptions.class.isAssignableFrom(selectorOptionClass)) {
								// found options
								
								// fetch constructor of the options
								@SuppressWarnings ("unchecked")
								final Constructor<ArgumentSetOptions<? extends Selector, ?>> constructor = (Constructor<ArgumentSetOptions<? extends Selector, ?>>) selectorOptionClass.getDeclaredConstructor(ArgumentSet.class,
								                                                                                                                                                                               Requirement.class);
								
								// instantiate the options and set to required if enabledSelectorsOptions contains
								// the
								// simple classname of the selector under suspect, i.e., c.getSimpleName()
								final ArgumentSetOptions<? extends Selector, ?> selectorOption = constructor.newInstance(set,
								                                                                                         Requirement.contains(this.enabledSelectorsOption,
								                                                                                                              selectorClass.getSimpleName()));
								
								if (Logger.logDebug()) {
									Logger.debug("Adding new mapping selectors dependency '%s' with list activator '%s'", //$NON-NLS-1$
									             selectorOption.getTag(), this.enabledSelectorsOption.getTag());
								}
								
								this.selectorOptions.put(selectorClass, selectorOption);
								map.put(selectorOption.getName(), selectorOption);
							}
						}
					} else {
						if (Logger.logInfo()) {
							Logger.info(Messages.getString("Selector.noDirectExtension", //$NON-NLS-1$
							                               selectorClass.getSimpleName(),
							                               Selector.class.getSimpleName(),
							                               selectorClass.getSuperclass().getSimpleName()));
						}
					}
				}
			} catch (final ClassNotFoundException | WrongClassSearchMethodException | IOException
			        | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			        | NoSuchMethodException | SecurityException | InstantiationException e) {
				throw new UnrecoverableError(e);
			}
			
			map.put(this.enabledSelectorsOption.getName(), this.enabledSelectorsOption);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
