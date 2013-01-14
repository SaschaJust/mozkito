/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.settings.RepositoryOptions;
import org.mozkito.versions.Repository;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class RepositoryToolchain.
 * 
 * {@link RepositoryToolchain} is the standard {@link Chain} to mine a repository.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class RepositoryToolchain extends Chain<Settings> {
	
	/** The thread pool. */
	private final Pool                                          threadPool;
	
	/** The repository arguments. */
	private final ArgumentSet<Repository, RepositoryOptions>    repositoryArguments;
	
	/** The database arguments. */
	private final ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	
	/** The persistence util. */
	private PersistenceUtil                                     persistenceUtil;
	
	/** The repository. */
	private Repository                                          repository;
	
	private VersionArchive                                      versionArchive;
	
	/**
	 * Instantiates a new repository toolchain.
	 * 
	 * @param settings
	 *            the settings
	 */
	public RepositoryToolchain(final Settings settings) {
		super(settings);
		
		try {
			
			this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "rcs");
			this.databaseArguments = ArgumentSetFactory.create(databaseOptions);
			final RepositoryOptions repositoryOptions = new RepositoryOptions(settings.getRoot(), Requirement.required,
			                                                                  databaseOptions);
			this.repositoryArguments = ArgumentSetFactory.create(repositoryOptions);
			
			if (getSettings().helpRequested()) {
				if (Logger.logAlways()) {
					Logger.always(getSettings().getHelpString());
				}
				throw new Shutdown();
			}
			
		} catch (final ArgumentRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final SettingsParseError e) {
			throw new Shutdown();
		}
	}
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistence util
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	/**
	 * Gets the repository.
	 * 
	 * @return the repository
	 */
	public Repository getRepository() {
		// PRECONDITIONS
		
		try {
			return this.repository;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the version archive.
	 * 
	 * @return the version archive
	 */
	public VersionArchive getVersionArchive() {
		return this.versionArchive;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.persistenceUtil = this.databaseArguments.getValue();
		// this has be done done BEFORE other instances like repository since
		// they could rely on data loading
		if (this.persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error("Database connection could not be established.");
			}
			shutdown();
		}
		
		this.repository = this.repositoryArguments.getValue();
		
		final InputStream metadataStream = RepositoryToolchain.class.getClassLoader()
		                                                            .getResourceAsStream("metadata.properties");
		if (metadataStream == null) {
			throw new UnrecoverableError(
			                             "Could not find required metadata.properties. This file must be on the classpath.");
		}
		final Properties metadata = new Properties();
		try {
			metadata.load(metadataStream);
		} catch (final IOException e1) {
			throw new UnrecoverableError(e1);
		}
		
		if (!metadata.containsKey("head")) {
			throw new UnrecoverableError("The metadata.properties file does not contain a value for 'hash'!");
		}
		if (!metadata.containsKey("version")) {
			throw new UnrecoverableError("The metadata.properties file does not contain a value for 'version'!");
		}
		
		try {
			this.versionArchive = new VersionArchive(this.repository.getRevDependencyGraph());
			
			this.versionArchive.setMiningDate(new DateTime());
			this.versionArchive.setMozkitoHash(metadata.getProperty("hash"));
			this.versionArchive.setMozkitoVersion(metadata.getProperty("version"));
			this.versionArchive.setUsedSettings(getSettings().getRoot().getHelpString());
			
			this.persistenceUtil.beginTransaction();
			this.persistenceUtil.save(this.versionArchive);
			this.persistenceUtil.commitTransaction();
			
			new RepositoryReader(this.threadPool.getThreadGroup(), getSettings(), this.repository);
			new RepositoryParser(this.threadPool.getThreadGroup(), getSettings(), this.repository, this.versionArchive);
			
			if (this.persistenceUtil != null) {
				new RepositoryPersister(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
			} else {
				new RepositoryVoidSink(this.threadPool.getThreadGroup(), getSettings());
			}
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
		}
	}
}
