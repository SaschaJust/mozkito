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
package de.unisaarland.cs.st.moskito;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.arguments.BooleanArgument;
import net.ownhero.dev.hiari.settings.arguments.LoggerArguments;
import net.ownhero.dev.hiari.settings.arguments.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Optional;
import net.ownhero.dev.hiari.settings.requirements.Required;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositoryArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * {@link RepositoryToolchain} is the standard {@link RepoSuiteToolchain} to mine a repository.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryToolchain extends Chain<RepositorySettings> {
	
	private final Pool                threadPool;
	private final RepositoryArguments repoSettings;
	private final LoggerArguments     logSettings;
	private final DatabaseArguments   databaseSettings;
	private PersistenceUtil           persistenceUtil;
	private Repository                repository;
	
	public RepositoryToolchain() {
		super(new RepositorySettings());
		this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
		final RepositorySettings settings = getSettings();
		
		try {
			this.repoSettings = settings.setRepositoryArg(new Required());
			this.databaseSettings = settings.setDatabaseArgs(new Optional(), "rcs");
			this.logSettings = settings.setLoggerArg(new Required());
			new BooleanArgument(settings.getRootArgumentSet(), "headless",
			                    "Can be enabled when running without graphical interface", "false", new Optional());
			new LongArgument(settings.getRootArgumentSet(), "cache.size",
			                 "determines the cache size (number of logs) that are prefetched during reading", "3000",
			                 new Required());
			new BooleanArgument(settings.getRootArgumentSet(), "repository.analyze",
			                    "Requires consistency checks on the repository", "false", new Optional());
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getMessage(), e);
		}
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			throw new UnrecoverableError(e);
		}
	}
	
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	public Repository getRepository() {
		// PRECONDITIONS
		
		try {
			return this.repository;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.logSettings.getValue();
		
		this.persistenceUtil = this.databaseSettings.getValue();
		// this has be done done BEFORE other instances like repository since
		// they could rely on data loading
		if (this.persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error("Database connection could not be established.");
			}
			shutdown();
		}
		
		this.repoSettings.setPersistenceUtil(this.persistenceUtil);
		this.repository = this.repoSettings.getValue();
		new RepositoryReader(this.threadPool.getThreadGroup(), getSettings(), this.repository);
		new RepositoryParser(this.threadPool.getThreadGroup(), getSettings(), this.repository);
		
		if (this.persistenceUtil != null) {
			new RepositoryPersister(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		} else {
			new RepositoryVoidSink(this.threadPool.getThreadGroup(), getSettings());
		}
	}
	
}
