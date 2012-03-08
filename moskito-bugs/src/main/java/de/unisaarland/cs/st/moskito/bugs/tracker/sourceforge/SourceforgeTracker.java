/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SourceforgeTracker extends Tracker {
	
	private static String   submittedPattern   = "({fullname}[^(]+)\\(\\s+({username}[^\\s]+)\\s+\\)\\s+-\\s+({timestamp}\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}.*)";
	
	protected static String groupIdPattern     = "group_id=({group_id}\\d+)";
	protected static String atIdPattern        = "atid=({atid}\\d+)";
	protected static String fileIdPattern      = "file_id=({fileid}\\d+)";
	protected static String offsetPattern      = "offset=({offset}\\d+)";
	protected static String limitPattern       = "limit=({limit}\\d+)";
	protected static String htmlCommentPattern = "(?#special condition to check wether we got a new line after or before the match to remove one of them)(?(?=<!--.*?-->$\\s)(?#used if condition was true)<!--.*?-->\\s|(?#used if condition was false)\\s?<!--.*?-->)";
	
	private static Priority buildPriority(final String value) {
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
	
	private static Resolution buildResolution(final String value) {
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
	
	private static Severity buildSeverity(final String value) {
		return Severity.UNKNOWN;
	}
	
	private static Status buildStatus(final String value) {
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
	
	private static Type buildType(final String value) {
		if (value.equalsIgnoreCase("BUG")) {
			return Type.BUG;
		} else if (value.equalsIgnoreCase("RFE")) {
			return Type.RFE;
		} else if (value.equalsIgnoreCase("TASK")) {
			return Type.TASK;
		} else if (value.equalsIgnoreCase("TEST")) {
			return Type.TEST;
		} else {
			return Type.OTHER;
		}
	}
	
	private final Regex subjectRegex = new Regex("({subject}.*)\\s+-\\s+ID:\\s+({bugid}\\d+)$");
	
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		boolean retValue = super.checkRAW(rawReport);
		
		// checking for the report to have at least 1000 characters
		retValue &= rawReport.getContent().length() > 1000;
		
		return retValue;
	}
	
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		final boolean retValue = super.checkXML(xmlReport);
		
		return retValue;
	}
	
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
		final BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		try {
			final SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
			final Document document = saxBuilder.build(reader);
			reader.close();
			return new XmlReport(rawReport, document);
		} catch (final TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		}
		throw new UnrecoverableError();
	}
	
	@Override
	public URI getLinkFromId(final Long bugId) {
		// PRECONDITIONS
		
		try {
			try {
				return new URI(Tracker.bugIdRegex.replaceAll(this.fetchURI.toString() + this.pattern, bugId + ""));
			} catch (final URISyntaxException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public OverviewParser getOverviewParser(final RawContent overviewContent) {
		// PRECONDITIONS
		
		try {
			if (Logger.logError()) {
				Logger.error("Overview parsing not supported yet.");
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser(final XmlReport xmlReport) {
		// PRECONDITIONS
		
		try {
			return new SourceForgeParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public void setup(final URI fetchURI,
	                  final URI overviewURI,
	                  final String pattern,
	                  final String username,
	                  final String password,
	                  final Long startAt,
	                  final Long stopAt,
	                  final File cacheDir) throws InvalidParameterException {
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt, cacheDir);
		
		this.initialized = true;
	}
}
