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
package net.ownhero.dev.andama.settings.arguments;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.Argument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class EnumArgument<T extends Enum<?>> extends Argument<T> {
	
	private static Class<?> getEnum(final Type type) {
		if (type instanceof Enum) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return getEnum(((ParameterizedType) type).getRawType());
		} else {
			return null;
		}
	}
	
	private final HashSet<T> possibleValues = new HashSet<T>();
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param requirements
	 * @throws ArgumentRegistrationException
	 */
	@SuppressWarnings ("unchecked")
	public EnumArgument(@NotNull final ArgumentSet<?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, @NotNull final T defaultValue,
	        @NotNull final Requirement requirements) throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue != null
		                                                          ? defaultValue.toString()
		                                                          : null, requirements);
		try {
			if (defaultValue == null) {
				throw new ArgumentRegistrationException(
				                                        "Default values may not be null when not specifying possible values.");
			}
			
			final Class<?> enumType = defaultValue.getClass();
			for (int i = 0; i < enumType.getEnumConstants().length; ++i) {
				this.possibleValues.add((T) enumType.getEnumConstants()[i]);
			}
		} finally {
			Condition.notNull(this.possibleValues, "The set of possible values for %s must not be null.", getHandle());
			CollectionCondition.notEmpty(this.possibleValues, "The set of possible values for %s must not be empty.",
			                             getHandle());
		}
	}
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param requirements
	 * @param possibleValues
	 * @throws ArgumentRegistrationException
	 */
	public EnumArgument(@NotNull final ArgumentSet<?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, final T defaultValue,
	        @NotNull final Requirement requirements, @NotNull @NotEmpty final T[] possibleValues)
	        throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue != null
		                                                          ? defaultValue.toString()
		                                                          : null, requirements);
		try {
			for (final T possibleValue : possibleValues) {
				this.possibleValues.add(possibleValue);
			}
		} finally {
			Condition.notNull(this.possibleValues, "The set of possible values for %s must not be null.", getHandle());
			CollectionCondition.notEmpty(this.possibleValues, "The set of possible values for %s must not be empty.",
			                             getHandle());
			CollectionCondition.sameSize(this.possibleValues,
			                             possibleValues,
			                             "The size of the set of possible values for %s must be exactly the same as the one given in the constructor.",
			                             getHandle());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						if (!validStringValue()) {
							if (required()) {
								// TODO error logs
							} else {
								setCachedValue(null);
								ret = true;
							}
						} else {
							
							final String value = getStringValue().toUpperCase();
							@SuppressWarnings ({ "unchecked", "static-access" })
							final T valueOf = (T) this.possibleValues.iterator()
							                                         .next()
							                                         .valueOf(this.possibleValues.iterator().next()
							                                                                     .getClass(), value);
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
						}
					} else {
						ret = true;
					}
				}
			} else {
				ret = true;
			}
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
	
}
