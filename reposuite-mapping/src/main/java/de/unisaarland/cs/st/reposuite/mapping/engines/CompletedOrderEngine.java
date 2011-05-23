/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class CompletedOrderEngine extends MappingEngine {
	
	private double scoreReportResolvedBeforeTransaction = -1d;
	
	/**
	 * 
	 */
	public CompletedOrderEngine() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores negative if the report was resolved before the transaction was committed.";
	}
	
	/**
	 * @return the scoreReportResolvedBeforeTransaction
	 */
	public double getScoreReportResolvedBeforeTransaction() {
		return this.scoreReportResolvedBeforeTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreReportResolvedBeforeTransaction((Double) getSettings().getSetting("mapping.score.ReportResolvedBeforeTransaction")
		                                                              .getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new DoubleArgument(settings, "mapping.score.ReportResolvedBeforeTransaction",
		                                         "Score in case the report was resolved before the transaction.", "-1",
		                                         isRequired));
	}
	
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
		if ((report.getResolutionTimestamp() != null)
		        && transaction.getTimestamp().isAfter(report.getResolutionTimestamp())) {
			score.addFeature(getScoreReportResolvedBeforeTransaction(), "timestamp", transaction.getTimestamp()
			                                                                                    .toString(),
			                 "creationTimestamp", report.getResolution() != null
			                                                                    ? report.getResolution().toString()
			                                                                    : "(null)", this.getClass());
		}
	}
	
	/**
	 * @param scoreReportResolvedBeforeTransaction the scoreReportResolvedBeforeTransaction to set
	 */
	public void setScoreReportResolvedBeforeTransaction(final double scoreReportResolvedBeforeTransaction) {
		this.scoreReportResolvedBeforeTransaction = scoreReportResolvedBeforeTransaction;
	}
	
}
