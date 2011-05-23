/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.List;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class ScoringReportReader extends RepoSuiteSourceThread<Report> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param persistenceUtil 
	 */
	public ScoringReportReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, ScoringReportReader.class.getSimpleName(), settings);
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
			
			Criteria<Report> criteria = this.persistenceUtil.createCriteria(Report.class);
			List<Report> list = this.persistenceUtil.load(criteria);
			
			for (Report report : list) {
				if (Logger.logDebug()) {
					Logger.debug("Providing " + report.getId() + ".");
				}
				
				write(report);
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
