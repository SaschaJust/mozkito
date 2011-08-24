package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.ownhero.dev.kanuni.checks.CollectionCheck;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.History;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableReport;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.And;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Atom;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Index;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.model.EnumTuple;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TimestampEngine extends MappingEngine {
	
	private org.joda.time.Interval windowReportResolvedAfterTransaction = new Interval(0, 7200000);
	
	@Override
	public String getDescription() {
		return "Scores based on the relation of close and commit timestamp (1/(1+days(close - upperbound).";
	}
	
	/**
	 * @return the windowReportResolvedAfterTransaction
	 */
	private org.joda.time.Interval getWindowReportResolvedAfterTransaction() {
		return this.windowReportResolvedAfterTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		@SuppressWarnings("unchecked") List<String> list = new LinkedList<String>((Set<String>) getSettings()
		        .getSetting("mapping.window.ReportResolvedAfterTransaction").getValue());
		CollectionCheck
		        .minSize(
		                list,
		                1,
		                "There are 1 to 2 values that have to be specified for a time interval to be valid. If only one is specified, the first one defaults to 0.");
		CollectionCheck
		        .maxSize(
		                list,
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
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init(de.
	 * unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings, final MappingArguments arguments, final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments
		        .addArgument(new ListArgument(
		                settings,
		                "mapping.window.ReportResolvedAfterTransaction",
		                "Time window for the 'mapping.score.ReportResolvedWithinWindow' setting in format '[+-]XXd XXh XXm XXs'.",
		                "-0d 0h 10m 0s,+0d 2h 0m 0s", isRequired));
	}
	
	/**
	 * @param windowReportResolvedAfterTransaction
	 *            the windowReportResolvedAfterTransaction to set
	 */
	private void setWindowReportResolvedAfterTransaction(
	        final org.joda.time.Interval windowReportResolvedAfterTransaction) {
		this.windowReportResolvedAfterTransaction = windowReportResolvedAfterTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.CREATION_TIMESTAMP), new And(new Atom(Index.TO, Report.class),
		        new Atom(Index.TO, FieldKey.RESOLUTION_TIMESTAMP)));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(MappableEntity element1, MappableEntity element2, MapScore score) {
		double value = 0d;
		
		DateTime element1Timestamp = ((DateTime) element1.get(FieldKey.CREATION_TIMESTAMP));
		DateTime element2CreationTimestamp = ((DateTime) element2.get(FieldKey.CREATION_TIMESTAMP));
		DateTime element2ResolutionTimestamp = ((DateTime) element2.get(FieldKey.RESOLUTION_TIMESTAMP));
		
		Report report = ((MappableReport) element2).getReport();
		
		Interval interval = new Interval(element1Timestamp.plus(getWindowReportResolvedAfterTransaction()
		        .getStartMillis()), element1Timestamp.plus(getWindowReportResolvedAfterTransaction().getEndMillis()));
		
		if (element2CreationTimestamp.isBefore(element1Timestamp) && (element2ResolutionTimestamp != null)) {
			History history = report.getHistory().get(Resolution.class.getSimpleName().toLowerCase());
			for (HistoryElement element : history.getElements()) {
				EnumTuple tuple = element.getChangedEnumValues().get(Resolution.class.getSimpleName().toLowerCase());
				@SuppressWarnings("unchecked") Enum<Resolution> val = (Enum<Resolution>) tuple.getNewValue();
				if (val.equals(Resolution.RESOLVED)) {
					if (interval.contains(element.getTimestamp())) {
						value = 1;
						
					} else if (element2ResolutionTimestamp.isAfter(element1Timestamp)) {
						value = Math
						        .max(value, 1.0d / (1.0d + (element2ResolutionTimestamp.getMillis() - element1Timestamp
						                .getMillis()) / 1000d / 3600d / 24d));
					}
				}
			}
			
			score.addFeature(value, FieldKey.CREATION_TIMESTAMP.name(), element1Timestamp.toString(),
			        element1Timestamp.toString(), FieldKey.RESOLUTION_TIMESTAMP.name(),
			        element2ResolutionTimestamp.toString(), element2ResolutionTimestamp.toString(), this.getClass());
		}
	}
	
}
