/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.mappings.settings.storages;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.storages.RepositoryStorage;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.Repository;
import org.mozkito.versions.concurrent.ConcurrentRepository;
import org.mozkito.versions.settings.RepositoryOptions;

/**
 * The Class RepositoryStorageOptions.
 */
public class RepositoryStorageOptions extends
        ArgumentSetOptions<RepositoryStorage, ArgumentSet<RepositoryStorage, RepositoryStorageOptions>> {
	
	/** The repository options. */
	RepositoryOptions repositoryOptions;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public RepositoryStorageOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, RepositoryStorage.TAG, RepositoryStorage.DESCRIPTION, requirements);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public RepositoryStorage init() {
		// PRECONDITIONS
		
		try {
			final ArgumentSet<Repository, RepositoryOptions> repositoryArgument = getSettings().getArgumentSet(this.repositoryOptions);
			return new RepositoryStorage(new ConcurrentRepository(repositoryArgument.getValue()));
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                    SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> ret = new HashMap<>();
			this.repositoryOptions = new RepositoryOptions(argumentSet, Requirement.required,
			                                               getSettings().getSetOptions(DatabaseOptions.class));
			
			if (required()) {
				ret.put(this.repositoryOptions.getName(), this.repositoryOptions);
			}
			
			return ret;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
