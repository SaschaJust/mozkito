/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import de.unisaarland.cs.st.reposuite.mapping.model.RCSFile2Bugs;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Files2BugsPersister extends RepoSuiteSinkThread<RCSFile2Bugs> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public Files2BugsPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, ScoringPersister.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
	}
	
	@Override
	public void run() {
		try {
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			RCSFile2Bugs files2Bugs;
			this.persistenceUtil.beginTransaction();
			int i = 0;
			
			while (!isShutdown() && ((files2Bugs = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Storing " + files2Bugs);
				}
				
				if (++i % 50 == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
				
				this.persistenceUtil.save(files2Bugs);
			}
			this.persistenceUtil.commitTransaction();
			this.persistenceUtil.shutdown();
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
