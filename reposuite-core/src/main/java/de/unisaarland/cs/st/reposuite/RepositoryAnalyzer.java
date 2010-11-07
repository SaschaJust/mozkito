/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The {@link RepositoryAnalyzer} is a null filter, i.e. it does not modify the
 * data, but analyzes it and prints warnings/errors to the STDOUT.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends RepoSuiteFilterThread<LogEntry> {
	
	private final List<LogEntry> entries = new LinkedList<LogEntry>();
	
	private boolean              analyze;
	private final Repository     repository;
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryAnalyzer(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final Repository repository) {
		super(threadGroup, RepositoryAnalyzer.class.getSimpleName(), settings);
		this.repository = repository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		this.analyze = (this.getSettings().getSetting("repository.analyze") != null)
		        && (this.getSettings().getSetting("repository.analyze").getValue() != null)
		        && (Boolean) this.getSettings().getSetting("repository.analyze").getValue();
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		LogEntry entry;
		try {
			while (!isShutdown() && ((entry = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Adding " + entry + " to analysis.");
				}
				if (this.analyze) {
					this.entries.add(entry);
				}
				
				if (Logger.logTrace()) {
					Logger.trace("filling queue [" + outputSize() + "]");
				}
				write(entry);
				
			}
			
			if (!isShutdown() && this.analyze) {
				this.repository.consistencyCheck(this.entries, ((Boolean) this.getSettings().getSetting("headless")
				        .getValue() == false));
			}
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
		
		if (Logger.logInfo()) {
			Logger.info("All done. Finishing.");
		}
	}
}
