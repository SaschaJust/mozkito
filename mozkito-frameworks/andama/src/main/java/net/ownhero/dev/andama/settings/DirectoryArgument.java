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
package net.ownhero.dev.andama.settings;

import java.io.File;

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
	public DirectoryArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final boolean create) {
		super(settings, name, description, defaultValue, isRequired);
		this.create = create;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	protected final boolean init() {
		if (this.stringValue == null) {
			setCachedValue(null);
			return true;
		}
		
		final File file = new File(this.stringValue.trim());
		
		if (!file.exists()) {
			if (!this.create) {
				if (Logger.logError()) {
					Logger.error("The file `" + this.stringValue + "` specified for argument `" + getName()
					        + "` does not exist.");
				}
				return false;
			} else {
				if (!file.mkdirs()) {
					if (Logger.logError()) {
						Logger.error("The file `" + this.stringValue + "` specified for argument `" + getName()
						        + "` does not exist and cannot be created.");
					}
					return false;
				}
			}
		}
		if (!file.isDirectory()) {
			if (Logger.logError()) {
				Logger.error("The directory `" + this.stringValue + "` specified for argument `" + getName()
				        + "` is not a directory. Please remove file or choose different argument value.");
			}
			return false;
		}
		setCachedValue(file);
		return true;
	}
}
