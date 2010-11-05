package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import de.unisaarland.cs.st.reposuite.Core;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class HibernateUtil {
	
	private static SessionFactory             sessionFactory;
	private static Map<Thread, HibernateUtil> instances = new HashMap<Thread, HibernateUtil>();
	
	protected static void createSessionFactory(final Properties properties) {
		
		if (sessionFactory == null) {
			AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
			annotationConfiguration.addProperties(properties);
			List<Class<?>> annotatedClasses;
			
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
		} else {
			
			if (Logger.logWarn()) {
				Logger.warn("Session factory already exists. Skipping creating.");
			}
		}
	}
	
	public static void createSessionFactory(final String host, final String database, final String user,
			final String password, final String type, final String driver) throws HibernateException {
		try {
			String url = "jdbc:" + type.toLowerCase() + "://" + host + "/" + database
			+ "?useUnicode=true&characterEncoding=UTF-8";
			
			Properties properties = new Properties();
			properties.put("hibernate.connection.url", url);
			properties.put("hibernate.hbm2ddl.auto", "update");
			
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
	
	public static HibernateUtil getInstance() throws UninitializedDatabaseException {
		if (sessionFactory == null) {
			throw new UninitializedDatabaseException();
		}
		if (instances.containsKey(Thread.currentThread())) {
			return instances.get(Thread.currentThread());
		} else {
			HibernateUtil util = new HibernateUtil();
			instances.put(Thread.currentThread(), util);
			return util;
		}
	}
	
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
	
	public HibernateUtil() {
		assert (sessionFactory != null);
		this.session = sessionFactory.openSession();
	}
	
	public void beginTransaction() {
		this.transaction = this.session.beginTransaction();
	}
	
	public void commitTransaction() {
		if ((this.transaction != null) && this.transaction.isActive()) {
			try {
				this.transaction.commit();
			} catch (HibernateException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				this.transaction.rollback();
			}
		}
	}
	
	public Criteria createCriteria(final Class<?> clazz) {
		if (Arrays.asList(clazz.getInterfaces()).contains(Annotated.class)) {
			return this.session.createCriteria(clazz);
		} else {
			return null;
		}
	}
	
	public void saveOrUpdate(final Annotated object) {
		Collection<Annotated> saveFirst = object.getSaveFirst();
		if (saveFirst != null) {
			
			if (Logger.logDebug()) {
				Logger.debug(JavaUtils.collectionToString(saveFirst));
			}
			for (Annotated innerObject : saveFirst) {
				this.session.saveOrUpdate(innerObject);
			}
		}
		this.session.saveOrUpdate(object);
	}
	
	private void shutdownSession() {
		this.session.close();
	}
	
}
