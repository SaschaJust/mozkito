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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.FileUtils;

import org.mozkito.persistence.DatabaseType;

/**
 * The Class PersistenceManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DatabaseManager {
	
	/** The Constant NATIVE_QUERIES. */
	private static final Map<DatabaseType, Map<String, String>>  NATIVE_QUERIES = new HashMap<DatabaseType, Map<String, String>>();
	
	/** The Constant STORED_QUERIES. */
	private static final Map<Class<?>, Map<String, Criteria<?>>> STORED_QUERIES = new HashMap<Class<?>, Map<String, Criteria<?>>>();
	
	/**
	 * Creates the database.
	 * 
	 * @param options
	 *            the options
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static synchronized void createDatabase(final DatabaseEnvironment options) throws SQLException {
		if (options.isLocal()) {
			final File file = new File(options.getDatabaseName());
			if (file.exists()) {
				if (file.isFile()) {
					if (Logger.logAlways()) {
						Logger.always("deleting database file '%s'", file.getAbsolutePath());
					}
					file.delete();
				} else if (file.isDirectory()) {
					try {
						if (Logger.logAlways()) {
							Logger.always("deleting database directory '%s'", file.getAbsolutePath());
						}
						FileUtils.deleteDirectory(file);
					} catch (final IOException e) {
						throw new SQLException("Could not delete database directory: " + file.getAbsolutePath(), e);
					}
				}
			}
		} else {
			if (DatabaseType.POSTGRESQL.equals(options.getDatabaseType())) {
				try (final Connection connection = DriverManager.getConnection("jdbc:" + options.getDatabaseType() + "://" + options.getDatabaseHost() + "/postgres", options.getDatabaseUsername(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				                                                               options.getDatabasePassword())) {
					if (connection != null) {
						final Statement statement = connection.createStatement();
						if (statement != null) {
							statement.executeUpdate("CREATE DATABASE " + options.getDatabaseName() + ";"); //$NON-NLS-1$ //$NON-NLS-2$
							statement.close();
						}
					}
				} finally {
					// ignore
				}
			} else {
				throw new SQLException("CREATE unsupported for remote database: " + options.getDatabaseType());
			}
		}
	}
	
	/**
	 * Creates the util.
	 * 
	 * @param options
	 *            the options
	 * @return the persistence util
	 */
	public static PersistenceUtil createUtil(final DatabaseEnvironment options) {
		
		PersistenceUtil instance = null;
		
		instance = new DatabaseUtil();
		instance.createConnector(options);
		
		return instance;
	}
	
	/**
	 * Drop database.
	 * 
	 * @param options
	 *            the options
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static synchronized void dropDatabase(final DatabaseEnvironment options) throws SQLException {
		
		if (options.isLocal()) {
			final File file = new File(options.getDatabaseName());
			if (file.exists()) {
				if (file.isFile()) {
					if (Logger.logAlways()) {
						Logger.always("deleting database file '%s'", file.getAbsolutePath());
					}
					file.delete();
				} else if (file.isDirectory()) {
					try {
						if (Logger.logAlways()) {
							Logger.always("deleting database directory '%s'", file.getAbsolutePath());
						}
						FileUtils.deleteDirectory(file);
					} catch (final IOException e) {
						throw new SQLException("Could not delete database directory: " + file.getAbsolutePath(), e);
						
					}
				}
			}
		} else {
			if (DatabaseType.POSTGRESQL.equals(options.getDatabaseType())) {
				try (final Connection connection = DriverManager.getConnection("jdbc:" + options.getDatabaseType() + "://" + options.getDatabaseHost() + "/postgres", options.getDatabaseUsername(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				                                                               options.getDatabasePassword())) {
					if (connection != null) {
						final Statement statement = connection.createStatement();
						if (statement != null) {
							statement.executeUpdate("DROP DATABASE " + options.getDatabaseName() + ";"); //$NON-NLS-1$ //$NON-NLS-2$
							statement.close();
						}
					}
				} finally {
					// ignore
				}
			} else {
				throw new SQLException("DROP unsupported for remote database: " + options.getDatabaseType());
			}
		}
	}
	
	/**
	 * Gets the native query.
	 * 
	 * @param util
	 *            the util
	 * @param id
	 *            the id
	 * @return the native query
	 */
	public static synchronized String getNativeQuery(final PersistenceUtil util,
	                                                 final String id) {
		final DatabaseType databaseType = util.getType();
		
		if (DatabaseManager.NATIVE_QUERIES.containsKey(databaseType)
		        && DatabaseManager.NATIVE_QUERIES.get(databaseType).containsKey(id)) {
			return DatabaseManager.NATIVE_QUERIES.get(databaseType).get(id);
		}
		return null;
	}
	
	/**
	 * Gets the stored query.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param id
	 *            the id
	 * @param clazz
	 *            the clazz
	 * @return the stored query
	 */
	@SuppressWarnings ("unchecked")
	public static synchronized <T extends Entity> Criteria<T> getStoredQuery(final String id,
	                                                                         final Class<T> clazz) {
		return (Criteria<T>) DatabaseManager.STORED_QUERIES.get(clazz).get(id);
	}
	
	/**
	 * Register native query.
	 * 
	 * @param type
	 *            the type
	 * @param id
	 *            the id
	 * @param query
	 *            the query
	 * @return the string
	 */
	public static synchronized String registerNativeQuery(final DatabaseType type,
	                                                      final String id,
	                                                      final String query) {
		if (!DatabaseManager.NATIVE_QUERIES.containsKey(type)) {
			DatabaseManager.NATIVE_QUERIES.put(type, new HashMap<String, String>());
		}
		
		final Map<String, String> map = DatabaseManager.NATIVE_QUERIES.get(type);
		return map.put(id, query);
	}
	
	/**
	 * Register query.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param id
	 *            the id
	 * @param criteria
	 *            the criteria
	 */
	@SuppressWarnings ("unchecked")
	public static synchronized <T extends Entity> void registerQuery(final String id,
	                                                                 final Criteria<T> criteria) {
		final Type actualTypeArgument = ((ParameterizedType) criteria.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<T> actualRawTypeArgument = null;
		if (actualTypeArgument instanceof ParameterizedType) {
			actualRawTypeArgument = (Class<T>) ((ParameterizedType) actualTypeArgument).getRawType();
		} else {
			actualRawTypeArgument = (Class<T>) actualTypeArgument;
		}
		
		if (!DatabaseManager.STORED_QUERIES.containsKey(actualRawTypeArgument)) {
			DatabaseManager.STORED_QUERIES.put(actualRawTypeArgument, new HashMap<String, Criteria<?>>());
		}
		
		DatabaseManager.STORED_QUERIES.get(actualRawTypeArgument).put(id, criteria);
	}
}
