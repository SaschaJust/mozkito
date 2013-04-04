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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import noNamespace.BugDocument.Bug;
import noNamespace.BugzillaDocument;
import noNamespace.BugzillaDocument.Bugzilla;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.xmlbeans.XmlException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.joda.time.DateTime;

import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.XmlReport;
import org.mozkito.persons.elements.PersonFactory;

/**
 * The Class BugzillaParser.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class BugzillaParser implements Parser {
	
	/** The history parser. */
	protected BugzillaHistoryParser                  historyParser   = null;
	
	/** The Constant parserVersions. */
	private static final Map<String, BugzillaParser> PARSER_VERSIONS = new HashMap<String, BugzillaParser>();
	
	/**
	 * Gets the parser.
	 * 
	 * @param bugzillaVersion
	 *            the bugzilla version
	 * @param personFactory
	 *            the person factory
	 * @return the parser. If no parser for version exists this method will return NULL.
	 */
	@NoneNull
	public static BugzillaParser getParser(final String bugzillaVersion,
	                                       final PersonFactory personFactory) {
		if (!BugzillaParser.PARSER_VERSIONS.containsKey(bugzillaVersion)) {
			if (Logger.logError()) {
				Logger.error("Bugzilla version " + bugzillaVersion
				        + " not yet supported! Please contact mozkito dev team.");
			}
		}
		return BugzillaParser.PARSER_VERSIONS.get(bugzillaVersion);
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
		switch (priorityString) {
			case "P1":
				return Priority.VERY_HIGH;
			case "P2":
				return Priority.HIGH;
			case "P3":
				return Priority.NORMAL;
			case "P4":
				return Priority.LOW;
			case "P5":
				return Priority.VERY_LOW;
			default:
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
		switch (resString) {
			case "FIXED":
				return Resolution.RESOLVED;
			case "INVALID":
				return Resolution.INVALID;
			case "WONTFIX":
				return Resolution.WONT_FIX;
			case "LATER":
				return Resolution.UNRESOLVED;
			case "REMIND":
				return Resolution.UNRESOLVED;
			case "DUPLICATE":
				return Resolution.DUPLICATE;
			case "WORKSFORME":
				return Resolution.WORKS_FOR_ME;
			case "NOT_ECLIPSE":
				return Resolution.INVALID;
			case "":
			case "---":
				return Resolution.UNRESOLVED;
			default:
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
		switch (serverityString) {
			case "blocker":
				return Severity.BLOCKER;
			case "critical":
				return Severity.CRITICAL;
			case "major":
				return Severity.MAJOR;
			case "normal":
				return Severity.NORMAL;
			case "minor":
				return Severity.MINOR;
			case "trivial":
				return Severity.TRIVIAL;
			case "enhancement":
				return Severity.ENHANCEMENT;
			default:
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
	protected static final Status getStatus(final String string) {
		final String statusString = string.toUpperCase();
		switch (statusString) {
			case "UNCONFIRMED":
				return Status.UNCONFIRMED;
			case "NEW":
				return Status.NEW;
			case "ASSIGNED":
				return Status.ASSIGNED;
			case "REOPENED":
				return Status.REOPENED;
			case "RESOLVED":
				return Status.CLOSED;
			case "VERIFIED":
				return Status.VERIFIED;
			case "CLOSED":
				return Status.CLOSED;
			default:
				return Status.UNKNOWN;
		}
	}
	
	/** The tracker. */
	protected Tracker           tracker = null;
	
	/** The supported versions. */
	private final Set<String>   supportedVersions;
	
	/** The xml report. */
	private XmlReport           xmlReport;
	
	/** The xml bug. */
	private Bug                 xmlBug;
	
	/** The md5. */
	private byte[]              md5;
	
	/** The report. */
	private Report              report;
	
	/** The person factory. */
	private final PersonFactory personFactory;
	
	/**
	 * Instantiates a new bugzilla parser.
	 * 
	 * @param personFactory
	 *            the person factory
	 * @param supportedVersions
	 *            the supported versions
	 */
	public BugzillaParser(final PersonFactory personFactory, final Set<String> supportedVersions) {
		this.supportedVersions = supportedVersions;
		for (final String supportedVersion : supportedVersions) {
			if (!BugzillaParser.PARSER_VERSIONS.containsKey(supportedVersion)) {
				BugzillaParser.PARSER_VERSIONS.put(supportedVersion, this);
			}
		}
		this.personFactory = personFactory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#checkRAW(de.unisaarland .cs.st.reposuite.bugs.tracker.RawReport)
	 */
	/**
	 * Check raw.
	 * 
	 * @param content
	 *            the content
	 * @return true, if successful
	 */
	protected boolean checkRAW(final String content) {
		if (content.contains("<bug error=\"NotFound\">")) {
			return false;
		}
		final Regex regex = new Regex("<head>\\s*<title>Format Not Found</title>");
		if (regex.matches(content)) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#checkXML(de.unisaarland .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	/**
	 * Check xml.
	 * 
	 * @param xml
	 *            the xml
	 * @return true, if successful
	 */
	protected boolean checkXML(@NotNull final XmlReport xml) {
		
		try {
			final BugzillaDocument bugzillaDocument = BugzillaDocument.Factory.parse(xml.getContent());
			final Bugzilla bugzilla = bugzillaDocument.getBugzilla();
			final Bug[] bugArray = bugzilla.getBugArray();
			return bugArray.length == 1;
		} catch (final XmlException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return false;
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#createDocument(de .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	/**
	 * Creates the document.
	 * 
	 * @param rawContent
	 *            the raw content
	 * @return the xml report
	 */
	protected XmlReport createDocument(@NotNull final RawContent rawContent) {
		final BufferedReader reader = new BufferedReader(new StringReader(rawContent.getContent()));
		try {
			
			final SAXBuilder saxBuilder = new SAXBuilder(
			                                             new XMLReaderSAX2Factory(false,
			                                                                      "org.apache.xerces.parsers.SAXParser"));
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			final Document document = saxBuilder.build(reader);
			reader.close();
			return new XmlReport(rawContent, document);
		} catch (final TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getFetchTime()
	 */
	/**
	 * Gets the fetch time.
	 * 
	 * @return the fetch time
	 */
	@Override
	public final DateTime getFetchTime() {
		// PRECONDITIONS
		
		try {
			return this.xmlReport.getFetchTime();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the history parser.
	 * 
	 * @return the history parser
	 */
	protected abstract BugzillaHistoryParser getHistoryParser();
	
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
	
	/**
	 * Gets the person factory.
	 * 
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
	
	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	public Report getReport() {
		return this.report;
	}
	
	/**
	 * Gets the supoorted versions.
	 * 
	 * @return the supoorted versions
	 */
	public final Set<String> getSupoortedVersions() {
		return this.supportedVersions;
	}
	
	/**
	 * Gets the xml bug.
	 * 
	 * @return the xml bug
	 */
	public Bug getXmlBug() {
		return this.xmlBug;
	}
	
	/**
	 * Gets the xml report.
	 * 
	 * @return the xml report
	 */
	public XmlReport getXmlReport() {
		return this.xmlReport;
	}
	
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
	public final Report setContext(final IssueTracker issueTracker,
	                               final ReportLink reportLink) {
		this.historyParser = null;
		try {
			final URI uri = reportLink.getUri();
			if (uri == null) {
				if (Logger.logError()) {
					Logger.error("Got URI from reportLink that is NULL!");
				}
				return null;
			}
			RawContent rawContent = null;
			
			rawContent = IOUtils.fetch(uri);
			
			if (!checkRAW(rawContent.getContent())) {
				if (Logger.logError()) {
					Logger.error("Failed to parse report " + uri.toASCIIString() + ": RAW check failed.");
				}
				return null;
			}
			this.md5 = DigestUtils.md5(rawContent.getContent());
			this.xmlReport = createDocument(rawContent);
			if (this.xmlReport == null) {
				if (Logger.logWarn()) {
					Logger.warn("Could not parse report %s. createDocument() returned NULL. See earlier errors.",
					            uri.toASCIIString());
				}
				return null;
			}
			if (!checkXML(this.xmlReport)) {
				if (Logger.logWarn()) {
					Logger.warn("Failed to parse report %s: XML check failed.", uri.toASCIIString());
				}
				return null;
			}
			
			final BugzillaDocument document = BugzillaDocument.Factory.parse(rawContent.getContent());
			final Bugzilla bugzilla = document.getBugzilla();
			final Bug[] bugArray = bugzilla.getBugArray();
			
			if (bugArray.length < 1) {
				if (Logger.logWarn()) {
					Logger.warn("XML document contains no bugzilla bug reports.");
				}
				this.xmlBug = null;
				return null;
			} else if (bugArray.length > 1) {
				if (Logger.logWarn()) {
					Logger.warn("XML document contains multiple bugzilla bug reports. This is unexpected. Parsing only first report.");
				}
			}
			this.xmlBug = bugArray[0];
			this.report = new Report(issueTracker, getId());
			return this.report;
		} catch (final XmlException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return null;
		} catch (final Exception e) {
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
	/**
	 * Sets the tracker.
	 * 
	 * @param tracker
	 *            the new tracker
	 */
	@Override
	@NoneNull
	public final void setTracker(final Tracker tracker) {
		// PRECONDITIONS
		try {
			this.tracker = tracker;
		} finally {
			// POSTCONDITIONS
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
