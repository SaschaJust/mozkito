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
package de.unisaarland.cs.st.reposuite.settings;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepositorySettings extends AndamaSettings {
	
	/**
	 * Add the settings set for the database.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the database settings required.
	 * @return
	 * @throws DuplicateArgumentException
	 */
	public DatabaseArguments setDatabaseArgs(final boolean isRequired,
	                                         final String unit) {
		DatabaseArguments minerDatabaseArguments = new DatabaseArguments(this, isRequired, unit);
		return minerDatabaseArguments;
	}
	
	/**
	 * Add the repository argument set.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the repository settings are
	 *            required.
	 * @return
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments setRepositoryArg(final boolean isRequired) {
		RepositoryArguments minerRepoArgSet = new RepositoryArguments(this, isRequired);
		return minerRepoArgSet;
	}
	
}
