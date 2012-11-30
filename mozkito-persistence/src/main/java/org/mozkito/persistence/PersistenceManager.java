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

/**
 * The Class PersistenceManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PersistenceManager {
	
	/** The Constant nativeQueries. */
	private static final Map<String, Map<String, String>>        nativeQueries = new HashMap<String, Map<String, String>>();
	
	/** The Constant storedQueries. */
	private static final Map<Class<?>, Map<String, Criteria<?>>> storedQueries = new HashMap<Class<?>, Map<String, Criteria<?>>>();
	
	/**
	 * Creates the database.
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
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static void createDatabase(final String host,
	                                  final String database,
	                                  final String user,
	                                  final String password,
	                                  final String type,
	                                  final String driver) throws SQLException {
		try {
			Class.forName(driver);
		} catch (final ClassNotFoundException e) {
			throw new SQLException("Could not load JDBC driver " + driver, e);
		}
		
		if ((host == null) || host.isEmpty()) {
			final File file = new File(database);
			if (file.exists()) {
				file.delete();
			}
			
		} else {
			// FIXME determine default database other than postgres
			final Connection connection = DriverManager.getConnection("jdbc:" + type + "://" + host + "/postgres", user, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			                                                          password);
			if (connection != null) {
				final Statement statement = connection.createStatement();
				if (statement != null) {
					statement.executeUpdate("CREATE DATABASE " + database + ";"); //$NON-NLS-1$
					statement.close();
				}
				connection.close();
			}
		}
	}
	
	/**
	 * Creates the util.
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
	 * @param dropContents
	 *            the drop contents
	 * @param middleware
	 *            the middleware
	 * @return the persistence util
	 * @throws UnrecoverableError
	 *             the unrecoverable error
	 */
	public static PersistenceUtil createUtil(final String host,
	                                         final String database,
	                                         final String user,
	                                         final String password,
	                                         final String type,
	                                         final String driver,
	                                         final String unit,
	                                         final ConnectOptions dropContents,
	                                         final Class<?> middleware) throws UnrecoverableError {
		
		PersistenceUtil instance = null;
		try {
			
			instance = (PersistenceUtil) middleware.newInstance();
			instance.createSessionFactory(host, database, user, password, type, driver, unit, dropContents);
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
	 * @param middleware
	 *            the middleware
	 * @return the persistence util
	 * @throws UnrecoverableError
	 *             the unrecoverable error
	 */
	@SuppressWarnings ("unchecked")
	public static PersistenceUtil createUtil(final String host,
	                                         final String database,
	                                         final String user,
	                                         final String password,
	                                         final String type,
	                                         final String driver,
	                                         final String unit,
	                                         final ConnectOptions options,
	                                         final String middleware) throws UnrecoverableError {
		final String className = PersistenceUtil.class.getPackage().getName() + "." + middleware + "Util";
		Class<PersistenceUtil> klass = null;
		try {
			
			klass = (Class<PersistenceUtil>) Class.forName(className);
			return createUtil(host, database, user, password, type, driver, unit, options, klass);
		} catch (final ClassNotFoundException e) {
			throw new ClassLoadingError(e, className);
		}
		
	}
	
	/**
	 * Drop database.
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
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static void dropDatabase(final String host,
	                                final String database,
	                                final String user,
	                                final String password,
	                                final String type,
	                                final String driver) throws SQLException {
		
		try {
			Class.forName(driver);
		} catch (final ClassNotFoundException e) {
			throw new SQLException("Could not load JDBC driver " + driver, e);
		}
		
		if ((host == null) || host.isEmpty()) {
			final File file = new File(database);
			file.delete();
		} else {
			final Connection connection = DriverManager.getConnection("jdbc:" + type + "://" + host + "/postgres", user, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			                                                          password);
			if (connection != null) {
				final Statement statement = connection.createStatement();
				if (statement != null) {
					statement.executeUpdate("DROP DATABASE " + database + ";"); //$NON-NLS-1$ //$NON-NLS-2$
					statement.close();
				}
				connection.close();
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
		final String databaseType = util.getType().toLowerCase();
		
		if (nativeQueries.containsKey(databaseType) && nativeQueries.get(databaseType).containsKey(id)) {
			return nativeQueries.get(databaseType).get(id);
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
		return (Criteria<T>) storedQueries.get(clazz).get(id);
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
	public static synchronized String registerNativeQuery(final String type,
	                                                      final String id,
	                                                      final String query) {
		final String databaseType = type.toLowerCase();
		if (!nativeQueries.containsKey(databaseType)) {
			nativeQueries.put(databaseType, new HashMap<String, String>());
		}
		
		final Map<String, String> map = nativeQueries.get(databaseType);
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
		
		if (!storedQueries.containsKey(actualRawTypeArgument)) {
			storedQueries.put(actualRawTypeArgument, new HashMap<String, Criteria<?>>());
		}
		
		storedQueries.get(actualRawTypeArgument).put(id, criteria);
	}
}
