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
package org.mozkito.issues.tracker.mantis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.MimeUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.MIMETypeDeterminationException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdom2.IllegalDataException;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.XmlReport;
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
 * The Class MantisParser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class MantisParser implements Parser {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAssignedTo()
	 */
	
	/** The Constant MIN_CLOSED_TD_TAGS. */
	private static final int MIN_CLOSED_TD_TAGS = 3;
	
	/** The Constant MIN_OPEN_TD_TAGS. */
	private static final int MIN_OPEN_TD_TAGS   = 3;
	
	/**
	 * Gets the priority.
	 * 
	 * @param s
	 *            the s
	 * @return the priority
	 */
	private static Priority getPriority(final String s) {
		if (s == null) {
			return null;
		}
		final String value = s.toLowerCase();
		switch (value) {
			case "none":
				return Priority.VERY_LOW;
			case "low":
				return Priority.LOW;
			case "normal":
				return Priority.NORMAL;
			case "high":
				return Priority.HIGH;
			case "urgent":
				return Priority.VERY_HIGH;
			case "immediate":
				return Priority.VERY_HIGH;
			default:
				return Priority.UNKNOWN;
		}
	}
	
	/**
	 * Gets the resolution.
	 * 
	 * @param s
	 *            the s
	 * @return the resolution
	 */
	private static Resolution getResolution(final String s) {
		if (s == null) {
			return null;
		}
		final String value = s.toLowerCase();
		switch (value) {
			case "open":
				return Resolution.UNRESOLVED;
			case "fixed":
				return Resolution.RESOLVED;
			case "unable to reproduce":
				return Resolution.WORKS_FOR_ME;
			case "duplicate":
				return Resolution.DUPLICATE;
			case "no change required":
				return Resolution.INVALID;
			case "suspended":
				return Resolution.UNRESOLVED;
			case "out of date":
				return Resolution.INVALID;
			case "invalid":
				return Resolution.INVALID;
			default:
				return Resolution.UNKNOWN;
		}
	}
	
	/**
	 * Gets the severity.
	 * 
	 * @param s
	 *            the s
	 * @return the severity
	 */
	private static Severity getSeverity(final String s) {
		if (s == null) {
			return null;
		}
		final String value = s.toLowerCase();
		switch (value) {
			case "trivial":
				return Severity.TRIVIAL;
			case "minor":
				return Severity.MINOR;
			case "major":
				return Severity.MAJOR;
			case "critical":
				return Severity.CRITICAL;
			default:
				return Severity.UNKNOWN;
		}
	}
	
	/**
	 * Gets the status.
	 * 
	 * @param s
	 *            the s
	 * @return the status
	 */
	private static Status getStatus(final String s) {
		if (s == null) {
			return null;
		}
		final String value = s.toLowerCase();
		switch (value) {
			case "new":
				return Status.NEW;
			case "feedback":
				return Status.FEEDBACK;
			case "acknowledged":
				return Status.ACKNOWLEDGED;
			case "scheduled":
				return Status.IN_PROGRESS;
			case "resolved":
				return Status.CLOSED;
			case "closed":
				return Status.CLOSED;
			default:
				return Status.UNKNOWN;
		}
	}
	
	/**
	 * Gets the type.
	 * 
	 * @param s
	 *            the s
	 * @return the type
	 */
	private static Type getType(final String s) {
		if (s == null) {
			return null;
		}
		final String value = s.toLowerCase();
		switch (value) {
			case "defect":
				return Type.BUG;
			case "design defect":
				return Type.DESIGN_DEFECT;
			case "feature request":
				return Type.RFE;
			case "backport":
				return Type.BACKPORT;
			default:
				return Type.UNKNOWN;
		}
	}
	
	/** The document. */
	private Document                        document;
	
	/** The main content table. */
	private Element                         mainContentTable;
	
	/** The attachment regex. */
	private final Regex                     attachmentRegex   = new Regex(
	                                                                      "({FILE}[^ ]+)\\s\\(({SIZE}[0-9,]+)\\)\\s({DATE}[1-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]\\s[0-2][0-9]:[0-5][0-9])\\s({URL}https://[^ ]+)");
	
	/** The attachment id regex. */
	private final Regex                     attachmentIdRegex = new Regex("file_id=({FILE_ID}\\d+)");
	
	/** The size regex. */
	private final Regex                     sizeRegex         = new Regex("\\(({SIZE}[0-9,]+)\\sbytes\\)");
	
	/** The project regex. */
	private final Regex                     projectRegex      = new Regex("\\[({PRJECT}[^\\]]+)\\]");
	
	/** The tracker. */
	private Tracker                         tracker;
	
	/** The history elements. */
	private final SortedSet<HistoryElement> historyElements   = null;
	
	/** The resolution timestamp. */
	private DateTime                        resolutionTimestamp;
	
	/** The resolver. */
	private Person                          resolver;
	
	/** The attachters. */
	private final Map<String, Person>       attachters        = new HashMap<String, Person>();
	
	/** The report. */
	private XmlReport                       xmlReport;
	
	/** The fetch time. */
	private DateTime                        fetchTime;
	
	/** The md5. */
	private byte[]                          md5;
	
	/** The report. */
	private Report                          report;
	
	/**
	 * Adds the change field.
	 * 
	 * @param historyElement
	 *            the history element
	 * @param field
	 *            the field
	 * @param change
	 *            the change
	 */
	private void addChangeField(final HistoryElement historyElement,
	                            final String field,
	                            final String change) {
		// PRECONDITIONS
		
		final String[] changedValues = change.split("=>");
		String oldValue = null;
		String newValue = null;
		if (changedValues.length > 1) {
			oldValue = changedValues[0].trim();
			newValue = changedValues[1].trim();
		} else if (changedValues.length > 0) {
			newValue = changedValues[0].trim();
		}
		if (((oldValue == null) && (newValue == null))
		        || ((oldValue != null) && (newValue != null) && (oldValue.isEmpty() && newValue.isEmpty()))) {
			return;
		}
		
		try {
			final String lowerCaseFiled = field.toLowerCase();
			switch (lowerCaseFiled) {
				case "category":
					historyElement.addChangedValue(field, oldValue, newValue);
					break;
				case "type":
					historyElement.addChangedValue(field, getType(oldValue), getType(newValue));
					break;
				case "severity":
					historyElement.addChangedValue(field, getSeverity(oldValue), getSeverity(newValue));
					break;
				case "assigned to":
					Person oldPerson = new Person(oldValue, null, null);
					if ((oldValue == null) || oldValue.isEmpty()) {
						oldPerson = Tracker.UNKNOWN_PERSON;
					}
					Person newPerson = new Person(newValue, null, null);
					if ((newValue == null) || newValue.isEmpty()) {
						newPerson = Tracker.UNKNOWN_PERSON;
					}
					historyElement.addChangedValue("assignedto", oldPerson, newPerson);
					break;
				case "priority":
					historyElement.addChangedValue(field, getPriority(oldValue), getPriority(newValue));
					break;
				case "resolution":
					final Resolution newResolution = getResolution(newValue);
					historyElement.addChangedValue(field, getResolution(oldValue), newResolution);
					if (newResolution.equals(Resolution.RESOLVED)) {
						this.resolutionTimestamp = historyElement.getTimestamp();
						this.resolver = historyElement.getAuthor();
					}
					break;
				case "fixed in scm revision":
					if (oldValue != null) {
						oldValue = oldValue.replaceAll("\\[\\^\\]", "").trim();
					}
					if (newValue != null) {
						newValue = newValue.replaceAll("\\[\\^\\]", "").trim();
					}
					historyElement.addChangedValue("scmfixversion", oldValue, newValue);
					break;
				case "product version":
					historyElement.addChangedValue(field, oldValue, newValue);
					break;
				case "modules":
					historyElement.addChangedValue("component", oldValue, newValue);
					break;
				case "summary":
					historyElement.addChangedValue(field, oldValue, newValue);
					break;
				case "description":
					historyElement.addChangedValue(field, oldValue, newValue);
					break;
				default:
					if (field.toLowerCase().startsWith("file added")) {
						this.attachters.put(field.replaceAll("File Added:", "").trim(), historyElement.getAuthor());
					} else if (field.toLowerCase().startsWith("status")) {
						historyElement.addChangedValue(field, getStatus(oldValue), getStatus(newValue));
					}
					break;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Check raw.
	 * 
	 * @param rawReport
	 *            the raw report
	 * @return true, if successful
	 */
	public boolean checkRAW(final RawContent rawReport) {
		Regex regex = new Regex("Issue\\s+\\d+\\s+not\\s+found.");
		MultiMatch findAll = regex.findAll(rawReport.getContent());
		if (findAll != null) {
			if (Logger.logInfo()) {
				Logger.info("Ignoring report " + rawReport.getUri().toASCIIString()
				        + ". checkRaw() failed: issue seems not to exist.");
			}
			return false;
		}
		regex = new Regex("Access Denied.");
		findAll = regex.findAll(rawReport.getContent());
		if (findAll != null) {
			if (Logger.logInfo()) {
				Logger.info("Ignoring report " + rawReport.getUri().toASCIIString()
				        + ". checkRaw() failed: issue requires special permission.");
			}
			return false;
		}
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#createDocument(org.mozkito.bugs.tracker.RawReport )
	 */
	/**
	 * Creates the document.
	 * 
	 * @param rawReport
	 *            the raw report
	 * @return the xml report
	 */
	public XmlReport createDocument(final RawContent rawReport) {
		final BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		
		try {
			final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
			                                                                      "org.ccil.cowan.tagsoup.Parser"));
			final org.jdom2.Document document = saxBuilder.build(reader);
			reader.close();
			
			return new XmlReport(rawReport, document);
		} catch (final TransformerFactoryConfigurationError | IOException | JDOMException | IllegalDataException e) {
			if (Logger.logError()) {
				Logger.error(e, "Cannot create XML document!");
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getCategory()
	 */
	
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			final Element td = getMainTableCell("Assigned To", 1);
			if (td == null) {
				return null;
			}
			final String username = td.text().trim();
			if (username.isEmpty()) {
				return Tracker.UNKNOWN_PERSON;
			}
			return new Person(td.text(), null, null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComment(int)
	 */
	
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
		try {
			
			parseHistoryElements(this.report.getHistory());
			
			final Element cell = getMainTableCell("Attached Files", 1);
			if (cell == null) {
				return null;
			}
			final Elements aTags = cell.getElementsByTag("a");
			AttachmentEntry attachmentEntry = null;
			
			for (final Element aTag : aTags) {
				String link = aTag.attr("href");
				
				final String reportLink = this.xmlReport.getUri().toASCIIString();
				final int index = reportLink.lastIndexOf("/");
				link = reportLink.substring(0, index + 1) + link;
				if ((attachmentEntry == null) || (!attachmentEntry.getLink().equals(link))) {
					final MultiMatch attachmentIdGroups = this.attachmentIdRegex.findAll(link);
					if ((attachmentIdGroups == null) || (attachmentIdGroups.size() != 1)) {
						// throw new UnrecoverableError("Could not extract attachment id from link url: " + link);
						continue;
					}
					
					attachmentEntry = new AttachmentEntry(attachmentIdGroups.getMatch(0).getGroup(1).getMatch());
					attachmentEntry.setLink(link);
					result.add(attachmentEntry);
					final Element filenameTag = aTag.nextElementSibling();
					if (filenameTag == null) {
						throw new UnrecoverableError("Could not find filenameTag for attachmentEntry.");
					}
					try {
						final URI attachmentURI = new URL(this.tracker.getUri().toASCIIString()
						        + attachmentEntry.getLink()).toURI();
						
						try {
							attachmentEntry.setFilename(filenameTag.text());
							attachmentEntry.setMime(MimeUtils.determineMIME(attachmentURI));
						} catch (final MIMETypeDeterminationException | IOException | UnsupportedProtocolException
						        | FetchException e) {
							if (Logger.logError()) {
								Logger.error("Could not determine MIME type for URL %s.", attachmentURI.toASCIIString());
							}
						}
					} catch (final URISyntaxException | MalformedURLException e) {
						if (Logger.logError()) {
							Logger.error("Could not generate attachment URI !");
						}
					}
					final Person person = this.attachters.get(attachmentEntry.getFilename());
					if (person == null) {
						if (Logger.logWarn()) {
							Logger.warn("Could not detect attacher for attchment `" + attachmentEntry.getFilename()
							        + "`.");
						}
					} else {
						attachmentEntry.setAuthor(person);
					}
					
					final Element headTag = filenameTag.nextElementSibling();
					if (headTag == null) {
						throw new UnrecoverableError("Could not find headTag for attachmentEntry.");
					}
					final Element dateTag = headTag.nextElementSibling();
					if (dateTag == null) {
						throw new UnrecoverableError("Could not find dateTag for attachmentEntry.");
					}
					attachmentEntry.setTimestamp(DateTimeUtils.parseDate(dateTag.text()));
				}
			}
			
			final String text = cell.ownText().trim();
			if (text.isEmpty()) {
				return result;
			}
			final MultiMatch sizeStrings = this.sizeRegex.findAll(text);
			if (result.size() != sizeStrings.size()) {
				throw new UnrecoverableError("Found " + result.size() + " attachments but " + sizeStrings.size()
				        + " file size strings.");
			}
			
			for (int i = 0; i < sizeStrings.size(); ++i) {
				final Match match = sizeStrings.getMatch(i);
				if (!match.hasGroups()) {
					throw new UnrecoverableError("Did not find attachment size for attachment " + result.get(i).getId());
				}
				final String sizeString = match.getGroup(1).getMatch().replace(",", "");
				try {
					result.get(i).setSize(Long.parseLong(sizeString));
				} catch (final NumberFormatException e) {
					throw new UnrecoverableError("Could not interpret size string " + sizeString + " as Long.");
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
	 * Gets the attachment id regex.
	 * 
	 * @return the attachment id regex
	 */
	public Regex getAttachmentIdRegex() {
		return this.attachmentIdRegex;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAttachmentEntries()
	 */
	/**
	 * Gets the attachment regex.
	 * 
	 * @return the attachment regex
	 */
	Regex getAttachmentRegex() {
		return this.attachmentRegex;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getDescription()
	 */
	
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell(4, 1);
			return cell.text().trim();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getHistoryLength()
	 */
	
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		
		final SortedSet<Comment> result = new TreeSet<Comment>();
		
		try {
			final Element notesDiv = this.document.getElementById("bugnotes_open");
			final Elements tbodyTags = notesDiv.getElementsByTag("tbody");
			if (tbodyTags.isEmpty()) {
				throw new UnrecoverableError("Could not find bug notes table.");
			}
			
			final Elements notes = tbodyTags.get(0).getElementsByClass("bugnote");
			for (final Element note : notes) {
				final Elements columns = note.getElementsByTag("td");
				if (columns.size() != 2) {
					throw new UnrecoverableError("Could detect bug note columns.");
				}
				final Elements idSpans = columns.get(0).getElementsByTag("span");
				if (idSpans.isEmpty()) {
					throw new UnrecoverableError("Could detect bug note ID span.");
				}
				final Element idSpan = idSpans.get(0);
				final String commentIdString = idSpan.text().replaceAll("\\(", "").replaceAll("\\)", "");
				try {
					final int id = Integer.parseInt(commentIdString);
					
					Element brTag = idSpan.nextElementSibling();
					if (brTag == null) {
						throw new UnrecoverableError("Could not find first <br> after comment id.");
					}
					final Element developerTag = brTag.nextElementSibling();
					if ((!"a".equals(developerTag.tagName().toLowerCase()))
					        && (!"font".equals(developerTag.tagName().toLowerCase()))) {
						throw new UnrecoverableError(
						                             "Could not extract comment author. Could not find <a> nor <font> tag.");
					}
					
					final String developerString = developerTag.text().trim();
					Person author = new Person(developerString, null, null);
					if (developerString.isEmpty()) {
						author = Tracker.UNKNOWN_PERSON;
					}
					
					final Element spanTag = developerTag.nextElementSibling();
					if (spanTag == null) {
						throw new UnrecoverableError("Could not find <span> tag containing author usergroup.");
					}
					
					brTag = spanTag.nextElementSibling();
					if (brTag == null) {
						throw new UnrecoverableError("Could not find first <br> after comment author.");
					}
					
					final Element timestampTag = brTag.nextElementSibling();
					if (timestampTag == null) {
						throw new UnrecoverableError("Could not find <span> containing the added timestamp of comment.");
					}
					final DateTime timestamp = DateTimeUtils.parseDate(timestampTag.text().trim());
					
					final String message = columns.get(1).html();
					
					final Comment comment = new Comment(id, author, timestamp, message);
					result.add(comment);
				} catch (final NumberFormatException e) {
					throw new UnrecoverableError("Could not interpret comment id " + commentIdString + " as integer.");
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getId()
	 */
	
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Modules", 1);
			if (cell == null) {
				return null;
			}
			return cell.text().trim();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getNumberOfAttachments()
	 */
	
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell(4, 4);
			return DateTimeUtils.parseDate(cell.text().trim());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getNumberOfComments()
	 */
	
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Description", 1);
			if (cell == null) {
				if (Logger.logWarn()) {
					Logger.warn("Could not detect description of bug report " + getId());
				}
				return null;
			}
			return cell.text().trim();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getPriority()
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
	 * @see org.mozkito.bugs.tracker.Parser#getHistoryElements()
	 */
	@Override
	public String getId() {
		// PRECONDITIONS
		
		try {
			final Element td = getMainTableCell(2, 0);
			return td.text().trim();
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
			final Element cell = getMainTableCell("Tags", 1);
			final String content = cell.text().trim();
			final Set<String> result = new HashSet<String>();
			if (!"no tags attached.".equals(content.toLowerCase())) {
				final String[] tags = content.split(",");
				for (final String tag : tags) {
					result.add(tag.trim());
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getLastUpdateTimestamp()
	 */
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell(4, 5);
			return DateTimeUtils.parseDate(cell.text().trim());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSeverity()
	 */
	
	/**
	 * Gets the main table cell.
	 * 
	 * @param row
	 *            the row
	 * @param column
	 *            the column
	 * @return the main table cell
	 */
	private Element getMainTableCell(final int row,
	                                 final int column) {
		final Elements trTags = this.mainContentTable.getElementsByTag("tr");
		if (trTags.size() <= row) {
			throw new UnrecoverableError("Requested row " + row + " in mainContentTable but it does not exist.");
		}
		final Element trTag = trTags.get(row);
		final Elements tdTags = trTag.getElementsByTag("td");
		if (tdTags.size() <= column) {
			throw new UnrecoverableError("Requested column " + column + " in mainContentTable row " + row
			        + " but the column does not exist in this row.");
		}
		return tdTags.get(column);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSiblings()
	 */
	
	/**
	 * Gets the main table cell.
	 * 
	 * @param rowName
	 *            the row name
	 * @param column
	 *            the column
	 * @return the main table cell
	 */
	private Element getMainTableCell(final String rowName,
	                                 final int column) {
		final Elements trTags = this.mainContentTable.getElementsByTag("tr");
		
		for (final Element trTag : trTags) {
			final Elements tdTags = trTag.getElementsByTag("td");
			
			if (tdTags.get(0).text().toLowerCase().trim().equals(rowName.toLowerCase())) {
				if (tdTags.size() <= column) {
					throw new UnrecoverableError("Requested column " + column + " in mainContentTable row " + rowName
					        + " but the column does not exist in this row.");
				}
				return tdTags.get(column);
			}
		}
		if (Logger.logDebug()) {
			Logger.debug("Requested row " + rowName + " in mainContentTable does not exist.");
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getStatus()
	 */
	
	@Override
	public final byte[] getMd5() {
		return this.md5;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubmitter()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolutionTimestamp()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Priority", 1);
			return getPriority(cell.text().trim());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
	 */
	
	@Override
	public String getProduct() {
		// PRECONDITIONS
		
		try {
			final String category = getCategory();
			final MultiMatch findAll = this.projectRegex.findAll(category);
			if ((findAll == null) || !findAll.getMatch(0).hasGroups()) {
				if (Logger.logWarn()) {
					Logger.warn("Could not find product description in category.");
				}
				return "";
			}
			return findAll.getMatch(0).getGroup(1).getMatch();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getType()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getResolution()
	 */
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Priority", 3);
			return getResolution(cell.text().trim());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolutionTimestamp()
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
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
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
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	
	@Override
	public String getScmFixVersion() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Status", 5);
			final Elements aTags = cell.getElementsByTag("a");
			if (aTags.isEmpty()) {
				return null;
			}
			return aTags.get(0).text().trim();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setXMLReport(org.mozkito.bugs.tracker.XmlReport )
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getSeverity()
	 */
	@Override
	public Severity getSeverity() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell(4, 2);
			return getSeverity(cell.text().trim());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSiblings()
	 */
	@Override
	public Set<String> getSiblings() {
		// PRECONDITIONS
		
		final Set<String> result = new HashSet<String>();
		try {
			final Element openRelationsDiv = this.document.getElementById("relationships_open");
			if (openRelationsDiv == null) {
				throw new UnrecoverableError("Could not find relationships_open <div>.");
			}
			final Elements openTrTags = openRelationsDiv.getElementsByTag("tr");
			if (openTrTags.size() > 2) {
				for (int i = 2; i < openTrTags.size(); ++i) {
					final Element openTrTag = openTrTags.get(i);
					if (openTrTag == null) {
						throw new UnrecoverableError("Could not find any row in open relationship table.");
					}
					final Elements openTdTags = openTrTag.getElementsByTag("td");
					if (openTdTags.isEmpty() || (openTdTags.size() < MantisParser.MIN_OPEN_TD_TAGS)) {
						// throw new UnrecoverableError("Could not find any column in open relationship table row.");
						continue;
					}
					final Element openTdTag = openTdTags.get(2);
					if (openTdTag == null) {
						throw new UnrecoverableError("Could not find relationship id cell.");
					}
					try {
						result.add(openTdTag.text().trim());
					} catch (final NumberFormatException e) {
						throw new UnrecoverableError("Could not interprete relationship id " + openTdTag.text().trim()
						        + " as Long.");
					}
				}
			}
			
			final Element closedRelationsDiv = this.document.getElementById("relationships_closed");
			if (closedRelationsDiv == null) {
				throw new UnrecoverableError("Could not find relationships_closed <div>.");
			}
			final Elements closedTrTags = closedRelationsDiv.getElementsByTag("tr");
			if (closedTrTags.size() > 2) {
				for (int i = 2; i < closedTrTags.size(); ++i) {
					final Element closedTrTag = closedTrTags.get(i);
					if (closedTrTag == null) {
						throw new UnrecoverableError("Could not find any row in closed relationship table.");
					}
					final Elements closedTdTags = closedTrTag.getElementsByTag("td");
					if (closedTdTags.isEmpty() || (closedTdTags.size() < MantisParser.MIN_CLOSED_TD_TAGS)) {
						throw new UnrecoverableError("Could not find any column in closed relationship table row.");
					}
					final Element closedTdTag = closedTdTags.get(2);
					if (closedTdTag == null) {
						throw new UnrecoverableError("Could not find relationship id cell.");
					}
					try {
						result.add(closedTdTag.text().trim());
					} catch (final NumberFormatException e) {
						throw new UnrecoverableError("Could not interprete relationship id "
						        + closedTdTag.text().trim() + " as Long.");
					}
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getStatus()
	 */
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Status", 1);
			return getStatus(cell.text().trim());
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
			final Element cell = getMainTableCell("Summary", 1);
			return cell.text().trim();
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
			final Element cell = getMainTableCell("Reporter", 1);
			final String username = cell.text().trim();
			if (username.isEmpty()) {
				return Tracker.UNKNOWN_PERSON;
			}
			return new Person(cell.text().trim(), null, null);
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
			final Element cell = getMainTableCell("Summary", 1);
			return cell.text().trim();
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
			final Element cell = getMainTableCell(4, 0);
			return getType(cell.text().trim());
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
			final Element cell = getMainTableCell("Product Version", 1);
			if (cell == null) {
				return null;
			}
			return cell.text().trim();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#parseHistoryElements(org.mozkito.issues.tracker.model.History)
	 */
	@Override
	public void parseHistoryElements(final History history) {
		// PRECONDITIONS
		
		try {
			if (this.historyElements != null) {
				return;
			}
			final SortedSet<HistoryElement> result = new TreeSet<HistoryElement>();
			final Element elementById = this.document.getElementById("history_open");
			final Elements trTags = elementById.getElementsByTag("tr");
			if (trTags.isEmpty()) {
				throw new UnrecoverableError("Could not find Issue History header row. Table is empty.");
			}
			
			final Element historyHeader = trTags.get(0);
			if (!historyHeader.text().trim().contains("Issue History")) {
				throw new UnrecoverableError("Could not find Issue History header row. Found: `"
				        + historyHeader.text().trim() + "`");
			}
			Element historyEntry = historyHeader.nextElementSibling();
			if (historyEntry == null) {
				throw new UnrecoverableError(
				                             "Issue history structure unknown. Expected to table header after issue history header.");
			}
			historyEntry = historyEntry.nextElementSibling();
			
			while (historyEntry != null) {
				
				final Element dateChild = historyEntry.child(0);
				if (dateChild == null) {
					throw new UnrecoverableError("Could not find history entry date column");
				}
				final DateTime timestamp = DateTimeUtils.parseDate(dateChild.text().trim());
				final Element authorChild = historyEntry.child(1);
				if (authorChild == null) {
					throw new UnrecoverableError("Could not find history entry author column");
				}
				
				if ((result.isEmpty()) || (!result.last().getTimestamp().isEqual(timestamp))) {
					final String authorString = authorChild.text().trim();
					Person author = new Person(authorString, null, null);
					if ((authorString == null) || authorString.isEmpty()) {
						author = Tracker.UNKNOWN_PERSON;
					}
					result.add(new HistoryElement(this.report.getHistory(), author, timestamp));
				}
				
				final Element fieldChild = historyEntry.child(2);
				if (fieldChild == null) {
					throw new UnrecoverableError("Could not find history entry field column");
				}
				final Element changeChild = historyEntry.child(3);
				if (changeChild == null) {
					throw new UnrecoverableError("Could not find history entry change column");
				}
				final String change = changeChild.text().trim();
				addChangeField(result.last(), fieldChild.text().trim(), change);
				
				historyEntry = historyEntry.nextElementSibling();
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setURI(org.mozkito.bugs.tracker.ReportLink)
	 */
	@Override
	@NoneNull
	public Report setContext(final IssueTracker issueTracker,
	                         final ReportLink reportLink) {
		// PRECONDITIONS
		
		Elements tables = null;
		try {
			
			final URI uri = reportLink.getUri();
			RawContent rawContent = null;
			rawContent = IOUtils.fetch(uri);
			
			this.fetchTime = new DateTime();
			if (!checkRAW(rawContent)) {
				if (Logger.logWarn()) {
					Logger.warn("Could not parse report " + uri + ". RAW check failed!");
				}
				return null;
			}
			
			this.md5 = DigestUtils.md5(rawContent.getContent());
			
			this.xmlReport = createDocument(rawContent);
			if (this.xmlReport == null) {
				return null;
			}
			this.document = Jsoup.parse(this.xmlReport.getContent());
			tables = this.document.getElementsByClass("width100");
			if ((tables == null) || (tables.isEmpty())) {
				throw new UnrecoverableError("Could not find main table tag for report with id "
				        + this.xmlReport.getUri().toASCIIString());
			}
			this.mainContentTable = tables.get(0);
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
			Condition.check((tables != null) && (tables.size() > 1), "There must be two tables within bug report.");
			Condition.notNull(this.document, "The document must not be null");
			Condition.notNull(this.mainContentTable, "The mainContentTable must not be null");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
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
