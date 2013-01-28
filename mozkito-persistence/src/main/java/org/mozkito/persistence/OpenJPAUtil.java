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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Root;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.lang.ArrayUtils;
import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaBuilder;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaQuery;

/**
 * The Class OpenJPAUtil.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class OpenJPAUtil implements PersistenceUtil {
	
	/** The factory. */
	private OpenJPAEntityManagerFactory factory;
	
	/** The entity manager. */
	private EntityManager               entityManager;
	
	/** The type. */
	private DatabaseType                type;
	
	/**
	 * Instantiates a new open jpa util.
	 */
	OpenJPAUtil() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#activeTransaction ()
	 */
	@Override
	public synchronized boolean activeTransaction() {
		return this.entityManager.getTransaction().isActive();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#beginTransaction ()
	 */
	@Override
	public synchronized void beginTransaction() {
		if (!this.entityManager.getTransaction().isActive()) {
			this.entityManager.getTransaction().begin();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#commitTransaction ()
	 */
	@Override
	public synchronized void commitTransaction() {
		if (this.entityManager.getTransaction().isActive()) {
			this.entityManager.getTransaction().commit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#createCriteria (java.lang.Class)
	 */
	@Override
	public <T> Criteria<T> createCriteria(final Class<T> clazz) {
		final OpenJPACriteriaBuilder builder = this.factory.getCriteriaBuilder();
		final OpenJPACriteriaQuery<T> query = this.factory.getCriteriaBuilder().createQuery(clazz);
		final Root<T> root = query.from(clazz);
		final Criteria<T> criteria = new Criteria<T>(root, builder, query);
		return criteria;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#createQuery (java.lang.String)
	 */
	@Override
	public <T> Query createNativeQuery(final String query,
	                                   final Class<T> clazz) {
		return this.entityManager.createNativeQuery(query, clazz);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#createQuery (java.lang.String)
	 */
	@Override
	public Query createQuery(final String query) {
		return this.entityManager.createQuery(query);
	}
	
	/**
	 * Creates the session factory.
	 * 
	 * @param options
	 *            the options
	 */
	@Override
	public void createSessionFactory(final DatabaseEnvironment options) {
		if (options.getDatabaseType().available()) {
			this.type = options.getDatabaseType();
			
			final Properties properties = new Properties();
			
			properties.put("openjpa.ConnectionURL", options.getUrl()); //$NON-NLS-1$
			
			switch (options.getDatabaseOptions()) {
				case VALIDATE_OR_CREATE_SCHEMA:
				case DROP_AND_CREATE_DATABASE:
					properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema"); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case DROP_CONTENTS:
					properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(SchemaAction='add,deleteTableContents')");//$NON-NLS-1$ //$NON-NLS-2$
					break;
				default:
					properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema"); //$NON-NLS-1$ //$NON-NLS-2$
					break;
			}
			
			properties.put("openjpa.ConnectionDriverName", options.getDatabaseDriver()); //$NON-NLS-1$
			properties.put("openjpa.ConnectionUserName", options.getDatabaseUsername()); //$NON-NLS-1$
			properties.put("openjpa.ConnectionPassword", options.getDatabasePassword()); //$NON-NLS-1$
			properties.put("openjpa.persistence-unit", options.getDatabaseUnit()); //$NON-NLS-1$
			
			createSessionFactory(properties);
		} else {
			throw UnrecoverableError.format("Driver for database type '%s' is not available. Please add '%s' to your classpath.", this.type, this.type.getDriver()); //$NON-NLS-1$
		}
	}
	
	/**
	 * Creates the session factory.
	 * 
	 * @param properties
	 *            the properties
	 */
	private void createSessionFactory(final Properties properties) {
		if (this.factory == null) {
			
			// this is a bity messy. this means someone called createSessionFactory directly
			assert this.type != null;
			
			String unit = properties.getProperty("openjpa.persistence-unit"); //$NON-NLS-1$
			
			if (unit == null) {
				final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
				ArrayUtils.reverse(trace);
				for (final StackTraceElement element : trace) {
					Class<?> activeClass;
					try {
						activeClass = Class.forName(element.getClassName());
						if (ClassFinder.extending(activeClass, Chain.class)) {
							unit = activeClass.getSimpleName().toLowerCase();
							break;
						}
					} catch (final ClassNotFoundException ignore) {
						// ignore
					}
				}
				
				if (unit == null) {
					if (Logger.logError()) {
						Logger.error("You have to set the 'database-unit' property.");
						
					}
					throw new Shutdown("Persistence unit property not specified and can't be determined automatically.");
				}
			}
			
			//			properties.remove("openjpa.persistence-unit"); //$NON-NLS-1$
			
			if (Logger.logInfo()) {
				Logger.info("Requesting persistence-unit: " + unit);
			}
			
			if (Logger.logDebug()) {
				Logger.debug("Using options: ");
				for (final Object property : properties.keySet()) {
					Logger.debug(property + ": " + properties.getProperty((String) property)); //$NON-NLS-1$
				}
			}
			if (Logger.logTrace()) {
				properties.put("openjpa.Log", "DefaultLevel=TRACE,Tool=TRACE");
			}
			
			final StringBuilder sb = new StringBuilder();
			sb.append(unit);
			sb.append("-persistence.xml");
			
			this.factory = OpenJPAPersistence.createEntityManagerFactory("persistence", sb.toString(), properties);
			
			if (this.factory == null) {
				if (Logger.logError()) {
					final InputStream stream = getClass().getResourceAsStream("/" + sb.toString());
					final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					String line = null;
					final StringBuilder builder = new StringBuilder();
					
					try {
						while ((line = reader.readLine()) != null) {
							builder.append(line).append(FileUtils.lineSeparator);
						}
					} catch (final IOException e) {
						if (Logger.logError()) {
							Logger.error(e);
						}
					}
					
					Logger.error("Cannot create persistence-unit using: " + JavaUtils.mapToString(properties)
					        + " and persistence.xml content: " + builder.toString());
				}
				throw new Shutdown("Could not initialize persistence-unit: " + unit);
			}
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Session factory already exists. Skipping creation.");
			}
		}
		this.entityManager = this.factory.createEntityManager();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#delete(de. unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void delete(final Annotated object) {
		this.entityManager.remove(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#executeNativeQuery (java.lang.String)
	 */
	@Override
	public void executeNativeQuery(final String queryString) {
		try {
			this.entityManager.getTransaction().begin();
			final OpenJPAEntityManager ojem = (OpenJPAEntityManager) this.entityManager;
			final Connection conn = (Connection) ojem.getConnection();
			final Statement statement = conn.createStatement();
			if (queryString.trim().toLowerCase().startsWith("select")) { //$NON-NLS-1$
				statement.execute(queryString);
			} else {
				statement.executeUpdate(queryString);
			}
			statement.close();
			this.entityManager.getTransaction().commit();
		} catch (final SQLException e) {
			e.printStackTrace();
			this.entityManager.getTransaction().rollback();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#executeNativeSelectQuery(java.lang.String)
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public List executeNativeSelectQuery(final String queryString) {
		final Query nativeQuery = this.entityManager.createNativeQuery(queryString);
		return nativeQuery.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#executeQuery (java.lang.String)
	 */
	@Override
	public void executeQuery(final String queryString) {
		final Query query = this.entityManager.createQuery(queryString);
		query.executeUpdate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#exmerge(de .unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void exmerge(final Annotated object) {
		this.entityManager.detach(object);
		this.entityManager.merge(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#flush()
	 */
	@Override
	public synchronized void flush() {
		this.entityManager.flush();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#getToolInformation ()
	 */
	@SuppressWarnings ("deprecation")
	@Override
	public String getToolInformation() {
		final OpenJPAConfiguration configuration = this.factory.getConfiguration();
		final Map<String, Object> properties = configuration.toProperties(false);
		final StringBuilder builder = new StringBuilder();
		int max = 0;
		
		for (final String key : properties.keySet()) {
			if (key.length() > max) {
				max = key.length();
			}
		}
		
		final Regex regex = new Regex(".*password.*|.*username.*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
		for (final String key : properties.keySet()) {
			if (regex.matches(key)) {
				properties.put(key, ((String) properties.get(key)).replaceAll(".", "*")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			builder.append(String.format("%-" + max + "s : %s", key, properties.get(key))); //$NON-NLS-1$ //$NON-NLS-2$
			builder.append(FileUtils.lineSeparator);
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#getType()
	 */
	@Override
	public DatabaseType getType() {
		return this.type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#load(javax .persistence.criteria.CriteriaQuery)
	 */
	@Override
	public <T> List<T> load(final Criteria<T> criteria) {
		final TypedQuery<T> query = this.entityManager.createQuery(criteria.getQuery());
		return query.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#load(de. unisaarland.cs.st.reposuite.persistence.Criteria, int)
	 */
	@Override
	public <T> List<T> load(final Criteria<T> criteria,
	                        final int sizeLimit) {
		final TypedQuery<T> query = this.entityManager.createQuery(criteria.getQuery()).setMaxResults(sizeLimit);
		return query.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#loadById(java .lang.Object, java.lang.Class)
	 */
	@Override
	public <T extends Annotated> T loadById(final Object id,
	                                        final Class<T> clazz) {
		final String prefix = "get";
		final int prefixLength = prefix.length();
		// determine id column
		for (final Method m : clazz.getDeclaredMethods()) {
			// found
			if ((m.getAnnotation(Id.class) != null) && m.getName().startsWith(prefix)) {
				if (m.getReturnType().equals(id.getClass()) || m.getReturnType().isAssignableFrom(id.getClass())
				        || wrap(m.getReturnType()).equals(wrap(id.getClass()))) {
					final Criteria<T> criteria = createCriteria(clazz);
					String column = null;
					
					column = m.getName().substring(prefixLength, prefixLength + 1).toLowerCase()
					        + m.getName().substring(prefixLength + 1);
					criteria.eq(column, id);
					final List<T> list = load(criteria);
					if (!list.isEmpty()) {
						return list.get(0);
					}
					return null;
				}
				throw new UnrecoverableError("Id type (" + id.getClass().getCanonicalName()
				        + ") does not match actual id type (" + m.getReturnType().getCanonicalName()
				        + ") which is not assignable from " + id.getClass().getCanonicalName() + ".");
			}
		}
		
		throw new UnrecoverableError("Class " + clazz.getCanonicalName()
		        + " does not have an Id column defined for a getter.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#commitTransaction ()
	 */
	@Override
	public synchronized void rollbackTransaction() {
		if (this.entityManager.getTransaction().isActive()) {
			this.entityManager.getTransaction().rollback();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#save(de. unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void save(final Annotated object) {
		this.entityManager.persist(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#saveOrUpdate (org.mozkito.persistence.Annotated)
	 */
	@Override
	public synchronized void saveOrUpdate(final Annotated object) {
		if (this.entityManager.contains(object)) {
			update(object);
		} else {
			save(object);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#shutdown()
	 */
	@Override
	public void shutdown() {
		if (this.entityManager.isOpen()) {
			this.entityManager.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.PersistenceUtil#update(de. unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void update(final Annotated object) {
		this.entityManager.merge(object);
	}
	
	/**
	 * Wrap.
	 * 
	 * @param returnType
	 *            the return type
	 * @return the class
	 */
	private Class<?> wrap(final Class<?> returnType) {
		if (returnType.isPrimitive()) {
			if ("long".equals(returnType.getCanonicalName())) { //$NON-NLS-1$
				return Long.class;
			}
			if ("char".equals(returnType.getCanonicalName())) { //$NON-NLS-1$
				return Character.class;
			}
			if ("int".equals(returnType.getCanonicalName())) { //$NON-NLS-1$
				return Integer.class;
			}
			if ("double".equals(returnType.getCanonicalName())) { //$NON-NLS-1$
				return Double.class;
			}
			if ("short".equals(returnType.getCanonicalName())) { //$NON-NLS-1$
				return Short.class;
			}
			if ("byte".equals(returnType.getCanonicalName())) { //$NON-NLS-1$
				return Byte.class;
			}
			if ("float".equals(returnType.getCanonicalName())) { //$NON-NLS-1$
				return Float.class;
			}
		}
		return returnType;
	}
	
}
