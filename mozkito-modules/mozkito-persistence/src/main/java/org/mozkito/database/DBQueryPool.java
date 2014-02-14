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

import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class LoaderPool.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DBQueryPool {
	
	/** The loaders. */
	private final Map<Class<?>, DBQuery<?>> dBQueries = new HashMap<Class<?>, DBQuery<?>>();
	
	/** The persistence util. */
	private PersistenceUtil                 persistenceUtil;
	
	/**
	 * Instantiates a new loader pool.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public DBQueryPool(final PersistenceUtil persistenceUtil) {
		PRECONDITIONS: {
			if (persistenceUtil == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			// body
			// TODO FIXME THIS SHOULD ACTUALLY BE A DATABASE CONNECTOR
			this.persistenceUtil = persistenceUtil;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.persistenceUtil,
				                  "Field '%s' in '%s'.", "this.persistenceUtil", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
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
	public PersistenceUtil getPersistenceUtil() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.persistenceUtil;
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
	public void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.persistenceUtil = persistenceUtil;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
