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
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
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

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.MimeUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
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
 * The Class MantisParser.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MantisParser implements Parser {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAssignedTo()
	 */
	
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
		if (value.equals("none")) {
			return Priority.VERY_LOW;
		} else if (value.equals("low")) {
			return Priority.LOW;
		} else if (value.equals("normal")) {
			return Priority.NORMAL;
		} else if (value.equals("high")) {
			return Priority.HIGH;
		} else if (value.equals("urgent")) {
			return Priority.VERY_HIGH;
		} else if (value.equals("immediate")) {
			return Priority.VERY_HIGH;
		} else {
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
		if (value.equals("open")) {
			return Resolution.UNRESOLVED;
		} else if (value.equals("fixed")) {
			return Resolution.RESOLVED;
		} else if (value.equals("unable to reproduce")) {
			return Resolution.WORKS_FOR_ME;
		} else if (value.equals("duplicate")) {
			return Resolution.DUPLICATE;
		} else if (value.equals("no change required")) {
			return Resolution.INVALID;
		} else if (value.equals("suspended")) {
			return Resolution.UNRESOLVED;
		} else if (value.equals("out of date")) {
			return Resolution.INVALID;
		} else if (value.equals("invalid")) {
			return Resolution.INVALID;
		} else {
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
		if (value.equals("trivial")) {
			return Severity.TRIVIAL;
		} else if (value.equals("minor")) {
			return Severity.MINOR;
		} else if (value.equals("major")) {
			return Severity.MAJOR;
		} else if (value.equals("critical")) {
			return Severity.CRITICAL;
		} else {
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
		if (value.equals("new")) {
			return Status.NEW;
		} else if (value.equals("feedback")) {
			return Status.FEEDBACK;
		} else if (value.equals("acknowledged")) {
			return Status.ACKNOWLEDGED;
		} else if (value.equals("scheduled")) {
			return Status.IN_PROGRESS;
		} else if (value.equals("resolved")) {
			return Status.CLOSED;
		} else if (value.equals("closed")) {
			return Status.CLOSED;
		} else {
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
		if (s.toLowerCase().equals("defect")) {
			return Type.BUG;
		} else if (s.toLowerCase().equals("design defect")) {
			return Type.DESIGN_DEFECT;
		} else if (s.toLowerCase().equals("feature request")) {
			return Type.RFE;
		} else if (s.toLowerCase().equals("feature request")) {
			return Type.BACKPORT;
		} else {
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
	private XmlReport                       report;
	
	/** The fetch time. */
	private DateTime                        fetchTime;
	
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
		        || ((oldValue != null) && (newValue != null) && (oldValue.equals("") && newValue.equals("")))) {
			return;
		}
		
		try {
			if (field.toLowerCase().equals("category")) {
				historyElement.addChangedValue(field, oldValue, newValue);
			} else if (field.toLowerCase().equals("type")) {
				historyElement.addChangedValue(field, getType(oldValue), getType(newValue));
			} else if (field.toLowerCase().equals("severity")) {
				historyElement.addChangedValue(field, getSeverity(oldValue), getSeverity(newValue));
			} else if (field.toLowerCase().equals("assigned to")) {
				Person oldPerson = new Person(oldValue, null, null);
				if ((oldValue == null) || oldValue.equals("")) {
					oldPerson = Tracker.unknownPerson;
				}
				Person newPerson = new Person(newValue, null, null);
				if ((newValue == null) || newValue.equals("")) {
					newPerson = Tracker.unknownPerson;
				}
				historyElement.addChangedValue("assignedto", oldPerson, newPerson);
			} else if (field.toLowerCase().equals("priority")) {
				historyElement.addChangedValue(field, getPriority(oldValue), getPriority(newValue));
			} else if (field.toLowerCase().equals("resolution")) {
				final Resolution newResolution = getResolution(newValue);
				historyElement.addChangedValue(field, getResolution(oldValue), newResolution);
				if (newResolution.equals(Resolution.RESOLVED)) {
					this.resolutionTimestamp = historyElement.getTimestamp();
					this.resolver = historyElement.getAuthor();
				}
			} else if (field.toLowerCase().equals("fixed in scm revision")) {
				if (oldValue != null) {
					oldValue = oldValue.replaceAll("\\[\\^\\]", "").trim();
				}
				if (newValue != null) {
					newValue = newValue.replaceAll("\\[\\^\\]", "").trim();
				}
				historyElement.addChangedValue("scmfixversion", oldValue, newValue);
			} else if (field.toLowerCase().equals("product version")) {
				historyElement.addChangedValue(field, oldValue, newValue);
			} else if (field.toLowerCase().equals("modules")) {
				historyElement.addChangedValue("component", oldValue, newValue);
			} else if (field.toLowerCase().equals("summary")) {
				historyElement.addChangedValue(field, oldValue, newValue);
			} else if (field.toLowerCase().equals("description")) {
				historyElement.addChangedValue(field, oldValue, newValue);
			} else if (field.toLowerCase().startsWith("file added")) {
				this.attachters.put(field.replaceAll("File Added:", "").trim(), historyElement.getAuthor());
			} else if (field.toLowerCase().startsWith("status")) {
				historyElement.addChangedValue(field, getStatus(oldValue), getStatus(newValue));
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
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#createDocument(de.unisaarland.cs.st.moskito.bugs.tracker.RawReport
	 * )
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
			final SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
			final org.jdom.Document document = saxBuilder.build(reader);
			reader.close();
			
			return new XmlReport(rawReport, document);
		} catch (final TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error(e, "Cannot create XML document!");
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e, "Cannot create XML document!");
			}
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e, "Cannot create XML document!");
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCategory()
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
			if (username.equals("")) {
				return Tracker.unknownPerson;
			}
			return new Person(td.text(), null, null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComment(int)
	 */
	
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
		try {
			
			getHistoryElements();
			
			final Element cell = getMainTableCell("Attached Files", 1);
			if (cell == null) {
				return null;
			}
			final Elements aTags = cell.getElementsByTag("a");
			AttachmentEntry attachmentEntry = null;
			
			for (final Element aTag : aTags) {
				String link = aTag.attr("href");
				
				final String reportLink = this.report.getUri().toASCIIString();
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
					attachmentEntry.setFilename(filenameTag.text());
					try {
						attachmentEntry.setMime(MimeUtils.determineMIME(new URL(this.tracker.getUri().toASCIIString()
						        + attachmentEntry.getLink()).toURI()));
					} catch (final Exception e) {
						if (Logger.logError()) {
							Logger.error(e, "Could not determine MIME type of attachment '%s'.",
							             attachmentEntry.getFilename());
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComponent()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAttachmentEntries()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getDescription()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getHistoryLength()
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
					if ((!developerTag.tagName().toLowerCase().equals("a"))
					        && (!developerTag.tagName().toLowerCase().equals("font"))) {
						throw new UnrecoverableError(
						                             "Could not extract comment author. Could not find <a> nor <font> tag.");
					}
					
					final String developerString = developerTag.text().trim();
					Person author = new Person(developerString, null, null);
					if (developerString.equals("")) {
						author = Tracker.unknownPerson;
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getId()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getNumberOfAttachments()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getNumberOfComments()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getPriority()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getHistoryElements()
	 */
	@Override
	public SortedSet<HistoryElement> getHistoryElements() {
		// PRECONDITIONS
		
		try {
			if (this.historyElements != null) {
				return this.historyElements;
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
					if ((authorString == null) || authorString.equals("")) {
						author = Tracker.unknownPerson;
					}
					result.add(new HistoryElement(getId(), author, timestamp));
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
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolver()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getKeywords()
	 */
	@Override
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Tags", 1);
			final String content = cell.text().trim();
			final Set<String> result = new HashSet<String>();
			if (!content.toLowerCase().equals("no tags attached.")) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSeverity()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSiblings()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getStatus()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolutionTimestamp()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolutionTimestamp()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
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
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
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
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setXMLReport(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport
	 * )
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSiblings()
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
					if (openTdTags.isEmpty() || (openTdTags.size() < 3)) {
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
					if (closedTdTags.isEmpty() || (closedTdTags.size() < 3)) {
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getStatus()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubject()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
	 */
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			final Element cell = getMainTableCell("Reporter", 1);
			final String username = cell.text().trim();
			if (username.equals("")) {
				return Tracker.unknownPerson;
			}
			return new Person(cell.text().trim(), null, null);
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
			final Element cell = getMainTableCell("Summary", 1);
			return cell.text().trim();
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
			final Element cell = getMainTableCell(4, 0);
			return getType(cell.text().trim());
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
		
		Elements tables = null;
		try {
			
			final URI uri = reportLink.getUri();
			RawContent rawContent = null;
			if ((this.tracker != null) && (this.tracker.getProxyConfig() != null)) {
				rawContent = IOUtils.fetch(uri, this.tracker.getProxyConfig());
			} else {
				rawContent = IOUtils.fetch(uri);
			}
			this.fetchTime = new DateTime();
			if (!checkRAW(rawContent)) {
				if (Logger.logWarn()) {
					Logger.warn("Could not parse report " + uri + ". RAW check failed!");
				}
				return false;
			}
			
			this.report = createDocument(rawContent);
			this.document = Jsoup.parse(this.report.getContent());
			tables = this.document.getElementsByClass("width100");
			if ((tables == null) || (tables.isEmpty())) {
				throw new UnrecoverableError("Could not find main table tag for report with id "
				        + this.report.getUri().toASCIIString());
			}
			this.mainContentTable = tables.get(0);
			return true;
		} catch (final UnsupportedProtocolException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return false;
		} catch (final FetchException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
			Condition.check((tables != null) && (tables.size() > 1), "There must be two tables within bug report.");
			Condition.notNull(this.document, "The document must not be null");
			Condition.notNull(this.mainContentTable, "The mainContentTable must not be null");
		}
	}
}
