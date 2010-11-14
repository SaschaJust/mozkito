/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
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
 * {@link Core} is the standard {@link RepoSuiteToolchain} to mine a repository.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Core extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool = new RepoSuiteThreadPool(Core.class.getSimpleName());
	private final RepositoryArguments repoSettings;
	private final LoggerArguments     logSettings;
	private final DatabaseArguments   databaseSettings;
	
	public Core() {
		super(new RepositorySettings());
		RepositorySettings settings = (RepositorySettings) getSettings();
		this.repoSettings = settings.setRepositoryArg(true);
		this.databaseSettings = settings.setDatabaseArgs(false);
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
		setup();
		this.threadPool.execute();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		Repository repository = this.repoSettings.getValue();
		this.logSettings.getValue();
		
		new RepositoryReader(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		new RepositoryAnalyzer(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		new RepositoryParser(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), repository);
		
		HibernateUtil hibernateUtil = this.databaseSettings.getValue();
		
		if (hibernateUtil != null) {
			new RepositoryPersister(this.threadPool.getThreadGroup(), (RepositorySettings) getSettings(), hibernateUtil);
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
	}
}
