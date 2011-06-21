/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepositorySettings extends RepoSuiteSettings {
	
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
