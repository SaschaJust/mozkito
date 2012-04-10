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
package de.unisaarland.cs.st.moskito.mapping.selectors;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.register.Node;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * Selectors analyze a {@link MappableEntity} and find possible candidates that can be mapped to the entity, due to some
 * relation.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingSelector extends Node {
	
	/**
	 * The Class Options.
	 */
	static class Options extends ArgumentSetOptions<Set<MappingSelector>, ArgumentSet<Set<MappingSelector>, Options>> {
		
		/** The Constant TAG. */
		static final String         TAG         = "selectors";                                      //$NON-NLS-1$
		                                                                                             
		/** The Constant DESCRIPTION. */
		static final String         DESCRIPTION = Messages.getString("MappingSelector.description"); //$NON-NLS-1$
		                                                                                             
		/** The selectors option. */
		private SetArgument.Options selectorsOption;
		
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
		@Override
		public Set<MappingSelector> init() {
			// PRECONDITIONS
			// PRECONDITIONS
			final Set<MappingSelector> set = new HashSet<MappingSelector>();
			
			try {
				
				final SetArgument argument = getSettings().getArgument(this.selectorsOption);
				final HashSet<String> value = argument.getValue();
				
				for (final String name : value) {
					@SuppressWarnings ("unchecked")
					final Class<? extends MappingSelector> clazz = (Class<? extends MappingSelector>) Class.forName(name);
					final MappingSelector instance = clazz.newInstance();
					instance.init();
					set.add(instance);
				}
				
				return set;
			} catch (final ClassNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new UnrecoverableError(e);
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
					final Collection<Class<? extends MappingSelector>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
					                                                                                                     MappingSelector.class,
					                                                                                                     Modifier.ABSTRACT
					                                                                                                             | Modifier.INTERFACE
					                                                                                                             | Modifier.PRIVATE
					                                                                                                             | Modifier.PROTECTED);
					for (final Class<? extends MappingSelector> c : collection) {
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
				
				this.selectorsOption = new SetArgument.Options(
				                                               set,
				                                               "enabled", Messages.getString("MappingSelector.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$ 
				                                               defaultSet, Requirement.required);
				
				map.put(this.selectorsOption.getName(), this.selectorsOption);
				
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The settings. */
	private ISettings settings;
	
	/** The options. */
	private Options   options;
	
	/**
	 * Gets the anchor.
	 * 
	 * @param settings
	 *            the settings
	 * @return the anchor
	 * @throws SettingsParseError
	 *             the settings parse error
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	protected final ArgumentSet<?, ?> getAnchor(@NotNull final ISettings settings) throws SettingsParseError,
	                                                                              ArgumentSetRegistrationException,
	                                                                              ArgumentRegistrationException {
		ArgumentSet<?, ?> anchor = settings.getAnchor(MappingSelector.Options.TAG);
		
		if (anchor == null) {
			if (this.options == null) {
				this.options = new MappingSelector.Options(settings.getRoot(), Requirement.required);
			}
			anchor = ArgumentSetFactory.create(this.options);
		}
		
		return anchor;
	}
	
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	public final ISettings getSettings() {
		// PRECONDITIONS
		
		try {
			return this.settings;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.settings, "Field '%s' in '%s'.", "settings", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Parses the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param entity
	 *            the element under subject
	 * @param targetType
	 *            the target entity type of the candidate
	 * @param util
	 *            the util
	 * @return a list of {@link Candidate}s that might be mapped to the given entity
	 */
	public abstract <T extends MappableEntity> List<T> parse(MappableEntity entity,
	                                                         Class<T> targetType,
	                                                         PersistenceUtil util);
	
	/**
	 * Sets the settings.
	 * 
	 * @param settings
	 *            the settings to set
	 */
	protected final void setSettings(@NotNull final ISettings settings) {
		// PRECONDITIONS
		
		try {
			this.settings = settings;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.settings, settings,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Supports.
	 * 
	 * @param from
	 *            the 'from' entity
	 * @param to
	 *            the 'to' entity
	 * @return true if the selector supports this combination of entities
	 */
	public abstract boolean supports(Class<?> from,
	                                 Class<?> to);
}
