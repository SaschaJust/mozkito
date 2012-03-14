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
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

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
	protected static Regex        groupIdPattern = new Regex("group_id=({group_id}\\d+)");
	
	/** The at id regex. */
	protected static Regex        atIdRegex      = new Regex("atid=({atid}\\d+)");
	
	/** The a id regex. */
	protected static Regex        aIdRegex       = new Regex("aid=({atid}\\d+)");
	
	/** The offset pattern. */
	protected static Regex        offsetPattern  = new Regex("offset=({offset}\\d+)");
	
	/** The limit pattern. */
	protected static Regex        limitPattern   = new Regex("limit=({limit}\\d+)");
	
	/** The issue links. */
	private final Set<ReportLink> issueLinks     = new HashSet<ReportLink>();
	
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
			return new SourceForgeParser();
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
	@SuppressWarnings ("unchecked")
	@Override
	public boolean parseOverview() {
		// PRECONDITIONS
		
		try {
			
			// FIXME require own argument for atid
			final List<List<RegexGroup>> findAll = atIdRegex.findAll(getPattern());
			if ((findAll == null) || (findAll.isEmpty())) {
				throw new UnrecoverableError(
				                             "Could not extract sourceforge ATID from tracker pattern. This is required! Please check your tracker pattern: "
				                                     + getPattern());
			}
			
			String atid = null;
			for (final List<RegexGroup> groupList : findAll) {
				for (final RegexGroup group : groupList) {
					if ((group.getName() != null) && (group.getName().equals(atid))) {
						atid = group.getMatch().trim();
					}
				}
			}
			
			if (atid == null) {
				throw new UnrecoverableError(
				                             "Could not extract sourceforge ATID from tracker pattern. This is required! Please check your tracker pattern: "
				                                     + getPattern());
			}
			StringBuilder overviewURLBuilder = new StringBuilder();
			overviewURLBuilder.append("http://sourceforge.net/api/artifact/index/tracker-id/");
			overviewURLBuilder.append(atid);
			overviewURLBuilder.append("/limit/200/rss");
			URL feedUrl = new URL(overviewURLBuilder.toString());
			
			final SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = null;
			try {
				feed = input.build(new XmlReader(feedUrl));
			} catch (final FeedException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			} catch (final IllegalArgumentException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			}
			List<SyndEntry> entries = feed.getEntries();
			int counter = 0;
			while ((entries != null) && (!entries.isEmpty())) {
				String aid = null;
				for (final SyndEntry entry : entries) {
					final String issuelink = StringEscapeUtils.unescapeHtml(entry.getLink());
					if (issuelink != null) {
						final List<List<RegexGroup>> atidHits = aIdRegex.findAll(issuelink);
						for (final List<RegexGroup> regexGroups : atidHits) {
							for (final RegexGroup group : regexGroups) {
								if ((group.getName() != null) & (group.getName().equals("atid"))) {
									aid = group.getMatch();
								}
							}
						}
					}
					if (aid != null) {
						this.issueLinks.add(new ReportLink(new URI(issuelink), aid));
					}
				}
				overviewURLBuilder = new StringBuilder();
				overviewURLBuilder.append("http://sourceforge.net/api/artifact/index/tracker-id/");
				overviewURLBuilder.append(atid);
				overviewURLBuilder.append("/limit/200");
				overviewURLBuilder.append("/offset/");
				overviewURLBuilder.append(++counter * 200);
				overviewURLBuilder.append("/rss");
				feedUrl = new URL(overviewURLBuilder.toString());
				try {
					feed = input.build(new XmlReader(feedUrl));
				} catch (final FeedException e) {
					break;
				} catch (final IllegalArgumentException e) {
					break;
				}
				entries = feed.getEntries();
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
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}
