package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import noNamespace.BugDocument.Bug;
import noNamespace.BugzillaDocument;
import noNamespace.BugzillaDocument.Bugzilla;

import org.apache.xmlbeans.XmlException;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;

/**
 * The Class BugzillaParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public abstract class BugzillaParser implements Parser {
	
	/** The Constant parserVersions. */
	private static final Map<String, BugzillaParser> parserVersions = new HashMap<String, BugzillaParser>();
	
	/**
	 * Gets the parser.
	 * 
	 * @param bugzillaVersion
	 *            the bugzilla version
	 * @return the parser. If no parser for version exists this method will return NULL.
	 */
	@NoneNull
	public static BugzillaParser getParser(final String bugzillaVersion) {
		if (!parserVersions.containsKey(bugzillaVersion)) {
			if (Logger.logError()) {
				Logger.error("Bugzilla version " + bugzillaVersion
				        + " not yet supported! Please contact moskito dev team.");
			}
		}
		return parserVersions.get(bugzillaVersion);
	}
	
	/**
	 * Gets the priority.
	 * 
	 * @param string
	 *            the string
	 * @return the priority
	 */
	protected static Priority getPriority(final String string) {
		final String priorityString = string.toUpperCase();
		if (priorityString.equals("P1")) {
			return Priority.VERY_HIGH;
		} else if (priorityString.equals("P2")) {
			return Priority.HIGH;
		} else if (priorityString.equals("P3")) {
			return Priority.NORMAL;
		} else if (priorityString.equals("P4")) {
			return Priority.LOW;
		} else if (priorityString.equals("P5")) {
			return Priority.VERY_LOW;
		} else {
			return Priority.UNKNOWN;
		}
	}
	
	/**
	 * Gets the resolution.
	 * 
	 * @param string
	 *            the string
	 * @return the resolution
	 */
	protected static Resolution getResolution(final String string) {
		final String resString = string.toUpperCase();
		if (resString.equals("FIXED")) {
			return Resolution.RESOLVED;
		} else if (resString.equals("INVALID")) {
			return Resolution.INVALID;
		} else if (resString.equals("WONTFIX")) {
			return Resolution.WONT_FIX;
		} else if (resString.equals("LATER")) {
			return Resolution.UNRESOLVED;
		} else if (resString.equals("REMIND")) {
			return Resolution.UNRESOLVED;
		} else if (resString.equals("DUPLICATE")) {
			return Resolution.DUPLICATE;
		} else if (resString.equals("WORKSFORME")) {
			return Resolution.WORKS_FOR_ME;
		} else if (resString.equals("DUPLICATE")) {
			return Resolution.DUPLICATE;
		} else if (resString.equals("NOT_ECLIPSE")) {
			return Resolution.INVALID;
		} else if (resString.equals("") || resString.equals("---")) {
			return Resolution.UNRESOLVED;
		} else {
			return Resolution.UNKNOWN;
		}
	}
	
	/**
	 * Gets the severity.
	 * 
	 * @param string
	 *            the string
	 * @return the severity
	 */
	protected static Severity getSeverity(final String string) {
		final String serverityString = string.toLowerCase();
		if (serverityString.equals("blocker")) {
			return Severity.BLOCKER;
		} else if (serverityString.equals("critical")) {
			return Severity.CRITICAL;
		} else if (serverityString.equals("major")) {
			return Severity.MAJOR;
		} else if (serverityString.equals("normal")) {
			return Severity.NORMAL;
		} else if (serverityString.equals("minor")) {
			return Severity.MINOR;
		} else if (serverityString.equals("trivial")) {
			return Severity.TRIVIAL;
		} else if (serverityString.equals("enhancement")) {
			return Severity.ENHANCEMENT;
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Bugzilla severity `" + serverityString + "` could not be mapped. Ignoring it.");
			}
			return null;
		}
	}
	
	/**
	 * Gets the status.
	 * 
	 * @param string
	 *            the string
	 * @return the status
	 */
	protected static Status getStatus(final String string) {
		final String statusString = string.toUpperCase();
		if (statusString.equals("UNCONFIRMED")) {
			return Status.UNCONFIRMED;
		} else if (statusString.equals("NEW")) {
			return Status.NEW;
		} else if (statusString.equals("ASSIGNED")) {
			return Status.ASSIGNED;
		} else if (statusString.equals("REOPENED")) {
			return Status.REOPENED;
		} else if (statusString.equals("RESOLVED")) {
			return Status.CLOSED;
		} else if (statusString.equals("VERIFIED")) {
			return Status.VERIFIED;
		} else if (statusString.equals("CLOSED")) {
			return Status.CLOSED;
		} else {
			return Status.UNKNOWN;
		}
	}
	
	/** The supported versions. */
	private final Set<String> supportedVersions;
	private XmlReport         xmlReport;
	private Bug               xmlBug;
	
	/**
	 * Instantiates a new bugzilla parser.
	 * 
	 * @param supportedVersions
	 *            the supported versions
	 */
	public BugzillaParser(final Set<String> supportedVersions) {
		this.supportedVersions = supportedVersions;
		for (final String supportedVersion : supportedVersions) {
			if (!parserVersions.containsKey(supportedVersion)) {
				parserVersions.put(supportedVersion, this);
			}
		}
	}
	
	protected abstract BugzillaHistoryParser getHistoryParser();
	
	/**
	 * Gets the supoorted versions.
	 * 
	 * @return the supoorted versions
	 */
	public final Set<String> getSupoortedVersions() {
		return this.supportedVersions;
	}
	
	public Bug getXmlBug() {
		return this.xmlBug;
	}
	
	public XmlReport getXmlReport() {
		return this.xmlReport;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setXMLReport(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport
	 * )
	 */
	@Override
	public void setXMLReport(@NotNull final XmlReport report) {
		// PRECONDITIONS
		this.xmlReport = report;
		try {
			final BugzillaDocument document = BugzillaDocument.Factory.parse(report.getContent());
			final Bugzilla bugzilla = document.getBugzilla();
			final Bug[] bugArray = bugzilla.getBugArray();
			
			if (bugArray.length < 1) {
				if (Logger.logWarn()) {
					Logger.warn("XML document contains no bugzilla bug reports.");
				}
				this.xmlBug = null;
				return;
			} else if (bugArray.length > 1) {
				if (Logger.logWarn()) {
					Logger.warn("XML document contains multiple bugzilla bug reports. This is unexpected. Parsing only first report.");
				}
			}
			this.xmlBug = bugArray[0];
		} catch (final XmlException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.xmlReport, "Our source data set may never be null.");
			Condition.notNull(this.xmlBug, "Our xmlBug instance may never be null.");
		}
	}
	
	/**
	 * Supports version.
	 * 
	 * @param version
	 *            the version
	 * @return true, if successful
	 */
	@NoneNull
	public final boolean supportsVersion(final String version) {
		return this.supportedVersions.contains(version);
	}
	
}
