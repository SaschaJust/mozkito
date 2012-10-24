/***********************************************************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.mappings.splitters;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozkito.mappings.engines.Messages;
import org.mozkito.mappings.register.Node;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.PersistenceUtil;

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

/**
 * The Class MappingSplitter.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class MappingSplitter extends Node {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<Set<MappingSplitter>, ArgumentSet<Set<MappingSplitter>, Options>> {
		
		/** The Constant TAG. */
		private static final String                                                                           TAG           = "filters";
		
		/** The Constant DESCRIPTION. */
		private static final String                                                                           DESCRIPTION   = "...";
		
		/** The enabled splitters option. */
		private SetArgument.Options                                                                           enabledSplittersOption;
		
		/** The engine options. */
		private final Map<Class<? extends MappingSplitter>, ArgumentSetOptions<? extends MappingSplitter, ?>> engineOptions = new HashMap<>();
		
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
		public Set<MappingSplitter> init() {
			// PRECONDITIONS
			final Set<MappingSplitter> set = new HashSet<MappingSplitter>();
			
			try {
				
				final SetArgument argument = getSettings().getArgument(this.enabledSplittersOption);
				System.err.println(argument);
				final HashSet<String> value = argument.getValue();
				
				for (final String name : value) {
					Class<? extends MappingSplitter> clazz;
					try {
						clazz = (Class<? extends MappingSplitter>) Class.forName(MappingSplitter.class.getPackage()
						                                                                              .getName()
						        + '.'
						        + name);
					} catch (final ClassNotFoundException e) {
						throw new UnrecoverableError("Could not load engine '%s'. Does probably not exist. Aborting.");
						
					}
					
					final ArgumentSetOptions<? extends MappingSplitter, ?> options = this.engineOptions.get(clazz);
					if (options == null) {
						if (Logger.logWarn()) {
							Logger.warn("Splitter '%s' is lagging a configuration class. Make sure there is an internal class 'public static final Options extends %s<%s, %s<%s, Options>>' ",
							            clazz.getSimpleName(), ArgumentSetOptions.class.getSimpleName(),
							            clazz.getSimpleName(), ArgumentSet.class.getSimpleName(), clazz.getSimpleName());
						}
						
					} else {
						final ArgumentSet<? extends MappingSplitter, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
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
					final Collection<Class<? extends MappingSplitter>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
					                                                                                                     MappingSplitter.class,
					                                                                                                     Modifier.ABSTRACT
					                                                                                                             | Modifier.INTERFACE
					                                                                                                             | Modifier.PRIVATE
					                                                                                                             | Modifier.PROTECTED);
					for (final Class<? extends MappingSplitter> c : collection) {
						if (c.getSuperclass() == MappingSplitter.class) {
							final Class<?>[] declaredClasses = c.getDeclaredClasses();
							for (final Class<?> dC : declaredClasses) {
								if (ArgumentSetOptions.class.isAssignableFrom(dC)) {
									// found options
									@SuppressWarnings ("unchecked")
									final Constructor<ArgumentSetOptions<? extends MappingSplitter, ?>> constructor = (Constructor<ArgumentSetOptions<? extends MappingSplitter, ?>>) dC.getDeclaredConstructor(ArgumentSet.class,
									                                                                                                                                                                            Requirement.class);
									final ArgumentSetOptions<? extends MappingSplitter, ?> instance = constructor.newInstance(set,
									                                                                                          Requirement.required);
									this.engineOptions.put(c, instance);
									map.put(instance.getName(), instance);
								}
							}
						} else {
							if (Logger.logInfo()) {
								Logger.info("The class '%s' is not a direct extension of '%s' and has to be loaded by its parent '%s'.",
								            c.getSimpleName(), MappingSplitter.class.getSimpleName(), c.getSuperclass()
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
				
				this.enabledSplittersOption = new SetArgument.Options(
				                                                      set,
				                                                      "enabled", Messages.getString("MappingSplitter.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$
				                                                      defaultSet, Requirement.required);
				
				map.put(this.enabledSplittersOption.getName(), this.enabledSplittersOption);
				
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
		
		return new MappingSplitter.Options(set, Requirement.required);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.register.Registered#getDescription ()
	 */
	@Override
	public abstract String getDescription();
	
	/**
	 * Process.
	 * 
	 * @param util
	 *            the util
	 * @return the list
	 */
	public abstract List<Annotated> process(PersistenceUtil util);
}
