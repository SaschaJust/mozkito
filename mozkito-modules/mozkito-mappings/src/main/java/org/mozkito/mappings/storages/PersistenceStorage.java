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

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class PersistenceStorage.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PersistenceStorage extends Storage {
	
	/** The Constant DESCRIPTION. */
	public static final String    DESCRIPTION = Messages.getString("PersistenceStorage.description"); //$NON-NLS-1$
	                                                                                                  
	/** The Constant TAG. */
	public static final String    TAG         = "persistence";                                       //$NON-NLS-1$
	                                                                                                  
	/** The util. */
	private final PersistenceUtil util;
	
	/**
	 * Instantiates a new persistence storage.
	 * 
	 * @param util
	 *            the util
	 */
	public PersistenceStorage(@NotNull final PersistenceUtil util) {
		this.util = util;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return DESCRIPTION;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the util.
	 * 
	 * @return the util
	 */
	public PersistenceUtil getUtil() {
		return this.util;
	}
	
}
