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

import org.mozkito.mappings.messages.Messages;
import org.mozkito.versions.Repository;
import org.mozkito.versions.concurrent.ConcurrentRepository;

/**
 * The Class RepositoryStorage.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class RepositoryStorage extends Storage {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("RepositoryStorage.description"); //$NON-NLS-1$
	                                                                                              
	/** The Constant TAG. */
	public static final String TAG         = "repository";                                       //$NON-NLS-1$
	                                                                                              
	/** The repository. */
	private Repository         repository;
	
	/**
	 * Instantiates a new repository storage.
	 * 
	 * @param repository
	 *            the repository
	 */
	public RepositoryStorage(final ConcurrentRepository repository) {
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
	 * Gets the repository.
	 * 
	 * @return the repository
	 */
	public Repository getRepository() {
		// PRECONDITIONS
		
		try {
			return this.repository;
		} finally {
			// POSTCONDITIONS
		}
	}
}
