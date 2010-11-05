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
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author just
 * 
 */
public class Core extends Thread implements RepoSuiteToolchain {
	
	private final RepoSuiteThreadGroup threads = new RepoSuiteThreadGroup(Core.class.getSimpleName());
	
	/**
	 * 
	 */
	public Core() {}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setup();
		
		for (Thread thread : this.threads.getThreads()) {
			thread.start();
		}
		
		for (Thread thread : this.threads.getThreads()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new RuntimeException();
			}
		}
	}
	
	@Override
	public void setup() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		RepositoryArguments repoSettings = settings.setRepositoryArg(true);
		DatabaseArguments databaseSettings = settings.setDatabaseArgs(false);
		LoggerArguments logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
				false);
		new LongArgument(settings, "repository.cachesize",
				"determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		new BooleanArgument(settings, "repository.analyze", "Requires consistency checks on the repository", "false",
				false);
		
		settings.parseArguments();
		
		Repository repository = repoSettings.getValue();
		logSettings.getValue();
		
		RepositoryReader reader = new RepositoryReader(this.threads, repository, settings);
		
		RepositoryAnalyzer analyzer = new RepositoryAnalyzer(this.threads, repository, settings);
		analyzer.connectInput(reader);
		
		RepositoryParser parser = new RepositoryParser(this.threads, repository, settings);
		parser.connectInput(analyzer);
		
		HibernateUtil hibernateUtil = databaseSettings.getValue();
		
		if (hibernateUtil != null) {
			RepositoryPersister persister = new RepositoryPersister(this.threads, hibernateUtil, settings);
			persister.connectInput(parser);
		} else {
			RepositoryVoidSink voidSink = new RepositoryVoidSink(this.threads);
			voidSink.connectInput(parser);
		}
	}
	
	/**
	 * 
	 */
	public void shutdown() {
		if (Logger.logError()) {
			Logger.error("Terminating " + this.threads.activeCount() + " threads.");
		}
		
		this.threads.shutdown();
	}
}
