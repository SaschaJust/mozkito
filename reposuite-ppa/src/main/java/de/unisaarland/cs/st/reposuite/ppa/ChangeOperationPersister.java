package de.unisaarland.cs.st.reposuite.ppa;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class ChangeOperationPersister extends RepoSuiteSinkThread<JavaChangeOperation> {
	
	private final HibernateUtil hibernateUtil;
	
	public ChangeOperationPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
			final HibernateUtil hibernateUtil) {
		super(threadGroup, ChangeOperationPersister.class.getSimpleName(), settings);
		this.hibernateUtil = hibernateUtil;
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
		this.hibernateUtil.beginTransaction();
		JavaChangeOperation currentOperation;
		int i = 0;
		
		try {
			while (!isShutdown() && ((currentOperation = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentOperation);
				}
				
				if (++i % 100 == 0) {
					this.hibernateUtil.commitTransaction();
					this.hibernateUtil.beginTransaction();
				}
				this.hibernateUtil.saveOrUpdate(currentOperation);
			}
			this.hibernateUtil.commitTransaction();
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
