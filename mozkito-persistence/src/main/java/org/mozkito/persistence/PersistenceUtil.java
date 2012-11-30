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
import java.util.Properties;

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
	public boolean activeTransaction();
	
	/**
	 * Begin transaction.
	 */
	public void beginTransaction();
	
	/**
	 * Commit transaction.
	 */
	public void commitTransaction();
	
	/**
	 * Creates the criteria.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @return the criteria
	 */
	public <T> Criteria<T> createCriteria(final Class<T> clazz);
	
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
	public <T> Query createNativeQuery(String query,
	                                   Class<T> clazz);
	
	/**
	 * Creates the query.
	 * 
	 * @param query
	 *            the query
	 * @return the query
	 */
	public Query createQuery(final String query);
	
	/**
	 * Creates the session factory.
	 * 
	 * @param properties
	 *            the properties
	 */
	public void createSessionFactory(final Properties properties);
	
	/**
	 * Creates the session factory.
	 * 
	 * @param host
	 *            the host
	 * @param database
	 *            the database
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @param type
	 *            the type
	 * @param driver
	 *            the driver
	 * @param unit
	 *            the unit
	 * @param options
	 *            the options
	 */
	public void createSessionFactory(final String host,
	                                 final String database,
	                                 final String user,
	                                 final String password,
	                                 final String type,
	                                 final String driver,
	                                 final String unit,
	                                 final ConnectOptions options);
	
	/**
	 * Delete.
	 * 
	 * @param object
	 *            the object
	 */
	public void delete(final Annotated object);
	
	/**
	 * Execute native query.
	 * 
	 * @param query
	 *            the query
	 */
	public void executeNativeQuery(final String query);
	
	/**
	 * Execute native select query.
	 * 
	 * @param queryString
	 *            the query string
	 * @return the list
	 */
	@SuppressWarnings ("rawtypes")
	public List executeNativeSelectQuery(final String queryString);
	
	/**
	 * Execute query.
	 * 
	 * @param query
	 *            the query
	 */
	public void executeQuery(final String query);
	
	/**
	 * Exmerge.
	 * 
	 * @param object
	 *            the object
	 */
	public void exmerge(final Annotated object);
	
	/**
	 * Flush.
	 */
	public void flush();
	
	/**
	 * Gets the tool information.
	 * 
	 * @return the tool information
	 */
	public String getToolInformation();
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType();
	
	/**
	 * Load.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param criteria
	 *            the criteria
	 * @return the list
	 */
	public <T> List<T> load(final Criteria<T> criteria);
	
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
	public <T> List<T> load(final Criteria<T> criteria,
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
	public <T extends Annotated> T loadById(final Object id,
	                                        Class<T> clazz);
	
	/**
	 * Rollback transaction.
	 */
	public void rollbackTransaction();
	
	/**
	 * Save.
	 * 
	 * @param object
	 *            the object
	 */
	public void save(final Annotated object);
	
	/**
	 * Save or update.
	 * 
	 * @param object
	 *            the object
	 */
	public void saveOrUpdate(final Annotated object);
	
	/**
	 * Shutdown.
	 */
	public void shutdown();
	
	/**
	 * Update.
	 * 
	 * @param object
	 *            the object
	 */
	public void update(final Annotated object);
}
