package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;

import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class DatabaseSettingsProcessor implements MoskitoSettingsProcessor {
	
	@Override
	public void evaluate(final Annotation annotation) throws Exception {
		final DatabaseSettings settings = (DatabaseSettings) annotation;
		
		final PersistenceUtil util = PersistenceManager.createUtil(settings.hostname(), settings.database(),
		                                                           settings.username(), settings.password(),
		                                                           settings.type(), settings.driver(), settings.unit(),
		                                                           settings.dropContents(), settings.util());
		MoskitoTest.setPersistenceUtil(util);
	}
	
}
