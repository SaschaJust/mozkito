/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.RepoSuiteThreadPool;
import de.unisaarland.cs.st.reposuite.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerArguments;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Bugs extends Thread implements RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool = new RepoSuiteThreadPool(Bugs.class.getSimpleName());
	
	/**
	 * 
	 */
	public Bugs() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setup();
		this.threadPool.execute();
	}
	
	@Override
	public void setup() {
		TrackerSettings settings = new TrackerSettings();
		TrackerArguments trackerArguments = settings.setTrackerArgs(true);
		DatabaseArguments databaseArguments = settings.setDatabaseArgs(false);
		LoggerArguments logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		        false);
		new LongArgument(settings, "cache.size",
		        "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		
		settings.parseArguments();
		Tracker tracker = trackerArguments.getValue();
		logSettings.getValue();
		
		new TrackerReader(this.threadPool.getThreadGroup(), settings, tracker);
		new TrackerRAWChecker(this.threadPool.getThreadGroup(), settings, tracker);
		new TrackerXMLTransformer(this.threadPool.getThreadGroup(), settings, tracker);
		new TrackerXMLChecker(this.threadPool.getThreadGroup(), settings, tracker);
		new TrackerParser(this.threadPool.getThreadGroup(), settings, tracker);
		
		if (databaseArguments.getValue() != null) {
			HibernateUtil hibernateUtil;
			try {
				hibernateUtil = HibernateUtil.getInstance();
				new TrackerPersister(this.threadPool.getThreadGroup(), settings, hibernateUtil);
			} catch (UninitializedDatabaseException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				shutdown();
			}
			
		} else {
			new TrackerVoidSink(this.threadPool.getThreadGroup(), settings);
		}
	}
	
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
	
}
