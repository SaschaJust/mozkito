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
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.utils.Preconditions;

/**
 * {@link Core} is the standard {@link RepoSuiteToolchain} to mine a repository.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Core extends Thread implements RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool = new RepoSuiteThreadPool(Core.class.getSimpleName());
	
	/**
	 * 
	 */
	public Core() {
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
		RepoSuiteSettings settings = new RepoSuiteSettings();
		RepositoryArguments repoSettings = settings.setRepositoryArg(true);
		DatabaseArguments databaseSettings = settings.setDatabaseArgs(false);
		LoggerArguments logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		        false);
		new LongArgument(settings, "cache.size",
		        "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		new BooleanArgument(settings, "repository.analyze", "Requires consistency checks on the repository", "false",
		        false);
		
		settings.parseArguments();
		
		Repository repository = repoSettings.getValue();
		logSettings.getValue();
		
		new RepositoryReader(this.threadPool.getThreadGroup(), settings, repository);
		new RepositoryAnalyzer(this.threadPool.getThreadGroup(), settings, repository);
		new RepositoryParser(this.threadPool.getThreadGroup(), settings, repository);
		
		HibernateUtil hibernateUtil = databaseSettings.getValue();
		
		if (hibernateUtil != null) {
			new RepositoryPersister(this.threadPool.getThreadGroup(), settings, hibernateUtil);
		} else {
			new RepositoryVoidSink(this.threadPool.getThreadGroup(), settings);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
}
