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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.ProxyConfig;
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
import com.atlassian.jira.rest.client.domain.ServerInfo;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
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
	
	/** The overview ur is. */
	private final Set<ReportLink> overviewURIs = new HashSet<ReportLink>();
	
	/** The pm. */
	private NullProgressMonitor   pm;
	
	/** The rest client. */
	private JiraRestClient        restClient;
	
	/** The project key. */
	private String                projectKey;
	
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
		if (Logger.logTrace()) {
			Logger.trace("Parsing overview.");
		}
		try {
			if (Logger.logTrace()) {
				Logger.trace("Building REST search query using project=%s.", this.projectKey);
			}
			
			// FIXME fetch more than 50 reports!
			final SearchResult searchJql = this.restClient.getSearchClient().searchJql("project=" + this.projectKey,
			                                                                           this.pm);
			int issueCounter = 0;
			for (final BasicIssue issue : searchJql.getIssues()) {
				this.overviewURIs.add(new ReportLink(issue.getSelf(), issue.getKey()));
				++issueCounter;
			}
			
			if (Logger.logDebug()) {
				Logger.debug("Found %s basic issue entries for project '%s'.", String.valueOf(issueCounter),
				             this.projectKey);
			}
			
			return true;
		} catch (final RestClientException e) {
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
			Logger.trace("Setting up JiraTracker with fetchURI=%s, username=%s, password=%s, projectKey=%s,proxyConfig=%s",
			             fetchURI == null
			                             ? "null"
			                             : fetchURI.toASCIIString(), username, password, projectKey,
			             proxyConfig == null
			                                ? "null"
			                                : proxyConfig.toString());
		}
		
		this.projectKey = projectKey;
		final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
		
		final DefaultApacheHttpClientConfig cc = new DefaultApacheHttpClientConfig();
		
		if (proxyConfig != null) {
			
			if (Logger.logTrace()) {
				Logger.trace("Configuring proxy.");
			}
			
			// support PROXYs: Atlassian has to enable support first final or we have final to extend the
			// JIRA version
			cc.getProperties().put(ApacheHttpClientConfig.PROPERTY_PROXY_URI,
			                       String.format("%s:%s", proxyConfig.getHost(), proxyConfig.getPort()));
			if (proxyConfig.getUsername() != null) {
				cc.getState().setProxyCredentials("Squid", proxyConfig.getHost(), proxyConfig.getPort(),
				                                  proxyConfig.getUsername(), proxyConfig.getPassword());
			}
			System.setProperty("http.proxyHost", proxyConfig.getHost());
			System.setProperty("http.proxyPort", String.valueOf(proxyConfig.getPort()));
			if (Logger.logWarn()) {
				Logger.warn("JIRA REST API does not support proxy usage yet. It's a missing feature from Atlassian. Configured proxy use but will most likely be ignored.");
			}
		}
		
		AuthenticationHandler authenticationHandler = new AnonymousAuthenticationHandler();
		if (username != null) {
			if (Logger.logTrace()) {
				Logger.trace("Configuring authentification handler.");
			}
			authenticationHandler = new BasicHttpAuthenticationHandler(username, password);
		}
		authenticationHandler.configure(cc);
		
		if (Logger.logTrace()) {
			Logger.trace("Creating REST client.");
		}
		this.restClient = factory.create(fetchURI, authenticationHandler);
		this.pm = new NullProgressMonitor();
		if (Logger.logTrace()) {
			final ServerInfo serverInfo = this.restClient.getMetadataClient().getServerInfo(this.pm);
			Logger.trace("JIRA REST API server scmInfo=%s.", serverInfo.getScmInfo());
			Logger.trace("JIRA REST API server baseURI=%s.", serverInfo.getBaseUri().toASCIIString());
			Logger.trace("JIRA REST API server buildNumber=%s.", serverInfo.getBuildNumber());
			Logger.trace("JIRA REST API server version=%s.", serverInfo.getVersion());
		}
		
		super.setup(fetchURI, username, password, proxyConfig);
	}
}
