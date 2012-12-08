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

package org.mozkito.mappings.storages;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.RepositoryOptions;
import org.mozkito.versions.Repository;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class RepositoryStorage extends Storage {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<RepositoryStorage, ArgumentSet<RepositoryStorage, Options>> {
		
		RepositoryOptions repositoryOptions;
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, RepositoryStorage.TAG, RepositoryStorage.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public RepositoryStorage init() {
			// PRECONDITIONS
			
			try {
				final ArgumentSet<Repository, RepositoryOptions> repositoryArgument = getSettings().getArgumentSet(this.repositoryOptions);
				return new RepositoryStorage(repositoryArgument.getValue());
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				final Map<String, IOptions<?, ?>> ret = new HashMap<>();
				// FIXME: getSettings().getOption(DatabaseOptions.class)
				this.repositoryOptions = new RepositoryOptions(argumentSet, Requirement.required, null);
				
				if (required()) {
					ret.put(this.repositoryOptions.getName(), this.repositoryOptions);
				}
				
				return ret;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = Messages.getString("RepositoryStorage.description"); //$NON-NLS-1$
	                                                                                               
	/** The Constant TAG. */
	private static final String TAG         = "repository";                                       //$NON-NLS-1$
	                                                                                               
	private Repository          repository;
	
	/**
	 * @param repository
	 * 
	 */
	public RepositoryStorage(final Repository repository) {
		// PRECONDITIONS
		
		try {
			this.repository = repository;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @return
	 */
	public Repository getRepository() {
		// PRECONDITIONS
		
		try {
			return this.repository;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.storages.Storage#loadData(org.mozkito.persistence.PersistenceUtil)
	 */
	@Override
	public void loadData(final PersistenceUtil util) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// super.loadData(util);
			throw new RuntimeException("Method 'loadData' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
}
