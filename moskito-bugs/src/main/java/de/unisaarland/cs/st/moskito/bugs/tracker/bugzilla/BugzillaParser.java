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
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

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
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import noNamespace.BugDocument.Bug;
import noNamespace.BugzillaDocument;
import noNamespace.BugzillaDocument.Bugzilla;

import org.apache.xmlbeans.XmlException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
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
	
	/** The tracker. */
	protected Tracker         tracker = null;
	
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkRAW(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.RawReport)
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkXML(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	protected boolean checkXML(final XmlReport xml) {
		
		try {
			final BugzillaDocument bugzillaDocument = BugzillaDocument.Factory.parse(xml.getContent());
			final Bugzilla bugzilla = bugzillaDocument.getBugzilla();
			final Bug[] bugArray = bugzilla.getBugArray();
			return bugArray.length == 1;
		} catch (final XmlException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	protected XmlReport createDocument(@NotNull final RawContent rawContent) {
		final BufferedReader reader = new BufferedReader(new StringReader(rawContent.getContent()));
		try {
			final SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			final Document document = saxBuilder.build(reader);
			reader.close();
			return new XmlReport(rawContent, document);
		} catch (final TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	@Override
	public final DateTime getFetchTime() {
		// PRECONDITIONS
		
		try {
			return this.xmlReport.getFetchTime();
		} finally {
			// POSTCONDITIONS
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
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
	 */
	@Override
	@NoneNull
	public final void setTracker(final Tracker tracker) {
		// PRECONDITIONS
		this.tracker = tracker;
		try {
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public final boolean setURI(final ReportLink reportLink) {
		
		try {
			final URI uri = reportLink.getUri();
			if (uri == null) {
				if (Logger.logError()) {
					Logger.error("Got URI from reportLink that is NULL!");
				}
				return false;
			}
			final RawContent rawContent = IOUtils.fetch(uri);
			if (!checkRAW(rawContent.getContent())) {
				if (Logger.logError()) {
					Logger.error("Failed to parse report " + uri.toASCIIString() + ": RAW check failed.");
				}
				return false;
			}
			this.xmlReport = createDocument(rawContent);
			if (!checkXML(this.xmlReport)) {
				if (Logger.logError()) {
					Logger.error("Failed to parse report " + uri.toASCIIString() + ": XML check failed.");
				}
				return false;
			}
			
			final BugzillaDocument document = BugzillaDocument.Factory.parse(rawContent.getContent());
			final Bugzilla bugzilla = document.getBugzilla();
			final Bug[] bugArray = bugzilla.getBugArray();
			
			if (bugArray.length < 1) {
				if (Logger.logWarn()) {
					Logger.warn("XML document contains no bugzilla bug reports.");
				}
				this.xmlBug = null;
				return false;
			} else if (bugArray.length > 1) {
				if (Logger.logWarn()) {
					Logger.warn("XML document contains multiple bugzilla bug reports. This is unexpected. Parsing only first report.");
				}
			}
			this.xmlBug = bugArray[0];
			return true;
		} catch (final XmlException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} finally {
			
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
