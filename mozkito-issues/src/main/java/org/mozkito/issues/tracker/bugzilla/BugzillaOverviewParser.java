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
package org.mozkito.issues.tracker.bugzilla;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozkito.issues.tracker.OverviewParser;
import org.mozkito.issues.tracker.ReportLink;


/**
 * The Class BugzillaOverviewParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class BugzillaOverviewParser implements OverviewParser {
	
	/** The bug ur is. */
	private final Set<ReportLink> bugURIs = new HashSet<ReportLink>();
	
	/** The overview uri. */
	private final URI             overviewURI;
	
	/** The tracker uri. */
	private final String          trackerURI;
	
	/** The proxy config. */
	private final ProxyConfig     proxyConfig;
	
	/**
	 * Instantiates a new bugzilla overview parser.
	 * 
	 * @param trackerURI
	 *            the tracker uri
	 * @param overviewURI
	 *            the overview uri
	 * @param proxyConfig
	 *            the proxy config
	 */
	public BugzillaOverviewParser(@NotNull final URI trackerURI, @NotNull final URI overviewURI,
	        final ProxyConfig proxyConfig) {
		this.overviewURI = overviewURI;
		this.trackerURI = trackerURI.toASCIIString();
		this.proxyConfig = proxyConfig;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	private Object getHandle() {
		// PRECONDITIONS
		
		try {
			return getClass().getSimpleName();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the link from id.
	 * 
	 * @param id
	 *            the id
	 * @return the link from id
	 */
	private ReportLink getLinkFromId(final String id) {
		// PRECONDITIONS
		
		try {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.trackerURI);
			sb.append("/show_bug.cgi?ctype=xml&id=");
			sb.append(id);
			return new ReportLink(new URI(sb.toString()), id);
		} catch (final URISyntaxException e) {
			throw new UnrecoverableError(e);
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
			return this.bugURIs;
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
			
			RawContent content;
			if (this.overviewURI == null) {
				Condition.notNull(this.overviewURI, "Field '%s' in '%s'.", "overviewURI", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			try {
				if (this.proxyConfig != null) {
					content = IOUtils.fetch(this.overviewURI, this.proxyConfig);
				} else {
					content = IOUtils.fetch(this.overviewURI);
				}
			} catch (final UnsupportedProtocolException e1) {
				if (Logger.logError()) {
					Logger.error(e1);
				}
				return false;
			} catch (final FetchException e1) {
				if (Logger.logError()) {
					Logger.error(e1);
				}
				return false;
			}
			
			if (Logger.logTrace()) {
				Logger.trace("Parsing overview URI using JSoup.");
			}
			
			final Document document = Jsoup.parse(content.getContent());
			final Element bugzillabody = document.getElementById("bugzilla-body");
			if (bugzillabody == null) {
				if (Logger.logError()) {
					Logger.error("Could not parse bugzilla overview page: Could not find bugzilla-body <div>.");
				}
				return false;
			}
			final Elements tables = bugzillabody.getElementsByTag("table");
			Element table = null;
			for (final Element e : tables) {
				if (e.attr("class").contains("bz_buglist")) {
					table = e;
					break;
				}
			}
			if (table == null) {
				if (Logger.logError()) {
					Logger.error("Could not parse bugzilla overview page: Could not find bz_Buglist <table>.");
				}
				return false;
			}
			final Elements tbodies = table.getElementsByTag("tbody");
			if ((tbodies == null) || tbodies.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Could not parse bugzilla overview page: Could not find <tbody>.");
				}
				return false;
			}
			final Elements trs = tbodies.get(0).getElementsByTag("tr");
			for (final Element tr : trs) {
				if (!tr.attr("id").equals("")) {
					for (final Element td : tr.getElementsByTag("td")) {
						if (td.attr("class").contains("bz_id_column")) {
							try {
								final String id = td.text().trim();
								final ReportLink linkFromId = getLinkFromId(id);
								if (Logger.logTrace()) {
									Logger.trace("Adding %s to the list of parsed issue IDs.", linkFromId);
								}
								this.bugURIs.add(linkFromId);
							} catch (final NumberFormatException e) {
								if (Logger.logError()) {
									Logger.error("Could not interprete bug id " + td.text().trim()
									        + " as long. Ignoring bug id.");
								}
							}
						}
					}
				}
			}
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
