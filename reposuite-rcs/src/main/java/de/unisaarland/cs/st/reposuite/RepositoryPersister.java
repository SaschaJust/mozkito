/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The {@link RepositoryPersister} taks {@link RCSTransaction} from the previous
 * node and dumps the data to the database.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryPersister extends RepoSuiteSinkThread<RCSTransaction> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @see RepoSuiteSinkThread
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil
	 */
	public RepositoryPersister(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, RepositoryPersister.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!checkConnections()) {
			return;
		}
		
		if (!checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		this.persistenceUtil.beginTransaction();
		RCSTransaction currentTransaction;
		int i = 0;
		
		try {
			while (!isShutdown() && ((currentTransaction = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentTransaction);
					System.err.println(currentTransaction.toTerm());
				}
				
				if (++i % 100 == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
				
				this.persistenceUtil.save(currentTransaction);
			}
			this.persistenceUtil.commitTransaction();
			
			if (Logger.logInfo()) {
				Logger.info("RepositoryPersister done. Terminating... ");
			}
			
			this.persistenceUtil.shutdown();
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
