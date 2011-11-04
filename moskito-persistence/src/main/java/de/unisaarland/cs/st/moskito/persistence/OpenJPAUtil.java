/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persistence;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
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
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.lang.ArrayUtils;
import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaBuilder;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaQuery;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class OpenJPAUtil implements PersistenceUtil {
	
	private static OpenJPAEntityManagerFactory factory;
	
	/**
	 * @param properties
	 */
	public static void createSessionFactory(final Properties properties) {
		if (factory == null) {
			if (type == null) {
				String url = (String) properties.get("openjpa.ConnectionURL");
				if (url != null) {
					type = url.split(":")[1];
				} else {
					type = "unknown";
				}
			}
			
			String unit = properties.getProperty("openjpa.persistence-unit");
			
			if (unit == null) {
				StackTraceElement[] trace = Thread.currentThread().getStackTrace();
				ArrayUtils.reverse(trace);
				for (StackTraceElement element : trace) {
					Class<?> activeClass;
					try {
						activeClass = Class.forName(element.getClassName());
						if (ClassFinder.extending(activeClass, AndamaChain.class)) {
							unit = activeClass.getSimpleName().toLowerCase();
							break;
						}
					} catch (ClassNotFoundException e) {
					}
				}
				
				if (unit == null) {
					if (Logger.logError()) {
						Logger.error("You have to set the 'database-unit' property.");
						
					}
					throw new Shutdown("Persistence unit property not specified and can't be determined automatically.");
				}
			}
			
			properties.remove("openjpa.persistence-unit");
			
			if (Logger.logInfo()) {
				Logger.info("Requesting persistence-unit: " + unit);
			}
			
			if (Logger.logDebug()) {
				Logger.debug("Using options: ");
				for (Object property : properties.keySet()) {
					Logger.debug(property + ": " + properties.getProperty((String) property));
				}
			}
			factory = OpenJPAPersistence.createEntityManagerFactory(unit, null, properties);
			// FIXME
			// try {
			// Collection<Class<?>> annotatedClasses =
			// ClassFinder.getClassesOfInterface(Core.class.getPackage(),
			// Annotated.class);
			//
			// if (Logger.logInfo()) {
			// for (Class<?> c : annotatedClasses) {
			// Logger.info("Registering persistence entity: " +
			// c.getCanonicalName());
			// }
			// }
			// ManagedClassSubclasser.prepareUnenhancedClasses(factory.getConfiguration(),
			// annotatedClasses, null);
			// } catch (Exception e) {
			// if (Logger.logError()) {
			// Logger.error(e.getMessage(), e);
			// }
			// throw new RuntimeException(e);
			// }
			
			if (factory == null) {
				throw new Shutdown("Could not initialize persistence-unit: " + unit);
			}
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Session factory already exists. Skipping creation.");
			}
		}
	}
	
	/**
	 * @param host
	 * @param database
	 * @param user
	 * @param password
	 * @param type
	 * @param driver
	 */
	public static void createSessionFactory(final String host,
			final String database,
			final String user,
			final String password,
			final String type,
			final String driver,
			final String unit) {
		String url = "jdbc:" + type.toLowerCase() + "://" + host + "/" + database;
		
		Properties properties = new Properties();
		properties.put("openjpa.ConnectionURL", url);
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema");
		properties.put("openjpa.ConnectionDriverName", driver);
		properties.put("openjpa.ConnectionUserName", user);
		properties.put("openjpa.ConnectionPassword", password);
		properties.put("openjpa.persistence-unit", unit);
		OpenJPAUtil.type = type;
		
		createSessionFactory(properties);
	}
	
	/**
	 * @param string
	 */
	public static void createTestSessionFactory(final String string) {
		Logger.setLogLevel(LogLevel.OFF);
		Properties properties = new Properties();
		String url = "jdbc:postgresql://" + System.getProperty("database.host", "grid1.st.cs.uni-saarland.de") + "/"
				+ System.getProperty("database.name", "reposuite_test");
		properties.put("openjpa.ConnectionURL", url);
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(SchemaAction='add,deleteTableContents')");
		properties.put("openjpa.ConnectionDriverName", "org.postgresql.Driver");
		properties.put("openjpa.ConnectionUserName", System.getProperty("database.username", "miner"));
		properties.put("openjpa.ConnectionPassword", System.getProperty("database.password", "miner"));
		properties.put("openjpa.persistence-unit", string);
		
		OpenJPAUtil.createSessionFactory(properties);
	}
	
	/**
	 * @return
	 * @throws UninitializedDatabaseException
	 */
	public static PersistenceUtil getInstance() throws UninitializedDatabaseException {
		if (factory == null) {
			throw new UninitializedDatabaseException();
		}
		
		if (provider.containsKey(Thread.currentThread().getName())) {
			return provider.get(Thread.currentThread().getName());
		} else {
			OpenJPAUtil util = new OpenJPAUtil();
			provider.put(Thread.currentThread(), util);
			return util;
		}
	}
	
	private final EntityManager             entityManager;
	private static String                   type;
	
	private static Map<Thread, OpenJPAUtil> provider = new HashMap<Thread, OpenJPAUtil>();
	
	/**
	 * 
	 */
	private OpenJPAUtil() {
		this.entityManager = factory.createEntityManager();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#activeTransaction
	 * ()
	 */
	@Override
	public synchronized boolean activeTransaction() {
		return this.entityManager.getTransaction().isActive();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#beginTransaction
	 * ()
	 */
	@Override
	public synchronized void beginTransaction() {
		if (!this.entityManager.getTransaction().isActive()) {
			this.entityManager.getTransaction().begin();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#commitTransaction
	 * ()
	 */
	@Override
	public synchronized void commitTransaction() {
		if (this.entityManager.getTransaction().isActive()) {
			this.entityManager.getTransaction().commit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createCriteria
	 * (java.lang.Class)
	 */
	@Override
	public <T> Criteria<T> createCriteria(final Class<T> clazz) {
		OpenJPACriteriaBuilder builder = factory.getCriteriaBuilder();
		OpenJPACriteriaQuery<T> query = factory.getCriteriaBuilder().createQuery(clazz);
		Root<T> root = query.from(clazz);
		Criteria<T> criteria = new Criteria<T>(root, builder, query);
		return criteria;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createQuery
	 * (java.lang.String)
	 */
	@Override
	public <T> Query createNativeQuery(final String query,
			final Class<T> clazz) {
		return this.entityManager.createNativeQuery(query, clazz);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createQuery
	 * (java.lang.String)
	 */
	@Override
	public Query createQuery(final String query) {
		return this.entityManager.createQuery(query);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#delete(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void delete(final Annotated object) {
		this.entityManager.remove(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#executeNativeQuery
	 * (java.lang.String)
	 */
	@Override
	public void executeNativeQuery(final String queryString) {
		try {
			this.entityManager.getTransaction().begin();
			OpenJPAEntityManager ojem = (OpenJPAEntityManager) this.entityManager;
			Connection conn = (Connection) ojem.getConnection();
			Statement statement = conn.createStatement();
			if (queryString.trim().toLowerCase().startsWith("select")) {
				statement.execute(queryString);
			} else {
				statement.executeUpdate(queryString);
			}
			statement.close();
			this.entityManager.getTransaction().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.entityManager.getTransaction().rollback();
		}
	}
	
	@SuppressWarnings ("rawtypes")
	@Override
	public List executeNativeSelectQuery(final String queryString) {
		Query nativeQuery = this.entityManager.createNativeQuery(queryString);
		return nativeQuery.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#executeQuery
	 * (java.lang.String)
	 */
	@Override
	public void executeQuery(final String queryString) {
		Query query = this.entityManager.createQuery(queryString);
		query.executeUpdate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#exmerge(de
	 * .unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void exmerge(final Annotated object) {
		this.entityManager.detach(object);
		this.entityManager.merge(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#flush()
	 */
	@Override
	public synchronized void flush() {
		this.entityManager.flush();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#getToolInformation
	 * ()
	 */
	@SuppressWarnings ("deprecation")
	@Override
	public String getToolInformation() {
		OpenJPAConfiguration configuration = factory.getConfiguration();
		Map<String, Object> properties = configuration.toProperties(false);
		StringBuilder builder = new StringBuilder();
		int max = 0;
		
		for (String key : properties.keySet()) {
			if (key.length() > max) {
				max = key.length();
			}
		}
		
		Regex regex = new Regex(".*password.*", Pattern.CASE_INSENSITIVE);
		for (String key : properties.keySet()) {
			// FIXME this should actually check for case-insensitive matches of
			// "password" and "username"
			if (regex.matches(key)) {
				properties.put(key, ((String) properties.get(key)).replaceAll(".", "*"));
			}
			
			builder.append(String.format("%-" + max + "s : %s", key, properties.get(key)));
			builder.append(FileUtils.lineSeparator);
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#getType()
	 */
	@Override
	public String getType() {
		return type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#globalShutdown
	 * ()
	 */
	@Override
	public synchronized void globalShutdown() {
		for (Thread t : provider.keySet()) {
			provider.get(t).shutdown();
		}
		factory.close();
		factory = null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#load(javax
	 * .persistence.criteria.CriteriaQuery)
	 */
	@Override
	public <T> List<T> load(final Criteria<T> criteria) {
		TypedQuery<T> query = this.entityManager.createQuery(criteria.getQuery());
		return query.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#load(de.
	 * unisaarland.cs.st.reposuite.persistence.Criteria, int)
	 */
	@Override
	public <T> List<T> load(final Criteria<T> criteria,
			final int sizeLimit) {
		TypedQuery<T> query = this.entityManager.createQuery(criteria.getQuery()).setMaxResults(sizeLimit);
		return query.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#loadById(java
	 * .lang.Object, java.lang.Class)
	 */
	@Override
	public <T, I> T loadById(final I id,
			final Class<T> clazz) {
		// determine id column
		for (Method m : clazz.getDeclaredMethods()) {
			// found
			if ((m.getAnnotation(Id.class) != null) && m.getName().startsWith("get")) {
				if (m.getReturnType().equals(id.getClass()) || m.getReturnType().isAssignableFrom(id.getClass())
						|| wrap(m.getReturnType()).equals(wrap(id.getClass()))) {
					Criteria<T> criteria = createCriteria(clazz);
					String column = null;
					
					column = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
					criteria.eq(column, id);
					List<T> list = load(criteria);
					if (!list.isEmpty()) {
						return list.get(0);
					} else {
						return null;
					}
				} else {
					throw new UnrecoverableError("Id type (" + id.getClass().getCanonicalName()
							+ ") does not match actual id type (" + m.getReturnType().getCanonicalName()
							+ ") which is not assignable from " + id.getClass().getCanonicalName() + ".");
				}
			}
		}
		
		throw new UnrecoverableError("Class " + clazz.getCanonicalName()
				+ " does not have an Id column defined for a getter.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#commitTransaction
	 * ()
	 */
	@Override
	public synchronized void rollbackTransaction() {
		if (this.entityManager.getTransaction().isActive()) {
			this.entityManager.getTransaction().rollback();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#save(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void save(final Annotated object) {
		this.entityManager.persist(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#saveOrUpdate
	 * (de.unisaarland.cs.st.moskito.persistence.Annotated)
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
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#shutdown()
	 */
	@Override
	public void shutdown() {
		if (this.entityManager.isOpen()) {
			this.entityManager.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#update(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public synchronized void update(final Annotated object) {
		this.entityManager.merge(object);
	}
	
	/**
	 * @param returnType
	 * @param class1
	 * @return
	 */
	private Class<?> wrap(final Class<?> returnType) {
		if (returnType.isPrimitive()) {
			if (returnType.getCanonicalName().equals("long")) {
				return Long.class;
			}
			if (returnType.getCanonicalName().equals("char")) {
				return Character.class;
			}
			if (returnType.getCanonicalName().equals("int")) {
				return Integer.class;
			}
			if (returnType.getCanonicalName().equals("double")) {
				return Double.class;
			}
			if (returnType.getCanonicalName().equals("short")) {
				return Short.class;
			}
			if (returnType.getCanonicalName().equals("byte")) {
				return Byte.class;
			}
			if (returnType.getCanonicalName().equals("float")) {
				return Float.class;
			}
		}
		return returnType;
	}
	
}
