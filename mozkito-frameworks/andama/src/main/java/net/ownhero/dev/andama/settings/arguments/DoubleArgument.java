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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.Argument;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DoubleArgument extends Argument<Double> {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public DoubleArgument(@NotNull final ArgumentSet<?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, final String defaultValue,
	        @NotNull final Requirement requirements) throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue, requirements);
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
								// TODO ERRor log
							} else {
								setCachedValue(null);
								ret = true;
							}
						} else {
							try {
								setCachedValue(Double.valueOf(getStringValue()));
								ret = true;
							} catch (final NumberFormatException e) {
								if (Logger.logError()) {
									Logger.error("Value given for argument `" + getName()
									        + "` could not be interpreted as a Double value. Abort!");
								}
								
								ret = false;
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
