/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.issues.tracker.bugzilla;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;
import noNamespace.AssignedToDocument.AssignedTo;
import noNamespace.AttachmentDocument.Attachment;
import noNamespace.LongDescDocument.LongDesc;
import noNamespace.ReporterDocument.Reporter;
import noNamespace.WhoDocument.Who;

import org.jdom2.Element;
import org.joda.time.DateTime;

import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.elements.Type;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.History;
import org.mozkito.issues.tracker.XmlReport;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.datetime.DateTimeUtils;

/**
 * The Class BugzillaParser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class BugzillaParser_4_0_4 extends BugzillaParser {
	
	/** The sibling regex. */
	protected static Regex siblingRegex = new Regex("bug\\s+({sibling}\\d+)");
	
	/**
	 * Instantiates a new bugzilla parser.
	 * 
	 * @param personFactory
	 *            the person factory
	 */
	public BugzillaParser_4_0_4(final PersonFactory personFactory) {
		super(personFactory, new HashSet<String>(Arrays.asList(new String[] { "4.0.4", "4.0.5+" })));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAssignedTo()
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			final AssignedTo assignedTo = getXmlBug().getAssignedTo();
			if ((assignedTo == null) || (assignedTo.getName() == null)) {
				return null;
			}
			final String name = assignedTo.getName().getStringValue();
			final String username = assignedTo.getDomNode().getFirstChild().getNodeValue().toString();
			return getPersonFactory().get(username, name, null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAttachmentEntries()
	 */
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
		
		try {
			final Attachment[] attachmentArray = getXmlBug().getAttachmentArray();
			for (final Attachment attachment : attachmentArray) {
				final AttachmentEntry attachmentEntry = new AttachmentEntry();
				attachmentEntry.setAuthor(getPersonFactory().get(attachment.getAttacher(), null, null));
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
					if (Logger.logWarn()) {
						Logger.warn("Could not interpret generated attachment uri as URL: " + uri + ". Ignoring link.");
					}
				}
				
				attachmentEntry.setMime(attachment.getType());
				try {
					attachmentEntry.setSize(Long.valueOf(attachment.getSize()));
				} catch (final NumberFormatException e) {
					if (Logger.logWarn()) {
						Logger.warn("Could not interpret attachment size as long: " + attachment.getSize()
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
	 * @see org.mozkito.bugs.tracker.Parser#getCategory()
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			return getXmlBug().getClassification();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getDescription()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		final SortedSet<Comment> result = new TreeSet<Comment>();
		try {
			boolean first = true;
			for (final LongDesc longDesc : getXmlBug().getLongDescArray()) {
				if (first) {
					first = false;
					continue;
				}
				try {
					final int id = Integer.valueOf(longDesc.getCommentid()).intValue();
					final Who who = longDesc.getWho();
					final String name = who.getName().getStringValue();
					final String username = who.getDomNode().getFirstChild().getNodeValue();
					final DateTime timestamp = DateTimeUtils.parseDate(longDesc.getBugWhen());
					Person person = null;
					if ((username == null) && (name == null)) {
						person = getPersonFactory().getUnknown();
					} else {
						person = getPersonFactory().get(username, name, null);
					}
					final Comment comment = new Comment(id, person, timestamp, longDesc.getThetext().trim());
					result.add(comment);
					if (Logger.logDebug()) {
						Logger.debug("Created comment for bug report " + getId() + ": " + comment);
					}
				} catch (final NumberFormatException e) {
					if (Logger.logWarn()) {
						Logger.warn("Could not interpret comment id " + longDesc.getCommentid()
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
	 * @see org.mozkito.bugs.tracker.Parser#getComponent()
	 */
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			return getXmlBug().getComponent();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			return DateTimeUtils.parseDate(getXmlBug().getCreationTs());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getProduct()
	 */
	
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			final LongDesc description = getXmlBug().getLongDescArray(0);
			if (description == null) {
				return null;
			}
			return description.getThetext();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolution()
	 */
	
	@Override
	protected BugzillaHistoryParser getHistoryParser() {
		// PRECONDITIONS
		
		try {
			
			if (this.historyParser == null) {
				
				final String uriString = getXmlReport().getUri().toString()
				                                       .replace("show_bug.cgi", "show_activity.cgi");
				try {
					final URI historyUri = new URI(uriString);
					this.historyParser = new BugzillaHistoryParser_4_0_4(historyUri, getId(), getPersonFactory());
				} catch (final Exception e) {
					if (Logger.logError()) {
						Logger.error("Could not fetch bug history from URI `" + uriString + "`.");
					}
				}
			}
			return this.historyParser;
			
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.historyParser, "At this point the historyParser must not be Null.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolutionTimestamp()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getId()
	 */
	@Override
	public String getId() {
		// PRECONDITIONS
		
		try {
			String bugId = null;
			try {
				bugId = getXmlBug().getBugId();
			} catch (final NumberFormatException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			return bugId;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolver()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getKeywords()
	 */
	@Override
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			final String[] keywords = getXmlBug().getKeywordsArray();
			final Set<String> result = new HashSet<String>();
			for (final String keyword : keywords) {
				if ((keyword != null)) {
					final String[] keywordParts = keyword.split(",");
					for (final String kw : keywordParts) {
						if (!kw.trim().isEmpty()) {
							result.add(kw);
						}
					}
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			final SortedSet<Comment> comments = getComments();
			if (comments.isEmpty()) {
				return null;
			}
			return comments.last().getTimestamp();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSeverity()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getPriority()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			return getPriority(getXmlBug().getPriority());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSiblings()
	 */
	
	@Override
	public String getProduct() {
		// PRECONDITIONS
		
		try {
			return getXmlBug().getProduct();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getStatus()
	 */
	
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			return getResolution(getXmlBug().getResolution());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubject()
	 */
	
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		if (!getHistoryParser().parse(getReport().getHistory())) {
			if (Logger.logError()) {
				Logger.error("Could not parse history! See earlier error messages");
			}
		}
		return getHistoryParser().getResolutionTimestamp();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubmitter()
	 */
	
	@Override
	public Person getResolver() {
		if (!getHistoryParser().parse(getReport().getHistory())) {
			if (Logger.logError()) {
				Logger.error("Could not parse history! See earlier error messages");
			}
		}
		return getHistoryParser().getResolver();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
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
	 * @see org.mozkito.bugs.tracker.Parser#getType()
	 */
	
	@Override
	public String getScmFixVersion() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
	 */
	
	@Override
	public Severity getSeverity() {
		// PRECONDITIONS
		
		try {
			return getSeverity(getXmlBug().getBugSeverity());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	
	@Override
	public Set<String> getSiblings() {
		// PRECONDITIONS
		final Set<String> result = new HashSet<String>();
		try {
			// first check for dependencies and blocking issues
			final String[] dependsOn = getXmlBug().getDependsonArray();
			for (final String dep : dependsOn) {
				result.add(dep);
			}
			
			final String[] blockedArray = getXmlBug().getBlockedArray();
			for (final String dep : blockedArray) {
				try {
					result.add(dep);
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Could not intepret bug reference as a Long: " + dep + ". Ignoring sibling.");
					}
				}
			}
			
			final LongDesc[] longDescArray = getXmlBug().getLongDescArray();
			for (final LongDesc dec : longDescArray) {
				final String comment = dec.getThetext();
				final MultiMatch groupsList = BugzillaParser_4_0_4.siblingRegex.findAll(comment);
				if (groupsList != null) {
					for (final Match groups : groupsList) {
						for (final Group group : groups) {
							if ("sibling".equals(group.getName())) {
								result.add(group.getMatch());
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
	 * @see org.mozkito.bugs.tracker.Parser#setXMLReport(org.mozkito.bugs.tracker.XmlReport )
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getStatus()
	 */
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			return getStatus(getXmlBug().getBugStatus());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubject()
	 */
	@Override
	public String getSubject() {
		// PRECONDITIONS
		
		try {
			return getXmlBug().getShortDesc();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubmitter()
	 */
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			final Reporter reporter = getXmlBug().getReporter();
			if ((reporter == null) || (reporter.getName() == null)) {
				return null;
			}
			final String name = reporter.getName().getStringValue();
			final String username = reporter.getDomNode().getFirstChild().getNodeValue().toString();
			return getPersonFactory().get(username, name, null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
	 */
	@Override
	public String getSummary() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getType()
	 */
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			final String bugSeverity = getXmlBug().getBugSeverity();
			if (bugSeverity == null) {
				return null;
			}
			if ("enhancement".equals(bugSeverity.toLowerCase())) {
				return Type.RFE;
			}
			return Type.BUG;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
	 */
	@Override
	public String getVersion() {
		// PRECONDITIONS
		
		try {
			return getXmlBug().getVersion();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public void parseHistoryElements(final History history) {
		// PRECONDITIONS
		
		try {
			if (!getHistoryParser().parse(history)) {
				if (Logger.logError()) {
					Logger.error("Could not parse history! See earlier error messages");
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
}
