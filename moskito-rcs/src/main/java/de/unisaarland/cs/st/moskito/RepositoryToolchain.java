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
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;
import de.unisaarland.cs.st.moskito.settings.RepositoryOptions;

/**
 * {@link RepositoryToolchain} is the standard {@link RepoSuiteToolchain} to mine a repository.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryToolchain extends Chain<Settings> {
	
	private final Pool                                          threadPool;
	private final ArgumentSet<Repository, RepositoryOptions>    repositoryArguments;
	private final ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	private PersistenceUtil                                     persistenceUtil;
	private Repository                                          repository;
	
	public RepositoryToolchain(final Settings settings) {
		super(settings);
		
		try {
			
			this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "rcs");
			this.databaseArguments = ArgumentSetFactory.create(databaseOptions);
			final RepositoryOptions repositoryOptions = new RepositoryOptions(settings.getRoot(), Requirement.required,
			                                                                  databaseOptions);
			this.repositoryArguments = ArgumentSetFactory.create(repositoryOptions);
			
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getMessage(), e);
		} catch (final ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getMessage(), e);
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getMessage(), e);
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
		
		new RepositoryReader(this.threadPool.getThreadGroup(), getSettings(), this.repository);
		new RepositoryParser(this.threadPool.getThreadGroup(), getSettings(), this.repository);
		
		if (this.persistenceUtil != null) {
			new RepositoryPersister(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		} else {
			new RepositoryVoidSink(this.threadPool.getThreadGroup(), getSettings());
		}
	}
	
}
