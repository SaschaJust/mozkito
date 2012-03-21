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

import java.io.File;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class FileArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class InputFileArgument extends Argument<File, InputFileArgument.Options> {
	
	/**
	 * The Class InputFileArgumentOptions.
	 */
	public static class Options extends ArgumentOptions<File, InputFileArgument> {
		
		/**
		 * Instantiates a new input file argument options.
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
		public Options(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
		        @NotNull @NotEmptyString final String description, final File defaultValue,
		        @NotNull final Requirement requirements) {
			super(argumentSet, name, description, defaultValue, requirements);
		}
		
	}
	
	/**
	 * Instantiates a new input file argument.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	protected InputFileArgument(@NotNull final Options options) throws ArgumentRegistrationException {
		super(options);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#init()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!validStringValue()) {
				if (required()) {
					// TODO error log
				} else {
					setCachedValue(null);
					ret = true;
				}
			} else {
				
				final File file = new File(getStringValue().trim());
				
				if (file.isDirectory()) {
					if (Logger.logError()) {
						Logger.error("The file `" + getStringValue() + "` specified for argument `" + getName()
						        + "` is a directory. Expected file. Abort.");
					}
				} else if (!file.exists()) {
					if (required()) {
						if (Logger.logError()) {
							Logger.error("The file `" + getStringValue() + "` specified for argument `" + getName()
							        + "` does not exists but is required!");
						}
					} else {
						if (Logger.logWarn()) {
							Logger.warn("The file `" + getStringValue() + "` specified for argument `" + getName()
							        + "` does not exists and is not required! Ignoring file argument!");
						}
						setCachedValue(null);
						ret = true;
					}
				} else if (!file.canRead()) {
					if (required()) {
						if (Logger.logError()) {
							Logger.error("The required file `" + getStringValue() + "` specified for argument `"
							        + getName() + "` exists but is not readable.");
						}
					} else {
						if (Logger.logWarn()) {
							Logger.warn("The file `" + getStringValue() + "` specified for argument `" + getName()
							        + "` exists but is not readable. Ignoring file argument!");
						}
						setCachedValue(null);
						ret = true;
					}
				} else {
					setCachedValue(file);
					ret = true;
				}
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
}
