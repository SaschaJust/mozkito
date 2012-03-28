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
package de.unisaarland.cs.st.moskito.mapping;

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
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingOptions;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;

/**
 * The Class MappingChain.
 */
public class MappingChain extends Chain<Settings> {
	
	/** The database arguments. */
	private final DatabaseOptions                         databaseOptions;
	
	/** The thread pool. */
	private final Pool                                    threadPool;
	
	private ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	
	private MappingOptions                                mappingOptions;
	
	private ArgumentSet<MappingFinder, MappingOptions>    mappingArguments;
	
	/**
	 * Instantiates a new mapping chain.
	 * 
	 */
	public MappingChain(final Settings settings) {
		super(settings, "mapping"); //$NON-NLS-1$
		this.threadPool = new Pool(Mapping.class.getSimpleName(), this);
		
		try {
			this.databaseOptions = new DatabaseOptions(getSettings().getRoot(), Requirement.required, "mapping");//$NON-NLS-1$
			this.databaseArguments = ArgumentSetFactory.create(this.databaseOptions);
			
			this.mappingOptions = new MappingOptions(getSettings().getRoot(), Requirement.required);
			this.mappingArguments = ArgumentSetFactory.create(this.mappingOptions);
		} catch (final ArgumentRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final SettingsParseError e) {
			throw new Shutdown(e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
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
			for (final MappingEngine engine : finder.getEngines().values()) {
				new MappingEngineProcessor(this.threadPool.getThreadGroup(), getSettings(), finder, engine);
			}
			
			for (final MappingStrategy strategy : finder.getStrategies().values()) {
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
}
