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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Graph extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool;
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
	 * @see de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#setup()
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
		new GraphPersister(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), this.persistenceUtil);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#shutdown()
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
