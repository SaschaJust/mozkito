/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.Set;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class ScoringProcessor extends RepoSuiteTransformerThread<RCSTransaction, MapScore> {
	
	private final PersistenceUtil persistenceUtil;
	private final MapScore        zeroScore = new MapScore(null, null);
	private final MappingFinder   mappingFinder;
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil 
	 */
	public ScoringProcessor(final RepoSuiteThreadGroup threadGroup, final MappingSettings settings,
	        final MappingFinder finder, final PersistenceUtil persistenceUtil) {
		super(threadGroup, ScoringProcessor.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
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
			
			RCSTransaction transaction = null;
			
			while (!isShutdown() && ((transaction = read()) != null)) {
				Set<Long> candidates = this.mappingFinder.getCandidates(transaction);
				
				if (!candidates.isEmpty()) {
					if (Logger.logDebug()) {
						Logger.debug("Fetching candidates (" + candidates.size() + ") "
						        + JavaUtils.collectionToString(candidates));
					}
					
					Criteria<Report> criteria = this.persistenceUtil.createCriteria(Report.class);
					criteria.in("id", candidates);
					
					for (Report report : this.persistenceUtil.load(criteria)) {
						if (Logger.logDebug()) {
							Logger.debug("Processing mapping for " + transaction.getId() + " to " + report.getId()
							        + ".");
						}
						
						MapScore score = this.mappingFinder.score(transaction, report);
						if (score.compareTo(this.zeroScore) > 0) {
							if (Logger.logInfo()) {
								Logger.info("Providing for store operation: " + transaction.getId() + " -> "
								        + report.getId() + " (score: " + score + ").");
							}
							write(score);
						} else {
							if (Logger.logDebug()) {
								Logger.debug("Discarding " + transaction.getId() + " -> " + report.getId()
								        + " due to non-positive score (" + score + ").");
							}
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
