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

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.andama.settings.LongArgument;
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
public class RepositoryToolchain extends AndamaChain {
	
	private final AndamaPool          threadPool;
	private final RepositoryArguments repoSettings;
	private final LoggerArguments     logSettings;
	private final DatabaseArguments   databaseSettings;
	private boolean                   shutdown;
	private PersistenceUtil           persistenceUtil;
	private Repository                repository;
	
	public RepositoryToolchain() {
		super(new RepositorySettings());
		this.threadPool = new AndamaPool(RepositoryToolchain.class.getSimpleName(), this);
		final RepositorySettings settings = (RepositorySettings) getSettings();
		this.databaseSettings = settings.setDatabaseArgs(false, "rcs");
		this.repoSettings = settings.setRepositoryArg(true);
		this.logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		                    false);
		new LongArgument(settings, "cache.size",
		                 "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		new BooleanArgument(settings, "repository.analyze", "Requires consistency checks on the repository", "false",
		                    false);
		
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
		// i din't think we can resume repository mining at all.
		// if (this.persistenceUtil != null) {
		// String start = repository.getStartRevision().equalsIgnoreCase("HEAD")
		// ? repository.getHEAD()
		// : repository.getStartRevision();
		// String end = repository.getEndRevision().equalsIgnoreCase("HEAD")
		// ? repository.getHEAD()
		// : repository.getEndRevision();
		//
		// if (Logger.logInfo()) {
		// Logger.info("Checking for persistent transactions (" + start + ".." +
		// end + ").");
		// }
		//
		// RCSTransaction startTransaction =
		// persistenceUtil.fetchRCSTransaction(start);
		// if (startTransaction != null) {
		//
		// if (Logger.logDebug()) {
		// Logger.debug("Found start transaction in persistence storage.");
		// }
		//
		// criteria = this.persistenceUtil.createCriteria(RCSTransaction.class);
		// criteria.add(Restrictions.eq("id", end));
		// @SuppressWarnings ("unchecked")
		// List<RCSTransaction> endTransactions = criteria.list();
		//
		// if ((endTransactions != null) && (endTransactions.size() > 0) &&
		// (endTransactions.get(0) != null)) {
		// if (Logger.logDebug()) {
		// Logger.debug("Found end transaction in persistence storage.");
		// }
		// if (Logger.logWarn()) {
		// Logger.warn("Nothing to do. Transactions from " + start + " to " +
		// end
		// + " are already persisten.");
		// }
		// shutdown();
		// } else {
		// criteria = this.persistenceUtil.createCriteria(RCSTransaction.class);
		// criteria.addOrder(Order.desc("id"));
		// @SuppressWarnings ("unchecked")
		// List<RCSTransaction> maxTransactions = criteria.list();
		// if ((maxTransactions != null) && (maxTransactions.size() > 0)) {
		// RCSTransaction maxPersistentTransaction = maxTransactions.get(0);
		// repository.setStartRevision(maxPersistentTransaction.getId());
		//
		// if (Logger.logWarn()) {
		// Logger.warn("Transactions known from " + startTransaction.getId() +
		// " to "
		// + maxPersistentTransaction.getId() + ". Skipping and fetching "
		// + maxPersistentTransaction.getId() + " to " +
		// repository.getEndRevision() + ".");
		// }
		//
		// if (Logger.logError()) {
		// Logger.error("UNSUPPORTED RESUME FOUND. PLEASE FIX THE CODE.");
		// }
		//
		// throw new UnrecoverableError();
		// //
		// repository.setStartTransaction(maxPersistentTransaction.getParents());
		// // persistenceUtil.delete(maxPersistentTransaction);
		// } else {
		//
		// if (Logger.logError()) {
		// Logger.error("Could not find max transaction although persitent transactions were found. Aborting.");
		// }
		//
		// shutdown();
		// }
		// }
		//
		//
		// }
		// }
		
		new RepositoryReader(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), this.repository);
		new RepositoryParser(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), this.repository,
		                     this.persistenceUtil);
		
		if (this.persistenceUtil != null) {
			new RepositoryPersister(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(),
			                        this.persistenceUtil);
		} else {
			new RepositoryVoidSink(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.RepoSuiteToolchain#shutdown()
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
