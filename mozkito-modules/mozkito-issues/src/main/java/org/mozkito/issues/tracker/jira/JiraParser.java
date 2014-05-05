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
package org.mozkito.issues.tracker.jira;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.elements.Type;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.History;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.XmlReport;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.datastructures.RawContent;
import org.mozkito.utilities.datetime.DateTimeUtils;
import org.mozkito.utilities.io.IOUtils;
import org.mozkito.utilities.io.exceptions.FetchException;
import org.mozkito.utilities.io.exceptions.UnsupportedProtocolException;
import org.mozkito.utilities.mime.MimeUtils;
import org.mozkito.utilities.mime.exceptions.MIMETypeDeterminationException;

/**
 * The Class JiraParser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class JiraParser implements Parser {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAssignedTo()
	 */
	
	/**
	 * Resolve resolution.
	 * 
	 * @param resolutionString
	 *            the resolution string
	 * @return the resolution
	 */
	public static Resolution resolveResolution(final String resolutionString) {
		// PRECONDITIONS
		
		try {
			if ("unresolved".equals(resolutionString.toLowerCase())) {
				return Resolution.UNRESOLVED;
			} else if ("fixed".equals(resolutionString.toLowerCase())) {
				return Resolution.RESOLVED;
			} else if ("won't fix".equals(resolutionString.toLowerCase())) {
				return Resolution.WONT_FIX;
			} else if ("duplicate".equals(resolutionString.toLowerCase())) {
				return Resolution.DUPLICATE;
			} else if ("incomplete".equals(resolutionString.toLowerCase())) {
				return Resolution.UNRESOLVED;
			} else if ("cannot reproduce".equals(resolutionString.toLowerCase())) {
				return Resolution.WORKS_FOR_ME;
			} else if ("not a bug".equals(resolutionString.toLowerCase())) {
				return Resolution.INVALID;
			} else {
				return Resolution.UNKNOWN;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Resolve severity.
	 * 
	 * @param severity
	 *            the severity
	 * @return the severity
	 */
	public static Severity resolveSeverity(final String severity) {
		if ("blocker".equals(severity.toLowerCase())) {
			return Severity.BLOCKER;
		} else if ("critical".equals(severity.toLowerCase())) {
			return Severity.CRITICAL;
		} else if ("major".equals(severity.toLowerCase())) {
			return Severity.MAJOR;
		} else if ("minor".equals(severity.toLowerCase())) {
			return Severity.MINOR;
		} else if ("trivial".equals(severity.toLowerCase())) {
			return Severity.TRIVIAL;
		} else if (severity.toLowerCase().isEmpty()) {
			return null;
		} else {
			return Severity.UNKNOWN;
		}
	}
	
	/**
	 * Resolve status.
	 * 
	 * @param statusStr
	 *            the status str
	 * @return the status
	 */
	public static Status resolveStatus(final String statusStr) {
		if ("open".equals(statusStr.toLowerCase())) {
			return Status.NEW;
		} else if ("in progress".equals(statusStr.toLowerCase())) {
			return Status.IN_PROGRESS;
		} else if ("reopened".equals(statusStr.toLowerCase())) {
			return Status.REOPENED;
		} else if ("resolved".equals(statusStr.toLowerCase())) {
			return Status.CLOSED;
		} else if ("closed".equals(statusStr.toLowerCase())) {
			return Status.CLOSED;
		} else if ("patch reviewed".equals(statusStr.toLowerCase())) {
			return Status.VERIFIED;
		} else if ("ready to review".equals(statusStr.toLowerCase())) {
			return Status.REVIEWPENDING;
		} else {
			return Status.UNKNOWN;
		}
	}
	
	/**
	 * Resolve type.
	 * 
	 * @param typeStr
	 *            the type str
	 * @return the type
	 */
	public static Type resolveType(final String typeStr) {
		if ("bug".equals(typeStr.toLowerCase())) {
			return Type.BUG;
		} else if ("new feature".equals(typeStr.toLowerCase())) {
			return Type.RFE;
		} else if ("task".equals(typeStr.toLowerCase())) {
			return Type.TASK;
		} else if ("improvement".equals(typeStr.toLowerCase())) {
			return Type.IMPROVEMENT;
		} else if ("test".equals(typeStr.toLowerCase())) {
			return Type.TEST;
		} else if (typeStr.toLowerCase().isEmpty()) {
			return Type.OTHER;
		}
		return null;
	}
	
	/** The fetch time. */
	private DateTime           fetchTime;
	
	/** The resolver. */
	private Person             resolver;
	
	/** The md5. */
	private byte[]             md5;
	
	/** The report. */
	private XmlReport          xmlReport;
	
	/** The document. */
	private Document           document;
	
	/** The base uri. */
	private String             baseUri;
	
	/** The issue id. */
	private String             issueId;
	
	private Report             report;
	
	private boolean            parsed            = false;
	
	private PersonFactory      personFactory;
	
	/** The Constant DATE_TIME_PATTERN. */
	public static final String DATE_TIME_PATTERN = "({E}[A-Za-z]{3}),\\s+({dd}[0-3]?\\d)\\s+({MMM}[A-Za-z]{3,})\\s+({yyyy}\\d{4})\\s+({HH}[0-2]\\d):({mm}[0-5]\\d):({ss}[0-5]\\d)({Z}\\s[+-]\\d{4})";
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComponent()
	 */
	
	/**
	 * Instantiates a new jira parser.
	 * 
	 * @param personFactory
	 *            the person factory
	 */
	public JiraParser(final PersonFactory personFactory) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.personFactory = personFactory;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	
	/**
	 * Check raw.
	 * 
	 * @param rawReport
	 *            the raw report
	 * @return true, if successful
	 */
	private boolean checkRAW(final RawContent rawReport) {
		// PRECONDITIONS
		
		try {
			if (rawReport.getContent().contains("The issue you are trying to view does not exist.")) {
				if (Logger.logInfo()) {
					Logger.info("Ignoring report " + rawReport.getUri().toASCIIString()
					        + ". checkRaw() failed: issue seems not to exist.");
				}
				return false;
			}
			final Regex regex = new Regex("Access Denied.");
			final MultiMatch findAll = regex.findAll(rawReport.getContent());
			if (findAll != null) {
				if (Logger.logInfo()) {
					Logger.info("Ignoring report " + rawReport.getUri().toASCIIString()
					        + ". checkRaw() failed: issue requires special permission.");
				}
				return false;
			}
			
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Creates the document.
	 * 
	 * @param rawReport
	 *            the raw report
	 * @return the xml report
	 */
	private XmlReport createDocument(final RawContent rawReport) {
		PRECONDITIONS: {
			if (rawReport == null) {
				throw new NullPointerException("RawReport must never be null.");
			}
		}
		
		try {
			final BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
			
			try {
				final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
				                                                                      "org.ccil.cowan.tagsoup.Parser"));
				final org.jdom2.Document document = saxBuilder.build(reader);
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
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getAssignedTo()
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			final NodeList nodeList = this.document.getElementsByTagName("assignee");
			if (nodeList.getLength() > 0) {
				final Node assignee = nodeList.item(0);
				final String username = assignee.getAttributes().getNamedItem("username").getTextContent();
				if ("-1".equals(username)) {
					return null;
				}
				return getPersonFactory().get(username, assignee.getTextContent(), null);
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
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
		
		try {
			
			final NodeList attachments = this.document.getElementsByTagName("attachment");
			for (int i = 0; i < attachments.getLength(); ++i) {
				final Node attachmentNode = attachments.item(i);
				final NamedNodeMap attributes = attachmentNode.getAttributes();
				final Node idNode = attributes.getNamedItem("id");
				final Node nameNode = attributes.getNamedItem("name");
				final Node sizeNode = attributes.getNamedItem("size");
				final Node authorNode = attributes.getNamedItem("author");
				final Node createdNode = attributes.getNamedItem("created");
				
				if (idNode == null) {
					continue;
				}
				final AttachmentEntry attachmentEntry = new AttachmentEntry(idNode.getTextContent());
				if (authorNode != null) {
					attachmentEntry.setAuthor(getPersonFactory().get(authorNode.getTextContent(), null, null));
				}
				final StringBuilder linkBuilder = new StringBuilder();
				if (nameNode != null) {
					attachmentEntry.setFilename(nameNode.getTextContent());
					linkBuilder.append(this.baseUri);
					if (!this.baseUri.endsWith("/")) {
						linkBuilder.append("/");
					}
					linkBuilder.append("secure/attachment/");
					linkBuilder.append(idNode.getTextContent());
					linkBuilder.append("/");
					linkBuilder.append(nameNode.getTextContent());
				}
				if (sizeNode != null) {
					attachmentEntry.setSize(Long.valueOf(sizeNode.getTextContent()));
				}
				if (createdNode != null) {
					attachmentEntry.setTimestamp(DateTimeUtils.parseDate(createdNode.getTextContent(),
					                                                     new Regex(JiraParser.DATE_TIME_PATTERN)));
				}
				final String link = linkBuilder.toString();
				attachmentEntry.setLink(link);
				
				if (!link.isEmpty()) {
					try {
						attachmentEntry.setMime(MimeUtils.determineMIME(new URI(link)));
					} catch (final MIMETypeDeterminationException | IOException | UnsupportedProtocolException
					        | FetchException | URISyntaxException e) {
						if (Logger.logError()) {
							Logger.error(e);
						}
					}
				}
				
				result.add(attachmentEntry);
				
				// <attachment id="12430620" name="LUCENE-2222.patch" size="7674" author="mikemccand"
				// created="Mon, 18 Jan 2010 11:20:11 +0000" />
				
				// https://issues.apache.org/jira/secure/attachment/12430620/LUCENE-2222.patch
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
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		
		final SortedSet<Comment> result = new TreeSet<Comment>();
		try {
			final NodeList nodes = this.document.getElementsByTagName("comment");
			for (int i = 0; i < nodes.getLength(); ++i) {
				final Node commentNode = nodes.item(i);
				// <comment id="12801635" author="renaud.delbru" created="Mon, 18 Jan 2010 00:45:29 +0000" >
				final NamedNodeMap attributes = commentNode.getAttributes();
				final Node idNode = attributes.getNamedItem("id");
				final Node authorNode = attributes.getNamedItem("author");
				final Node createdNode = attributes.getNamedItem("created");
				
				if (idNode == null) {
					if (Logger.logWarn()) {
						Logger.warn("Found comment without ID. Ignoring comment entry.");
					}
					continue;
				}
				Person author = null;
				if (authorNode != null) {
					final String authorString = authorNode.getTextContent();
					final Regex emailRegex = new Regex(net.ownhero.dev.regex.util.Patterns.EMAIL_ADDRESS);
					if (emailRegex.matches(authorString)) {
						author = getPersonFactory().get(null, null, authorString);
					} else {
						author = getPersonFactory().get(authorString, null, null);
					}
				}
				DateTime timestamp = null;
				if (createdNode != null) {
					timestamp = DateTimeUtils.parseDate(createdNode.getTextContent(),
					                                    new Regex(JiraParser.DATE_TIME_PATTERN));
				}
				
				final Comment comment = new Comment(Integer.valueOf(idNode.getTextContent()).intValue(), author,
				                                    timestamp, commentNode.getTextContent());
				result.add(comment);
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
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			final NodeList nodeList = this.document.getElementsByTagName("component");
			if (nodeList.getLength() > 0) {
				return nodeList.item(0).getTextContent();
			}
			return null;
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
			final NodeList nodeList = this.document.getElementsByTagName("created");
			if (nodeList.getLength() > 0) {
				return DateTimeUtils.parseDate(nodeList.item(0).getTextContent(),
				                               new Regex(JiraParser.DATE_TIME_PATTERN));
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
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			final NodeList nodeList = this.document.getElementsByTagName("description");
			if (nodeList.getLength() > 1) {
				return nodeList.item(1).getTextContent();
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
	@Override
	public String getId() {
		// PRECONDITIONS
		
		try {
			return this.issueId;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getPriority()
	 */
	@Override
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			final Set<String> result = new HashSet<String>();
			
			final NodeList labels = this.document.getElementsByTagName("label");
			for (int i = 0; i < labels.getLength(); ++i) {
				result.add(labels.item(i).getTextContent());
			}
			
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getProduct()
	 */
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			final NodeList nodeList = this.document.getElementsByTagName("updated");
			if (nodeList.getLength() > 0) {
				final String dateString = nodeList.item(0).getTextContent();
				if (dateString.isEmpty()) {
					return null;
				}
				return DateTimeUtils.parseDate(dateString, new Regex(JiraParser.DATE_TIME_PATTERN));
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getMd5()
	 */
	@Override
	public final byte[] getMd5() {
		return this.md5;
	}
	
	/**
	 * @return the personFactory
	 */
	public final PersonFactory getPersonFactory() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.personFactory;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.personFactory,
				                  "Field '%s' in '%s'.", "personFactory", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSeverity()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			return Priority.NORMAL;
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
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getStatus()
	 */
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			final NodeList resolutions = this.document.getElementsByTagName("resolution");
			if (resolutions.getLength() > 0) {
				return resolveResolution(resolutions.item(0).getTextContent());
			}
			return Resolution.UNKNOWN;
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
		
		try {
			final NodeList nodeList = this.document.getElementsByTagName("resolved");
			if (nodeList.getLength() > 0) {
				final String dateString = nodeList.item(0).getTextContent();
				if (dateString.isEmpty()) {
					return null;
				}
				return DateTimeUtils.parseDate(dateString, new Regex(JiraParser.DATE_TIME_PATTERN));
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubmitter()
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
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
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
	 * @see org.mozkito.bugs.tracker.Parser#getType()
	 */
	@Override
	public Severity getSeverity() {
		// PRECONDITIONS
		
		try {
			final NodeList nodes = this.document.getElementsByTagName("priority");
			if (nodes.getLength() > 0) {
				return resolveSeverity(nodes.item(0).getTextContent());
			}
			return Severity.UNKNOWN;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
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
	 * @see org.mozkito.issues.tracker.Parser#getStatus()
	 */
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			final NodeList nodes = this.document.getElementsByTagName("status");
			if (nodes.getLength() > 0) {
				return resolveStatus(nodes.item(0).getTextContent());
			}
			return Status.UNKNOWN;
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
			final NodeList nodeList = this.document.getElementsByTagName("title");
			if (nodeList.getLength() > 1) {
				return nodeList.item(1).getTextContent();
			}
			return null;
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
			final NodeList nodeList = this.document.getElementsByTagName("reporter");
			if (nodeList.getLength() > 0) {
				final Node node = nodeList.item(0);
				final String name = node.getTextContent();
				final NamedNodeMap attributes = node.getAttributes();
				final Node usernameNode = attributes.getNamedItem("username");
				String username = null;
				if (usernameNode != null) {
					username = usernameNode.getTextContent();
				}
				return getPersonFactory().get(username, name, null);
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
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			final NodeList nodeList = this.document.getElementsByTagName("type");
			if (nodeList.getLength() > 0) {
				return resolveType(nodeList.item(0).getTextContent());
			}
			return Type.UNKNOWN;
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
			final NodeList nodeList = this.document.getElementsByTagName("version");
			if (nodeList.getLength() > 1) {
				return nodeList.item(1).getTextContent();
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getId()
	 */
	@Override
	public void parseHistoryElements(final History history) {
		// PRECONDITIONS
		
		try {
			// https://issues.apache.org/jira/browse/LUCENE-2222?page=com.atlassian.jira.plugin.system.issuetabpanels:changehistory-tabpanel#issue-tabs
			if (!this.parsed) {
				final StringBuilder sb = new StringBuilder();
				sb.append("https://issues.apache.org/jira/browse/");
				sb.append(getId());
				sb.append("?page=com.atlassian.jira.plugin.system.issuetabpanels:changehistory-tabpanel#issue-tabs");
				if (Logger.logDebug()) {
					Logger.debug("Fetching issue report history from %s", sb.toString());
				}
				final JiraHistoryParser historyParser = new JiraHistoryParser(new URI(sb.toString()),
				                                                              getPersonFactory());
				if (historyParser.parse(history)) {
					this.resolver = historyParser.getResolver();
					this.parsed = true;
				}
			}
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#setURI(org.mozkito.issues.tracker.ReportLink)
	 */
	@Override
	public Report setContext(final IssueTracker issueTracker,
	                         final ReportLink reportLink) {
		// PRECONDITIONS
		
		try {
			
			final URI uri = reportLink.getUri();
			this.issueId = reportLink.getBugId();
			RawContent rawContent = null;
			rawContent = IOUtils.fetch(uri);
			
			if (rawContent.getContent().trim().isEmpty()) {
				return null;
			}
			
			this.fetchTime = new DateTime();
			if (!checkRAW(rawContent)) {
				if (Logger.logWarn()) {
					Logger.warn("Could not parse report " + uri + ". RAW check failed!");
				}
				return null;
			}
			
			this.md5 = DigestUtils.md5(rawContent.getContent());
			
			this.xmlReport = createDocument(rawContent);
			
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			this.document = docBuilder.parse(new ByteArrayInputStream(this.xmlReport.getContent().getBytes()));
			this.baseUri = this.document.getElementsByTagName("link").item(0).getTextContent();
			this.report = new Report(issueTracker, getId());
			return this.report;
			
		} catch (final UnsupportedProtocolException | FetchException | ParserConfigurationException | SAXException
		        | IOException e) {
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
	 * @see org.mozkito.bugs.tracker.Parser#setURI(org.mozkito.bugs.tracker.ReportLink)
	 */
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	@Override
	public void setTracker(final Tracker tracker) {
		// PRECONDITIONS
		
		try {
		} finally {
			// POSTCONDITIONS
		}
	}
}
