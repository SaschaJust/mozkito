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
import net.ownhero.dev.kanuni.conditions.FileCondition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class DirectoryArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class DirectoryArgument extends Argument<File, DirectoryArgument.Options> {
	
	/**
	 * The Class DirectoryArgumentOptions.
	 */
	public static class Options extends ArgumentOptions<File, DirectoryArgument> {
		
		/** The create. */
		private final boolean create;
		
		/**
		 * Instantiates a new directory argument options.
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
		 * @param create
		 *            the create
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final String name, final String description,
		        final File defaultValue, final Requirement requirements, final boolean create) {
			super(argumentSet, name, description, defaultValue, requirements);
			this.create = create;
		}
		
		/**
		 * Checks if is creates the.
		 * 
		 * @return the create
		 */
		public boolean isCreate() {
			return this.create;
		}
		
	}
	
	/** The create. */
	private boolean create = false;
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	protected DirectoryArgument(@NotNull final Options options) throws ArgumentRegistrationException {
		super(options);
		this.create = options.isCreate();
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
					if (Logger.logError()) {
						Logger.error("The required file argument '" + getName() + "' is not set.");
					}
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Optional argument is not set: %s", getTag());
					}
					setCachedValue(null);
					ret = true;
				}
			} else {
				
				final File directory = new File(getStringValue().trim());
				
				if (!directory.exists()) {
					if (!isCreate()) {
						if (Logger.logError()) {
							Logger.error("The file '" + getStringValue() + "' specified for argument '" + getName()
							        + "' does not exist.");
						}
					} else {
						if (!directory.mkdirs()) {
							if (Logger.logError()) {
								Logger.error("The file '" + getStringValue() + "' specified for argument '" + getName()
								        + "' does not exist and cannot be created.");
							}
						} else {
							if (!directory.canExecute() || !directory.canRead() || !directory.canWrite()) {
								if (Logger.logError()) {
									Logger.error("The file '" + getStringValue() + "' specified for argument '"
									        + getName() + "' could be created bu has wrong permissions.");
								}
							} else {
								setCachedValue(directory);
								ret = true;
							}
						}
					}
				} else if (!directory.isDirectory()) {
					if (Logger.logError()) {
						Logger.error("The directory '" + getStringValue() + "' specified for argument '" + getName()
						        + "' is not a directory. Please remove file or choose different argument value.");
					}
				} else if (!directory.canExecute()) {
					if (Logger.logError()) {
						Logger.error("The directory '" + getStringValue() + "' specified for argument '" + getName()
						        + "' is not executable. Please fix the modifiers for this directory.");
					}
				} else {
					setCachedValue(directory);
					ret = true;
				}
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
			
			if (ret && required()) {
				FileCondition.readableWritableDirectory(getCachedValue(),
				                                        "%s has been successful initialized with config value '%s' but the target directory does not match the following criteria: EXISTS, DIRECTORY, READABLE, WRITABLE, EXECUTABLE",
				                                        getHandle(), getStringValue());
				
			}
		}
	}
	
	/**
	 * Checks if is creates the.
	 * 
	 * @return the create
	 */
	private final boolean isCreate() {
		return this.create;
	}
}
