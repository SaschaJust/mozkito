/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerArguments;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadPool;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Bugs extends RepoSuiteToolchain {
	
	private final RepoSuiteThreadPool threadPool;
	private final TrackerArguments    trackerArguments;
	private final DatabaseArguments   databaseArguments;
	private final LoggerArguments     logSettings;
	
	/**
	 * 
	 */
	public Bugs() {
		super(new TrackerSettings());
		this.threadPool = new RepoSuiteThreadPool(Bugs.class.getSimpleName(), this);
		TrackerSettings settings = (TrackerSettings) getSettings();
		this.trackerArguments = settings.setTrackerArgs(true);
		this.databaseArguments = settings.setDatabaseArgs(false, this.getClass().getSimpleName().toLowerCase());
		this.logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		                    false);
		new LongArgument(settings, "cache.size",
		                 "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setup();
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		Tracker tracker = this.trackerArguments.getValue();
		this.logSettings.getValue();
		
		new TrackerReader(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerRAWChecker(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerXMLTransformer(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerXMLChecker(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerParser(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		
		if (this.databaseArguments.getValue() != null) {
			PersistenceUtil persistenceUtil;
			try {
				persistenceUtil = PersistenceManager.getUtil();
				new TrackerPersister(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker,
				                     persistenceUtil);
			} catch (UninitializedDatabaseException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				shutdown();
			}
			
		} else {
			new TrackerVoidSink(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings());
		}
	}
	
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
	
}
