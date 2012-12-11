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

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.junit.After;
import org.junit.Before;
import org.mozkito.exceptions.TestSettingsError;
import org.mozkito.testing.annotation.EnvironmentProcessor;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.testing.annotation.RepositorySettings;
import org.mozkito.testing.annotation.processors.RepositoryProcessor;
import org.mozkito.versions.Repository;

/**
 * The Class VersionTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class VersionsTest extends DatabaseTest {
	
	/** The repositories. */
	private Map<String, Repository> repositories               = new HashMap<>();
	
	/** The annotation. */
	private Annotation              annotation;
	
	/** The processor. */
	private RepositoryProcessor     processor;
	
	/** The temporary source directories. */
	private final Set<File>         temporarySourceDirectories = new HashSet<>();
	
	/** The working directories. */
	private final Set<File>         workingDirectories         = new HashSet<>();
	
	/**
	 * Instantiates a new version test.
	 */
	public VersionsTest() {
		// PRECONDITIONS
		
		try {
			initializeProcessor();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adds the temporary source directory.
	 * 
	 * @param dir
	 *            the dir
	 * @return true, if successful
	 */
	public boolean addTemporarySourceDirectory(final File dir) {
		return this.temporarySourceDirectories.add(dir);
	}
	
	/**
	 * Adds the working directory.
	 * 
	 * @param dir
	 *            the dir
	 * @return true, if successful
	 */
	public boolean addWorkingDirectory(final File dir) {
		return this.workingDirectories.add(dir);
	}
	
	/**
	 * Returns a map from RepositorySettingsID to Repositories.
	 * 
	 * @return the repositories
	 */
	public final Map<String, Repository> getRepositories() {
		// PRECONDITIONS
		
		try {
			return this.repositories;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.repositories, "Field '%s' in '%s'.", "repositories", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the temporary source directories.
	 * 
	 * @return the temporary source directories
	 */
	public final Set<File> getTemporarySourceDirectories() {
		// PRECONDITIONS
		
		try {
			return this.temporarySourceDirectories;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.temporarySourceDirectories,
			                  "Field '%s' in '%s'.", "temporarySourceDirectories", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the working directories.
	 * 
	 * @return the working directories
	 */
	public final Set<File> getWorkingDirectories() {
		// PRECONDITIONS
		
		try {
			return this.workingDirectories;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.workingDirectories,
			                  "Field '%s' in '%s'.", "workingDirectories", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Initialize processor.
	 */
	private void initializeProcessor() {
		// PRECONDITIONS
		
		try {
			try {
				final RepositorySetting repositorySetting = getClass().getAnnotation(RepositorySetting.class);
				if (repositorySetting == null) {
					final RepositorySettings repositorySettings = getClass().getAnnotation(RepositorySettings.class);
					if (repositorySettings == null) {
						throw new TestSettingsError("");
					} else {
						this.annotation = repositorySettings;
					}
				} else {
					this.annotation = repositorySetting;
				}
				assert (this.annotation != null);
				
				final EnvironmentProcessor processorAnnotation = this.annotation.annotationType()
				                                                                .getAnnotation(EnvironmentProcessor.class);
				final RepositoryProcessor processor = (RepositoryProcessor) processorAnnotation.value().newInstance();
				
				this.processor = processor;
				processor.setup(this, this.annotation);
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
	 * Sets the repositories.
	 * 
	 * @param repositories
	 *            the repositories
	 */
	public final void setRepositories(final Map<String, Repository> repositories) {
		// PRECONDITIONS
		Condition.notNull(repositories, "Argument '%s' in '%s'.", "repositories", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.repositories = repositories;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.repositories, repositories,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Setup repositories.
	 */
	@Before
	public void setupRepositories() {
		// ignore
	}
	
	/**
	 * Shutdown repositories.
	 */
	@After
	public void shutdownRepositories() {
		// ignore for now
		this.processor.tearDown(this, this.annotation);
	}
	
}
