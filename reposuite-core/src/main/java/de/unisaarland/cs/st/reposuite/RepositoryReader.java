/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryReader extends RepositoryThread {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RepositoryReader.class.getSimpleName();
	}
	
	private LogIterator             logIterator;
	
	private final Repository        repository;
	
	private final RepoSuiteSettings settings;
	private final boolean           shutdown = false;
	
	public RepositoryReader(final ThreadGroup threadGroup, final Repository repository, final RepoSuiteSettings settings) {
		super(threadGroup, getHandle());
		this.repository = repository;
		this.settings = settings;
	}
	
	public synchronized LogIterator getIterator() {
		if (this.logIterator == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		return this.logIterator;
	}
	
	public Repository getRepository() {
		return this.repository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!this.shutdown) {
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			if (Logger.logInfo()) {
				Logger.info("Requesting logs from " + this.repository);
			}
			
			this.repository.getTransactionCount();
			long cacheSize = (Long) this.settings.getSetting("repository.cachesize").getValue();
			this.logIterator = (LogIterator) this.repository.log(this.repository.getFirstRevisionId(),
			        this.repository.getLastRevisionId(), (int) cacheSize);
			
			if (Logger.logInfo()) {
				Logger.info("Created iterator.");
			}
			wake();
			
			while (!this.shutdown && !this.logIterator.done()) {
				this.logIterator.update();
			}
		}
	}
}
