/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import noNamespace.AssignedToDocument.AssignedTo;
import noNamespace.AttachmentDocument.Attachment;
import noNamespace.BugDocument.Bug;
import noNamespace.BugzillaDocument;
import noNamespace.BugzillaDocument.Bugzilla;
import noNamespace.LongDescDocument.LongDesc;
import noNamespace.ReporterDocument.Reporter;
import noNamespace.WhoDocument.Who;

import org.apache.xmlbeans.XmlException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * The Class BugzillaParser.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class BugzillaParser implements Parser {
	
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
	 * @param string
	 * @return
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
		} else {
			return Resolution.UNKNOWN;
		}
	}
	
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
	 * @param string
	 * @return
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
	
	/** The xml report. */
	private XmlReport             xmlReport;
	
	/** The xml bug. */
	private Bug                   xmlBug;
	
	/** The tracker. */
	private final BugzillaTracker tracker;
	
	/** The sibling regex. */
	protected static Regex        siblingRegex = new Regex("bug\\s+({sibling}\\d+)");
	
	private BugzillaHistoryParser historyParser;
	
	/**
	 * Instantiates a new bugzilla parser.
	 * 
	 * @param tracker
	 *            the tracker
	 */
	public BugzillaParser(final BugzillaTracker tracker) {
		this.tracker = tracker;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAssignedTo()
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			final AssignedTo assignedTo = this.xmlBug.getAssignedTo();
			final String name = assignedTo.getName().getStringValue();
			final String username = assignedTo.getDomNode().getFirstChild().getNodeValue().toString();
			return new Person(username, name, null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAttachmentEntries()
	 */
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
		
		try {
			final Attachment[] attachmentArray = this.xmlBug.getAttachmentArray();
			for (final Attachment attachment : attachmentArray) {
				final AttachmentEntry attachmentEntry = new AttachmentEntry();
				attachmentEntry.setAuthor(new Person(attachment.getAttacher(), null, null));
				attachmentEntry.setDeltaTS(DateTimeUtils.parseDate(attachment.getDeltaTs()));
				attachmentEntry.setDescription(attachment.getDesc());
				attachmentEntry.setFilename(attachment.getFilename());
				attachmentEntry.setId(attachment.getAttachid());
				
				String uri = this.tracker.getUri().toString();
				if (!uri.endsWith("/")) {
					uri += "/";
				}
				uri += "attachment.cgi?id=" + attachmentEntry.getId();
				
				try {
					attachmentEntry.setLink(new URL(uri));
				} catch (final MalformedURLException e1) {
					if (Logger.logError()) {
						Logger.error("Could not interpret generated attachment uri as URL: " + uri + ". Ignoring link.");
					}
				}
				
				attachmentEntry.setMime(attachment.getType());
				try {
					attachmentEntry.setSize(Long.valueOf(attachment.getSize()));
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Could not interpret attachment size as long: " + attachment.getSize()
						        + ". Ignoring field.");
					}
				}
				attachmentEntry.setTimestamp(DateTimeUtils.parseDate(attachment.getDate()));
				result.add(attachmentEntry);
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getDescription()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCategory()
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			return this.xmlBug.getClassification();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		final SortedSet<Comment> result = new TreeSet<Comment>();
		try {
			boolean first = true;
			for (final LongDesc longDesc : this.xmlBug.getLongDescArray()) {
				if (first) {
					first = false;
					continue;
				}
				try {
					System.err.println(longDesc.getCommentid());
					final int id = Integer.valueOf(longDesc.getCommentid()).intValue();
					final Who who = longDesc.getWho();
					final String name = who.getName().getStringValue();
					final String username = who.getDomNode().getFirstChild().getNodeValue();
					final DateTime timestamp = DateTimeUtils.parseDate(longDesc.getBugWhen());
					final Comment comment = new Comment(id, new Person(username, name, null), timestamp,
					                                    longDesc.getThetext().trim());
					result.add(comment);
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Could not interpret comment id " + longDesc.getCommentid()
						        + " as integer. Comment got ignored.");
					}
					continue;
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(result, "Return an empty SortedSet but not null.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComponent()
	 */
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			return this.xmlBug.getComponent();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getProduct()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			return DateTimeUtils.parseDate(this.xmlBug.getCreationTs());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolution()
	 */
	
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			final LongDesc description = this.xmlBug.getLongDescArray(0);
			return description.getThetext();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolutionTimestamp()
	 */
	
	@Override
	public SortedSet<HistoryElement> getHistoryElements() {
		// PRECONDITIONS
		
		try {
			parseHistory();
			return this.historyParser.getHistory();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolver()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getId()
	 */
	@Override
	public Long getId() {
		// PRECONDITIONS
		
		try {
			Long bugId = null;
			try {
				bugId = Long.valueOf(this.xmlBug.getBugId());
			} catch (final NumberFormatException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
			return bugId;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			final SortedSet<Comment> comments = getComments();
			return comments.last().getTimestamp();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSeverity()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getPriority()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			return getPriority(this.xmlBug.getPriority());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSiblings()
	 */
	
	@Override
	public String getProduct() {
		// PRECONDITIONS
		
		try {
			return this.xmlBug.getProduct();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getStatus()
	 */
	
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			return getResolution(this.xmlBug.getResolution());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubject()
	 */
	
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		
		try {
			parseHistory();
			return this.historyParser.getResolutionTimestamp();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
	 */
	
	@Override
	public Person getResolver() {
		// PRECONDITIONS
		
		try {
			parseHistory();
			return this.historyParser.getResolver();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
	 */
	
	/**
	 * Gets the root element.
	 * 
	 * @param rawReport
	 *            the raw report
	 * @return the root element
	 */
	protected Element getRootElement(@NotNull final XmlReport rawReport) {
		return rawReport.getDocument().getRootElement().getChild("bug");
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
	 */
	
	@Override
	public Severity getSeverity() {
		// PRECONDITIONS
		
		try {
			return getSeverity(this.xmlBug.getBugSeverity());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
	 */
	
	@Override
	public Set<Long> getSiblings() {
		// PRECONDITIONS
		final Set<Long> result = new HashSet<Long>();
		try {
			// first check for dependencies and blocking bugs
			final String[] dependsOn = this.xmlBug.getDependsonArray();
			for (final String dep : dependsOn) {
				try {
					result.add(Long.valueOf(dep));
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Could not intepret bug reference as a Long: " + dep + ". Ignoring sibling.");
					}
				}
			}
			
			final String[] blockedArray = this.xmlBug.getBlockedArray();
			for (final String dep : blockedArray) {
				try {
					result.add(Long.valueOf(dep));
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Could not intepret bug reference as a Long: " + dep + ". Ignoring sibling.");
					}
				}
			}
			
			final LongDesc[] longDescArray = this.xmlBug.getLongDescArray();
			for (final LongDesc dec : longDescArray) {
				final String comment = dec.getThetext();
				final List<List<RegexGroup>> groupsList = siblingRegex.findAll(comment);
				if (groupsList != null) {
					for (final List<RegexGroup> groups : groupsList) {
						for (final RegexGroup group : groups) {
							if (group.getName().equals("sibling")) {
								try {
									final Long sibling = Long.valueOf(group.getMatch());
									result.add(sibling);
								} catch (final NumberFormatException e) {
									Logger.error("Could not intepret bug reference as a Long: " + group.getMatch()
									        + ". Ignoring sibling.");
								}
							}
						}
					}
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(result, "You should return an empty set instead of NULL.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
	 */
	
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			return getStatus(this.xmlBug.getBugStatus());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setXMLReport(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport
	 * )
	 */
	
	@Override
	public String getSubject() {
		// PRECONDITIONS
		
		try {
			return this.xmlBug.getShortDesc();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			final Reporter reporter = this.xmlBug.getReporter();
			final String name = reporter.getName().getStringValue();
			final String username = reporter.getDomNode().getFirstChild().getNodeValue().toString();
			return new Person(username, name, null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public String getSummary() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			final String bugSeverity = this.xmlBug.getBugSeverity().toLowerCase();
			if (bugSeverity.equals("enhancement")) {
				return Type.RFE;
			}
			return Type.BUG;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public String getVersion() {
		// PRECONDITIONS
		
		try {
			return this.xmlBug.getVersion();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	private void parseHistory() {
		try {
			this.historyParser.parse();
		} catch (final SecurityException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
		} catch (final UnsupportedProtocolException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
		} catch (final FetchException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
		} catch (final NoSuchFieldException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		
		if (!this.historyParser.hasParsed()) {
			if (Logger.logError()) {
				Logger.error("Could not parse history for bug report " + getId());
			}
		}
	}
	
	@Override
	public void setTracker(final Tracker tracker) {
		// PRECONDITIONS
		
		try {
		} finally {
			// POSTCONDITIONS
		}
	}
	
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
		
		final String uriString = this.xmlReport.getUri().toString().replace("show_bug.cgi", "show_activity.cgi");
		try {
			final URI historyUri = new URI(uriString);
			this.historyParser = new BugzillaHistoryParser(historyUri, getId());
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error("Could not fetch bug history from URI `" + uriString + "`.");
			}
		}
	}
	
}
