package de.unisaarland.cs.st.reposuite.ppa;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;

/**
 * The Class ChangeOperationPersister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeOperationPersister extends RepoSuiteSinkThread<JavaChangeOperation> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * Instantiates a new change operation persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public ChangeOperationPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, ChangeOperationPersister.class.getSimpleName(), settings);
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
		
		persistenceUtil.beginTransaction();
		JavaChangeOperation currentOperation;
		String lastTransactionId = "";
		
		try {
			while (!isShutdown() && ((currentOperation = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentOperation);
				}
				
				String currentTransactionId = currentOperation.getRevision().getTransaction().getId();
				
				if (lastTransactionId.equals("")) {
					lastTransactionId = currentTransactionId;
				}
				if (!currentTransactionId.equals(lastTransactionId)) {
					persistenceUtil.commitTransaction();
					lastTransactionId = currentTransactionId;
					persistenceUtil.beginTransaction();
				}
				persistenceUtil.save(currentOperation);
			}
			persistenceUtil.commitTransaction();
			if (Logger.logInfo()) {
				Logger.info("ChangeOperationPersister done. Terminating... ");
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
