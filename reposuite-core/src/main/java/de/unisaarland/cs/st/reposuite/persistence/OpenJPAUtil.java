/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Collection;
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

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.enhance.ManagedClassSubclasser;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaBuilder;
import org.apache.openjpa.persistence.criteria.OpenJPACriteriaQuery;

import de.unisaarland.cs.st.reposuite.Core;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
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
	@SuppressWarnings ("deprecation")
	static void createSessionFactory(final Properties properties) {
		if (factory == null) {
			
			factory = OpenJPAPersistence.createEntityManagerFactory("Reposuite", null, properties);
			try {
				Collection<Class<?>> annotatedClasses = ClassFinder.getClassesOfInterface(Core.class.getPackage(),
				                                                                          Annotated.class);
				ManagedClassSubclasser.prepareUnenhancedClasses(factory.getConfiguration(), annotatedClasses, null);
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new RuntimeException();
			}
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Session factory already exists. Skipping creating.");
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
	                                        final String driver) {
		String url = "jdbc:" + type.toLowerCase() + "://" + host + "/" + database;
		// + "?useUnicode=true&characterEncoding=UTF-8;";
		
		Properties properties = new Properties();
		properties.put("openjpa.ConnectionURL", url);
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema");
		properties.put("openjpa.ConnectionDriverName", driver);
		properties.put("openjpa.ConnectionUserName", user);
		properties.put("openjpa.ConnectionPassword", password);
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
	
	public OpenJPAUtil() {
		this.entityManager = factory.createEntityManager();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#beginTransaction
	 * ()
	 */
	@Override
	public void beginTransaction() {
		this.entityManager.getTransaction().begin();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#commitTransaction
	 * ()
	 */
	@Override
	public void commitTransaction() {
		if (this.entityManager.getTransaction().isActive()) {
			this.entityManager.getTransaction().commit();
		}
	}
	
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public Criteria createCriteria(final Class<?> clazz) {
		OpenJPACriteriaBuilder builder = factory.getCriteriaBuilder();
		OpenJPACriteriaQuery<?> query = factory.getCriteriaBuilder().createQuery(clazz);
		Root<?> root = query.from(clazz);
		Criteria criteria = new Criteria(root, builder, query);
		return criteria;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#delete(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void delete(final Annotated object) {
		this.entityManager.remove(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#executeQuery
	 * (java.lang.String)
	 */
	@Override
	public void executeQuery(final String queryString) {
		Query query = this.entityManager.createQuery(queryString);
		query.executeUpdate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#
	 * fetchRCSTransaction(java.lang.String)
	 */
	@Override
	public RCSTransaction fetchRCSTransaction(final String id) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<RCSTransaction> criteria = builder.createQuery(RCSTransaction.class);
		Root<RCSTransaction> transaction = criteria.from(RCSTransaction.class);
		criteria.where(builder.equal(transaction.get("id"), id));
		TypedQuery<RCSTransaction> query = this.entityManager.createQuery(criteria);
		
		return query.getResultList().get(0);
	}
	
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
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public List load(final Criteria criteria) {
		TypedQuery query = this.entityManager.createQuery(criteria.getQuery());
		return query.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#save(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void save(final Annotated object) {
		this.entityManager.persist(object);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#saveOrUpdate
	 * (de.unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void saveOrUpdate(final Annotated object) {
		if (this.entityManager.contains(object)) {
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
		this.entityManager.close();
		factory.close();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil#update(de.
	 * unisaarland.cs.st.reposuite.persistence.Annotated)
	 */
	@Override
	public void update(final Annotated object) {
		this.entityManager.merge(object);
	}
	
}
