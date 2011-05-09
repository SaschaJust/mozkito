/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import net.ownhero.dev.kisa.Logger;

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
	public RepositoryReader(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
			final Repository repository) {
		super(threadGroup, RepositoryReader.class.getSimpleName(), settings);
		this.repository = repository;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
			Logger.info("Requesting logs from " + repository);
		}
		
		repository.getTransactionCount();
		long cacheSize = (Long) this.getSettings().getSetting("cache.size").getValue();
		logIterator = (LogIterator) repository.log(repository.getFirstRevisionId(), repository.getEndRevision(),
				(int) cacheSize);
		
		if (Logger.logInfo()) {
			Logger.info("Created iterator.");
		}
		
		try {
			while (!isShutdown() && logIterator.hasNext()) {
				if (Logger.logTrace()) {
					Logger.trace("filling queue [" + outputSize() + "]");
				}
				LogEntry entry = logIterator.next();
				if (Logger.logTrace()) {
					Logger.trace("with entry: " + entry);
				}
				write(entry);
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
