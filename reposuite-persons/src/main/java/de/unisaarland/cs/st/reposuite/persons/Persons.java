/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import de.unisaarland.cs.st.reposuite.RCS;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.persons.processing.MergingProcessor;
import de.unisaarland.cs.st.reposuite.persons.settings.PersonsArguments;
import de.unisaarland.cs.st.reposuite.persons.settings.PersonsSettings;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadPool;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Persons extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool;
	private final DatabaseArguments   databaseArguments;
	private final LoggerArguments     logSettings;
	private final PersonsArguments    personsArguments;
	
	/**
	 * 
	 */
	public Persons() {
		super(new PersonsSettings());
		this.threadPool = new RepoSuiteThreadPool(RCS.class.getSimpleName(), this);
		
		RepoSuiteSettings settings = getSettings();
		this.databaseArguments = ((RepositorySettings) settings).setDatabaseArgs(true, "persistence");
		this.logSettings = settings.setLoggerArg(true);
		this.personsArguments = ((PersonsSettings) settings).setPersonsArgs(true);
		
		settings.parseArguments();
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
	 * @see de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.logSettings.getValue();
		PersistenceUtil persistenceUtil = null;
		
		if (this.databaseArguments.getValue() != null) {
			try {
				persistenceUtil = PersistenceManager.getUtil();
			} catch (UninitializedDatabaseException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				
				shutdown();
			}
			
		} else {
			if (Logger.logError()) {
				Logger.error("Database arguments are not set (required when merging persons).");
			}
			
			shutdown();
		}
		
		MergingProcessor processor = this.personsArguments.getValue();
		processor.providePersistenceUtil(persistenceUtil);
		
		new PersonsReader(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil);
		new PersonsMerger(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil, processor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
	
}
