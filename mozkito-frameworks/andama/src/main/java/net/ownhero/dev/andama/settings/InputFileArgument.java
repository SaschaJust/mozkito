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

import java.io.File;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class FileArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class InputFileArgument extends AndamaArgument<File> {
	
	/**
	 * Constructor for FileArgument. Besides the obvious and general
	 * RepoSuiteArgument parameters, FileArguments can be configures using two
	 * special parameters: <code>overwrite</code> and <code>mustExist</code>.
	 * 
	 * @param settings
	 *            The RepoSuiteSetting instance this argument will register for
	 * @param name
	 *            Name of the Argument
	 * @param description
	 *            The help string description
	 * @param defaultValue
	 *            The default value given as string will be interpreted as path
	 * @param isRequired
	 *            Set to <code>true</code> if this argument will be required
	 * @param overwrite
	 *            Set to <code>true</code> if you want the RepoSuite tool to
	 *            attempt overwriting the file located at given path if
	 *            possible.
	 * @param mustExist
	 *            Set to true if you want to ensure that the file at given
	 *            location must already exist.
	 * @throws ArgumentRegistrationException
	 */
	public InputFileArgument(@NotNull final AndamaArgumentSet<?> argumentSet,
	        @NotNull @NotEmptyString final String name, @NotNull @NotEmptyString final String description,
	        final String defaultValue, @NotNull final Requirement requirements) throws ArgumentRegistrationException {
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
									Logger.error("The file `" + getStringValue() + "` specified for argument `"
									        + getName() + "` is a directory. Expected file. Abort.");
								}
							} else if (!file.exists()) {
								if (required()) {
									if (Logger.logError()) {
										Logger.error("The file `" + getStringValue() + "` specified for argument `"
										        + getName() + "` does not exists but is required!");
									}
								} else {
									if (Logger.logWarn()) {
										Logger.warn("The file `" + getStringValue() + "` specified for argument `"
										        + getName()
										        + "` does not exists and is not required! Ignoring file argument!");
									}
									setCachedValue(null);
									ret = true;
								}
							} else if (!file.canRead()) {
								if (required()) {
									if (Logger.logError()) {
										Logger.error("The required file `" + getStringValue()
										        + "` specified for argument `" + getName()
										        + "` exists but is not readable.");
									}
								} else {
									if (Logger.logWarn()) {
										Logger.warn("The file `" + getStringValue() + "` specified for argument `"
										        + getName() + "` exists but is not readable. Ignoring file argument!");
									}
									setCachedValue(null);
									ret = true;
								}
							} else {
								setCachedValue(file);
								ret = true;
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
