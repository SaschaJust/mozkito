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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;

/**
 * The Class SourceforgeTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SourceforgeTracker extends Tracker implements OverviewParser {
	
	/** The group id pattern. */
	protected static Regex        groupIdRegex = new Regex("group_id=({group_id}\\d+)");
	
	/** The at id regex. */
	protected static Regex        atIdRegex    = new Regex("atid=({atid}\\d+)");
	
	/** The a id regex. */
	protected static Regex        aIdRegex     = new Regex("aid=({atid}\\d+)");
	
	/** The offset pattern. */
	protected static Regex        offsetRegex  = new Regex("offset=({offset}\\d+)");
	
	/** The limit pattern. */
	protected static Regex        limitRegex   = new Regex("limit=({limit}\\d+)");
	
	/** The issue links. */
	private final Set<ReportLink> issueLinks   = new HashSet<ReportLink>();
	
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getLinkFromId(java.lang.String)
	 */
	@Override
	public ReportLink getLinkFromId(final String bugId) {
		// PRECONDITIONS
		
		try {
			try {
				return new ReportLink(new URI(Tracker.bugIdRegex.replaceAll(this.fetchURI.toString() + this.pattern,
				                                                            bugId + "")), bugId);
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getOverviewParser()
	 */
	@Override
	public OverviewParser getOverviewParser() {
		// PRECONDITIONS
		
		try {
			return this;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new SourceforgeParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser#getReportLinks()
	 */
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			return this.issueLinks;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser#parseOverview()
	 */
	@Override
	public boolean parseOverview() {
		// PRECONDITIONS
		
		try {
			
			// FIXME require own argument for atid
			
			if (getOverviewURI() == null) {
				if (Logger.logError()) {
					Logger.error("No overview URI specified.");
				}
				return false;
			}
			
			final String uri = getOverviewURI().toASCIIString();
			
			groupIdRegex.find(getOverviewURI().toASCIIString());
			final String groupId = groupIdRegex.getGroup("group_id");
			if (groupId == null) {
				if (Logger.logError()) {
					Logger.error("Could not extract group_id from uri: " + uri.toString());
				}
			}
			atIdRegex.find(uri.toString());
			final String atId = atIdRegex.getGroup("atid");
			if (atId == null) {
				if (Logger.logError()) {
					Logger.error("Could not extract atid from uri: " + uri.toString());
				}
			}
			
			String baseUriString = uri.toString();
			
			limitRegex.find(uri.toString());
			final String limit = limitRegex.getGroup("limit");
			if (limit == null) {
				baseUriString += "&limit=100";
			} else {
				baseUriString = limitRegex.replaceAll(uri.toString(), "limit=100");
			}
			
			offsetRegex.find(uri.toString());
			final String offsetString = offsetRegex.getGroup("offset");
			if (offsetString != null) {
				baseUriString = offsetRegex.replaceAll(uri.toString(), "");
			}
			baseUriString += "&offset=";
			
			int offset = 0;
			boolean running = true;
			while (running) {
				final String nextUri = baseUriString + offset;
				offset += 100;
				final URL url = new URL(nextUri);
				final SourceforgeSummaryParser parseHandler = new SourceforgeSummaryParser();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
				
				final StringBuilder htmlSB = new StringBuilder();
				// write file to disk
				String line = "";
				while ((line = br.readLine()) != null) {
					htmlSB.append(line);
					htmlSB.append(FileUtils.lineSeparator);
				}
				br.close();
				
				final String html = htmlSB.toString();
				
				if ((html.contains("There was an error processing your request ..."))
				        || (html.contains("No results were found to match your current search criteria."))) {
					running = false;
					break;
				}
				
				final SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
				try {
					final Document document = saxBuilder.build(new StringReader(html));
					final XMLOutputter outp = new XMLOutputter();
					outp.setFormat(Format.getPrettyFormat());
					final String xml = outp.outputString(document);
					
					br = new BufferedReader(new StringReader(xml));
					final XMLReader parser = XMLReaderFactory.createXMLReader();
					parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
					parser.setContentHandler(parseHandler);
					final InputSource inputSource = new InputSource(br);
					parser.parse(inputSource);
					
					final Set<String> idSet = parseHandler.getIDs();
					if (idSet.size() < 1) {
						running = false;
					} else {
						for (final String id : idSet) {
							this.issueLinks.add(new ReportLink(new URI(getUri().toASCIIString() + getPattern()
							        + "&aid=" + id), id));
						}
					}
				} catch (final JDOMException e) {
					if (Logger.logError()) {
						Logger.error("Could not convert overview to XHTML!", e);
					}
					return false;
				} catch (final SAXException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
					return false;
				} catch (final URISyntaxException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
					return false;
				}
			}
			return true;
		} catch (final MalformedURLException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}
