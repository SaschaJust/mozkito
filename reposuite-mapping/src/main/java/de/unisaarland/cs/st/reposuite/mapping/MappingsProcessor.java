/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingsProcessor extends RepoSuiteTransformerThread<RCSTransaction, MapScore> {
	
	private final HibernateUtil hibernateUtil;
	private final MapScore      zeroScore = new MapScore(null, null);
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param hibernateUtil 
	 */
	public MappingsProcessor(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final HibernateUtil hibernateUtil) {
		super(threadGroup, MappingsProcessor.class.getSimpleName(), settings);
		this.hibernateUtil = hibernateUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@SuppressWarnings ("unchecked")
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
				Set<Long> candidates = MappingFinder.getCandidates(transaction);
				
				if (!candidates.isEmpty()) {
					if (Logger.logDebug()) {
						Logger.debug("Fetching candidates (" + candidates.size() + ") "
						        + JavaUtils.collectionToString(candidates));
					}
					
					Criteria criteria = this.hibernateUtil.createCriteria(Report.class);
					criteria.add(Restrictions.in("id", candidates));
					
					for (Report report : (List<Report>) criteria.list()) {
						if (Logger.logDebug()) {
							Logger.debug("Processing mapping for " + transaction.getId() + " to " + report.getId()
							        + ".");
						}
						
						MapScore score = MappingFinder.score(transaction, report);
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
