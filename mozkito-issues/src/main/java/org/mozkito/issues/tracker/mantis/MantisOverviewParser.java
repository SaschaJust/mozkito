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
package org.mozkito.issues.tracker.mantis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozkito.issues.tracker.OverviewParser;
import org.mozkito.issues.tracker.ReportLink;

/**
 * The Class MantisOverviewParser.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class MantisOverviewParser implements OverviewParser {
	
	private static final int MANTIS_BUG_ID_LENGTH = 7;
	
	/**
	 * Create a ReportLing instance.
	 * 
	 * @param uri
	 *            the the report can be found
	 * @param bugId
	 *            the bug id. Mantis bug IDs have leading zeros. The provided ID might be the short ID (no leading
	 *            zeros).
	 * @return the link from id
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	public static ReportLink getLinkFromId(final URI uri,
	                                       final String bugId) throws URISyntaxException {
		final StringBuilder sb = new StringBuilder();
		sb.append(uri.toASCIIString());
		sb.append("view.php?id=");
		sb.append(bugId);
		final StringBuilder bugIdBuilder = new StringBuilder();
		for (int i = 0; i < (MANTIS_BUG_ID_LENGTH - bugId.length()); ++i) {
			bugIdBuilder.append("0");
		}
		bugIdBuilder.append(bugId);
		if (Logger.logDebug()) {
			Logger.debug("Creating ReportLink with uri %s and bugId %s", sb.toString(), bugIdBuilder.toString());
		}
		return new ReportLink(new URI(sb.toString()), bugIdBuilder.toString());
	}
	
	/** The tracker uri. */
	private final MantisTracker   tracker;
	
	/** The report links. */
	private final Set<ReportLink> reportLinks = new HashSet<ReportLink>();
	
	/** The page regex. */
	private final Regex           pageRegex   = new Regex("\\?page_number=({page_number}\\d+)");
	
	/** The report regex. */
	private final Regex           reportRegex = new Regex("view.php\\?id=({bugid}\\d+)");
	
	/**
	 * Instantiates a new mantis overview parser.
	 * 
	 * @param tracker
	 *            the tracker
	 */
	public MantisOverviewParser(final MantisTracker tracker) {
		this.tracker = tracker;
	}
	
	/**
	 * Instantiates a new mantis overview parser.
	 * 
	 * @param uri
	 *            the uri
	 */
	protected MantisOverviewParser(final URI uri) {
		// PRECONDITIONS
		
		try {
			this.tracker = new MantisTracker();
			this.tracker.setUri(uri);
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Determine num pages.
	 * 
	 * @param uri
	 *            the uri
	 * @return the int
	 */
	protected int determineNumPages(final URI uri) {
		// PRECONDITIONS
		
		try {
			RawContent firstPage = null;
			if (this.tracker.getProxyConfig() != null) {
				firstPage = IOUtils.fetch(uri, this.tracker.getProxyConfig());
			} else {
				firstPage = IOUtils.fetch(uri);
			}
			
			final Document document = Jsoup.parse(firstPage.getContent());
			final Element buglistTable = document.getElementById("buglist");
			if (buglistTable == null) {
				throw new UnrecoverableError(
				                             "Could not find <table id=\"buglist\"> in overview HTML page. Maybe CSS changed.");
			}
			final Elements trs = buglistTable.getElementsByTag("tr");
			if ((trs == null) || (trs.isEmpty())) {
				if (Logger.logWarn()) {
					Logger.warn("Found <table id=\"buglist\"> table with no table rows.");
				}
				return 0;
			}
			final Elements tds = trs.get(0).getElementsByTag("td");
			if ((tds == null) || (tds.isEmpty()) || (tds.size() < 2)) {
				if (Logger.logWarn()) {
					Logger.warn("Found <table id=\"buglist\"> table with no or too less table columns in first row.");
				}
				return 0;
			}
			final Elements aTags = tds.get(1).getElementsByTag("a");
			if ((aTags == null) || (aTags.isEmpty())) {
				if (Logger.logWarn()) {
					Logger.warn("Could not find any page links.");
				}
				return 0;
			}
			
			final Element lastATag = aTags.get(aTags.size() - 1);
			final String href = lastATag.attr("href");
			final Match regexGroups = this.pageRegex.find(href);
			for (final Group regexGroup : regexGroups) {
				if ((regexGroup.getName() != null) && ("page_number".equals(regexGroup.getName()))) {
					return Integer.valueOf(regexGroup.getMatch());
				}
			}
			return 0;
			
		} catch (final UnsupportedProtocolException e) {
			throw new UnrecoverableError(e);
		} catch (final FetchException e) {
			throw new UnrecoverableError(e);
		} catch (final NumberFormatException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the link from id.
	 * 
	 * @param bugId
	 *            the bug id
	 * @return the link from id
	 */
	public ReportLink getLinkFromId(final String bugId) {
		// PRECONDITIONS
		
		try {
			return getLinkFromId(this.tracker.getUri(), bugId);
		} catch (final URISyntaxException e) {
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
	 * @see org.mozkito.bugs.tracker.OverviewParser#getReportLinks()
	 */
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			return this.reportLinks;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Handle page.
	 * 
	 * @param pageUri
	 *            the page uri
	 * @return the list
	 */
	protected List<ReportLink> handlePage(final URI pageUri) {
		// PRECONDITIONS
		
		final List<ReportLink> result = new LinkedList<ReportLink>();
		try {
			
			RawContent firstPage = null;
			if (this.tracker.getProxyConfig() != null) {
				firstPage = IOUtils.fetch(pageUri, this.tracker.getProxyConfig());
			} else {
				firstPage = IOUtils.fetch(pageUri);
			}
			final Document document = Jsoup.parse(firstPage.getContent());
			final Element buglistTable = document.getElementById("buglist");
			if (buglistTable == null) {
				throw new UnrecoverableError(
				                             "Could not find <table id=\"buglist\"> in overview HTML page. Maybe CSS changed.");
			}
			final Elements trs = buglistTable.getElementsByTag("tr");
			final int min_num_trs = 4;
			if ((trs == null) || (trs.isEmpty()) || (trs.size() < min_num_trs)) {
				if (Logger.logWarn()) {
					Logger.warn("Found <table id=\"buglist\"> table with less than 4 table rows.");
				}
				return result;
			}
			final int num_ignore_trs = 3;
			for (int i = num_ignore_trs; i < trs.size(); ++i) {
				final Element row = trs.get(i);
				final Elements aTags = row.getElementsByTag("a");
				if ((aTags == null) || (aTags.isEmpty())) {
					continue;
				}
				for (final Element aTag : aTags) {
					final String href = aTag.attr("href").trim();
					final Match find = this.reportRegex.find(href);
					if (find == null) {
						continue;
					}
					for (final Group regexGroup : find) {
						if ((regexGroup.getName() != null) && ("bugid".equals(regexGroup.getName()))) {
							
							final String bugId = regexGroup.getMatch();
							final StringBuilder bugIdBuilder = new StringBuilder();
							for (int j = 0; j < (MANTIS_BUG_ID_LENGTH - bugId.length()); ++j) {
								bugIdBuilder.append("0");
							}
							bugIdBuilder.append(bugId);
							if (Logger.logDebug()) {
								Logger.debug("Creating ReportLink with uri %s and bugId %s",
								             this.tracker.getUri().toASCIIString() + href, bugIdBuilder.toString());
							}
							result.add(getLinkFromId(this.tracker.getUri(), bugIdBuilder.toString()));
							break;
						}
					}
					break;
				}
			}
			return result;
		} catch (final UnsupportedProtocolException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return result;
		} catch (final FetchException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return result;
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.OverviewParser#parseOverview()
	 */
	@Override
	public boolean parseOverview() {
		// PRECONDITIONS
		
		try {
			final String firstPage = "view_all_bug_page.php";
			
			// determine the number of pages
			StringBuilder sb = new StringBuilder();
			sb.append(this.tracker.getUri().toASCIIString());
			sb.append("/");
			sb.append(firstPage);
			final int numPages = determineNumPages(new URI(sb.toString()));
			
			// handle each page
			for (int page = 1; page <= numPages; ++page) {
				sb = new StringBuilder();
				sb.append(this.tracker.getUri().toASCIIString());
				sb.append("/");
				sb.append(firstPage);
				sb.append("?page_number=");
				sb.append(page);
				
				final URI pageUri = new URI(sb.toString());
				this.reportLinks.addAll(handlePage(pageUri));
			}
			
			return true;
		} catch (final URISyntaxException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
