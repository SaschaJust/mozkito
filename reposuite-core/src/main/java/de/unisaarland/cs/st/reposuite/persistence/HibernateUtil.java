package de.unisaarland.cs.st.reposuite.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

import de.unisaarland.cs.st.reposuite.Core;
import de.unisaarland.cs.st.reposuite.exceptions.LoadingException;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class HibernateUtil {
	
	private static class QueryWork implements Work {
		
		private final String query;
		
		public QueryWork(final String q) {
			this.query = q;
		}
		
		@Override
		public void execute(final Connection connection) throws SQLException {
			Statement st = connection.createStatement();
			st.execute(this.query);
		}
	}
	
	private static SessionFactory             sessionFactory;
	private static String                     type;
	
	private static Map<Thread, HibernateUtil> instances = new HashMap<Thread, HibernateUtil>();
	
	/**
	 * @param properties
	 */
	public static void createSessionFactory(final Properties properties) {
		if (sessionFactory == null) {
			AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
			annotationConfiguration.addProperties(properties);
			Collection<Class<?>> annotatedClasses;
			
			try {
				annotatedClasses = ClassFinder.getClassesOfInterface(Core.class.getPackage(), Annotated.class);
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new RuntimeException();
			}
			
			for (Class<?> clazz : annotatedClasses) {
				
				if (Logger.logDebug()) {
					Logger.debug("Registering class " + clazz.getSimpleName() + " for persistence.	");
				}
				annotationConfiguration.addAnnotatedClass(clazz);
			}
			
			sessionFactory = annotationConfiguration.buildSessionFactory();
			String url = properties.get("hibernate.connection.url").toString();
			String[] split = url.split(":");
			if (split.length < 3) {
				if (Logger.logWarn()) {
					Logger.warn("Could not determine connection type. Set to `<unknown>`.");
				}
				type = "<unknown>";
			} else {
				type = split[1];
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
	 * @throws HibernateException
	 */
	public static void createSessionFactory(final String host, final String database, final String user,
	                                        final String password, final String type, final String driver) throws HibernateException {
		try {
			String url = "jdbc:" + type.toLowerCase() + "://" + host + "/" + database
			+ "?useUnicode=true&characterEncoding=UTF-8";
			
			Properties properties = new Properties();
			properties.put("hibernate.connection.url", url);
			properties.put("hibernate.hbm2ddl.auto", "update");
			
			//			properties.put("hibernate.show_sql", "true");
			
			properties.put("hibernate.connection.autocommit", "false");
			properties.put("hibernate.show_sql", "false");
			properties.put("hibernate.connection.driver_class", driver);
			properties.put("hibernate.connection.username", user);
			properties.put("hibernate.connection.password", password);
			
			createSessionFactory(properties);
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	
	/**
	 * @return
	 * @throws UninitializedDatabaseException
	 */
	public static HibernateUtil getInstance() throws UninitializedDatabaseException {
		if (sessionFactory == null) {
			throw new UninitializedDatabaseException();
		}
		
		// FIXME this should be a toolchain instead of a thread
		if (instances.containsKey(Thread.currentThread())) {
			return instances.get(Thread.currentThread());
		} else {
			HibernateUtil util = new HibernateUtil();
			instances.put(Thread.currentThread(), util);
			return util;
		}
	}
	
	public static String getType() {
		return type;
	}
	
	/**
	 * 
	 */
	public static void shutdown() {
		for (Thread thread : instances.keySet()) {
			instances.get(thread).shutdownSession();
		}
		
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}
	
	private final Session session;
	
	private Transaction   transaction;
	
	/**
	 * 
	 */
	private HibernateUtil() {
		Condition.notNull(sessionFactory, "");
		this.session = sessionFactory.openSession();
	}
	
	/**
	 * 
	 */
	public void beginTransaction() {
		if (this.transaction == null) {
			this.transaction = this.session.beginTransaction();
		}
	}
	
	/**
	 * 
	 */
	public void commitTransaction() {
		
		if ((this.transaction != null) && this.transaction.isActive()) {
			try {
				this.transaction.commit();
			} catch (HibernateException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				this.transaction.rollback();
				throw new RuntimeException(HibernateUtil.class.getSimpleName() + ": " + e.getMessage(), e);
			}
		}
		this.transaction = this.session.beginTransaction();
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public Criteria createCriteria(final Class<?> clazz) {
		if (Arrays.asList(clazz.getInterfaces()).contains(Annotated.class)) {
			return this.session.createCriteria(clazz);
		} else {
			return null;
		}
	}
	
	/**
	 * Creates the query.
	 * 
	 * @param query
	 *            the query
	 * @param clazz
	 *            the clazz
	 * @return the criteria
	 */
	@NoneNull
	public SQLQuery createSQLQuery(@NotNull final String query) {
		return this.session.createSQLQuery(query);
	}
	
	/**
	 * Creates the query.
	 * 
	 * @param query
	 *            the query
	 * @param clazz
	 *            the clazz
	 * @return the criteria
	 */
	@NoneNull
	public SQLQuery createSQLQuery(final String query, final Class<?> clazz) {
		if (Arrays.asList(clazz.getInterfaces()).contains(Annotated.class)) {
			SQLQuery hibernateQuery = this.session.createSQLQuery(query);
			if (hibernateQuery == null) {
				return null;
			}
			return hibernateQuery.addEntity(clazz);
		} else {
			return null;
		}
	}
	
	/**
	 * @param object
	 */
	public synchronized void delete(final Annotated object) {
		this.session.delete(object);
	}
	
	/**
	 * Executes the query.
	 * 
	 * @param query
	 *            the query
	 * @return the criteria
	 * @throws SQLException
	 * @throws HibernateException
	 */
	public void executeQuery(final String query) throws HibernateException, SQLException {
		Condition.notNull(query, "Calling execute query with query=(null) does not make any sense.");
		this.session.doWork(new QueryWork(query));
	}
	
	@NoneNull
	public RCSTransaction fetchRCSTransaction(final String id) throws LoadingException {
		
		Criteria parentCriteria = this.createCriteria(RCSTransaction.class).add(Restrictions.eq("id", id));
		@SuppressWarnings("unchecked") List<RCSTransaction> list = parentCriteria.list();
		if (list.isEmpty()) {
			throw new LoadingException("Could not fetch RCSTrabsaction with id `" + id + "`");
		} else if (list.size() > 1) {
			throw new UnrecoverableError("Found multiple RCSTransactions with same ID. This should be impossible.");
		}
		return list.get(0);
	}
	
	protected Session getSession(){
		return this.session;
	}
	
	
	@NoneNull
	public RCSTransaction getSessionRCSTransaction(final String id){
		
		Object cached = this.session.get(RCSTransaction.class, id);
		if(cached != null){
			return (RCSTransaction) cached;
		}
		return null;
	}
	
	/**
	 * @param object
	 */
	public synchronized void save(final Annotated object) {
		Collection<Annotated> saveFirst = object.saveFirst();
		RCSTransaction rcstransaction = null;
		if (object instanceof RCSTransaction) {
			rcstransaction = (RCSTransaction) object;
		}
		
		if (saveFirst != null) {
			
			if (Logger.logDebug()) {
				Logger.debug(JavaUtils.collectionToString(saveFirst));
			}
			
			// this fixes a major design flaw
			// we avoid cascaded updates on RCSFiles caused
			// by the saveFirst() collections manually
			// if—and only if—the RCSFile was not modified for
			// the transaction under suspect.
			for (Annotated innerObject : saveFirst) {
				if (innerObject instanceof RCSFile) {
					RCSFile file = (RCSFile) innerObject;
					if (file.saved() && !file.getChangedNames().containsKey(rcstransaction.getId())) {
						continue;
					}
				}
				saveOrUpdate(innerObject);
			}
		}
		this.session.save(object);
	}
	
	/**
	 * @param object
	 */
	public synchronized void saveOrUpdate(final Annotated object) {
		Collection<Annotated> saveFirst = object.saveFirst();
		if (Logger.logDebug()) {
			Logger.debug("Persisting request for " + object);
		}
		RCSTransaction rcstransaction = null;
		if (object instanceof RCSTransaction) {
			rcstransaction = (RCSTransaction) object;
		}
		
		if (saveFirst != null) {
			if (Logger.logDebug()) {
				Logger.debug("Save first triggered...");
			}
			// this fixes a major design flaw
			// we avoid cascaded updates on RCSFiles caused
			// by the saveFirst() collections manually
			// if—and only if—the RCSFile was not modified for
			// the transaction under suspect.
			for (Annotated innerObject : saveFirst) {
				if (innerObject instanceof RCSFile) {
					RCSFile file = (RCSFile) innerObject;
					if (file.saved() && !file.getChangedNames().containsKey(rcstransaction.getId())) {
						continue;
					}
				}
				saveOrUpdate(innerObject);
			}
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Persisting " + object);
		}
		// System.out.println("Persisting: " + object);
		this.session.saveOrUpdate(object);
	}
	
	/**
	 * 
	 */
	private void shutdownSession() {
		this.session.close();
	}
	
	/**
	 * @param object
	 */
	public synchronized void update(final Annotated object) {
		this.session.update(object);
	}
	
}
