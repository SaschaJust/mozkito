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

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.andama.exceptions.InstantiationError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.FileUtils;

/**
 * The Class PersistenceManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PersistenceManager {
	
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
	 * @param middleware
	 *            the middleware
	 * @return the persistence util
	 */
	public static PersistenceUtil createUtil(final DatabaseEnvironment options,
	                                         final Class<?> middleware) {
		
		PersistenceUtil instance = null;
		try {
			
			instance = (PersistenceUtil) middleware.newInstance();
			instance.createSessionFactory(options);
		} catch (final InstantiationException e) {
			throw new InstantiationError(e, middleware, null, new Object[0]);
		} catch (final IllegalAccessException e) {
			throw new UnrecoverableError(e);
		}
		return instance;
	}
	
	/**
	 * Creates the util.
	 * 
	 * @param options
	 *            the options
	 * @param middleware
	 *            the middleware
	 * @return the persistence util
	 */
	@SuppressWarnings ("unchecked")
	public static PersistenceUtil createUtil(final DatabaseEnvironment options,
	                                         final String middleware) {
		final String className = PersistenceUtil.class.getPackage().getName() + "." + middleware + "Util";
		Class<PersistenceUtil> klass = null;
		try {
			
			klass = (Class<PersistenceUtil>) Class.forName(className);
			return createUtil(options, klass);
		} catch (final ClassNotFoundException e) {
			throw new ClassLoadingError(e, className);
		}
		
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
		
		if (PersistenceManager.NATIVE_QUERIES.containsKey(databaseType)
		        && PersistenceManager.NATIVE_QUERIES.get(databaseType).containsKey(id)) {
			return PersistenceManager.NATIVE_QUERIES.get(databaseType).get(id);
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
	public static synchronized <T> Criteria<T> getStoredQuery(final String id,
	                                                          final Class<T> clazz) {
		return (Criteria<T>) PersistenceManager.STORED_QUERIES.get(clazz).get(id);
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
		if (!PersistenceManager.NATIVE_QUERIES.containsKey(type)) {
			PersistenceManager.NATIVE_QUERIES.put(type, new HashMap<String, String>());
		}
		
		final Map<String, String> map = PersistenceManager.NATIVE_QUERIES.get(type);
		return map.put(id, query);
	}
	
	/**
	 * Register prepared query.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param query
	 *            the query
	 */
	public static synchronized <T> void registerPreparedQuery(final PreparedQuery<T> query) {
		// ignore
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
	public static synchronized <T> void registerQuery(final String id,
	                                                  final Criteria<T> criteria) {
		final Type actualTypeArgument = ((ParameterizedType) criteria.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<T> actualRawTypeArgument = null;
		if (actualTypeArgument instanceof ParameterizedType) {
			actualRawTypeArgument = (Class<T>) ((ParameterizedType) actualTypeArgument).getRawType();
		} else {
			actualRawTypeArgument = (Class<T>) actualTypeArgument;
		}
		
		if (!PersistenceManager.STORED_QUERIES.containsKey(actualRawTypeArgument)) {
			PersistenceManager.STORED_QUERIES.put(actualRawTypeArgument, new HashMap<String, Criteria<?>>());
		}
		
		PersistenceManager.STORED_QUERIES.get(actualRawTypeArgument).put(id, criteria);
	}
}
