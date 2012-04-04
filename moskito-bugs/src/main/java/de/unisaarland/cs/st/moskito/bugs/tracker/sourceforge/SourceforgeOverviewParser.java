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
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
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
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;

/**
 * The Class SourceforgeOverviewParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SourceforgeOverviewParser implements OverviewParser {
	
	/** The limit pattern. */
	protected static Regex        limitRegex  = new Regex("limit=({limit}\\d+)");
	/** The offset pattern. */
	protected static Regex        offsetRegex = new Regex("offset=({offset}\\d+)");
	
	/** The issue links. */
	private final Set<ReportLink> issueLinks  = new HashSet<ReportLink>();
	
	/** The base overview uri. */
	private String                baseOverviewURI;
	
	/** The base report uri. */
	private String                baseReportURI;
	private SourceforgeTracker    tracker;
	
	/**
	 * Instantiates a new sourceforge overview parser.
	 * 
	 * @param atId
	 *            the at id
	 * @param groupId
	 *            the group id
	 */
	public SourceforgeOverviewParser(final SourceforgeTracker tracker) {
		// PRECONDITIONS
		
		try {
			this.tracker = tracker;
			this.baseOverviewURI = "http://sourceforge.net/tracker/?group_id=" + this.tracker.getGroupId() + "&atid="
			        + this.tracker.getAtId() + "&limit=100&offset=";
			this.baseReportURI = "http://sourceforge.net/tracker/?func=detail&atid=" + this.tracker.getAtId()
			        + "&group_id=" + this.tracker.getGroupId() + "&aid=";
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
			
			int offset = 0;
			boolean running = true;
			while (running) {
				final String nextUri = this.baseOverviewURI + offset;
				offset += 100;
				final URL url = new URL(nextUri);
				final SourceforgeSummaryParser parseHandler = new SourceforgeSummaryParser();
				
				RawContent rawContent = null;
				if ((this.tracker != null) && (this.tracker.getProxyConfig() != null)) {
					rawContent = IOUtils.fetch(url.toURI(), this.tracker.getProxyConfig());
				} else {
					rawContent = IOUtils.fetch(url.toURI());
				}
				
				final String html = rawContent.getContent();
				
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
					
					final BufferedReader br = new BufferedReader(new StringReader(xml));
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
							this.issueLinks.add(new ReportLink(new URI(this.baseReportURI + id), id));
						}
					}
				} catch (final JDOMException e) {
					if (Logger.logError()) {
						Logger.error("Could not convert overview to XHTML!", e);
					}
					return false;
				} catch (final SAXException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
					return false;
				} catch (final URISyntaxException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
					return false;
				}
			}
			return true;
		} catch (final MalformedURLException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return false;
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return false;
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
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}
