package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class DatabaseSettingsProcessor implements MoskitoSettingsProcessor {
	
	@Override
	public void evaluate(final Annotation annotation) throws Exception {
		final DatabaseSettings settings = (DatabaseSettings) annotation;
		
		final Method method = settings.util().getMethod("createSessionFactory", String.class, String.class,
		                                                String.class, String.class, String.class, String.class,
		                                                String.class);
		method.invoke(null, settings.hostname(), settings.database(), settings.username(), settings.password(),
		              settings.type(), settings.driver(), settings.unit());
		PersistenceManager.registerMiddleware((Class<PersistenceUtil>) settings.util());
	}
	
}
