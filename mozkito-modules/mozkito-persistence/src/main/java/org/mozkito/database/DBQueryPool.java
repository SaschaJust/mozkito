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

package org.mozkito.database;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class LoaderPool.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DBQueryPool {
	
	/** The loaders. */
	private final Map<Class<?>, DBQuery<?>> dBQueries = new HashMap<Class<?>, DBQuery<?>>();
	
	/** The persistence util. */
	private DBConnector                     connector;
	
	/**
	 * Instantiates a new loader pool.
	 * 
	 * @param connector
	 *            the persistence util
	 */
	public DBQueryPool(final DBConnector connector) {
		PRECONDITIONS: {
			if (connector == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			// body
			// TODO FIXME THIS SHOULD ACTUALLY BE A DATABASE CONNECTOR
			this.connector = connector;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.connector, "Field '%s' in '%s'.", "this.connector", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Adds the loader.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @param loader
	 *            the loader
	 * @return the loader
	 */
	public <T extends DBEntity> DBQuery<?> addLoader(final Class<T> clazz,
	                                                 final DBQuery<T> loader) {
		return this.dBQueries.put(clazz, loader);
	}
	
	public DBEntityCache getEntityCache() {
		return null;
	}
	
	/**
	 * Gets the loader.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @return the loader
	 */
	@SuppressWarnings ("unchecked")
	public <T extends DBEntity> DBQuery<T> getLoader(final Class<T> clazz) {
		return (DBQuery<T>) this.dBQueries.get(clazz);
	}
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistenceUtil
	 */
	public DBConnector getPersistenceUtil() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.connector;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the persistence util.
	 * 
	 * @param persistenceUtil
	 *            the persistenceUtil to set
	 */
	public void setPersistenceUtil(final DBConnector persistenceUtil) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.connector = persistenceUtil;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
