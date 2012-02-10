/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
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
public class EnumArgument<T extends Enum<?>> extends AndamaArgument<T> {
	
	private final HashSet<String> possibleValues = new HashSet<String>();
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param requirements
	 * @throws ArgumentRegistrationException
	 */
	public EnumArgument(@NotNull final AndamaArgumentSet<?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, final T defaultValue,
	        @NotNull final Requirement requirements) throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue.toString(), requirements);
		try {
			Class<?> enumType = ((Class<?>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
			for (int i = 0; i < enumType.getEnumConstants().length; ++i) {
				this.possibleValues.add(enumType.getEnumConstants()[i].toString());
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
	public EnumArgument(@NotNull final AndamaArgumentSet<?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, final T defaultValue,
	        @NotNull final Requirement requirements, @NotNull @NotEmpty final String[] possibleValues)
	        throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue.toString(), requirements);
		try {
			for (final String possibleValue : possibleValues) {
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
	@SuppressWarnings ("unchecked")
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						if (validStringValue()) {
							if (required()) {
								// TODO error logs
							} else {
								setCachedValue(null);
								ret = true;
							}
						} else {
							
							final String value = getStringValue().toUpperCase();
							
							if (!this.possibleValues.contains(value)) {
								if (Logger.logError()) {
									final StringBuilder ss = new StringBuilder();
									ss.append("Value `" + value + "` set for argument `");
									ss.append(getName());
									ss.append("` is invalid.");
									ss.append(System.getProperty("line.separator"));
									ss.append("Please choose one of the following possible values:");
									ss.append(System.getProperty("line.separator"));
									
									for (final String s : this.possibleValues) {
										ss.append("\t");
										ss.append(s);
										ss.append(System.getProperty("line.separator"));
									}
									
									Logger.error(ss.toString());
								}
								
								ret = false;
							} else {
								Class<?> enumType = ((Class<?>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
								
								for (int i = 0; i < enumType.getEnumConstants().length; ++i) {
									if (enumType.getEnumConstants()[i].toString()
									                                  .equalsIgnoreCase(getStringValue().trim())) {
										setCachedValue((T) enumType.getEnumConstants()[i]);
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
