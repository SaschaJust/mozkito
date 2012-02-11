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
package net.ownhero.dev.andama.settings.arguments;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.Argument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class FileArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class OutputFileArgument extends Argument<File> {
	
	private boolean overwrite = false;
	
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
	 * @throws ArgumentRegistrationException
	 */
	public OutputFileArgument(@NotNull final ArgumentSet<?> argumentSet,
	        @NotNull @NotEmptyString final String name, @NotNull @NotEmptyString final String description,
	        final String defaultValue, @NotNull final Requirement requirements, final boolean overwrite)
	        throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue, requirements);
		this.overwrite = overwrite;
	}
	
	/**
	 * @param file
	 * @return
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
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						if (!validStringValue()) {
							if (required()) {
								// TODO error log
							} else {
								// TODO warn log
								setCachedValue(null);
								ret = true;
							}
						} else {
							final File file = new File(getStringValue().trim());
							
							if (file.exists()) {
								if (!file.isFile()) {
									if (Logger.logError()) {
										Logger.error("The file `" + getStringValue() + "` specified for argument `"
										        + getName() + "` is not a regular file. Abort.");
									}
								} else if (!isOverwrite()) {
									
									if (required()) {
										if (Logger.logError()) {
											
											Logger.error("The file `"
											        + getStringValue()
											        + "` specified for argument `"
											        + getName()
											        + "` exists already. Please remove file or choose different argument value.");
										}
									} else {
										if (Logger.logWarn()) {
											Logger.warn("The file `" + getStringValue() + "` specified for argument `"
											        + getName()
											        + "` exists already and cannot be overwritten. Ignoring argument!.");
										}
										setCachedValue(null);
										ret = true;
									}
									
								} else {
									
									if (Logger.logDebug()) {
										if (Logger.logDebug()) {
											Logger.debug("Attempt overwriting file `" + file.getAbsolutePath()
											        + "` ...");
										}
									}
									
									if (!file.delete()) {
										if (required()) {
											if (Logger.logError()) {
												Logger.error("Could not delete file `" + file.getAbsolutePath()
												        + "`. Abort.");
											}
										} else {
											if (Logger.logWarn()) {
												Logger.warn("The file `" + getStringValue()
												        + "` specified for argument `" + getName()
												        + "` exists already and cannot be deleted. Ignoring argument!.");
											}
											setCachedValue(null);
											ret = true;
										}
									} else {
										// file does not exist now
										ret = createFile(file);
										if (ret) {
											setCachedValue(file);
										}
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
	
	/**
	 * @return the overwrite
	 */
	private final boolean isOverwrite() {
		return this.overwrite;
	}
}
