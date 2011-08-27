/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.andama.exceptions.Shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DirectoryArgument extends AndamaArgument<File> {
	
	final Logger    logger = LoggerFactory.getLogger(DirectoryArgument.class);
	
	private boolean create = false;
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.AndamaArgument
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
	public File getValue() {
		if (this.actualValue == null) {
			return null;
		}
		
		File file = new File(this.actualValue.trim());
		
		if (!file.exists()) {
			if (!this.create) {
				throw new Shutdown(new IOException("The file `" + this.actualValue + "` specified for argument `"
				        + getName() + "` does not exist."));
			} else {
				if (!file.mkdirs()) {
					throw new Shutdown(new IOException("The file `" + this.actualValue + "` specified for argument `"
					        + getName() + "` does not exist and cannot be created."));
				}
			}
		}
		if (!file.isDirectory()) {
			throw new Shutdown(new IOException("The directory `" + this.actualValue + "` specified for argument `"
			        + getName() + "` is not a directory. Please remove file or choose different argument value."));
		}
		return file;
	}
}
