package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;

import de.unisaarland.cs.st.moskito.exceptions.TestSettingsError;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class DatabaseSettingsProcessor implements MoskitoSettingsProcessor {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#setup(java.lang.annotation.Annotation)
	 */
	@Override
	public void setup(final Annotation annotation) throws TestSettingsError {
		final DatabaseSettings settings = (DatabaseSettings) annotation;
		ManagementFactory.getRuntimeMXBean().getName();
		final PersistenceUtil util = PersistenceManager.createUtil(settings.hostname(), settings.database(),
		                                                           settings.username(), settings.password(),
		                                                           settings.type(), settings.driver(), settings.unit(),
		                                                           settings.options(), settings.util());
		MoskitoTest.setPersistenceUtil(util);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#tearDown(java.lang.annotation.Annotation)
	 */
	@Override
	public void tearDown(final Annotation annotation) throws TestSettingsError {
		MoskitoTest.getPersistenceUtil().shutdown();
	}
	
}
