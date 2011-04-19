/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.ArrayUtils;
import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaBuilder;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaQuery;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
						if (ClassFinder.extending(activeClass, RepoSuiteToolchain.class)) {
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
		entityManager = factory.createEntityManager();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#activeTransaction
	 * ()
	 */
	@Override
	public boolean activeTransaction() {
		return entityManager.getTransaction().isActive();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#beginTransaction
	 * ()
	 */
	@Override
	public void beginTransaction() {
		entityManager.getTransaction().begin();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#commitTransaction
	 * ()
	 */
	@Override
	public void commitTransaction() {
		if (entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().commit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#createCriteria
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
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#createQuery
	 * (java.lang.String)
	 */
	@Override
	public Query createQuery(final String query) {
		return entityManager.createQuery(query);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#delete(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void delete(final Annotated object) {
		entityManager.remove(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#executeNativeQuery
	 * (java.lang.String)
	 */
	@Override
	public void executeNativeQuery(final String queryString) {
		try {
			entityManager.getTransaction().begin();
			OpenJPAEntityManager ojem = (OpenJPAEntityManager) entityManager;
			Connection conn = (Connection) ojem.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate(queryString);
			statement.close();
			entityManager.getTransaction().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
		}
	}
	
	@SuppressWarnings ("rawtypes")
	@Override
	public List executeNativeSelectQuery(final String queryString) {
		Query nativeQuery = entityManager.createNativeQuery(queryString);
		return nativeQuery.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#executeQuery
	 * (java.lang.String)
	 */
	@Override
	public void executeQuery(final String queryString) {
		Query query = entityManager.createQuery(queryString);
		query.executeUpdate();
	}
	
	@Override
	public RCSFile fetchRCSFile(final Long id) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RCSFile> criteria = builder.createQuery(RCSFile.class);
		Root<RCSFile> file = criteria.from(RCSFile.class);
		criteria.where(builder.equal(file.get("generatedId"), id));
		TypedQuery<RCSFile> query = entityManager.createQuery(criteria);
		
		return query.getResultList().get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#
	 * fetchRCSTransaction(java.lang.String)
	 */
	@Override
	public RCSTransaction fetchRCSTransaction(final String id) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RCSTransaction> criteria = builder.createQuery(RCSTransaction.class);
		Root<RCSTransaction> transaction = criteria.from(RCSTransaction.class);
		criteria.where(builder.equal(transaction.get("id"), id));
		TypedQuery<RCSTransaction> query = entityManager.createQuery(criteria);
		
		return query.getResultList().get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#getToolInformation
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
		
		for (String key : properties.keySet()) {
			if (((String) properties.get(key)).contains("Password")) {
				properties.put(key, ((String) properties.get(key)).replaceAll(".", "*"));
			}
			
			builder.append(String.format("%-" + max + "s : %s", key, properties.get(key)));
			builder.append(FileUtils.lineSeparator);
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#getType()
	 */
	@Override
	public String getType() {
		return type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#load(javax
	 * .persistence.criteria.CriteriaQuery)
	 */
	@Override
	public <T> List<T> load(final Criteria<T> criteria) {
		TypedQuery<T> query = entityManager.createQuery(criteria.getQuery());
		return query.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#save(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void save(final Annotated object) {
		entityManager.persist(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#saveOrUpdate
	 * (de.unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void saveOrUpdate(final Annotated object) {
		if (entityManager.contains(object)) {
			update(object);
		} else {
			save(object);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#shutdown()
	 */
	@Override
	public void shutdown() {
		for (Thread t : provider.keySet()) {
			provider.get(t).entityManager.close();
		}
		
		if (factory.isOpen()) {
			factory.close();
			factory = null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#update(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void update(final Annotated object) {
		entityManager.merge(object);
	}
	
}
