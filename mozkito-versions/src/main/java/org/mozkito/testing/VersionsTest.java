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

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.junit.After;
import org.junit.Before;

import org.mozkito.exceptions.UnregisteredRepositoryTypeException;
import org.mozkito.testing.annotation.MozkitoTestAnnotation;
import org.mozkito.testing.annotation.RepositorySettings;
import org.mozkito.testing.annotation.processors.RepositorySettingsProcessor;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.Repository;
import org.mozkito.versions.RepositoryFactory;
import org.mozkito.versions.RepositoryType;

/**
 * The Class VersionTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class VersionsTest extends DatabaseTest {
	
	/** The path name. */
	private String                      pathName;
	
	/** The repositories. */
	private final List<Repository>      repositories = new LinkedList<Repository>();
	
	/** The repo map. */
	private Map<RepositoryType, URI>    repoMap;
	
	/** The annotation. */
	private RepositorySettings          annotation;
	
	/** The processor. */
	private RepositorySettingsProcessor processor;
	
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
	 * Gets the path name.
	 * 
	 * @return the path name
	 */
	public final String getPathName() {
		// PRECONDITIONS
		
		try {
			return this.pathName;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.pathName, "Field '%s' in '%s'.", "pathName", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the repo map.
	 * 
	 * @return the repo map
	 */
	public final Map<RepositoryType, URI> getRepoMap() {
		// PRECONDITIONS
		
		try {
			return this.repoMap;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.repoMap, "Field '%s' in '%s'.", "repoMap", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the repositories.
	 * 
	 * @return the repositories
	 */
	public final List<Repository> getRepositories() {
		// PRECONDITIONS
		
		try {
			return this.repositories;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.repositories, "Field '%s' in '%s'.", "repositories", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Initialize processor.
	 */
	private void initializeProcessor() {
		for (final Annotation annotation : getClass().getAnnotations()) {
			final MozkitoTestAnnotation mka = annotation.annotationType().getAnnotation(MozkitoTestAnnotation.class);
			if (mka != null) {
				try {
					if (annotation.annotationType().equals(RepositorySettings.class)) {
						this.annotation = (RepositorySettings) annotation;
						final RepositorySettingsProcessor processor = (RepositorySettingsProcessor) mka.value()
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
	 * Initialize repositories.
	 */
	private void initializeRepositories() {
		this.processor.setup(this, this.annotation);
		this.repoMap = new HashMap<RepositoryType, URI>();
		
		for (final RepositoryType type : RepositoryType.values()) {
			
			final String pathName = RepositorySettingsProcessor.getPathName(getClass(), type);
			if (pathName != null) {
				try {
					this.repoMap.put(type, new URI("file://" + pathName + File.separator + "repotest."
					        + type.name().toLowerCase()));
				} catch (final URISyntaxException e) {
					fail(e.getMessage());
				}
			} else {
				fail();
			}
			
			Repository repository = null;
			try {
				repository = RepositoryFactory.getRepositoryHandler(type).newInstance();
			} catch (final InstantiationException e1) {
				e1.printStackTrace();
				fail();
			} catch (final IllegalAccessException e1) {
				e1.printStackTrace();
				fail();
			} catch (final UnregisteredRepositoryTypeException e1) {
				e1.printStackTrace();
				fail();
			}
			assert (repository != null);
			this.repositories.add(repository);
			
			final File urlFile = new File(this.repoMap.get(type));
			
			try {
				repository.setup(urlFile.toURI(), new BranchFactory(null), null, "master");
			} catch (final Exception e) {
				System.err.println(e.getMessage());
				fail(e.getMessage());
			}
		}
		
	}
	
	/**
	 * Setup repositories.
	 */
	@Before
	public void setupRepositories() {
		initializeRepositories();
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
