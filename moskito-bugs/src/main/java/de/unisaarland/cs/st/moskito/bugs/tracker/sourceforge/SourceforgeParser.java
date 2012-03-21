/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

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
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
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
 * The Class SourceForgeParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SourceforgeParser implements Parser {
	
	/**
	 * The Class AttachmentHistoryEntry.
	 *
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	private class AttachmentHistoryEntry {
		
		/** The author. */
		private final Person   author;
		
		/** The timestamp. */
		private final DateTime timestamp;
		
		/**
		 * Instantiates a new attachment history entry.
		 *
		 * @param author the author
		 * @param timestamp the timestamp
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAssignedTo()
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
		final int priority = Integer.parseInt(value);
		switch (priority) {
			case 1:
			case 2:
				return Priority.VERY_LOW;
			case 3:
			case 4:
				return Priority.LOW;
			case 5:
				return Priority.NORMAL;
			case 6:
			case 7:
				return Priority.HIGH;
			case 8:
			case 9:
				return Priority.VERY_HIGH;
			default:
				return Priority.UNKNOWN;
		}
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
		if (value.equalsIgnoreCase("ACCEPTED")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("DUPLICATE")) {
			return Resolution.DUPLICATE;
		} else if (value.equalsIgnoreCase("FIXED")) {
			return Resolution.RESOLVED;
		} else if (value.equalsIgnoreCase("INVALID")) {
			return Resolution.INVALID;
		} else if (value.equalsIgnoreCase("LATER")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("NONE")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("OUT_OF_DATE")) {
			return Resolution.UNKNOWN;
		} else if (value.equalsIgnoreCase("POSTPONED")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("REJECTED")) {
			return Resolution.INVALID;
		} else if (value.equalsIgnoreCase("REMIND")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("WONT_FIX")) {
			return Resolution.WONT_FIX;
		} else if (value.equalsIgnoreCase("WORKS_FOR_ME")) {
			return Resolution.WORKS_FOR_ME;
		} else {
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
		if (value.equalsIgnoreCase("CLOSED")) {
			return Status.CLOSED;
		} else if (value.equalsIgnoreCase("DELETED")) {
			return Status.CLOSED;
		} else if (value.equalsIgnoreCase("OPEN")) {
			return Status.NEW;
		} else if (value.equalsIgnoreCase("PENDING")) {
			return Status.IN_PROGRESS;
		} else {
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
	private SortedSet<HistoryElement>                 history                = null;
	
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
	
	/**
	 * Instantiates a new sourceforge parser.
	 *
	 * @param bugType the bug type
	 */
	public SourceforgeParser(final Type bugType) {
		this.bugType = bugType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAssignedTo()
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.rightGBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Assigned:")) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAttachmentEntries()
	 */
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		try {
			final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
			
			getHistoryElements();
			
			final Elements tables = this.attachmentTableContainer.getElementsByTag("table");
			if (tables.isEmpty()) {
				return result;
			}
			final Element table = tables.get(0);
			final Element tbody = table.getElementsByTag("tbody").get(0);
			for (final Element tr : tbody.getElementsByTag("tr")) {
				final Elements tds = tr.getElementsByTag("td");
				if (tds.size() < 3) {
					continue;
				}
				final String filename = htmlCommentRegex.removeAll(tds.get(0).text()).replaceAll("\"", "").trim();
				final String description = htmlCommentRegex.removeAll(tds.get(1).text()).replaceAll("\"", "").trim();
				final Element linkTd = tds.get(2);
				final Elements aTags = linkTd.getElementsByTag("a");
				if (aTags.isEmpty()) {
					continue;
				}
				String link = aTags.get(0).attr("href");
				String attachId = null;
				for (final RegexGroup group : fileIdPattern.find(link)) {
					if ((group.getName() != null) && (group.getName().equals("fileid"))) {
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
						Logger.error(e.getMessage(), e);
					}
				} catch (final IOException e) {
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
				} catch (final URISyntaxException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCategory()
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.rightGBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Category:")) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		
		try {
			final SortedSet<Comment> result = new TreeSet<Comment>();
			for (final Element tr : this.commentTable.getElementsByTag("tr")) {
				final List<RegexGroup> commentIdMatch = this.artifactCommentIdRegex.find(tr.attr("id"));
				if (commentIdMatch.isEmpty()) {
					continue;
				}
				int commentId = -1;
				for (final RegexGroup group : commentIdMatch) {
					if ((group.getName() != null) && (group.getName().equals("comment_id"))) {
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
				final String rightColumm = htmlCommentRegex.removeAll(yui_u.text()).replaceAll("\"", "").trim();
				final List<RegexGroup> find = commentRegex.find(leftColumm);
				DateTime timestamp = null;
				Person sender = null;
				for (final RegexGroup group : find) {
					if ((group.getName() != null) && (group.getName().equals("timestamp"))) {
						timestamp = DateTimeUtils.parseDate(group.getMatch());
					} else if ((group.getName() != null) && (group.getName().equals("username"))) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComponent()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Submitted:")) {
					final Element pElement = child.nextElementSibling();
					final List<List<RegexGroup>> findAll = SourceforgeParser.submittedRegex.findAll(pElement.text()
					                                                                                        .trim());
					if ((findAll != null) && (!findAll.isEmpty())) {
						final List<RegexGroup> groups = findAll.get(0);
						String dateStr = null;
						for (final RegexGroup group : groups) {
							if ((group.getName() != null) && (group.getName().equals("timestamp"))) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.gBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Details:")) {
					final String description = child.nextElementSibling().text().trim();
					return htmlCommentRegex.removeAll(description);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getHistoryElement(int)
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getId()
	 */
	@Override
	public SortedSet<HistoryElement> getHistoryElements() {
		// PRECONDITIONS
		
		try {
			if (this.history == null) {
				this.history = new TreeSet<HistoryElement>();
				
				HistoryElement lastHistoryElement = null;
				Person assignedTo = getAssignedTo();
				String category = getCategory();
				Resolution resolution = getResolution();
				Status status = getStatus();
				Priority priority = getPriority();
				String summary = getSummary();
				
				for (final Element tr : this.historyTableContainer.getElementsByTag("tr")) {
					final Elements tds = tr.getElementsByTag("td");
					if (tds.size() < 4) {
						continue;
					}
					final String fieldname = tds.get(0).text().trim();
					final String oldValue = tds.get(1).text().trim();
					final DateTime timestamp = DateTimeUtils.parseDate(tds.get(2).text().trim());
					final Element aElem = tds.get(3).child(0);
					final Person author = new Person(aElem.text().trim(), aElem.attr("title"), null);
					
					if (this.lastUpdateTimestamp == null) {
						this.lastUpdateTimestamp = timestamp;
					}
					
					if ((lastHistoryElement == null) || (!lastHistoryElement.getTimestamp().isEqual(timestamp))) {
						final HistoryElement newHistoryElement = new HistoryElement(getId(), author, timestamp);
						if ((lastHistoryElement != null) && (!lastHistoryElement.isEmpty())) {
							this.history.add(lastHistoryElement);
						}
						lastHistoryElement = newHistoryElement;
					}
					
					if (fieldname.equals("summary")) {
						lastHistoryElement.addChangedValue("summary", oldValue, summary);
						summary = oldValue;
					} else if (fieldname.equals("assigned_to")) {
						if (assignedTo == null) {
							assignedTo = Tracker.unknownPerson;
						}
						final Person oldAssignedTo = new Person(oldValue, null, null);
						lastHistoryElement.addChangedValue("assignedTo", oldAssignedTo, assignedTo);
						assignedTo = oldAssignedTo;
					} else if (fieldname.equals("category_id")) {
						lastHistoryElement.addChangedValue("category", oldValue, category);
						category = oldValue;
					} else if (fieldname.equals("resolution_id")) {
						final Resolution oldResolution = resolveResolution(oldValue);
						lastHistoryElement.addChangedValue("resolution", oldResolution, resolution);
						resolution = oldResolution;
						if (resolution.equals(Resolution.RESOLVED) && (this.resolver == null)) {
							this.resolver = author;
							this.resolutionTimestamp = timestamp;
						}
					} else if (fieldname.equals("status_id")) {
						final Status oldStatus = resolveStatus(oldValue);
						lastHistoryElement.addChangedValue("status", oldStatus, status);
						status = oldStatus;
					} else if (fieldname.equals("priority")) {
						final Priority oldPriority = resolvePriority(oldValue);
						lastHistoryElement.addChangedValue("priority", oldPriority, priority);
						priority = oldPriority;
					} else if (fieldname.equals("File Added")) {
						final String[] split = oldValue.split(":");
						String filename = split[0];
						if (split.length > 1) {
							filename = split[1];
						}
						if (!this.attachmentHistory.containsKey(filename)) {
							this.attachmentHistory.put(filename.trim(), new AttachmentHistoryEntry(author, timestamp));
						}
					}
				}
				if ((lastHistoryElement != null) && (!lastHistoryElement.isEmpty())) {
					this.history.add(lastHistoryElement);
				}
			}
			
			return this.history;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getNumberOfComments()
	 */
	@Override
	public String getId() {
		// PRECONDITIONS
		
		try {
			final List<List<RegexGroup>> findAll = this.subjectRegex.findAll(this.headerBox.text());
			if ((findAll != null) && (!findAll.isEmpty())) {
				for (final RegexGroup group : findAll.get(0)) {
					if ((group.getName() != null) && (group.getName().equals("bugid"))) {
						return group.getMatch().trim();
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getPriority()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getProduct()
	 */
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			getHistoryElements();
			return this.lastUpdateTimestamp;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolver()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Priority:")) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSeverity()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSiblings()
	 */
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Resolution:")) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getStatus()
	 */
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		
		try {
			getHistoryElements();
			return this.resolutionTimestamp;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubject()
	 */
	@Override
	public Person getResolver() {
		// PRECONDITIONS
		
		try {
			getHistoryElements();
			return this.resolver;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
	 */
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Status:")) {
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
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
	 */
	@Override
	public String getSubject() {
		// PRECONDITIONS
		
		try {
			final List<List<RegexGroup>> findAll = this.subjectRegex.findAll(this.headerBox.text());
			if ((findAll != null) && (!findAll.isEmpty())) {
				for (final RegexGroup group : findAll.get(0)) {
					if ((group.getName() != null) && (group.getName().equals("subject"))) {
						return group.getMatch().trim();
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
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setXMLReport(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport
	 * )
	 */
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			for (final Element child : this.leftGBox.children()) {
				if (child.tag().getName().equals("label") && child.text().trim().equals("Submitted:")) {
					final Element pElement = child.nextElementSibling();
					final List<List<RegexGroup>> findAll = SourceforgeParser.submittedRegex.findAll(pElement.text()
					                                                                                        .trim());
					if ((findAll != null) && (!findAll.isEmpty())) {
						String name = null;
						String uname = null;
						final List<RegexGroup> groups = findAll.get(0);
						for (final RegexGroup group : groups) {
							if ((group.getName() != null) && (group.getName().equals("fullname"))) {
								name = group.getMatch().trim();
							} else if ((group.getName() != null) && (group.getName().equals("username"))) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
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
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setURI(de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink)
	 */
	@Override
	public boolean setURI(final ReportLink reportLink) {
		// PRECONDITIONS
		
		try {
			final RawContent rawContent = IOUtils.fetch(reportLink.getUri());
			this.fetchTime = new DateTime();
			final Document document = Jsoup.parse(rawContent.getContent());
			final Elements errorElements = document.getElementsByClass("error");
			if (!errorElements.isEmpty()) {
				for (final Element errorElem : errorElements) {
					if ((errorElem.tag().getName().equals("h4"))
					        && (errorElem.text().trim().replaceAll("\"", "").trim().equals("Error"))) {
						return false;
					}
				}
			}
			this.mainElement = document.getElementById("yui-main");
			if (this.mainElement == null) {
				if (Logger.logError()) {
					Logger.error("Could not find main element: <div id=\"yui-main\">");
				}
				return false;
			}
			
			Elements headerBoxes = this.mainElement.getElementsByClass("yui-gc");
			if ((headerBoxes == null) || (headerBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-gc box\">");
				}
				return false;
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
				return false;
			}
			
			headerBoxes = yui_gc_box.getElementsByClass("yui-u");
			if ((headerBoxes == null) || (headerBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-u first\">");
				}
				return false;
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
				return false;
			}
			
			this.headerBox = yui_u_first;
			
			final Elements gBoxes = this.mainElement.getElementsByClass("yui-g");
			if ((gBoxes == null) || (gBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-g box\">");
				}
				return false;
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
				return false;
			}
			
			final Elements leftGBoxes = this.gBox.getElementsByClass("yui-u");
			if ((leftGBoxes == null) || (leftGBoxes.isEmpty())) {
				if (Logger.logError()) {
					Logger.error("Could not find <div class=\"yui-u first\">");
				}
				return false;
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
				return false;
			}
			
			this.rightGBox = this.leftGBox.nextElementSibling();
			
			final Element commentTableContainer = this.mainElement.getElementById("comment_table_container");
			this.commentTable = commentTableContainer.child(0);
			if ((this.commentTable == null) || (!this.commentTable.tag().getName().equals("table"))) {
				if (Logger.logError()) {
					Logger.error("Could not find comment table.");
				}
				return false;
			}
			
			final Element h4FileBar = this.mainElement.getElementById("filebar");
			if (h4FileBar == null) {
				if (Logger.logError()) {
					Logger.error("Could not find filebar");
				}
				return false;
			}
			this.attachmentTableContainer = h4FileBar.nextElementSibling();
			
			final Element h4ChangeBar = this.mainElement.getElementById("changebar");
			if (h4ChangeBar == null) {
				if (Logger.logError()) {
					Logger.error("Could not find changebar");
				}
				return false;
			}
			this.historyTableContainer = h4ChangeBar.nextElementSibling();
			
			return true;
		} catch (final UnsupportedProtocolException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final FetchException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}
