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
package org.mozkito.issues.tracker.sourceforge;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.MimeUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.MIMETypeDeterminationException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.elements.Priority;
import org.mozkito.issues.tracker.elements.Resolution;
import org.mozkito.issues.tracker.elements.Severity;
import org.mozkito.issues.tracker.elements.Status;
import org.mozkito.issues.tracker.elements.Type;
import org.mozkito.issues.tracker.model.AttachmentEntry;
import org.mozkito.issues.tracker.model.Comment;
import org.mozkito.issues.tracker.model.History;
import org.mozkito.issues.tracker.model.HistoryElement;
import org.mozkito.issues.tracker.model.IssueTracker;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.persistence.model.Person;

/**
 * The Class SourceForgeParser.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class SourceforgeParser implements Parser {
	
	/**
	 * The Class AttachmentHistoryEntry.
	 * 
	 * @author Kim Herzig <herzig@mozkito.org>
	 */
	private class AttachmentHistoryEntry {
		
		/** The author. */
		private final Person   author;
		
		/** The timestamp. */
		private final DateTime timestamp;
		
		/**
		 * Instantiates a new attachment history entry.
		 * 
		 * @param author
		 *            the author
		 * @param timestamp
		 *            the timestamp
		 */
		public AttachmentHistoryEntry(final Person author, final DateTime timestamp) {
			this.author = author;
			this.timestamp = timestamp;
		}
		
		/**
		 * Gets the author.
		 * 
		 * @return the author
		 */
		public Person getAuthor() {
			return this.author;
		}
		
		/**
		 * Gets the timestamp.
		 * 
		 * @return the timestamp
		 */
		public DateTime getTimestamp() {
			return this.timestamp;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAssignedTo()
	 */
	
	/**
	 * Builds the priority.
	 * 
	 * @param value
	 *            the value
	 * @return the priority
	 */
	private static Priority resolvePriority(final String value) {
		// 1..9;
		// UNKNOWN, VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH;
		@SuppressWarnings ("serial")
		final Map<String, Priority> PRIORITY_MAP = new HashMap<String, Priority>() {
			
			{
				put("1", Priority.VERY_LOW);
				put("2", Priority.VERY_LOW);
				put("3", Priority.LOW);
				put("4", Priority.LOW);
				put("5", Priority.NORMAL);
				put("6", Priority.HIGH);
				put("7", Priority.HIGH);
				put("8", Priority.VERY_HIGH);
				put("9", Priority.VERY_HIGH);
			}
		};
		
		if (PRIORITY_MAP.containsKey(value)) {
			return PRIORITY_MAP.get(value);
		}
		return Priority.UNKNOWN;
	}
	
	/**
	 * Builds the resolution.
	 * 
	 * @param value
	 *            the value
	 * @return the resolution
	 */
	private static Resolution resolveResolution(final String value) {
		// from: ACCEPTED, DUPLICATE, FIXED, INVALID, LATER, NONE,
		// OUT_OF_DATE, POSTPONED, REJECTED, REMIND, WONT_FIX,
		// WORKS_FOR_ME;
		// to: UNKNOWN, UNRESOLVED, DUPLICATE, RESOLVED, INVALID,
		// WONT_FIX, WORKS_FOR_ME;
		switch (value.toUpperCase()) {
			case "ACCEPTED":
				return Resolution.UNRESOLVED;
			case "DUPLICATE":
				return Resolution.DUPLICATE;
			case "FIXED":
				return Resolution.RESOLVED;
			case "INVALID":
				return Resolution.INVALID;
			case "LATER":
				return Resolution.UNRESOLVED;
			case "NONE":
				return Resolution.UNRESOLVED;
			case "OUT_OF_DATE":
				return Resolution.UNKNOWN;
			case "POSTPONED":
				return Resolution.UNRESOLVED;
			case "REJECTED":
				return Resolution.INVALID;
			case "REMIND":
				return Resolution.UNRESOLVED;
			case "WONT_FIX":
				return Resolution.WONT_FIX;
			case "WORKS_FOR_ME":
				return Resolution.WORKS_FOR_ME;
			default:
				return Resolution.UNKNOWN;
		}
	}
	
	/**
	 * Builds the status.
	 * 
	 * @param value
	 *            the value
	 * @return the status
	 */
	private static Status resolveStatus(final String value) {
		// from: CLOSED, DELETED, OPEN, PENDING
		// to: UNKNOWN, UNCONFIRMED, NEW, ASSIGNED, IN_PROGRESS,
		// REOPENED, RESOLVED, VERIFIED, CLOSED
		switch (value.toUpperCase()) {
			case "CLOSED":
				return Status.CLOSED;
			case "DELETED":
				return Status.CLOSED;
			case "OPEN":
				return Status.NEW;
			case "PENDING":
				return Status.IN_PROGRESS;
			default:
				return Status.UNKNOWN;
		}
	}
	
	/** The main element. */
	private Element                                   mainElement;
	
	/** The right g box. */
	private Element                                   rightGBox;
	
	/** The g box. */
	private Element                                   gBox;
	
	/** The header box. */
	private Element                                   headerBox;
	
	/** The left g box. */
	private Element                                   leftGBox;
	
	/** The submitted regex. */
	private static Regex                              submittedRegex         = new Regex(
	                                                                                     "({fullname}[^(]+)\\(\\s+({username}[^\\s]+)\\s+\\)\\s+-\\s+({timestamp}\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}.*)");
	
	/** The comment regex. */
	private static Regex                              commentRegex           = new Regex(
	                                                                                     "Date:\\s+({timestamp}\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}.*).*Sender:\\s+({username}.*)");
	
	/** The html comment regex. */
	protected static Regex                            htmlCommentRegex       = new Regex(
	                                                                                     "(?#special condition to check wether we got a new line after or before the match to remove one of them)(?(?=<!--.*?-->$\\s)(?#used if condition was true)<!--.*?-->\\s|(?#used if condition was false)\\s?<!--.*?-->)");
	
	/** The fetch time. */
	private DateTime                                  fetchTime;
	
	/** The file id pattern. */
	protected static Regex                            fileIdPattern          = new Regex("file_id=({fileid}\\d+)");
	
	/** The subject regex. */
	private final Regex                               subjectRegex           = new Regex(
	                                                                                     "\\s*\\d\\s*({subject}.*)\\s+-\\s+ID:\\s+({bugid}\\d+)$");
	
	/** The artifact comment id regex. */
	private final Regex                               artifactCommentIdRegex = new Regex(
	                                                                                     "artifact_comment_({comment_id}\\d+)");
	
	/** The last update timestamp. */
	private DateTime                                  lastUpdateTimestamp;
	
	/** The resolution timestamp. */
	private DateTime                                  resolutionTimestamp;
	
	/** The resolver. */
	private Person                                    resolver;
	
	/** The history. */
	private SortedSet<HistoryElement>                 historyElements        = null;
	
	/** The attachment history. */
	private final Map<String, AttachmentHistoryEntry> attachmentHistory      = new HashMap<String, AttachmentHistoryEntry>();
	
	/** The comment table. */
	private Element                                   commentTable;
	
	/** The attachment table container. */
	private Element                                   attachmentTableContainer;
	
	/** The history table container. */
	private Element                                   historyTableContainer;
	
	/** The tracker. */
	private Tracker                                   tracker;
	
	/** The bug type. */
	private final Type                                bugType;
	
	/** The md5. */
	private byte[]                                    md5;
	
	/** The report. */
	private Report                                    report;
	
	/**
	 * Instantiates a new sourceforge parser.
	 * 
	 * @param bugType
	 *            the bug type
	 */
	@NoneNull
	public SourceforgeParser(final Type bugType) {
		this.bugType = bugType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAssignedTo()
	 */
	/**
	 * Gets the assigned to.
	 * 
	 * @return the assigned to
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.rightGBox.children()) {
				if ("label".equals(child.tag().getName()) && "Assigned:".equals(child.text().trim())) {
					final String name = child.nextElementSibling().text();
					return new Person(null, name, null);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAttachmentEntries()
	 */
	/**
	 * Gets the attachment entries.
	 * 
	 * @return the attachment entries
	 */
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		try {
			final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
			
			parseHistoryElements(this.report.getHistory());
			
			final Elements tables = this.attachmentTableContainer.getElementsByTag("table");
			if (tables.isEmpty()) {
				return result;
			}
			final Element table = tables.get(0);
			final Element tbody = table.getElementsByTag("tbody").get(0);
			for (final Element tr : tbody.getElementsByTag("tr")) {
				final Elements tds = tr.getElementsByTag("td");
				final int TD_SIZE = 3;
				if (tds.size() < TD_SIZE) {
					continue;
				}
				final String filename = SourceforgeParser.htmlCommentRegex.removeAll(tds.get(0).text())
				                                                          .replaceAll("\"", "").trim();
				final String description = SourceforgeParser.htmlCommentRegex.removeAll(tds.get(1).text())
				                                                             .replaceAll("\"", "").trim();
				final Element linkTd = tds.get(2);
				final Elements aTags = linkTd.getElementsByTag("a");
				if (aTags.isEmpty()) {
					continue;
				}
				String link = aTags.get(0).attr("href");
				String attachId = null;
				for (final Group group : SourceforgeParser.fileIdPattern.find(link)) {
					if ((group.getName() != null) && ("fileid".equals(group.getName()))) {
						attachId = group.getMatch().trim();
					}
				}
				if (attachId == null) {
					continue;
				}
				link = this.tracker.getUri() + link;
				
				final AttachmentHistoryEntry attachmentHistoryEntry = this.attachmentHistory.get(filename);
				
				final AttachmentEntry attachmentEntry = new AttachmentEntry(attachId);
				if (attachmentHistoryEntry != null) {
					attachmentEntry.setAuthor(attachmentHistoryEntry.getAuthor());
					attachmentEntry.setTimestamp(attachmentHistoryEntry.getTimestamp());
				}
				attachmentEntry.setDescription(description);
				attachmentEntry.setFilename(filename);
				attachmentEntry.setLink(link);
				try {
					attachmentEntry.setMime(MimeUtils.determineMIME(new URI(link)));
				} catch (final MIMETypeDeterminationException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final UnsupportedProtocolException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final FetchException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final URISyntaxException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
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
	/**
	 * Gets the category.
	 * 
	 * @return the category
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.rightGBox.children()) {
				if ("label".equals(child.tag().getName()) && "Category:".equals(child.text().trim())) {
					return child.nextElementSibling().text().trim();
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComments()
	 */
	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		
		try {
			final SortedSet<Comment> result = new TreeSet<Comment>();
			for (final Element tr : this.commentTable.getElementsByTag("tr")) {
				final Match commentIdMatch = this.artifactCommentIdRegex.find(tr.attr("id"));
				if (commentIdMatch == null) {
					continue;
				}
				int commentId = -1;
				for (final Group group : commentIdMatch) {
					if ((group.getName() != null) && ("comment_id".equals(group.getName()))) {
						commentId = Integer.valueOf(group.getMatch().trim()).intValue();
					}
				}
				
				final Elements elementsByClass = tr.getElementsByClass("yui-u");
				if ((elementsByClass == null) || (elementsByClass.isEmpty())) {
					continue;
				}
				Element yui_u_first = null;
				for (final Element tmp : elementsByClass) {
					if (tmp.classNames().contains("first")) {
						yui_u_first = tmp;
						break;
					}
				}
				if (yui_u_first == null) {
					continue;
				}
				final Element yui_u = yui_u_first.nextElementSibling();
				
				final String leftColumm = yui_u_first.text().replaceAll("\"", "").trim();
				final String rightColumm = SourceforgeParser.htmlCommentRegex.removeAll(yui_u.text())
				                                                             .replaceAll("\"", "").trim();
				final Match find = SourceforgeParser.commentRegex.find(leftColumm);
				DateTime timestamp = null;
				Person sender = null;
				for (final Group group : find) {
					if ((group.getName() != null) && ("timestamp".equals(group.getName()))) {
						timestamp = DateTimeUtils.parseDate(group.getMatch());
					} else if ((group.getName() != null) && ("username".equals(group.getName()))) {
						sender = new Person(group.getMatch(), null, null);
					}
				}
				if ((timestamp != null) || (sender != null)) {
					result.add(new Comment(commentId, sender, timestamp, rightColumm));
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComponent()
	 */
	/**
	 * Gets the component.
	 * 
	 * @return the component
	 */
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	/**
	 * Gets the creation timestamp.
	 * 
	 * @return the creation timestamp
	 */
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if ("label".equals(child.tag().getName()) && "Submitted:".equals(child.text().trim())) {
					final Element pElement = child.nextElementSibling();
					final MultiMatch findAll = SourceforgeParser.submittedRegex.findAll(pElement.text().trim());
					if (findAll != null) {
						final Match groups = findAll.getMatch(0);
						String dateStr = null;
						for (final Group group : groups) {
							if ((group.getName() != null) && ("timestamp".equals(group.getName()))) {
								dateStr = group.getMatch().trim();
								break;
							}
						}
						if (dateStr != null) {
							return DateTimeUtils.parseDate(dateStr);
						}
					}
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getDescription()
	 */
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.gBox.children()) {
				if ("label".equals(child.tag().getName()) && "Details:".equals(child.text().trim())) {
					final String description = child.nextElementSibling().text().trim();
					return SourceforgeParser.htmlCommentRegex.removeAll(description);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getHistoryElement(int)
	 */
	/**
	 * Gets the fetch time.
	 * 
	 * @return the fetch time
	 */
	@Override
	public DateTime getFetchTime() {
		// PRECONDITIONS
		
		try {
			return this.fetchTime;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getNumberOfComments()
	 */
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Override
	public String getId() {
		// PRECONDITIONS
		
		try {
			final MultiMatch findAll = this.subjectRegex.findAll(this.headerBox.text());
			if (findAll != null) {
				final Group[] groups = findAll.getGroup("bugid");
				if (groups.length > 0) {
					return groups[0].getMatch().trim();
				}
			}
			
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getPriority()
	 */
	/**
	 * Gets the keywords.
	 * 
	 * @return the keywords
	 */
	@Override
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			return new HashSet<String>();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getProduct()
	 */
	/**
	 * Gets the last update timestamp.
	 * 
	 * @return the last update timestamp
	 */
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			parseHistoryElements(this.report.getHistory());
			return this.lastUpdateTimestamp;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getMd5()
	 */
	/**
	 * Gets the md5.
	 * 
	 * @return the md5
	 */
	@Override
	public final byte[] getMd5() {
		return this.md5;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolver()
	 */
	/**
	 * Gets the priority.
	 * 
	 * @return the priority
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if ("label".equals(child.tag().getName()) && "Priority:".equals(child.text().trim())) {
					final String priorityStr = child.nextElementSibling().text();
					return resolvePriority(priorityStr);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSeverity()
	 */
	/**
	 * Gets the product.
	 * 
	 * @return the product
	 */
	@Override
	public String getProduct() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSiblings()
	 */
	/**
	 * Gets the resolution.
	 * 
	 * @return the resolution
	 */
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if ("label".equals(child.tag().getName()) && "Resolution:".equals(child.text().trim())) {
					final String str = child.nextElementSibling().text();
					return resolveResolution(str);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getStatus()
	 */
	/**
	 * Gets the resolution timestamp.
	 * 
	 * @return the resolution timestamp
	 */
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		
		try {
			parseHistoryElements(this.report.getHistory());
			return this.resolutionTimestamp;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubject()
	 */
	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	@Override
	public Person getResolver() {
		// PRECONDITIONS
		
		try {
			parseHistoryElements(this.report.getHistory());
			return this.resolver;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubmitter()
	 */
	/**
	 * Gets the scm fix version.
	 * 
	 * @return the scm fix version
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
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
	 */
	/**
	 * Gets the severity.
	 * 
	 * @return the severity
	 */
	@Override
	public Severity getSeverity() {
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
	/**
	 * Gets the siblings.
	 * 
	 * @return the siblings
	 */
	@Override
	public Set<String> getSiblings() {
		// PRECONDITIONS
		
		try {
			return new HashSet<String>();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
	 */
	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if ("label".equals(child.tag().getName()) && "Status:".equals(child.text().trim())) {
					final String str = child.nextElementSibling().text();
					return resolveStatus(str);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	@Override
	public String getSubject() {
		// PRECONDITIONS
		
		try {
			final MultiMatch findAll = this.subjectRegex.findAll(this.headerBox.text());
			if (findAll != null) {
				final Group[] groups = findAll.getGroup("subject");
				if (groups.length > 0) {
					return groups[0].getMatch().trim();
				}
			}
			
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setXMLReport(org.mozkito.bugs.tracker.XmlReport )
	 */
	/**
	 * Gets the submitter.
	 * 
	 * @return the submitter
	 */
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if ("label".equals(child.tag().getName()) && "Submitted:".equals(child.text().trim())) {
					final Element pElement = child.nextElementSibling();
					final MultiMatch multiMatch = SourceforgeParser.submittedRegex.findAll(pElement.text().trim());
					if (multiMatch != null) {
						String name = null;
						String uname = null;
						final Match groups = multiMatch.getMatch(0);
						for (final Group group : groups) {
							if ((group.getName() != null) && ("fullname".equals(group.getName()))) {
								name = group.getMatch().trim();
							} else if ((group.getName() != null) && ("username".equals(group.getName()))) {
								uname = group.getMatch().trim();
							}
						}
						if ((uname != null) || (name != null)) {
							return new Person(uname, name, null);
						}
					}
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
	 */
	/**
	 * Gets the summary.
	 * 
	 * @return the summary
	 */
	@Override
	public String getSummary() {
		// PRECONDITIONS
		
		try {
			return getSubject();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getType()
	 */
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			return this.bugType;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
	 */
	/**
	 * Gets the version.
	 * 
	 * @return the version
	 */
	@Override
	public String getVersion() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getHistoryElements()
	 */
	/**
	 * Parses the history elements.
	 * 
	 * @param history
	 *            the history
	 */
	@Override
	public void parseHistoryElements(final History history) {
		// PRECONDITIONS
		
		try {
			if (this.historyElements == null) {
				this.historyElements = new TreeSet<HistoryElement>();
				
				HistoryElement lastHistoryElement = null;
				Person assignedTo = getAssignedTo();
				String category = getCategory();
				Resolution resolution = getResolution();
				Status status = getStatus();
				Priority priority = getPriority();
				String summary = getSummary();
				
				for (final Element tr : this.historyTableContainer.getElementsByTag("tr")) {
					final Elements tds = tr.getElementsByTag("td");
					final int TD_SIZE = 4;
					if (tds.size() < TD_SIZE) {
						continue;
					}
					final String fieldname = tds.get(0).text().trim();
					final String oldValue = tds.get(1).text().trim();
					final DateTime timestamp = DateTimeUtils.parseDate(tds.get(2).text().trim());
					final int A_ELEMENT_INDEX = 3;
					final Element aElem = tds.get(A_ELEMENT_INDEX).child(0);
					final Person author = new Person(aElem.text().trim(), aElem.attr("title"), null);
					
					if (this.lastUpdateTimestamp == null) {
						this.lastUpdateTimestamp = timestamp;
					}
					
					if ((lastHistoryElement == null) || (!lastHistoryElement.getTimestamp().isEqual(timestamp))) {
						final HistoryElement newHistoryElement = new HistoryElement(history, author, timestamp);
						if ((lastHistoryElement != null) && (!lastHistoryElement.isEmpty())) {
							this.historyElements.add(lastHistoryElement);
						}
						lastHistoryElement = newHistoryElement;
					}
					
					switch (fieldname) {
						case "summary":
							lastHistoryElement.addChangedValue("summary", oldValue, summary);
							summary = oldValue;
							break;
						case "assigned_to":
							if (assignedTo == null) {
								assignedTo = Tracker.UNKNOWN_PERSON;
							}
							final Person oldAssignedTo = new Person(oldValue, null, null);
							lastHistoryElement.addChangedValue("assignedTo", oldAssignedTo, assignedTo);
							assignedTo = oldAssignedTo;
							break;
						case "category_id":
							lastHistoryElement.addChangedValue("category", oldValue, category);
							category = oldValue;
							break;
						case "resolution_id":
							final Resolution oldResolution = resolveResolution(oldValue);
							lastHistoryElement.addChangedValue("resolution", oldResolution, resolution);
							resolution = oldResolution;
							if (resolution.equals(Resolution.RESOLVED) && (this.resolver == null)) {
								this.resolver = author;
								this.resolutionTimestamp = timestamp;
							}
							break;
						case "status_id":
							final Status oldStatus = resolveStatus(oldValue);
							lastHistoryElement.addChangedValue("status", oldStatus, status);
							status = oldStatus;
							break;
						case "priority":
							final Priority oldPriority = resolvePriority(oldValue);
							lastHistoryElement.addChangedValue("priority", oldPriority, priority);
							priority = oldPriority;
							break;
						case "File Added":
							final String[] split = oldValue.split(":");
							String filename = split[0];
							if (split.length > 1) {
								filename = split[1];
							}
							if (!this.attachmentHistory.containsKey(filename)) {
								this.attachmentHistory.put(filename.trim(), new AttachmentHistoryEntry(author,
								                                                                       timestamp));
							}
							break;
						default:
							break;
					}
				}
				if ((lastHistoryElement != null) && (!lastHistoryElement.isEmpty())) {
					this.historyElements.add(lastHistoryElement);
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setURI(org.mozkito.bugs.tracker.ReportLink)
	 */
	/**
	 * Sets the context.
	 * 
	 * @param issueTracker
	 *            the issue tracker
	 * @param reportLink
	 *            the report link
	 * @return the report
	 */
	@Override
	public Report setContext(final IssueTracker issueTracker,
	                         final ReportLink reportLink) {
		// PRECONDITIONS
		
		try {
			RawContent rawContent = null;
			
			rawContent = IOUtils.fetch(reportLink.getUri());
			
			this.fetchTime = new DateTime();
			
			this.md5 = DigestUtils.md5(rawContent.getContent());
			
			final Document document = Jsoup.parse(rawContent.getContent());
			final Elements errorElements = document.getElementsByClass("error");
			if (!errorElements.isEmpty()) {
				for (final Element errorElem : errorElements) {
					if (("h4".equals(errorElem.tag().getName()))
					        && ("Error".equals(errorElem.text().trim().replaceAll("\"", "").trim()))) {
						return null;
					}
				}
			}
			this.mainElement = document.getElementById("yui-main");
			if (this.mainElement == null) {
				if (Logger.logError()) {
					Logger.error("Could not find main element: <div id=\"yui-main\">");
				}
				return null;
			}
			
			Elements headerBoxes = this.mainElement.getElementsByClass("yui-gc");
			if ((headerBoxes == null) || (headerBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-gc box\">");
				}
				return null;
			}
			Element yui_gc_box = null;
			for (final Element tmp : headerBoxes) {
				if (tmp.classNames().contains("box")) {
					yui_gc_box = tmp;
					break;
				}
			}
			if (yui_gc_box == null) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-gc box\">");
				}
				return null;
			}
			
			headerBoxes = yui_gc_box.getElementsByClass("yui-u");
			if ((headerBoxes == null) || (headerBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-u first\">");
				}
				return null;
			}
			Element yui_u_first = null;
			for (final Element tmp : headerBoxes) {
				if (tmp.classNames().contains("first")) {
					yui_u_first = tmp;
					break;
				}
			}
			if (yui_u_first == null) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-u first\">");
				}
				return null;
			}
			
			this.headerBox = yui_u_first;
			
			final Elements gBoxes = this.mainElement.getElementsByClass("yui-g");
			if ((gBoxes == null) || (gBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-g box\">");
				}
				return null;
			}
			for (final Element tmp : gBoxes) {
				if (tmp.classNames().contains("box")) {
					this.gBox = tmp;
					break;
				}
			}
			if (this.gBox == null) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-g box\">");
				}
				return null;
			}
			
			final Elements leftGBoxes = this.gBox.getElementsByClass("yui-u");
			if ((leftGBoxes == null) || (leftGBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-u first\">");
				}
				return null;
			}
			for (final Element tmp : leftGBoxes) {
				if (tmp.classNames().contains("first")) {
					this.leftGBox = tmp;
					break;
				}
			}
			if (this.leftGBox == null) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-u first\">");
				}
				return null;
			}
			
			this.rightGBox = this.leftGBox.nextElementSibling();
			
			final Element commentTableContainer = this.mainElement.getElementById("comment_table_container");
			this.commentTable = commentTableContainer.child(0);
			if ((this.commentTable == null) || (!"table".equals(this.commentTable.tag().getName()))) {
				if (Logger.logError()) {
					Logger.error("Could not find comment table.");
				}
				return null;
			}
			
			final Element h4FileBar = this.mainElement.getElementById("filebar");
			if (h4FileBar == null) {
				if (Logger.logError()) {
					Logger.error("Could not find filebar");
				}
				return null;
			}
			this.attachmentTableContainer = h4FileBar.nextElementSibling();
			
			final Element h4ChangeBar = this.mainElement.getElementById("changebar");
			if (h4ChangeBar == null) {
				if (Logger.logError()) {
					Logger.error("Could not find changebar");
				}
				return null;
			}
			this.historyTableContainer = h4ChangeBar.nextElementSibling();
			this.report = new Report(issueTracker, getId());
			return this.report;
		} catch (final UnsupportedProtocolException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return null;
		} catch (final FetchException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	/**
	 * Sets the tracker.
	 * 
	 * @param tracker
	 *            the new tracker
	 */
	@Override
	public void setTracker(final Tracker tracker) {
		// PRECONDITIONS
		
		try {
			this.tracker = tracker;
		} finally {
			// POSTCONDITIONS
		}
	}
}
