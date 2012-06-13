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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;

/**
 * The Class JiraTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JiraTracker extends Tracker implements OverviewParser {
	
	/** The overview ur is. */
	private final Set<ReportLink> overviewURIs = new HashSet<ReportLink>();
	
	/** The project key. */
	private String                projectKey;
	
	private RawContent fetch(final URI uri) throws UnsupportedProtocolException, FetchException {
		if (getProxyConfig() == null) {
			return IOUtils.fetch(uri);
		}
		return IOUtils.fetch(uri, getProxyConfig());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			final JiraParser parser = new JiraParser();
			if (getProxyConfig() != null) {
				parser.setProxyConfig(getProxyConfig());
			}
			return parser;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getReportLinks()
	 */
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			if (!parseOverview()) {
				return new HashSet<ReportLink>();
			}
			return this.overviewURIs;
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
		if (Logger.logInfo()) {
			Logger.info("Parsing overview. This might take a while ...");
		}
		try {
			
			int offset = 0;
			final int limit = 1000;
			
			boolean moreToCome = true;
			
			do {
				final StringBuilder sb = new StringBuilder();
				sb.append(getUri().toASCIIString());
				sb.append("/sr/jira.issueviews:searchrequest-rss/temp/SearchRequest.xml?jqlQuery=project=");
				sb.append(this.projectKey);
				sb.append("&tempMax=");
				sb.append(limit);
				sb.append("&pager/start=");
				sb.append(offset);
				
				if (Logger.logDebug()) {
					Logger.debug("Parsing overview URI %s", sb.toString());
				}
				
				final URI overviewUri = new URI(sb.toString());
				final RawContent overviewContent = fetch(overviewUri);
				
				final XMLReader parser = XMLReaderFactory.createXMLReader();
				final JiraIDExtractor handler = new JiraIDExtractor();
				parser.setContentHandler(handler);
				final InputSource inputSource = new InputSource(new StringReader(overviewContent.getContent()));
				parser.parse(inputSource);
				final List<String> bugIDs = handler.getIds();
				for (final String id : bugIDs) {
					final StringBuilder linkBuilder = new StringBuilder();
					linkBuilder.append(getUri());
					linkBuilder.append("/si/jira.issueviews:issue-xml/");
					linkBuilder.append(id);
					linkBuilder.append("/");
					linkBuilder.append(id);
					linkBuilder.append(".xml");
					this.overviewURIs.add(new ReportLink(new URI(linkBuilder.toString()), id));
				}
				moreToCome = bugIDs.size() == limit;
				offset += limit;
			} while (moreToCome);
			return true;
		} catch (final URISyntaxException | UnsupportedProtocolException | FetchException | IOException | SAXException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Setup.
	 * 
	 * @param fetchURI
	 *            the fetch uri
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param projectKey
	 *            the project key
	 * @param proxyConfig
	 *            the proxy config
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final String projectKey,
	                  final ProxyConfig proxyConfig) throws InvalidParameterException {
		
		if (Logger.logTrace()) {
			Logger.trace("Setting up JiraTracker with fetchURI=%s, username=%s, password=%s, projectKey=%s, proxyConfig=%s",
			             fetchURI == null
			                             ? "null"
			                             : fetchURI.toASCIIString(), username, password, projectKey,
			             proxyConfig == null
			                                ? "null"
			                                : proxyConfig.toString());
		}
		
		this.projectKey = projectKey;
		super.setup(fetchURI, username, password, proxyConfig);
	}
}
