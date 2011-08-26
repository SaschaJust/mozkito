package de.unisaarland.cs.st.reposuite.mapping.engines;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableReport;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableTransaction;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.And;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Atom;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
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
	 * 
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
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreReportResolvedBeforeTransaction((Double) getSettings().getSetting(
		        "mapping.score.ReportResolvedBeforeTransaction").getValue());
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
		arguments.addArgument(new DoubleArgument(settings, "mapping.score.ReportResolvedBeforeTransaction",
		        "Score in case the report was resolved before the transaction.", "-1", isRequired));
	}
	
	/**
	 * @param scoreReportResolvedBeforeTransaction
	 *            the scoreReportResolvedBeforeTransaction to set
	 */
	public void setScoreReportResolvedBeforeTransaction(final double scoreReportResolvedBeforeTransaction) {
		this.scoreReportResolvedBeforeTransaction = scoreReportResolvedBeforeTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(2, Report.class), new Atom(1, RCSTransaction.class));
	}
	
	@Override
	public void score(MappableEntity from, MappableEntity to, MapScore score) {
		RCSTransaction transaction = ((MappableTransaction) from).getTransaction();
		Report report = ((MappableReport) to).getReport();
		
		if ((report.getResolutionTimestamp() != null)
		        && transaction.getTimestamp().isAfter(report.getResolutionTimestamp())) {
			score.addFeature(getScoreReportResolvedBeforeTransaction(), "timestamp", transaction.getTimestamp()
			        .toString(), "creationTimestamp", report.getResolution() != null ? report.getResolution()
			        .toString() : "(null)", this.getClass());
		}
	}
	
}
