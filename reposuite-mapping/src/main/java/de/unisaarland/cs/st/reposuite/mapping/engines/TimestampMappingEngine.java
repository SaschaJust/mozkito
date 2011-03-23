/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TimestampMappingEngine extends MappingEngine {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.rcs.model.RCSTransaction,
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		if (report.getCreationTimestamp().isBefore(transaction.getTimestamp())) {
			if (!report.getComments().isEmpty()
			        && report.getComments().last().getTimestamp().isAfter(transaction.getTimestamp())) {
				score.addFeature(0.3, "timestamp", transaction.getTimestamp().toString(), this.getClass());
			} else {
				score.addFeature(0.1, "timestamp", transaction.getTimestamp().toString(), this.getClass());
			}
		}
		
	}
	
}
