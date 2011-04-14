/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.Collection;

import org.joda.time.Interval;

import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TimestampMappingEngine extends MappingEngine {
	
	private static double                 scoreReportCreatedAfterTransaction   = -10d;
	private static double                 scoreReportResolvedWithinWindow      = 2d;
	// TODO use meaningful values here
	private static org.joda.time.Interval windowReportResolvedAfterTransaction = new Interval(0, 100);
	
	/**
	 * @return the scoreReportCreatedAfterTransaction
	 */
	private static double getScoreReportCreatedAfterTransaction() {
		return scoreReportCreatedAfterTransaction;
	}
	
	/**
	 * @return the scoreReportResolvedWithinWindow
	 */
	private static double getScoreReportResolvedWithinWindow() {
		return scoreReportResolvedWithinWindow;
	}
	
	/**
	 * @return the windowReportResolvedAfterTransaction
	 */
	private static org.joda.time.Interval getWindowReportResolvedAfterTransaction() {
		return windowReportResolvedAfterTransaction;
	}
	
	/**
	 * @param scoreReportCreatedAfterTransaction the scoreReportCreatedAfterTransaction to set
	 */
	private static void setScoreReportCreatedAfterTransaction(final double scoreReportCreatedAfterTransaction) {
		TimestampMappingEngine.scoreReportCreatedAfterTransaction = scoreReportCreatedAfterTransaction;
	}
	
	/**
	 * @param scoreReportResolvedWithinWindow the scoreReportResolvedWithinWindow to set
	 */
	private static void setScoreReportResolvedWithinWindow(final double scoreReportResolvedAfterTransaction) {
		TimestampMappingEngine.scoreReportResolvedWithinWindow = scoreReportResolvedAfterTransaction;
	}
	
	/**
	 * @param windowReportResolvedAfterTransaction the windowReportResolvedAfterTransaction to set
	 */
	private static void setWindowReportResolvedAfterTransaction(final org.joda.time.Interval windowReportResolvedAfterTransaction) {
		TimestampMappingEngine.windowReportResolvedAfterTransaction = windowReportResolvedAfterTransaction;
	}
	
	/**
	 * @param settings
	 */
	TimestampMappingEngine(final MappingSettings settings) {
		super(settings);
		setScoreReportCreatedAfterTransaction((Double) getSettings().getSetting("mapping.score.ReportCreatedAfterTransaction")
		                                                            .getValue());
		setScoreReportResolvedWithinWindow((Double) getSettings().getSetting("mapping.score.ReportResolvedWithinWindow")
		                                                         .getValue());
		setWindowReportResolvedAfterTransaction(new Interval(0, 100));
		// TODO
		// (String)
		// getSettings().getSetting("mapping.window.ReportResolvedAfterTransaction")
		// .getValue()
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
		double value = 0d;
		Interval interval = new Interval(transaction.getTimestamp()
		                                            .plus(getWindowReportResolvedAfterTransaction().getStartMillis()),
		                                 transaction.getTimestamp()
		                                            .plus(getWindowReportResolvedAfterTransaction().getEndMillis()));
		
		if (report.getCreationTimestamp().isBefore(transaction.getTimestamp())) {
			// report created before transaction
			
			if (!report.getHistory().get("status").isEmpty()) {
				report.getHistory().get("status").iterator();
				Collection<Report> historicalReports = report.timewarp(interval);
				
				if (!historicalReports.isEmpty()) {
					if (historicalReports.iterator().next().getResolution() != Resolution.RESOLVED) {
						for (Report hReport : historicalReports) {
							if (hReport.getResolution() == Resolution.RESOLVED) {
								value += getScoreReportResolvedWithinWindow();
							}
						}
					}
				}
			}
		} else {
			value += getScoreReportCreatedAfterTransaction();
		}
		
		score.addFeature(value, "timestamp", transaction.getTimestamp().toString(), this.getClass());
	}
	
}
