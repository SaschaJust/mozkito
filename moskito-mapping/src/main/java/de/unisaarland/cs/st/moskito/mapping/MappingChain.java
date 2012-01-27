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
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;

public class MappingChain extends AndamaChain {
	
	private final DatabaseArguments databaseArguments;
	private final LoggerArguments   logSettings;
	private final MappingArguments  mappingArguments;
	private final AndamaPool        threadPool;
	
	/**
	 * 
	 */
	public MappingChain() {
		super(new MappingSettings(), "mapping");
		this.threadPool = new AndamaPool(Mapping.class.getSimpleName(), this);
		final MappingSettings settings = getSettings();
		this.databaseArguments = settings.setDatabaseArgs(true, "mapping");
		this.logSettings = settings.setLoggerArg(true);
		this.mappingArguments = settings.setMappingArgs(this, true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		                    false);
		new LongArgument(settings, "cache.size",
		                 "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#getSettings()
	 */
	@Override
	public MappingSettings getSettings() {
		return (MappingSettings) super.getSettings();
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
		
		final MappingFinder finder = this.mappingArguments.getValue();
		
		if (finder == null) {
			if (Logger.logError()) {
				Logger.error("MappingFinder initialization failed. Aborting...");
				shutdown();
			}
		}
		
		final PersistenceUtil persistenceUtil = this.databaseArguments.getValue();
		
		if (persistenceUtil != null) {
			
			finder.loadData(persistenceUtil);
			new ReportReader(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil);
			new TransactionFinder(this.threadPool.getThreadGroup(), getSettings(), finder, persistenceUtil);
			new TransactionReader(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil);
			new ReportFinder(this.threadPool.getThreadGroup(), getSettings(), finder, persistenceUtil);
			new CandidatesConverter(this.threadPool.getThreadGroup(), getSettings());
			// new ScoringMappingFilter(this.threadPool.getThreadGroup(),
			// getSettings(), finder);
			new CandidatesDemux(this.threadPool.getThreadGroup(), getSettings());
			// new ScoringFilterMux(this.threadPool.getThreadGroup(),
			// getSettings());
			// new ScoringMappingMux(this.threadPool.getThreadGroup(),
			// getSettings());
			for (final MappingEngine engine : this.mappingArguments.getEngines()) {
				new MappingEngineProcessor(this.threadPool.getThreadGroup(), getSettings(), finder, engine);
			}
			
			for (final MappingStrategy strategy : this.mappingArguments.getStrategies()) {
				new MappingStrategyProcessor(this.threadPool.getThreadGroup(), getSettings(), finder, strategy);
			}
			// new ScoringPersister(this.threadPool.getThreadGroup(),
			// getSettings(), persistenceUtil);
			// ScoringSplitter splitter = new
			// ScoringSplitter(this.threadPool.getThreadGroup(),
			// getSettings(), finder,
			// persistenceUtil);
			// ScoringFilterPersister persister = new
			// ScoringFilterPersister(this.threadPool.getThreadGroup(),
			// getSettings(), persistenceUtil);
			new MappingPersister(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil);
			
		} else {
			if (Logger.logError()) {
				Logger.error("Database arguments not valid. Aborting...");
			}
			shutdown();
		}
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
