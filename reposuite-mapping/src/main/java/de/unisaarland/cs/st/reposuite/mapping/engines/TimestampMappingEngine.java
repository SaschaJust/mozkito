/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.ownhero.dev.kanuni.checks.CollectionCheck;

import org.joda.time.Interval;

import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TimestampMappingEngine extends MappingEngine {
	
	private static double                 scoreReportCreatedAfterTransaction   = -10d;
	private static double                 scoreReportResolvedWithinWindow      = 2d;
	// TODO use meaningful values here
	private static org.joda.time.Interval windowReportResolvedAfterTransaction = new Interval(0, 7200000);
	
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
	public TimestampMappingEngine(final MappingSettings settings) {
		super(settings);
		setScoreReportCreatedAfterTransaction((Double) getSettings().getSetting("mapping.score.ReportCreatedAfterTransaction")
		                                                            .getValue());
		setScoreReportResolvedWithinWindow((Double) getSettings().getSetting("mapping.score.ReportResolvedWithinWindow")
		                                                         .getValue());
		@SuppressWarnings ("unchecked")
		List<String> list = new LinkedList<String>(
		                                           (Set<String>) getSettings().getSetting("mapping.window.ReportResolvedAfterTransaction")
		                                                                      .getValue());
		CollectionCheck.minSize(list,
		                        1,
		                        "There are 1 to 2 values that have to be specified for a time interval to be valid. If only one is specified, the first one defaults to 0.");
		CollectionCheck.maxSize(list,
		                        2,
		                        "There are 1 to 2 values that have to be specified for a time interval to be valid. If only one is specified, the first one defaults to 0.");
		
		int start = 0;
		int end = 0;
		
		if (list.size() == 2) {
			start = parseIntervalString(list.get(0));
			end = parseIntervalString(list.get(1));
		} else {
			end = parseIntervalString(list.get(0));
		}
		
		if (start > end) {
			start ^= end ^= start ^= end;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Using score 'ReportCreatedAfterTransaction': " + getScoreReportCreatedAfterTransaction());
			Logger.info("Using score 'ReportResolvedWithinWindow': " + getScoreReportResolvedWithinWindow());
			Logger.info("Using interval: [" + start + ", " + end + "].");
		}
		
		setWindowReportResolvedAfterTransaction(new Interval(start * 1000, end * 1000));
	}
	
	private int parseIntervalString(final String string) {
		int value = 0;
		Regex regex = new Regex(
		                        "\\s*[+-]?({days}[0-9]+)d\\s*({hours}[0-9]+)h\\s*({minutes}[0-9]+)m\\s*({seconds}[0-9]+)s",
		                        Pattern.CASE_INSENSITIVE);
		List<RegexGroup> find = regex.find(string);
		
		if (find == null) {
			throw new Shutdown("Interval specification invalid. String under subject: " + string + " using regex "
			        + regex.getPattern());
		} else {
			value += Integer.parseInt(regex.getGroup("days")) * 24 * 60 * 60;
			value += Integer.parseInt(regex.getGroup("hours")) * 60 * 60;
			value += Integer.parseInt(regex.getGroup("minutes")) * 60;
			value += Integer.parseInt(regex.getGroup("seconds"));
		}
		
		if (string.startsWith("-")) {
			value *= -1;
		}
		
		return value;
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
			
			if (Logger.logDebug()) {
				Logger.debug("Report was created before transaction.");
			}
			
			if (!report.getHistory().get("status").isEmpty()) {
				report.getHistory().get("status").iterator();
				Collection<Report> historicalReports = report.timewarp(interval, "status");
				
				if (!historicalReports.isEmpty()) {
					for (Report hReport : historicalReports) {
						if (hReport.getResolution() == Resolution.RESOLVED) {
							value += getScoreReportResolvedWithinWindow();
							
							if (Logger.logDebug()) {
								Logger.debug("Report was marked as " + Resolution.RESOLVED.name()
								        + " within the specified time window.");
							}
							break;
						}
					}
					
					if (Logger.logDebug()) {
						if (Double.compare(value, 0d) == 0) {
							Logger.debug("But report hasn't been marked as " + Resolution.RESOLVED.name()
							        + " within the time window.");
						}
					}
				} else {
					if (Logger.logDebug()) {
						Logger.debug("But report hasn't been marked as " + Resolution.RESOLVED.name()
						        + " within the time window.");
					}
				}
			}
		} else {
			
			if (Logger.logDebug()) {
				Logger.debug("Report created after transaction. Scoring: " + getScoreReportCreatedAfterTransaction());
			}
			value += getScoreReportCreatedAfterTransaction();
		}
		
		score.addFeature(value, "timestamp", transaction.getTimestamp().toString(), this.getClass());
	}
	
}
