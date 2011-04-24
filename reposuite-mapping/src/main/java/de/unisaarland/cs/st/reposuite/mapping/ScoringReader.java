/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.List;

import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class ScoringReader extends RepoSuiteSourceThread<RCSTransaction> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param persistenceUtil 
	 */
	public ScoringReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, ScoringReader.class.getSimpleName(), settings);
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
			
			Criteria<RCSTransaction> criteria = this.persistenceUtil.createCriteria(RCSTransaction.class);
			List<RCSTransaction> list = this.persistenceUtil.load(criteria);
			
			for (RCSTransaction transaction : list) {
				if (Logger.logDebug()) {
					Logger.debug("Providing " + transaction.getId() + ".");
				}
				
				write(transaction);
			}
			
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
