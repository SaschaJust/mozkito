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

import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.junit.After;
import org.junit.Before;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.testing.annotation.MozkitoTestAnnotation;
import org.mozkito.testing.annotation.processors.DatabaseSettingsProcessor;

/**
 * The Class TestZweiPunktNull.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DatabaseTest {
	
	/** The util. */
	private PersistenceUtil           util;
	
	/** The processor. */
	private DatabaseSettingsProcessor processor;
	
	/** The annotation. */
	private DatabaseSettings          annotation;
	
	/** The database name. */
	private String                    databaseName;
	
	/**
	 * Instantiates a new test zwei punkt null.
	 */
	public DatabaseTest() {
		// PRECONDITIONS
		
		try {
			
			// check if developer uses @BeforeClass instead of a constructor
			// for (final Method method : getClass().getMethods()) {
			// if ((method.getModifiers() & Modifier.STATIC) != 0) {
			// for (final Annotation annotation : method.getAnnotations()) {
			// if (annotation.annotationType().equals(BeforeClass.class)) {
			// fail("@BeforeClass is not supported in " + getHandle()
			// + ". Please use a default constructor.");
			// }
			// }
			// }
			// }
			
			initializeProcessor();
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Setup.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public final void databaseSetup() throws Exception {
		setupDatabase();
	}
	
	/**
	 * Tear down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public final void databaseTearDown() throws Exception {
		shutdownDatabase();
		
	}
	
	/**
	 * Gets the database name.
	 * 
	 * @return the database name
	 */
	public final String getDatabaseName() {
		// PRECONDITIONS
		
		try {
			return this.databaseName;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.databaseName, "Field '%s' in '%s'.", "databaseName", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
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
		for (final Annotation annotation : getClass().getAnnotations()) {
			final MozkitoTestAnnotation mka = annotation.annotationType().getAnnotation(MozkitoTestAnnotation.class);
			if (mka != null) {
				try {
					if (annotation.annotationType().equals(DatabaseSettings.class)) {
						this.annotation = (DatabaseSettings) annotation;
						final DatabaseSettingsProcessor processor = (DatabaseSettingsProcessor) mka.value()
						                                                                           .newInstance();
						this.processor = processor;
					}
				} catch (final InstantiationException | IllegalAccessException e) {
					fail(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Sets the database name.
	 * 
	 * @param databaseName
	 *            the new database name
	 */
	public final void setDatabaseName(final String databaseName) {
		// PRECONDITIONS
		Condition.notNull(databaseName, "Argument '%s' in '%s'.", "databaseName", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.databaseName = databaseName;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.databaseName, databaseName,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Setup database.
	 */
	private void setupDatabase() {
		if (this.processor != null) {
			this.processor.setup(this, this.annotation);
		}
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
		if (this.processor != null) {
			this.processor.tearDown(this, this.annotation);
		}
	}
}
