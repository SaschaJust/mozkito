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
import java.io.IOException;

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
public class LogFileArgument extends Argument<File, LogFileArgument.Options> {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends ArgumentOptions<File, LogFileArgument> {
		
		/**
		 * Instantiates a new options.
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
	 * Constructor for FileArgument. Besides the obvious and general RepoSuiteArgument parameters, FileArguments can be
	 * configures using two special parameters: <code>overwrite</code> and <code>mustExist</code>.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	protected LogFileArgument(@NotNull final Options options) throws ArgumentRegistrationException {
		super(options);
	}
	
	/**
	 * Creates the file.
	 * 
	 * @param file
	 *            the file
	 * @return true, if successful
	 */
	private final boolean createFile(final File file) {
		boolean ret = false;
		
		try {
			try {
				if (!file.createNewFile()) {
					if (required()) {
						if (Logger.logError()) {
							Logger.error("Could not re-create file `" + file.getAbsolutePath() + "`. Abort.");
						}
					} else {
						if (Logger.logWarn()) {
							Logger.warn("The file `" + getStringValue() + "` specified for argument `" + getName()
							        + "` exists already and cannot be re-created. Ignoring argument!.");
						}
						setCachedValue(null);
						ret = true;
					}
				} else {
					if (!file.canWrite()) {
						if (required()) {
							if (Logger.logError()) {
								Logger.error("Permissions do not allow writing to file `" + file.getAbsolutePath()
								        + "`. Abort.");
							}
						} else {
							if (Logger.logWarn()) {
								Logger.warn("The file `" + getStringValue() + "` specified for argument `" + getName()
								        + "` is not writable (due to permissions). Ignoring argument!.");
							}
							setCachedValue(null);
							ret = true;
						}
					} else {
						ret = true;
					}
				}
			} catch (final IOException e) {
				if (required()) {
					if (Logger.logError()) {
						Logger.error("Could not re-create file `" + file.getAbsolutePath() + "`. Abort.");
					}
					ret = false;
				} else {
					if (Logger.logWarn()) {
						Logger.warn("The file `" + getStringValue() + "` specified for argument `" + getName()
						        + "` exists already and cannot be re-created. Ignoring argument!.");
					}
					setCachedValue(null);
					ret = true;
				}
			}
			
			return ret;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#init()
	 */
	@Override
	protected boolean init() {
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
				final File file = new File(getStringValue().trim());
				
				if (file.exists()) {
					if (!file.isFile()) {
						if (Logger.logError()) {
							Logger.error("The file `" + getStringValue() + "` specified for argument `" + getName()
							        + "` is not a regular file. Abort.");
						}
					} else if (!file.canWrite()) {
						
						if (Logger.logError()) {
							
							Logger.error("The file `"
							        + getStringValue()
							        + "` specified for argument `"
							        + getName()
							        + "` exists already but can't be written to. Please remove file or choose different argument value.");
						}
					} else {
						
						if (Logger.logDebug()) {
							if (Logger.logDebug()) {
								Logger.debug("Attempt append to file `" + file.getAbsolutePath() + "` ...");
							}
						}
						
						ret = true;
						if (ret) {
							setCachedValue(file);
						}
						
					}
				} else {
					// file does not exist so far
					ret = createFile(file);
					if (ret) {
						setCachedValue(file);
					}
				}
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
	
}
