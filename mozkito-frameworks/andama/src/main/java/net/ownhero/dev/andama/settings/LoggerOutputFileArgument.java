/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

public class LoggerOutputFileArgument extends OutputFileArgument {
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.OutputFileArgument#init()
	 */
	LoggerOutputFileArgument(@NotNull final AndamaArgumentSet<?> argumentSet,
	        @NotNull @NotEmptyString final String name, @NotNull @NotEmptyString final String description,
	        final String defaultValue, @NotNull final Requirement requirements, final boolean overwrite)
	        throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue, requirements, overwrite);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.OutputFileArgument#init()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						setCachedValue(null);
						ret = true;
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
