/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingsPersister extends RepoSuiteSinkThread<MapScore> {
	
	private final HibernateUtil hibernateUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param hibernateUtil 
	 */
	public MappingsPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final HibernateUtil hibernateUtil) {
		super(threadGroup, MappingsPersister.class.getSimpleName(), settings);
		this.hibernateUtil = hibernateUtil;
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
			this.hibernateUtil.beginTransaction();
			int i = 0;
			
			while (!isShutdown() && ((score = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + score);
				}
				
				if (++i % 15 == 0) {
					this.hibernateUtil.commitTransaction();
					this.hibernateUtil.beginTransaction();
				}
				
				this.hibernateUtil.saveOrUpdate(score);
			}
			this.hibernateUtil.commitTransaction();
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
