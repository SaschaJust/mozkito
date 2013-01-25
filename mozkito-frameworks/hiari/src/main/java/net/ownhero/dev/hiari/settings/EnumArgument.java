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
package net.ownhero.dev.hiari.settings;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class EnumArgument.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class EnumArgument<T extends Enum<?>> extends Argument<T, EnumArgument.Options<T>> {
	
	/**
	 * The Class EnumArgumentOptions.
	 * 
	 * @param <X>
	 *            the generic type
	 */
	public static class Options<X extends Enum<?>> extends ArgumentOptions<X, EnumArgument<X>> {
		
		/** The possible values. */
		private final HashSet<X> possibleValues = new HashSet<X>();
		
		/**
		 * Instantiates a new enum argument options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param name
		 *            the name
		 * @param description
		 *            the description
		 * @param defaultValue
		 *            the default value
		 * @param requirements
		 *            the requirements
		 */
		@SuppressWarnings ("unchecked")
		public Options(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
		        @NotNull @NotEmptyString final String description, @NotNull final X defaultValue,
		        @NotNull final Requirement requirements) {
			super(argumentSet, name, description, defaultValue, requirements);
			
			try {
				if (defaultValue != null) {
					final Class<?> enumType = defaultValue.getClass();
					
					for (int i = 0; i < enumType.getEnumConstants().length; ++i) {
						this.possibleValues.add((X) enumType.getEnumConstants()[i]);
					}
				}
			} finally {
				Condition.notNull(this.possibleValues, "The set of possible values for %s must not be null.",
				                  getHandle());
				CollectionCondition.notEmpty(this.possibleValues,
				                             "The set of possible values for %s must not be empty.", getHandle());
			}
		}
		
		/**
		 * Instantiates a new enum argument options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param name
		 *            the name
		 * @param description
		 *            the description
		 * @param defaultValue
		 *            the default value
		 * @param requirements
		 *            the requirements
		 * @param possibleValues
		 *            the possible values
		 */
		public Options(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
		        @NotNull @NotEmptyString final String description, final X defaultValue,
		        @NotNull final Requirement requirements, @NotNull @NotEmpty final X[] possibleValues) {
			super(argumentSet, name, description, defaultValue, requirements);
			
			try {
				for (final X possibleValue : possibleValues) {
					this.possibleValues.add(possibleValue);
				}
			} finally {
				Condition.notNull(this.possibleValues, "The set of possible values for %s must not be null.",
				                  getHandle());
				CollectionCondition.notEmpty(this.possibleValues,
				                             "The set of possible values for %s must not be empty.", getHandle());
				CollectionCondition.sameSize(this.possibleValues,
				                             possibleValues,
				                             "The size of the set of possible values for %s must be exactly the same as the one given in the constructor.",
				                             getHandle());
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.andama.settings.ArgumentOptions#getAdditionalHelpString()
		 */
		/**
		 * {@inheritDoc}
		 * 
		 * @see net.ownhero.dev.hiari.settings.ArgumentOptions#getAdditionalHelpString()
		 */
		@Override
		public String getAdditionalHelpString() {
			// PRECONDITIONS
			
			try {
				return "Valid values: " + JavaUtils.collectionToString(getPossibleValues());
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * Gets the possible values.
		 * 
		 * @return the possibleValues
		 */
		public final HashSet<X> getPossibleValues() {
			// PRECONDITIONS
			
			try {
				return this.possibleValues;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.possibleValues, "The set of possible values for %s must not be null.",
				                  getHandle());
				CollectionCondition.notEmpty(this.possibleValues,
				                             "The set of possible values for %s must not be empty.", getHandle());
			}
		}
	}
	
	/**
	 * Gets the enum.
	 * 
	 * @param type
	 *            the type
	 * @return the enum
	 */
	private static Class<?> getEnum(final Type type) {
		if (type instanceof Enum) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return getEnum(((ParameterizedType) type).getRawType());
		} else {
			return null;
		}
	}
	
	/** The possible values. */
	private final HashSet<T> possibleValues = new HashSet<T>();
	
	/**
	 * Instantiates a new enum argument.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	protected EnumArgument(@NotNull final Options<T> options) throws ArgumentRegistrationException {
		super(options);
		
		this.possibleValues.clear();
		this.possibleValues.addAll(options.getPossibleValues());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.Argument#init()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!validStringValue()) {
				if (required()) {
					if (Logger.logError()) {
						Logger.error("Argument required but doesn't have a valid string value (from options '%s').",
						             getOptions());
					}
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Optional argument is not set: %s", getTag());
					}
					setCachedValue(null);
					ret = true;
				}
			} else {
				
				final String value = getStringValue().toUpperCase();
				
				try {
					@SuppressWarnings ({ "unchecked" })
					final T valueOf = (T) Enum.valueOf(this.possibleValues.iterator().next().getClass(), value);
					if (!this.possibleValues.contains(valueOf)) {
						if (Logger.logError()) {
							final StringBuilder ss = new StringBuilder();
							ss.append("Value `" + value + "` set for argument `");
							ss.append(getName());
							ss.append("` is invalid.");
							ss.append(System.getProperty("line.separator"));
							ss.append("Please choose one of the following possible values:");
							ss.append(System.getProperty("line.separator"));
							
							for (final T s : this.possibleValues) {
								ss.append("\t");
								ss.append(s);
								ss.append(System.getProperty("line.separator"));
							}
							
							Logger.error(ss.toString());
						}
						
						ret = false;
					} else {
						for (final T val : this.possibleValues) {
							if (val.toString().equalsIgnoreCase(getStringValue().trim())) {
								setCachedValue(val);
								ret = true;
								break;
							}
						}
					}
				} catch (final IllegalArgumentException e) {
					ret = false;
				}
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
}
