/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingsPersister extends RepoSuiteSinkThread<MapScore> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param persistenceUtil 
	 */
	public MappingsPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, MappingsPersister.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			MapScore score;
			this.persistenceUtil.beginTransaction();
			int i = 0;
			
			while (!isShutdown() && ((score = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + score);
				}
				
				if (++i % 50 == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
				
				this.persistenceUtil.save(score);
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
