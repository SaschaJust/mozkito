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
package de.unisaarland.cs.st.moskito.persons;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.RepositoryToolchain;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persons.processing.MergingProcessor;
import de.unisaarland.cs.st.moskito.persons.settings.PersonsArguments;
import de.unisaarland.cs.st.moskito.persons.settings.PersonsSettings;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Persons extends Chain {
	
	private final Pool       threadPool;
	private DatabaseArguments      databaseArguments;
	private final LoggerArguments  logSettings;
	private final PersonsArguments personsArguments;
	private PersistenceUtil        persistenceUtil;
	
	/**
	 * 
	 */
	public Persons() {
		super(new PersonsSettings());
		this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
		
		final Settings settings = getSettings();
		this.databaseArguments = ((RepositorySettings) settings).setDatabaseArgs(true, "persistence");
		this.logSettings = settings.setLoggerArg(true);
		this.personsArguments = ((PersonsSettings) settings).setPersonsArgs(true);
		
		settings.parseArguments();
	}
	
	Persons(final PersistenceUtil util) {
		super(new PersonsSettings());
		this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
		final Settings settings = getSettings();
		this.personsArguments = ((PersonsSettings) settings).setPersonsArgs(false);
		this.logSettings = settings.setLoggerArg(true);
		this.persistenceUtil = util;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setup();
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.logSettings.getValue();
		if ((this.persistenceUtil == null) && (this.databaseArguments != null)) {
			this.persistenceUtil = this.databaseArguments.getValue();
		}
		if (this.persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error("Database arguments are not set (required when merging persons).");
			}
			shutdown();
		}
		
		final MergingProcessor processor = this.personsArguments.getValue();
		processor.providePersistenceUtil(this.persistenceUtil);
		
		new PersonsReader(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		new PersonsMerger(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil, processor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
	
}
