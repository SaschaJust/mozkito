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
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import com.atlassian.jira.rest.client.AuthenticationHandler;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

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
	
	private final Set<ReportLink> overviewURIs = new HashSet<ReportLink>();
	
	private NullProgressMonitor   pm;
	
	private JiraRestClient        restClient;
	
	@Override
	public ReportLink getLinkFromId(final String bugId) {
		// PRECONDITIONS
		
		try {
			final SearchResult searchJql = this.restClient.getSearchClient().searchJql("key=" + this.pattern + "-"
			                                                                                   + bugId, this.pm);
			if (searchJql.getTotal() < 1) {
				if (Logger.logError()) {
					Logger.error("Could not find an issue report with bugid=" + this.pattern + "-" + bugId);
				}
				return null;
			} else if (searchJql.getTotal() > 1) {
				if (Logger.logWarn()) {
					Logger.warn("Multiple issue reports found matching bugId=" + this.pattern + "-" + bugId
					        + ". Using first.");
				}
			}
			final BasicIssue issue = searchJql.getIssues().iterator().next();
			return new ReportLink(issue.getSelf(), this.pattern + "-" + bugId);
		} finally {
			// POSTCONDITIONS
		}
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
			return new JiraParser(this.restClient);
		} finally {
			// POSTCONDITIONS
		}
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
			final SearchResult searchJql = this.restClient.getSearchClient().searchJql("project=" + this.pattern,
			                                                                           this.pm);
			for (final BasicIssue issue : searchJql.getIssues()) {
				this.overviewURIs.add(new ReportLink(issue.getSelf(), issue.getKey()));
			}
			return true;
		} catch (final RestClientException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public void setup(@NotNull final URI fetchURI,
	                  final URI overviewURI,
	                  final String pattern,
	                  final String username,
	                  final String password,
	                  final Long startAt,
	                  final Long stopAt,
	                  final File cacheDir) throws InvalidParameterException {
		
		final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
		
		final DefaultApacheHttpClientConfig cc = new DefaultApacheHttpClientConfig();
		// TODO support PROXYs
		// cc.getProperties().put(DefaultApacheHttpClientConfig.PROPERTY_PROXY_URI,"proxy.ergogroup.no:3128");
		
		AuthenticationHandler authenticationHandler = new AnonymousAuthenticationHandler();
		if (username != null) {
			authenticationHandler = new BasicHttpAuthenticationHandler(username, password);
		}
		authenticationHandler.configure(cc);
		this.restClient = factory.create(fetchURI, authenticationHandler);
		this.pm = new NullProgressMonitor();
		
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt, cacheDir);
		
	}
}
