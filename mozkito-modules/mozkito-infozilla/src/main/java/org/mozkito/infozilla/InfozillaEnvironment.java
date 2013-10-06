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

package org.mozkito.infozilla;

import java.io.File;
import java.util.Set;

import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.managers.IManager;

/**
 * The Class InfozillaEnvironment.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class InfozillaEnvironment {
	
	/** The directory. */
	private final File                directory;
	
	/** The filter managers. */
	private final Set<IManager> managers;
	
	/** The filters. */
	private final Set<Filter<?>>      filters;
	
	/**
	 * Instantiates a new infozilla environment.
	 * 
	 * @param directory
	 *            the directory
	 * @param managers
	 *            the filter managers
	 * @param filters
	 *            the filters
	 */
	public InfozillaEnvironment(final File directory, final Set<IManager> managers,
	        final Set<Filter<?>> filters) {
		super();
		this.directory = directory;
		this.managers = managers;
		this.filters = filters;
	}
	
	/**
	 * Gets the directory.
	 * 
	 * @return the directory
	 */
	public File getDirectory() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.directory;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the filter managers.
	 * 
	 * @return the filterManagers
	 */
	public Set<IManager> getFilterManagers() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.managers;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the filters.
	 * 
	 * @return the filters
	 */
	public Set<Filter<?>> getFilters() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.filters;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
