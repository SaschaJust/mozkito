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
package de.unisaarland.cs.st.moskito.bugs.tracker.google;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
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
	
	protected static String       fetchRegexPattern = "((https?://code.google.com/feeds/issues/p/({=project}\\S+)/issues/full)|(https?://code.google.com/p/({=project}\\S+)/issues/list))";
	private String                projectName;
	private ProjectHostingService service;
	
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
	public Collection<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			
			final Set<ReportLink> overviewURIs = new HashSet<ReportLink>();
			try {
				
				int startIndex = 1;
				final int maxResults = 100;
				
				IssuesFeed resultFeed = this.service.getFeed(new URL(getUri().toString() + "?start-index=" + startIndex
				        + "&max-results=" + maxResults), IssuesFeed.class);
				if (Logger.logDebug()) {
					Logger.debug(this.trackerURI.toString() + "?start-index=" + startIndex + "&amp;max-results="
					        + maxResults);
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
					resultFeed = this.service.getFeed(new URL(this.trackerURI.toString() + "?start-index=" + startIndex
					        + "&max-results=" + maxResults), IssuesFeed.class);
					if (Logger.logDebug()) {
						Logger.debug(this.trackerURI.toString() + "?start-index=" + startIndex + "&amp;max-results="
						        + maxResults);
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
	
	public void setup(final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final String projectName) throws InvalidParameterException {
		this.projectName = projectName;
		this.service = new ProjectHostingService("unisaarland-reposuite-0.1");
		try {
			if ((this.username != null) && (this.password != null) && (!this.username.trim().equals(""))) {
				this.service.setUserCredentials(this.username, this.password);
			}
		} catch (final AuthenticationException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		super.setup(fetchURI, username, password);
	}
}
