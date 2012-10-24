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
/**
 * 
 */
package de.unisaarland.cs.st.mozkito.persons.engine;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.SettingsProvider;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.mozkito.persistence.model.Person;
import de.unisaarland.cs.st.mozkito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.mozkito.persons.elements.PersonBucket;
import de.unisaarland.cs.st.mozkito.persons.processing.PersonManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MergingEngine implements SettingsProvider {
	
	public static class Options extends
	        ArgumentSetOptions<Set<MergingEngine>, ArgumentSet<Set<MergingEngine>, Options>> {
		
		protected static final String tag         = "persons";                                             //$NON-NLS-1$
		private static final String   description = Messages.getString("MergingEngine.optionsDescription"); //$NON-NLS-1$
		private SetArgument.Options   personsOption;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, tag, description, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public Set<MergingEngine> init() {
			// PRECONDITIONS
			final Set<MergingEngine> set = new HashSet<MergingEngine>();
			String className = null;
			try {
				
				final SetArgument argument = getSettings().getArgument(this.personsOption);
				final HashSet<String> value = argument.getValue();
				System.err.println(JavaUtils.collectionToString(value));
				
				for (final String name : value) {
					className = name;
					@SuppressWarnings ("unchecked")
					final Class<? extends MergingEngine> clazz = (Class<? extends MergingEngine>) Class.forName(MergingEngine.class.getPackage()
					                                                                                                               .getName()
					        + "." + name);
					final MergingEngine instance = clazz.newInstance();
					instance.init();
					set.add(instance);
				}
				
				return set;
			} catch (final ClassNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new ClassLoadingError(e, className);
			} catch (final InstantiationException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new UnrecoverableError(e);
			} catch (final IllegalAccessException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new UnrecoverableError(e);
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
					final Collection<Class<? extends MergingEngine>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
					                                                                                                   MergingEngine.class,
					                                                                                                   Modifier.ABSTRACT
					                                                                                                           | Modifier.INTERFACE
					                                                                                                           | Modifier.PRIVATE
					                                                                                                           | Modifier.PROTECTED);
					for (final Class<? extends MergingEngine> c : collection) {
						defaultSet.add(c.getSimpleName());
					}
				} catch (final ClassNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
					
				} catch (final WrongClassSearchMethodException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
					
				} catch (final IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
					
				}
				
				this.personsOption = new SetArgument.Options(
				                                             set,
				                                             "enabled", Messages.getString("MergingEngine.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$
				                                             defaultSet, Requirement.required);
				
				map.put(this.personsOption.getName(), this.personsOption);
				
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	public MergingEngine() {
		
	}
	
	/**
	 * @param person
	 * @param container
	 * @param manager
	 * @param features
	 * @return
	 */
	public abstract List<PersonBucket> collides(Person person,
	                                            PersonContainer container,
	                                            PersonManager manager);
	
	/**
	 * @return
	 */
	public abstract String getDescription();
	
}
