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
/**
 * 
 */
package org.mozkito.database;

import java.util.Collection;
import java.util.List;

import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.persistence.DatabaseType;

/**
 * The Interface PersistenceUtil.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface PersistenceUtil {
	
	/**
	 * Active transaction.
	 * 
	 * @return true, if successful
	 */
	boolean activeTransaction();
	
	/**
	 * Begin transaction.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	void beginTransaction() throws DatabaseException;
	
	/**
	 * Commit transaction.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	void commitTransaction() throws DatabaseException;
	
	/**
	 * Creates the session factory.
	 * 
	 * @param options
	 *            the options
	 */
	void createConnector(DatabaseEnvironment options);
	
	/**
	 * Creates the criteria.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @return the criteria
	 */
	<T extends Entity> Criteria<T> createCriteria(final Class<T> clazz);
	
	/**
	 * Delete.
	 * 
	 * @param object
	 *            the object
	 */
	void delete(final Entity object);
	
	/**
	 * Detach.
	 * 
	 * @param object
	 *            the object
	 */
	void detach(Entity object);
	
	/**
	 * Execute native select query.
	 * 
	 * @param queryString
	 *            the query string
	 * @return the list
	 */
	@SuppressWarnings ("rawtypes")
	List executeNativeSelectQuery(final String queryString);
	
	/**
	 * Execute query.
	 * 
	 * @param query
	 *            the query
	 */
	void executeQuery(final String query);
	
	/**
	 * Flush.
	 */
	void flush();
	
	/**
	 * Gets the managed entities.
	 * 
	 * @return the managed entities
	 */
	public String getManagedEntities();
	
	/**
	 * Gets the connector.
	 * 
	 * @return the connector
	 */
	QueryPool getPool();
	
	/**
	 * Gets the tool information.
	 * 
	 * @return the tool information
	 */
	String getToolInformation();
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	DatabaseType getType();
	
	/**
	 * Load.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param criteria
	 *            the criteria
	 * @return the list
	 * @throws DatabaseException
	 */
	<T extends Entity> List<T> load(final Criteria<T> criteria) throws DatabaseException;
	
	/**
	 * Load.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param criteria
	 *            the criteria
	 * @param sizeLimit
	 *            the size limit
	 * @return the list
	 */
	<T extends Entity> List<T> load(final Criteria<T> criteria,
	                                int sizeLimit);
	
	/**
	 * Load by id.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @param id
	 *            the id
	 * @return the t
	 */
	<T extends Entity> T loadById(Class<T> clazz,
	                              final Object id);
	
	/**
	 * Load by ids.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @param ids
	 *            the ids
	 * @return the list
	 */
	<T extends Entity> List<T> loadByIds(Class<T> clazz,
	                                     final Collection<?> ids);
	
	/**
	 * Load by ids.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @param ids
	 *            the ids
	 * @return the list
	 */
	<T extends Entity> List<T> loadByIds(Class<T> clazz,
	                                     final Object... ids);
	
	/**
	 * Rollback transaction.
	 */
	void rollback();
	
	/**
	 * Save.
	 * 
	 * @param object
	 *            the object
	 */
	void save(final Entity object);
	
	/**
	 * Save or update.
	 * 
	 * @param object
	 *            the object
	 */
	void saveOrUpdate(final Entity object);
	
	/**
	 * Shutdown.
	 */
	void shutdown();
	
	/**
	 * Update.
	 * 
	 * @param object
	 *            the object
	 */
	void update(final Entity object);
}
