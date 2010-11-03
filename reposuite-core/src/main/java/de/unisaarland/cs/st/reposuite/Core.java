/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.ArrayList;

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
public class Core extends Thread {
	
	protected static class CoreThreadGroup extends ThreadGroup {
		
		private final Core core;
		
		public CoreThreadGroup(final Core core, final String name) {
			super(name);
			this.core = core;
		}
		
		@Override
		public void uncaughtException(final Thread t, final Throwable e) {
			
			if (Logger.logError()) {
				Logger.error("Thread " + t.getName() + " terminated with uncaught exception " + e.getClass().getName()
				        + ". Message: " + e.getMessage(), e);
			}
			this.core.shutdown();
		}
	}
	
	private final ThreadGroup                 threads    = new CoreThreadGroup(this, Core.class.getSimpleName());
	private final ArrayList<RepositoryThread> threadList = new ArrayList<RepositoryThread>();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		RepositoryArguments repoSettings = settings.setRepositoryArg(true);
		DatabaseArguments databaseSettings = settings.setDatabaseArgs(true);
		LoggerArguments logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		        false);
		new LongArgument(settings, "repository.cachesize",
		        "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		settings.parseArguments();
		
		Repository repository = repoSettings.getValue();
		logSettings.getValue();
		HibernateUtil hibernateUtil = databaseSettings.getValue();
		
		RepositoryReader reader = new RepositoryReader(this.threads, repository, settings);
		this.threadList.add(reader);
		RepositoryAnalyzer analyzer = new RepositoryAnalyzer(this.threads, reader, settings);
		this.threadList.add(analyzer);
		RepositoryParser parser = new RepositoryParser(this.threads, analyzer);
		this.threadList.add(parser);
		RepositoryPersister persister = new RepositoryPersister(this.threads, parser, hibernateUtil);
		this.threadList.add(persister);
		// RepositoryThread t = new RepositoryThread(this.threads, "test") {
		//
		// @Override
		// public void run() {
		// throw new RuntimeException();
		// }
		// };
		// this.threadList.add(t);
		
		for (Thread thread : this.threadList) {
			thread.start();
		}
		
		for (Thread thread : this.threadList) {
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
	
	/**
	 * 
	 */
	public void shutdown() {
		if (Logger.logError()) {
			Logger.error("Terminating " + this.threads.activeCount() + " threads.");
		}
		
		for (RepositoryThread thread : this.threadList) {
			thread.shutdown();
		}
	}
}
