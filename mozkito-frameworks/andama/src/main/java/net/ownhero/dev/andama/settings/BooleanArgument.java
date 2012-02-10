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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class BooleanArgument extends AndamaArgument<Boolean> {
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param requirements
	 * @throws ArgumentRegistrationException
	 */
	public BooleanArgument(@NotNull final AndamaArgumentSet<?> argumentSet, @NotNull @NotEmptyString final String name,
	        @NotNull @NotEmptyString final String description, final String defaultValue,
	        @NotNull final Requirement requirements) throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#init()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						if (getStringValue() == null) {
							if (required()) {
								// TODO Error log
							} else {
								// TODO Warn log
								setCachedValue(null);
								ret = true;
							}
						} else if (getStringValue().trim().equals("")) {
							setCachedValue(true);
							ret = true;
						} else {
							setCachedValue(Boolean.parseBoolean(getStringValue()));
							ret = true;
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
