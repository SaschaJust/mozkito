/*********************************************************************************************************************
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
 ********************************************************************************************************************/
package org.mozkito.mappings.engines;

import static net.ownhero.dev.ioda.StringUtils.truncate;

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
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.register.Node;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.settings.DatabaseOptions;

/**
 * 
 * Engines analyze two candidates to match certain criteria and score neutral (0) if they don't match or
 * positive/negative according to the criterion under suspect.
 * 
 * Generating feature vectors for the candidates is a task that is accomplished by the scoring node. In the scoring
 * step, reposuite uses all enabled engines to compute a vector consisting of confidence values. Every engine may have
 * its own configuration options that are required to execute reposuite as soon as the engine is enabled. If the engine
 * depends on certain storages, further configuration dependencies might be pulled in. An engine takes a candidate pair
 * an checks for certain criteria and scores accordingly. Engines can score in three ways: positive, if they consider a
 * pair a valid mapping, negative if they consider the pair to be a false positive or 0 if they can't decide on any of
 * that. A criterion of an engine should be as atomic as possible, that means an engine shouldn't check for multiple
 * criteria at a time. After all engines have been execute on one candidate pair, the resulting feature vector is stored
 * to the database (incl. the information what has been scored by each engine and what data was considered while
 * computing the score).
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class MappingEngine extends Node {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<Set<MappingEngine>, ArgumentSet<Set<MappingEngine>, Options>> {
		
		/** The Constant tag. */
		static final String                                                                               tag           = "engines";      //$NON-NLS-1$
		                                                                                                                                   
		/** The engines option. */
		private SetArgument.Options                                                                       enabledEnginesOption;
		
		private final Map<Class<? extends MappingEngine>, ArgumentSetOptions<? extends MappingEngine, ?>> engineOptions = new HashMap<>();
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, tag, "...", requirements); //$NON-NLS-1$
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@SuppressWarnings ({ "rawtypes", "unchecked" })
		@Override
		public Set<MappingEngine> init() {
			// PRECONDITIONS
			final Set<MappingEngine> set = new HashSet<MappingEngine>();
			
			try {
				
				final SetArgument argument = getSettings().getArgument(this.enabledEnginesOption);
				System.err.println(argument);
				final HashSet<String> value = argument.getValue();
				
				for (final String name : value) {
					Class<? extends MappingEngine> clazz;
					try {
						clazz = (Class<? extends MappingEngine>) Class.forName(MappingEngine.class.getPackage()
						                                                                          .getName()
						        + '.'
						        + name);
					} catch (final ClassNotFoundException e) {
						throw new UnrecoverableError(
						                             String.format("Could not load engine '%s'. Does probably not exist. Aborting.",
						                                           name));
						
					}
					
					final ArgumentSetOptions<? extends MappingEngine, ?> options = this.engineOptions.get(clazz);
					if (options == null) {
						if (Logger.logWarn()) {
							Logger.warn("Engine '%s' is lagging a configuration class. Make sure there is an internal class 'public static final Options extends %s<%s, %s<%s, Options>>' ",
							            clazz.getSimpleName(), ArgumentSetOptions.class.getSimpleName(),
							            clazz.getSimpleName(), ArgumentSet.class.getSimpleName(), clazz.getSimpleName());
						}
						
					} else {
						final ArgumentSet<? extends MappingEngine, ?> argumentSet = getSettings().getArgumentSet((IArgumentSetOptions) options);
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
					// first off, find all implemented engines
					final Collection<Class<? extends MappingEngine>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
					                                                                                                   MappingEngine.class,
					                                                                                                   Modifier.ABSTRACT
					                                                                                                           | Modifier.INTERFACE
					                                                                                                           | Modifier.PRIVATE
					                                                                                                           | Modifier.PROTECTED);
					
					// first compute the default value for the engine enabler option, i.e., all implemented engines
					for (final Class<? extends MappingEngine> engineClass : collection) {
						defaultSet.add(engineClass.getSimpleName());
					}
					
					// now create the enabler for the engines
					this.enabledEnginesOption = new SetArgument.Options(
					                                                    set,
					                                                    "enabled", Messages.getString("MappingEngine.enabledDescription"), //$NON-NLS-1$ //$NON-NLS-2$
					                                                    defaultSet, Requirement.required);
					
					// iterate over the engine classes to process dependencies
					for (final Class<? extends MappingEngine> engineClass : collection) {
						// loading of engines is in the responsibility of the direct parent class, thus we only process
						// mapping classes of direct extensions of MappingEngine
						if (engineClass.getSuperclass() == MappingEngine.class) {
							// MappingEngines have to encapsulate their initializer/options. fetch them first.
							final Class<?>[] declaredClasses = engineClass.getDeclaredClasses();
							
							for (final Class<?> engineOptionClass : declaredClasses) {
								// check if we found the options to initialize the engine under suspect
								if (ArgumentSetOptions.class.isAssignableFrom(engineOptionClass)) {
									// found options
									
									// fetch constructor of the options
									@SuppressWarnings ("unchecked")
									final Constructor<ArgumentSetOptions<? extends MappingEngine, ?>> constructor = (Constructor<ArgumentSetOptions<? extends MappingEngine, ?>>) engineOptionClass.getDeclaredConstructor(ArgumentSet.class,
									                                                                                                                                                                                       Requirement.class);
									
									// instantiate the options and set to required if enabledEnginesOptions contains the
									// simple classname of the engine under suspect, i.e., c.getSimpleName()
									final ArgumentSetOptions<? extends MappingEngine, ?> engineOption = constructor.newInstance(set,
									                                                                                            Requirement.contains(this.enabledEnginesOption,
									                                                                                                                 engineClass.getSimpleName()));
									System.out.println(engineOption
									        + " is "
									        + (Requirement.contains(this.enabledEnginesOption,
									                                engineClass.getSimpleName()).check()
									                                                                    ? "required"
									                                                                    : "unrequired"));
									
									if (Logger.logDebug()) {
										Logger.debug("Adding new mapping engines dependency '%s' with list activator '%s'",
										             engineOption.getTag(), this.enabledEnginesOption.getTag());
									}
									
									this.engineOptions.put(engineClass, engineOption);
									map.put(engineOption.getName(), engineOption);
								}
							}
						} else {
							if (Logger.logInfo()) {
								Logger.info("The class '%s' is not a direct extension of '%s' and has to be loaded by its parent '%s'.",
								            engineClass.getSimpleName(), MappingEngine.class.getSimpleName(),
								            engineClass.getSuperclass().getSimpleName());
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
	
	/** The Constant defaultNegative. */
	private static final String DEFAULT_NEGATIVE = "-1";                                       //$NON-NLS-1$
	                                                                                            
	/** The Constant defaultPositive. */
	private static final String DEFAULT_POSITIVE = "1";                                        //$NON-NLS-1$
	                                                                                            
	/** The Constant unknown. */
	private static final String UNKNOWN          = Messages.getString("MappingEngine.unknown"); //$NON-NLS-1$
	                                                                                            
	/** The Constant unused. */
	private static final String UNUSED           = Messages.getString("MappingEngine.unused"); //$NON-NLS-1$
	                                                                                            
	/**
	 * Gets the default negative.
	 * 
	 * @return the defaultNegative
	 */
	public static String getDefaultNegative() {
		return DEFAULT_NEGATIVE;
	}
	
	/**
	 * Gets the default positive.
	 * 
	 * @return the defaultPositive
	 */
	public static String getDefaultPositive() {
		return DEFAULT_POSITIVE;
	}
	
	/**
	 * Gets the options.
	 * 
	 * @param settings
	 *            the settings
	 * @return the options
	 */
	public static final Options getOptions(@NotNull final ArgumentSet<?, ?> set) {
		
		return new MappingEngine.Options(set, Requirement.required);
	}
	
	/**
	 * Gets the unknown.
	 * 
	 * @return the unknown
	 */
	public static String getUnknown() {
		return UNKNOWN;
	}
	
	/**
	 * Gets the unused.
	 * 
	 * @return the unused
	 */
	public static String getUnused() {
		return UNUSED;
	}
	
	private ISettings settings;
	
	/**
	 * Using this method, one can add features to a given {@link Relation}. The given score will be manipulated using
	 * the values given. The values are automatically <code>null</code> checked and truncated if needed.
	 * 
	 * @param score
	 *            the {@link Relation} a new feature shall be added
	 * @param confidence
	 *            a confidence value representing the impact of the feature
	 * @param fromFieldName
	 *            the name of the field (see {@link FieldKey}) of the "from" entity that caused this feature
	 * @param fromFieldContent
	 *            the content of the field (see {@link FieldKey}) of the "from" entity that caused this feature
	 * @param fromSubstring
	 *            the particular substring of the field (see {@link FieldKey}) of the "from" entity that caused this
	 *            feature
	 * @param toFieldName
	 *            the name of the field (see {@link FieldKey}) of the "to" entity that caused this feature
	 * @param toFieldContent
	 *            the content of the field (see {@link FieldKey}) of the "to" entity that caused this feature
	 * @param toSubstring
	 *            the particular substring of the field (see {@link FieldKey}) of the "to" entity that caused this
	 *            feature
	 */
	public final void addFeature(@NotNull final Relation score,
	                             final double confidence,
	                             @NotNull @NotEmpty final String fromFieldName,
	                             final Object fromFieldContent,
	                             final Object fromSubstring,
	                             @NotNull @NotEmpty final String toFieldName,
	                             final Object toFieldContent,
	                             final Object toSubstring) {
		score.addFeature(confidence, truncate(fromFieldName != null
		                                                           ? fromFieldName
		                                                           : UNUSED),
		                 truncate(fromFieldContent != null
		                                                  ? fromFieldContent.toString()
		                                                  : UNKNOWN),
		                 truncate(fromSubstring != null
		                                               ? fromSubstring.toString()
		                                               : truncate(fromFieldContent != null
		                                                                                  ? fromFieldContent.toString()
		                                                                                  : UNKNOWN)),
		                 truncate(toFieldName != null
		                                             ? toFieldName
		                                             : UNUSED),
		                 truncate(toFieldContent != null
		                                                ? toFieldContent.toString()
		                                                : UNKNOWN),
		                 truncate(toSubstring != null
		                                             ? toSubstring.toString()
		                                             : truncate(toFieldContent != null
		                                                                              ? toFieldContent.toString()
		                                                                              : UNKNOWN)), getClass());
	}
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistence util
	 */
	public org.mozkito.persistence.PersistenceUtil getPersistenceUtil() {
		final ArgumentSet<?, ?> set = getSettings().getAnchor("database"); //$NON-NLS-1$
		@SuppressWarnings ("unchecked")
		final ArgumentSet<org.mozkito.persistence.PersistenceUtil, DatabaseOptions> databaseArgumentSet = (ArgumentSet<org.mozkito.persistence.PersistenceUtil, DatabaseOptions>) set;
		final org.mozkito.persistence.PersistenceUtil persistenceUtil = databaseArgumentSet.getValue();
		return persistenceUtil;
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
	 * Score.
	 * 
	 * @param from
	 *            the 'from' entity
	 * @param to
	 *            the 'to' entity
	 * @param score
	 *            the actual {@link Relation} that will be manipulated by this method
	 */
	@NoneNull
	public abstract void score(final MappableEntity from,
	                           final MappableEntity to,
	                           final Relation score);
	
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
	 * Supported.
	 * 
	 * @return an instance of {@link Expression} that represents the support of this engine
	 */
	public abstract Expression supported();
	
}
