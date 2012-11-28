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
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public interface PersistenceUtil {
	
	boolean activeTransaction();
	
	void beginTransaction();
	
	void commitTransaction();
	
	<T> Criteria<T> createCriteria(final Class<T> clazz);
	
	<T> Query createNativeQuery(String query,
	                            Class<T> clazz);
	
	Query createQuery(final String query);
	
	void createSessionFactory(final Properties properties);
	
	void createSessionFactory(final String host,
	                          final String database,
	                          final String user,
	                          final String password,
	                          final String type,
	                          final String driver,
	                          final String unit,
	                          final ConnectOptions options);
	
	void delete(final Annotated object);
	
	void executeNativeQuery(final String query);
	
	@SuppressWarnings ("rawtypes")
	List executeNativeSelectQuery(final String queryString);
	
	void executeQuery(final String query);
	
	void exmerge(final Annotated object);
	
	void flush();
	
	String getToolInformation();
	
	String getType();
	
	<T> List<T> load(final Criteria<T> criteria);
	
	<T> List<T> load(final Criteria<T> criteria,
	                 int sizeLimit);
	
	<T extends Annotated> T loadById(final Object id,
	                                 Class<T> clazz);
	
	void rollbackTransaction();
	
	void save(final Annotated object);
	
	void saveOrUpdate(final Annotated object);
	
	void shutdown();
	
	void update(final Annotated object);
}
