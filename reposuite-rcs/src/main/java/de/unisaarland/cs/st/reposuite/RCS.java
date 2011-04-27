/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

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
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * {@link RCS} is the standard {@link RepoSuiteToolchain} to mine a repository.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RCS extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool;
	private final RepositoryArguments repoSettings;
	private final LoggerArguments     logSettings;
	private final DatabaseArguments   databaseSettings;
	private boolean                   shutdown;
	private PersistenceUtil           persistenceUtil;
	
	public RCS() {
		super(new RepositorySettings());
		this.threadPool = new RepoSuiteThreadPool(RCS.class.getSimpleName(), this);
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
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteToolchain#setup()
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
		
		// TODO i din't think we can resume repository mining at all.
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
		
		new RepositoryReader(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		new RepositoryAnalyzer(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		new RepositoryParser(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		
		if (this.persistenceUtil != null) {
			new GraphBuilder(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository,
			                 this.persistenceUtil);
			new RepositoryPersister(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(),
			                        this.persistenceUtil);
		} else {
			new RepositoryVoidSink(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings());
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
		this.threadPool.shutdown();
		this.shutdown = true;
	}
}
