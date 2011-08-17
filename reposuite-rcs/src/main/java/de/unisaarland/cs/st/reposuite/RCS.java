/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadPool;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;

/**
 * {@link RCS} is the standard {@link RepoSuiteToolchain} to mine a repository.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RCS extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool;
	private RepositoryArguments       repoSettings;
	private LoggerArguments           logSettings;
	private DatabaseArguments         databaseSettings;
	private boolean                   shutdown;
	private PersistenceUtil           persistenceUtil;
	private Repository                repository;
	
	public RCS() {
		super(new RepositorySettings());
		threadPool = new RepoSuiteThreadPool(RCS.class.getSimpleName(), this);
		RepositorySettings settings = (RepositorySettings) getSettings();
		repoSettings = settings.setRepositoryArg(true);
		databaseSettings = settings.setDatabaseArgs(false, "rcs");
		logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
				false);
		new LongArgument(settings, "cache.size",
				"determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		new BooleanArgument(settings, "repository.analyze", "Requires consistency checks on the repository", "false",
				false);
		
		settings.parseArguments();
	}
	
	public RCS(final Repository repository, final PersistenceUtil persistenceUtil) {
		super(new RepositorySettings());
		RepositorySettings settings = (RepositorySettings) getSettings();
		threadPool = new RepoSuiteThreadPool(RCS.class.getSimpleName(), this);
		logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		        false);
		new LongArgument(settings, "cache.size",
		        "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		new BooleanArgument(settings, "repository.analyze", "Requires consistency checks on the repository", "false",
		        false);
		this.persistenceUtil = persistenceUtil;
		this.repository = repository;
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
		if (!shutdown) {
			setup();
			if (!shutdown) {
				threadPool.execute();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		logSettings.getValue();
		
		// this has be done done BEFORE other instances like repository since
		// they could rely on data loading
		if (persistenceUtil == null) {
			if (databaseSettings.getValue() != null) {
				try {
					persistenceUtil = PersistenceManager.getUtil();
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
		}
		
		if (repository == null) {
			repository = repoSettings.getValue();
		}
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
		
		new RepositoryReader(threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		new RepositoryAnalyzer(threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		new RepositoryParser(threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		
		if (persistenceUtil != null) {
			new RepositoryPersister(threadPool.getThreadGroup(), (RepositorySettings) getSettings(),
					persistenceUtil);
		} else {
			new RepositoryVoidSink(threadPool.getThreadGroup(), (RepositorySettings) getSettings());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		
		if (Logger.logInfo()) {
			Logger.info("Toolchain shutdown.");
		}
		threadPool.shutdown();
		shutdown = true;
	}
}
