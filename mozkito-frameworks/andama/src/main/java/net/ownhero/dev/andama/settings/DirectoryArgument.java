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

import net.ownhero.dev.andama.settings.dependencies.Requirement;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DirectoryArgument extends AndamaArgument<File> {
	
	private boolean create = false;
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @param create
	 *            Attempts to create directory if not exist
	 */
	public DirectoryArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final Requirement requirements, final boolean create) {
		super(argumentSet, name, description, defaultValue, requirements);
		this.create = create;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#init()
	 */
	@Override
	protected final boolean init() {
		if (!isInitialized()) {
			synchronized (this) {
				if (!isInitialized()) {
					if (getStringValue() == null) {
						setCachedValue(null);
						return true;
					}
					
					final File file = new File(getStringValue().trim());
					
					if (!file.exists()) {
						if (!isCreate()) {
							if (Logger.logError()) {
								Logger.error("The file `" + getStringValue() + "` specified for argument `" + getName()
								        + "` does not exist.");
							}
							return false;
						} else {
							if (!file.mkdirs()) {
								if (Logger.logError()) {
									Logger.error("The file `" + getStringValue() + "` specified for argument `"
									        + getName() + "` does not exist and cannot be created.");
								}
								return false;
							}
						}
					} else if (!file.isDirectory()) {
						if (Logger.logError()) {
							Logger.error("The directory `" + getStringValue() + "` specified for argument `"
							        + getName()
							        + "` is not a directory. Please remove file or choose different argument value.");
						}
						return false;
					} else if (!file.canExecute()) {
						if (Logger.logError()) {
							Logger.error("The directory `" + getStringValue() + "` specified for argument `"
							        + getName() + "` is not executable. Please fix the modifiers for this directory.");
						}
						return false;
					}
					setCachedValue(file);
					return true;
				} else {
					return true;
				}
			}
		} else {
			return true;
		}
		
	}
	
	/**
	 * @return the create
	 */
	private final boolean isCreate() {
		return this.create;
	}
}
