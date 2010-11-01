package de.unisaarland.cs.st.reposuite.persistence;

import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import de.unisaarland.cs.st.reposuite.Core;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class HibernateUtil {
	
	private static Session        session;
	private static SessionFactory sessionFactory;
	
	public static SessionFactory createSessionFactory() throws HibernateException {
		if (sessionFactory == null) {
			AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
			Properties properties = annotationConfiguration.getProperties();
			return createSessionFactory(properties);
		}
		return sessionFactory;
	}
	
	private static SessionFactory createSessionFactory(Properties properties) {
		if (sessionFactory == null) {
			AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
			annotationConfiguration.setProperties(properties);
			List<Class<?>> annotatedClasses;
			
			try {
				annotatedClasses = ClassFinder.getClassesExtendingClass(Core.class.getPackage(), Annotated.class);
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new RuntimeException();
			}
			
			for (Class<?> clazz : annotatedClasses) {
				annotationConfiguration.addAnnotatedClass(clazz);
			}
			
			sessionFactory = annotationConfiguration.configure().buildSessionFactory();
		}
		return sessionFactory;
	}
	
	public static SessionFactory createSessionFactory(String host, String database, String user, String password,
	        String type, String driver) throws HibernateException {
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
			
			return createSessionFactory(properties);
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static void shutdown() {
		session.close();
		sessionFactory.close();
		session = null;
		sessionFactory = null;
	}
	
}
