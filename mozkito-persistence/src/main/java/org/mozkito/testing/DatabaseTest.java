/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.testing;

import java.lang.annotation.Annotation;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;

import org.junit.After;
import org.junit.Before;

import org.mozkito.exceptions.TestSetupException;
import org.mozkito.persistence.DatabaseEnvironment;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.testing.annotation.EnvironmentProcessor;
import org.mozkito.testing.annotation.processors.DatabaseSettingsProcessor;

/**
 * The Class TestZweiPunktNull.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DatabaseTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The util. */
	private PersistenceUtil           util;
	
	/** The processor. */
	private DatabaseSettingsProcessor processor;
	
	/** The annotation. */
	private DatabaseSettings          annotation;
	
	/** The options. */
	private DatabaseEnvironment       options;
	
	/**
	 * Instantiates a new database test.
	 */
	public DatabaseTest() {
		// PRECONDITIONS
		
		try {
			final Annotation annotation = getClass().getAnnotation(DatabaseSettings.class);
			
			// if (annotation == null) {
			// throw new TestSettingsError(String.format("Test '%s' is a '%s', but is lagging '%s' annotation.",
			// getHandle(), DatabaseTest.class.getSimpleName(),
			// DatabaseSettings.class.getSimpleName()));
			// }
			
			if (annotation != null) {
				initializeProcessor();
			}
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Setup.
	 * 
	 * @throws TestSetupException
	 *             the exception
	 */
	@Before
	public final void databaseSetup() throws TestSetupException {
		if (this.annotation != null) {
			if (Logger.logInfo()) {
				Logger.info("Connecting to database: " + this.annotation);
			}
			setupDatabase();
			if (getPersistenceUtil() == null) {
				throw new TestSetupException(String.format("Establishing "));
			}
		}
	}
	
	/**
	 * Tear down.
	 * 
	 */
	@After
	public final void databaseTearDown() {
		if (this.annotation != null) {
			shutdownDatabase();
		}
	}
	
	/**
	 * Gets the database name.
	 * 
	 * @return the database name
	 */
	public final String getDatabaseName() {
		// PRECONDITIONS
		
		try {
			return this.options != null
			                           ? this.options.getDatabaseName()
			                           : null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(DatabaseTest.class);
	}
	
	/**
	 * Gets the options.
	 * 
	 * @return the options
	 */
	public final DatabaseEnvironment getOptions() {
		// PRECONDITIONS
		
		try {
			return this.options;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the util.
	 * 
	 * @return the util
	 */
	public final PersistenceUtil getPersistenceUtil() {
		return this.util;
	}
	
	/**
	 * Initialize processors.
	 */
	private void initializeProcessor() {
		// PRECONDITIONS
		
		try {
			try {
				final DatabaseSettings databaseSettings = getClass().getAnnotation(DatabaseSettings.class);
				final EnvironmentProcessor processorAnnotation = databaseSettings.annotationType()
				                                                                 .getAnnotation(EnvironmentProcessor.class);
				final DatabaseSettingsProcessor processor = (DatabaseSettingsProcessor) processorAnnotation.value()
				                                                                                           .newInstance();
				
				this.annotation = databaseSettings;
				this.processor = processor;
			} catch (final InstantiationException | IllegalAccessException e) {
				final UnrecoverableError error = new UnrecoverableError(e);
				if (Logger.logError()) {
					Logger.error(error.analyzeFailureCause());
				}
				throw error;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the options.
	 * 
	 * @param options
	 *            the new options
	 */
	public void setOptions(final DatabaseEnvironment options) {
		this.options = options;
	}
	
	/**
	 * Setup database.
	 */
	private void setupDatabase() {
		Condition.notNull(this.processor, "Field '%s' in '%s'.", "processor", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		this.processor.setup(this, this.annotation);
	}
	
	/**
	 * Sets the util.
	 * 
	 * @param util
	 *            the new util
	 */
	public final void setUtil(final PersistenceUtil util) {
		this.util = util;
	}
	
	/**
	 * Shutdown database.
	 */
	private void shutdownDatabase() {
		Condition.notNull(this.processor, "Field '%s' in '%s'.", "processor", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		this.processor.tearDown(this, this.annotation);
	}
}
