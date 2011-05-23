/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class ScoringReportProcessor extends RepoSuiteTransformerThread<Report, MapScore> {
	
	private final MapScore      zeroScore = new MapScore(null, null);
	private final MappingFinder mappingFinder;
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil 
	 */
	public ScoringReportProcessor(final RepoSuiteThreadGroup threadGroup, final MappingSettings settings,
	        final MappingFinder finder, final PersistenceUtil persistenceUtil) {
		super(threadGroup, ScoringReportProcessor.class.getSimpleName(), settings);
		this.mappingFinder = finder;
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
			
			Report report = null;
			
			while (!isShutdown() && ((report = read()) != null)) {
				
				for (RCSTransaction transaction : this.mappingFinder.getCandidates(report, RCSTransaction.class)) {
					if (Logger.logDebug()) {
						Logger.debug("Processing mapping for " + report.getId() + " to " + report.getId() + ".");
					}
					
					MapScore score = this.mappingFinder.score(transaction, report);
					if (score.compareTo(this.zeroScore) > 0) {
						if (Logger.logInfo()) {
							Logger.info("Providing for store operation: " + report.getId() + " -> " + report.getId()
							        + " (score: " + score + ").");
						}
						write(score);
					} else {
						if (Logger.logDebug()) {
							Logger.debug("Discarding " + report.getId() + " -> " + report.getId()
							        + " due to non-positive score (" + score + ").");
						}
					}
				}
				
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
