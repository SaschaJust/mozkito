/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerPersister extends RepoSuiteSinkThread<Report> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param persistenceUtil
	 */
	public TrackerPersister(final RepoSuiteThreadGroup threadGroup, final TrackerSettings settings,
	        final Tracker tracker, final PersistenceUtil persistenceUtil) {
		super(threadGroup, TrackerPersister.class.getSimpleName(), settings);
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
			
			Report bugReport;
			this.persistenceUtil.beginTransaction();
			int i = 0;
			
			while (!isShutdown() && ((bugReport = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + bugReport);
				}
				
				if (++i % 15 == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
				
				this.persistenceUtil.saveOrUpdate(bugReport);
			}
			this.persistenceUtil.commitTransaction();
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
