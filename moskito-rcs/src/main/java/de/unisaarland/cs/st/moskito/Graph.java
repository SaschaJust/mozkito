/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositoryArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Graph extends AndamaChain {
	
	private final AndamaPool          threadPool;
	private final RepositoryArguments repoSettings;
	private final DatabaseArguments   databaseSettings;
	private final LoggerArguments     logSettings;
	private boolean                   shutdown;
	private PersistenceUtil           persistenceUtil;
	
	/**
	 * @param settings
	 */
	public Graph() {
		super(new RepositorySettings());
		this.threadPool = new AndamaPool(RCS.class.getSimpleName(), this);
		RepositorySettings settings = (RepositorySettings) getSettings();
		this.repoSettings = settings.setRepositoryArg(true);
		this.databaseSettings = settings.setDatabaseArgs(false, "rcs");
		this.logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		                    false);
		new LongArgument(settings, "cache.size",
		                 "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		new BooleanArgument(settings, "repository.analyze", "Requires consistency checks on the repository", "false",
		                    false);
		
		settings.parseArguments();
	}
	
	@Override
	public void run() {
		if (!this.shutdown) {
			setup();
			if (!this.shutdown) {
				this.threadPool.execute();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.logSettings.getValue();
		
		// this has be done done BEFORE other instances like repository since
		// they could rely on data loading
		if (this.databaseSettings.getValue() != null) {
			try {
				this.persistenceUtil = PersistenceManager.getUtil();
			} catch (Exception e) {
				e.printStackTrace();
				if (Logger.logError()) {
					Logger.error("Database connection could not be established.", e);
				}
				shutdown();
			}
		} else {
			if (Logger.logError()) {
				Logger.error("Missing database settings.");
			}
			
			shutdown();
		}
		
		Repository repository = this.repoSettings.getValue();
		
		new GraphReader(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), this.persistenceUtil);
		new GraphBuilder(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository,
		                 this.persistenceUtil);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		
		if (Logger.logInfo()) {
			Logger.info("Toolchain shutdown.");
		}
		this.threadPool.shutdown();
		this.shutdown = true;
	}
	
}
