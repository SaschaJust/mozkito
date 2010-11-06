/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryPersister extends RepoSuiteSinkThread<RCSTransaction> {
	
	private final HibernateUtil hibernateUtil;
	
	public RepositoryPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final HibernateUtil hibernateUtil) {
		super(threadGroup, RepositoryPersister.class.getSimpleName(), settings);
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
		RCSTransaction currentTransaction;
		int i = 0;
		try {
			while (!isShutdown() && ((currentTransaction = this.inputStorage.read()) != null)) {
				
				if (Logger.logTrace()) {
					Logger.trace("Saving " + currentTransaction);
				}
				
				if (++i % 1000 == 0) {
					this.hibernateUtil.commitTransaction();
					this.hibernateUtil.beginTransaction();
				}
				this.hibernateUtil.saveOrUpdate(currentTransaction);
			}
			this.hibernateUtil.commitTransaction();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
