/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.concurrent.CountDownLatch;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

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
		CountDownLatch currentLatch = new CountDownLatch(1);
		Tuple<RCSTransaction, CountDownLatch> tuple;
		int i = 0;
		
		try {
			while (!isShutdown() && ((tuple = readLatch()) != null)) {
				currentTransaction = tuple.getFirst();
				currentLatch = tuple.getSecond();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentTransaction);
				}
				
				if (++i % 100 == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
				
				this.persistenceUtil.save(currentTransaction);
				currentLatch.countDown();
			}
			this.persistenceUtil.commitTransaction();
			
			if (Logger.logInfo()) {
				Logger.info("RepositoryPersister done. Terminating... ");
			}
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
