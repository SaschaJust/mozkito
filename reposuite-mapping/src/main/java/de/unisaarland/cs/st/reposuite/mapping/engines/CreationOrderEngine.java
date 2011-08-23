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
public class CreationOrderEngine extends MappingEngine {
	
	private double scoreReportCreatedAfterTransaction = -1d;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores negative if the report was created after the transaction was committed.";
	}
	
	/**
	 * @return the scoreReportCreatedAfterTransaction
	 */
	public double getScoreReportCreatedAfterTransaction() {
		return this.scoreReportCreatedAfterTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		setScoreReportCreatedAfterTransaction((Double) getSettings().getSetting(
		        "mapping.score.ReportCreatedAfterTransaction").getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings, final MappingArguments arguments, final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new DoubleArgument(settings, "mapping.score.ReportCreatedAfterTransaction",
		        "Score in case the report was created after the transaction.", "-1", isRequired));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.rcs.model.RCSTransaction,
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(final RCSTransaction transaction, final Report report, final MapScore score) {
		if (transaction.getTimestamp().isBefore(report.getCreationTimestamp())) {
			score.addFeature(getScoreReportCreatedAfterTransaction(), "timestamp", transaction.getTimestamp()
			        .toString(), "creationTimestamp", report.getCreationTimestamp().toString(), this.getClass());
		}
	}
	
	/**
	 * @param scoreReportCreatedAfterTransaction
	 *            the scoreReportCreatedAfterTransaction to set
	 */
	public void setScoreReportCreatedAfterTransaction(final double scoreReportCreatedAfterTransaction) {
		this.scoreReportCreatedAfterTransaction = scoreReportCreatedAfterTransaction;
	}
	
}
