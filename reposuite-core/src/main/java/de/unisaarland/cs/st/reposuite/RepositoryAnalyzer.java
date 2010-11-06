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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends RepoSuiteFilterThread<LogEntry> {
	
	private final List<LogEntry> entries = new LinkedList<LogEntry>();
	
	private boolean              analyze;
	private final Repository     repository;
	
	public RepositoryAnalyzer(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final Repository repository) {
		super(threadGroup, RepositoryAnalyzer.class.getSimpleName(), settings);
		this.repository = repository;
	}
	
	@Override
	public void run() {
		
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		this.analyze = (this.settings.getSetting("repository.analyze") != null)
		        && (this.settings.getSetting("repository.analyze").getValue() != null)
		        && (Boolean) this.settings.getSetting("repository.analyze").getValue();
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		LogEntry entry;
		try {
			while (!isShutdown() && ((entry = this.inputStorage.read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Adding " + entry + " to analysis.");
				}
				if (this.analyze) {
					this.entries.add(entry);
				}
				
				if (Logger.logTrace()) {
					Logger.trace("filling queue [" + this.outputStorage.size() + "]");
				}
				this.outputStorage.write(entry);
				
			}
			
			if (!this.shutdown && this.analyze) {
				this.repository.consistencyCheck(this.entries, ((Boolean) this.settings.getSetting("headless")
				        .getValue() == false));
			}
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
