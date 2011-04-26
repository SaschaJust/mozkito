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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class GraphPersister extends RepoSuiteSinkThread<RCSTransaction> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public GraphPersister(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, RepositoryPersister.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
	}
	
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
				}
				
				if (++i % 100 == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
				
				this.persistenceUtil.update(currentTransaction);
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
