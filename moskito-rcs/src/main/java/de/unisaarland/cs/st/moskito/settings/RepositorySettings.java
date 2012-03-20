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
package de.unisaarland.cs.st.moskito.settings;

import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepositorySettings extends Settings {
	
	public static final boolean debug = Boolean.parseBoolean(System.getProperty("debug"));
	
	/**
	 * Add the settings set for the database.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the database settings required.
	 * @return
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public DatabaseOptions setDatabaseArgs(final Requirement requirement,
	                                         final String unit) throws ArgumentRegistrationException {
		final DatabaseOptions minerDatabaseArguments = new DatabaseOptions(getRootArgumentSet(), requirement, unit);
		return minerDatabaseArguments;
	}
	
	/**
	 * Add the repository argument set.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the repository settings are required.
	 * @return
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments setRepositoryArg(final Requirement requirement) throws ArgumentRegistrationException {
		final RepositoryArguments minerRepoArgSet = new RepositoryArguments(getRootArgumentSet(), requirement);
		return minerRepoArgSet;
	}
	
}
