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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.andama.settings.arguments.BooleanArgument;
import net.ownhero.dev.andama.settings.arguments.LoggerArguments;
import net.ownhero.dev.andama.settings.arguments.LongArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositoryArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Graph extends Chain<RepositorySettings> {
	
	private final Pool                threadPool;
	private final RepositoryArguments repoSettings;
	private final DatabaseArguments   databaseSettings;
	private final LoggerArguments     logSettings;
	private PersistenceUtil           persistenceUtil;
	
	/**
	 * @param settings
	 * @throws ArgumentRegistrationException
	 * @throws SettingsParseError
	 */
	public Graph() throws ArgumentRegistrationException, SettingsParseError {
		super(new RepositorySettings());
		this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
		final RepositorySettings settings = getSettings();
		
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
		
		settings.parse();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.logSettings.getValue();
		this.persistenceUtil = this.databaseSettings.getValue();
		if (this.persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error("Database connection could not be established.");
			}
			shutdown();
		}
		
		this.repoSettings.setPersistenceUtil(this.persistenceUtil);
		final Repository repository = this.repoSettings.getValue();
		
		new GraphReader(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		new GraphBuilder(this.threadPool.getThreadGroup(), getSettings(), repository, this.persistenceUtil);
	}
}
