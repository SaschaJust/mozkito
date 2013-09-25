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
package org.mozkito.persistence;

import java.util.List;

import javax.persistence.Query;

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
	 */
	void beginTransaction();
	
	/**
	 * Commit transaction.
	 */
	void commitTransaction();
	
	/**
	 * Creates the criteria.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @return the criteria
	 */
	<T> Criteria<T> createCriteria(final Class<T> clazz);
	
	/**
	 * Creates the native query.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param query
	 *            the query
	 * @param clazz
	 *            the clazz
	 * @return the query
	 */
	<T> Query createNativeQuery(String query,
	                            Class<T> clazz);
	
	/**
	 * Creates the query.
	 * 
	 * @param query
	 *            the query
	 * @return the query
	 */
	Query createQuery(final String query);
	
	/**
	 * Creates the session factory.
	 * 
	 * @param options
	 *            the options
	 */
	void createSessionFactory(DatabaseEnvironment options);
	
	/**
	 * Delete.
	 * 
	 * @param object
	 *            the object
	 */
	void delete(final Annotated object);
	
	/**
	 * Detach.
	 * 
	 * @param object
	 *            the object
	 */
	void detach(Annotated object);
	
	/**
	 * Execute native query.
	 * 
	 * @param query
	 *            the query
	 */
	void executeNativeQuery(final String query);
	
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
	 * Exmerge.
	 * 
	 * @param object
	 *            the object
	 */
	void exmerge(final Annotated object);
	
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
	 */
	<T> List<T> load(final Criteria<T> criteria);
	
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
	<T> List<T> load(final Criteria<T> criteria,
	                 int sizeLimit);
	
	/**
	 * Load by id.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param id
	 *            the id
	 * @param clazz
	 *            the clazz
	 * @return the t
	 */
	<T extends Annotated> T loadById(final Object id,
	                                 Class<T> clazz);
	
	/**
	 * Rollback transaction.
	 */
	void rollbackTransaction();
	
	/**
	 * Save.
	 * 
	 * @param object
	 *            the object
	 */
	void save(final Annotated object);
	
	/**
	 * Save or update.
	 * 
	 * @param object
	 *            the object
	 */
	void saveOrUpdate(final Annotated object);
	
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
	void update(final Annotated object);
}
