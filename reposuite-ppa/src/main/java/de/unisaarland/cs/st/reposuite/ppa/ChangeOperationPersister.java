package de.unisaarland.cs.st.reposuite.ppa;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import net.ownhero.dev.kisa.Logger;

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
		
		if (!this.checkConnections()) {
			return;
		}
		
		if (!this.checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + this.getHandle());
		}
		
		this.persistenceUtil.beginTransaction();
		JavaChangeOperation currentOperation;
		String lastTransactionId = "";
		
		try {
			while (!this.isShutdown() && ((currentOperation = this.read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentOperation);
				}
				
				String currentTransactionId = currentOperation.getRevision().getTransaction().getId();
				
				if (lastTransactionId.equals("")) {
					lastTransactionId = currentTransactionId;
				}
				if (!currentTransactionId.equals(lastTransactionId)) {
					this.persistenceUtil.commitTransaction();
					lastTransactionId = currentTransactionId;
					this.persistenceUtil.beginTransaction();
				}
				this.persistenceUtil.save(currentOperation);
			}
			this.persistenceUtil.commitTransaction();
			if (Logger.logInfo()) {
				Logger.info("ChangeOperationPersister done. Terminating... ");
			}
			this.finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			this.shutdown();
		}
	}
}
