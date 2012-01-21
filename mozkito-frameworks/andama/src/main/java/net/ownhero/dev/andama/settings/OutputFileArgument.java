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
import java.io.IOException;

import net.ownhero.dev.kisa.Logger;

/**
 * The Class FileArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class OutputFileArgument extends AndamaArgument<File> {
	
	private boolean overwrite   = false;
	private boolean selfWritten = false;
	
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
	 */
	public OutputFileArgument(final AndamaSettings settings, final String name, final String description,
			final String defaultValue, final boolean isRequired, final boolean overwrite) {
		super(settings, name, description, defaultValue, isRequired);
		this.overwrite = overwrite;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public boolean init() {
		// FIME seprate input and output files. Fix the mustExist and overwrite
		// conbinations!
		if (this.stringValue == null) {
			return false;
		}
		File file = new File(this.stringValue.trim());
		
		if (file.isDirectory()) {
			if (Logger.logError()) {
				Logger.error("The file `" + this.stringValue + "` specified for argument `" + getName()
						+ "` is a directory. Expected file. Abort.");
			}
			return false;
		}
		if (file.exists() && (!this.overwrite) && (!this.selfWritten)) {
			if (this.isRequired()) {
				if (Logger.logError()) {
					
					Logger.error("The file `" + this.stringValue + "` specified for argument `" + getName()
							+ "` exists already. Please remove file or choose different argument value.");
				}
				return false;
			} else {
				if (Logger.logWarn()) {
					Logger.warn("The file `" + this.stringValue + "` specified for argument `" + getName()
							+ "` exists already and cannot be overwritten. Ignoring argument!.");
				}
				return false;
			}
			
		} else if (file.exists() && (this.overwrite)) {
			
			if (Logger.logDebug()) {
				if (Logger.logDebug()) {
					Logger.debug("Attempt overwriting file `" + file.getAbsolutePath() + "` ...");
				}
			}
			
			if (!file.delete()) {
				if (Logger.logError()) {
					Logger.error("Could not delete file `" + file.getAbsolutePath() + "`. Abort.");
				}
				return false;
			}
			try {
				if (!file.createNewFile()) {
					if (Logger.logError()) {
						Logger.error("Could not re-create file `" + file.getAbsolutePath() + "`. Abort.");
					}
					return false;
				}
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
					Logger.error(e.getMessage());
				}
				
			}
		} else if (!this.selfWritten) {
			// file does not exist so far
			try {
				if (!file.createNewFile()) {
					if (Logger.logError()) {
						Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
					}
					if (this.isRequired()) {
						return false;
					} else {
						setCachedValue(null);
					}
				}
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
					Logger.error(e.getMessage());
				}
				if (this.isRequired()) {
					return false;
				} else {
					setCachedValue(null);
				}
			}
		}
		this.selfWritten = true;
		this.setCachedValue(file);
		return true;
	}
}
