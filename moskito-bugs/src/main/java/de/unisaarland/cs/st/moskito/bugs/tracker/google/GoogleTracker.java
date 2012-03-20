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
package de.unisaarland.cs.st.moskito.bugs.tracker.google;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import com.google.gdata.client.projecthosting.ProjectHostingService;
import com.google.gdata.data.projecthosting.IssuesEntry;
import com.google.gdata.data.projecthosting.IssuesFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;

/**
 * The Class GoogleTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GoogleTracker extends Tracker implements OverviewParser {
	
	protected static String       fetchRegexPattern = "((https?://code.google.com/feeds/issues/p/({=project}\\S+)/issues/full)|(https?://code.google.com/p/({=project}\\S+)/issues/list))";
	private String                projectName;
	private ProjectHostingService service;
	private Set<ReportLink>       overviewURIs;
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getLinkFromId(java .lang.Long)
	 */
	@Override
	public ReportLink getLinkFromId(final String bugId) {
		return new ReportLink(null, bugId);
	}
	
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
	
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			
			return this.overviewURIs;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public boolean parseOverview() {
		// PRECONDITIONS
		
		try {
			this.overviewURIs = new HashSet<ReportLink>();
			try {
				
				int startIndex = 1;
				final int maxResults = 100;
				
				IssuesFeed resultFeed = this.service.getFeed(new URL(this.fetchURI.toString() + "?start-index="
				        + startIndex + "&max-results=" + maxResults), IssuesFeed.class);
				if (Logger.logDebug()) {
					Logger.debug(this.fetchURI.toString() + "?start-index=" + startIndex + "&amp;max-results="
					        + maxResults);
				}
				List<IssuesEntry> feedEntries = resultFeed.getEntries();
				while (feedEntries.size() > 0) {
					for (int i = 0; i < feedEntries.size(); i++) {
						final IssuesEntry entry = feedEntries.get(i);
						final String bugId = entry.getIssueId().getValue().toString();
						this.overviewURIs.add(getLinkFromId(bugId));
						if (Logger.logDebug()) {
							Logger.debug("GOOGLE TRACKER: adding issue #" + bugId + " to process list.");
						}
					}
					startIndex += maxResults;
					resultFeed = this.service.getFeed(new URL(this.fetchURI.toString() + "?start-index=" + startIndex
					        + "&max-results=" + maxResults), IssuesFeed.class);
					if (Logger.logDebug()) {
						Logger.debug(this.fetchURI.toString() + "?start-index=" + startIndex + "&amp;max-results="
						        + maxResults);
					}
					feedEntries = resultFeed.getEntries();
				}
			} catch (final AuthenticationException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			} catch (final ServiceException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			}
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#setup(java.net.URI, java.net.URI, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public void setup(URI fetchURI,
	                  final URI overviewURI,
	                  final String pattern,
	                  final String username,
	                  final String password,
	                  final Long startAt,
	                  final Long stopAt,
	                  final File cacheDir) throws InvalidParameterException {
		
		final Regex fetchRegex = new Regex(fetchRegexPattern);
		final List<RegexGroup> groups = fetchRegex.find(fetchURI.toString());
		if ((groups == null) || (groups.size() < 2) || (fetchRegex.getGroup("project") == null)) {
			throw new UnrecoverableError("The specified fetchUri cannot be parser (is invalid). Abort.");
		}
		
		this.projectName = fetchRegex.getGroup("project");
		
		if (!fetchURI.toString().contains("feeds/issues")) {
			try {
				fetchURI = new URI("https://code.google.com/feeds/issues/p/" + this.projectName + "/issues/full");
			} catch (final URISyntaxException e) {
				throw new UnrecoverableError(e.getMessage(), e);
			}
		}
		
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt, cacheDir);
		
		this.service = new ProjectHostingService("unisaarland-reposuite-0.1");
		try {
			if ((this.username != null) && (this.password != null) && (!this.username.trim().equals(""))) {
				this.service.setUserCredentials(this.username, this.password);
			}
		} catch (final AuthenticationException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
}
