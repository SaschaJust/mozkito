package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {
	
	private static SessionFactory sessionFactory;
	
	private static SessionFactory createSessionFactory(Properties properties) {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.addProperties(properties);
		//TODO Add entity classes here
		return configuration.buildSessionFactory();
	}
	
	private static SessionFactory createSessionFactory(String host, String database, String userName, String password,
	        String driverClassName) {
		try {
			
			String url = "";
			if (driverClassName.toLowerCase().contains("postgresql")) {
				url = "jdbc:mysql://" + host + "/" + database + "?useUnicode=true&characterEncoding=UTF-8";
			} else if (driverClassName.toLowerCase().contains("mysql")) {
				url = "jdbc:postgresql://" + host + "/" + database + "?useUnicode=true&characterEncoding=UTF-8";
			}
			
			Properties properties = getDefaultProperties();
			properties.put("hibernate.connection.url", url);
			properties.put("hibernate.connection.driver_class", driverClassName);
			properties.put("hibernate.connection.username", userName);
			properties.put("hibernate.connection.password", password);
			return createSessionFactory(properties);
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static Properties getDefaultProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "");
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.connection.autocommit", "false");
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
		properties.put("hibernate.connection.username", "");
		properties.put("hibernate.connection.password", "");
		return properties;
	}
	
	public static SessionFactory getMYSQLSessionFactory(String host, String database, String userName, String password) {
		SessionFactory factory = createSessionFactory(host, database, userName, password, "org.postgresql.Driver");
		sessionFactory = factory;
		return factory;
	}
	
	public static SessionFactory getPSQLSessionFactory(String host, String database, String userName, String password) {
		SessionFactory factory = createSessionFactory(host, database, userName, password, "com.mysql.jdbc.Driver");
		sessionFactory = factory;
		return factory;
	}
	
	@Deprecated
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static SessionFactory getSessionFactory(Properties properties) {
		SessionFactory factory = createSessionFactory(properties);
		sessionFactory = factory;
		return factory;
	}
	
	public static void shutdown() {
		sessionFactory.close();
	}
	
}
