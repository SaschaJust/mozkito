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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.junit.BeforeClass;

import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.annotation.MozkitoTestAnnotation;
import org.mozkito.testing.annotation.processors.MozkitoSettingsProcessor;

/**
 * The Class TestZweiPunktNull.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DatabaseTest {
	
	/** The util. */
	private PersistenceUtil util;
	private ConnectOptions  connectOptions;
	
	/**
	 * Instantiates a new test zwei punkt null.
	 */
	public DatabaseTest() {
		// PRECONDITIONS
		
		try {
			
			// check if developer uses @BeforeClass instead of a constructor
			for (final Method method : getClass().getMethods()) {
				if ((method.getModifiers() & Modifier.STATIC) != 0) {
					for (final Annotation annotation : method.getAnnotations()) {
						if (annotation.annotationType().equals(BeforeClass.class)) {
							fail("@BeforeClass is not supported in " + getHandle()
							        + ". Please use a default constructor.");
						}
					}
				}
			}
			
			initializeProcessors();
			
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
	 * Gets the util.
	 * 
	 * @return the util
	 */
	public final PersistenceUtil getPersistenceUtil() {
		// PRECONDITIONS
		
		try {
			switch (this.connectOptions) {
				case CREATE:
					// reading mode
					return this.util;
				case DB_DROP_CREATE:
					// writing mode
					initializeProcessors();
					return this.util;
				default:
					fail("Unsupported database connection option: " + this.connectOptions.name());
					return null;
			}
			
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.util, "Field '%s' in '%s'.", "util", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void initializeProcessors() {
		for (final Annotation annotation : getClass().getAnnotations()) {
			final MozkitoTestAnnotation mka = annotation.annotationType().getAnnotation(MozkitoTestAnnotation.class);
			if (mka != null) {
				try {
					final MozkitoSettingsProcessor processor = mka.value().newInstance();
					processor.setup(this, annotation);
				} catch (InstantiationException | IllegalAccessException e) {
					fail(e.getMessage());
				}
			}
		}
	}
	
	public void setConnectionOption(final ConnectOptions option) {
		this.connectOptions = option;
	}
	
	/**
	 * Sets the util.
	 * 
	 * @param util
	 *            the new util
	 */
	public final void setUtil(final PersistenceUtil util) {
		// PRECONDITIONS
		Condition.notNull(util, "Argument '%s' in '%s'.", "util", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.util = util;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.util, util,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
}
