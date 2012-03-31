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
package de.unisaarland.cs.st.moskito.bugs.tracker.google;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kisa.Logger;

import com.google.gdata.client.projecthosting.ProjectHostingService;
import com.google.gdata.data.projecthosting.IssuesEntry;
import com.google.gdata.data.projecthosting.IssuesFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;

/**
 * The Class GoogleTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GoogleTracker extends Tracker {
	
	/** The fetch regex pattern. */
	protected static String       fetchRegexPattern = "((https?://code.google.com/feeds/issues/p/({=project}\\S+)/issues/full)|(https?://code.google.com/p/({=project}\\S+)/issues/list))";
	
	/** The project name. */
	private String                projectName;
	
	/** The service. */
	private ProjectHostingService service;
	
	public String getIssuesFeedUri() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getUri().toString());
		if (!getUri().toString().endsWith("/")) {
			sb.append("/");
		}
		sb.append(getProjectName());
		sb.append("/issues/full");
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new GoogleParser(this, this.service);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the project name.
	 * 
	 * @return the project name
	 */
	public String getProjectName() {
		return this.projectName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getReportLinks()
	 */
	@Override
	public Collection<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			
			final Set<ReportLink> overviewURIs = new HashSet<ReportLink>();
			try {
				
				int startIndex = 1;
				final int maxResults = 100;
				
				final String baseUrlStr = getIssuesFeedUri();
				IssuesFeed resultFeed = this.service.getFeed(new URL(baseUrlStr + "?start-index=" + startIndex
				        + "&max-results=" + maxResults), IssuesFeed.class);
				if (Logger.logDebug()) {
					Logger.debug(baseUrlStr + "?start-index=" + startIndex + "&amp;max-results=" + maxResults);
				}
				List<IssuesEntry> feedEntries = resultFeed.getEntries();
				while (feedEntries.size() > 0) {
					for (int i = 0; i < feedEntries.size(); i++) {
						final IssuesEntry entry = feedEntries.get(i);
						final String bugId = entry.getIssueId().getValue().toString();
						overviewURIs.add(new ReportLink(null, bugId));
						if (Logger.logDebug()) {
							Logger.debug("GOOGLE TRACKER: adding issue #" + bugId + " to process list.");
						}
					}
					startIndex += maxResults;
					resultFeed = this.service.getFeed(new URL(baseUrlStr + "?start-index=" + startIndex
					        + "&max-results=" + maxResults), IssuesFeed.class);
					if (Logger.logDebug()) {
						Logger.debug(baseUrlStr + "?start-index=" + startIndex + "&max-results=" + maxResults);
					}
					feedEntries = resultFeed.getEntries();
				}
			} catch (final AuthenticationException e) {
				throw new UnrecoverableError(e);
			} catch (final IOException e) {
				throw new UnrecoverableError(e);
			} catch (final ServiceException e) {
				throw new UnrecoverableError(e);
			}
			return overviewURIs;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Setup.
	 * 
	 * @param projectName
	 *            the project name
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(final String projectName,
	                  final ProxyConfig proxyConfig) throws InvalidParameterException {
		this.projectName = projectName;
		this.service = new ProjectHostingService("unisaarland-reposuite-0.1");
		
		try {
			final URI fetchURI = new URI("https://code.google.com/feeds/issues/p/");
			super.setup(fetchURI, getUsername(), getPassword(), proxyConfig);
		} catch (final URISyntaxException e) {
			throw new UnrecoverableError(e);
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
	 * @param projectName
	 *            the project name
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(final String username,
	                  final String password,
	                  final String projectName,
	                  final ProxyConfig proxyConfig) throws InvalidParameterException {
		this.projectName = projectName;
		
		// This should suffice as stated on gdata documents: http://code.google.com/apis/gdata/articles/proxy_setup.html
		System.setProperty("http.proxyHost", proxyConfig.getHost());
		System.setProperty("http.proxyPort", String.valueOf(proxyConfig.getPort()));
		
		this.service = new ProjectHostingService("unisaarland-reposuite-0.1");
		
		try {
			if ((getUsername() != null) && (getPassword() != null) && (!getUsername().trim().equals(""))) {
				this.service.setUserCredentials(getUsername(), getPassword());
			}
		} catch (final AuthenticationException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		try {
			final URI fetchURI = new URI("https://code.google.com/feeds/issues/p/");
			super.setup(fetchURI, username, password, proxyConfig);
		} catch (final URISyntaxException e) {
			throw new UnrecoverableError(e);
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
	 * @param projectName
	 *            the project name
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	protected void testSetup(final String projectName) throws InvalidParameterException {
		this.projectName = projectName;
		this.service = new ProjectHostingService("unisaarland-reposuite-0.1");
		try {
			this.trackerURI = new URI("https://code.google.com/feeds/issues/p/");
		} catch (final URISyntaxException e) {
			throw new UnrecoverableError(e);
		}
		
	}
}
