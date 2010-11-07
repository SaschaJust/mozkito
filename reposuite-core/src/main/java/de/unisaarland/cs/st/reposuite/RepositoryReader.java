/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The {@link RepositoryReader} reads data from a given {@link Repository} and
 * outputs {@link LogEntry} chunks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryReader extends RepoSuiteSourceThread<LogEntry> {
	
	private LogIterator      logIterator;
	private final Repository repository;
	
	/**
	 * @see RepoSuiteSourceThread
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final Repository repository) {
		super(threadGroup, RepositoryReader.class.getSimpleName(), settings);
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
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
			Logger.info("Requesting logs from " + this.repository);
		}
		
		this.repository.getTransactionCount();
		long cacheSize = (Long) this.getSettings().getSetting("cache.size").getValue();
		this.logIterator = (LogIterator) this.repository.log(this.repository.getFirstRevisionId(),
		        this.repository.getLastRevisionId(), (int) cacheSize);
		
		if (Logger.logInfo()) {
			Logger.info("Created iterator.");
		}
		
		try {
			while (!isShutdown() && this.logIterator.hasNext()) {
				if (Logger.logTrace()) {
					Logger.trace("filling queue [" + outputSize() + "]");
				}
				
				write(this.logIterator.next());
				
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
