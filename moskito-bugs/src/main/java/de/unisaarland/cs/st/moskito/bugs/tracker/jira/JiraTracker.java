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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;

import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;

/**
 * The Class JiraTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JiraTracker extends Tracker implements OverviewParser {
	
	private static Regex doesNotExistRegex = new Regex(
	                                                   "<title>Issue\\s+Does\\s+Not\\s+Exist\\s+-\\s+jira.codehaus.org\\s+</title>");
	
	private static Regex errorRegex        = new Regex(
	                                                   "<title>\\s+Oops\\s+-\\s+an\\s+error\\s+has\\s+occurred\\s+</title>");
	
	protected static String getHistoryURL(final URI uri) {
		final String xmlUrl = uri.toString();
		final int index = xmlUrl.lastIndexOf("/");
		final String suffix = xmlUrl.substring(index, xmlUrl.length());
		final String historyUrl = xmlUrl.replace("si/jira.issueviews:issue-xml/", "browse/");
		return historyUrl.replace(suffix,
		                          "?page=com.atlassian.jira.plugin.system.issuetabpanels:changehistory-tabpanel#issue-tabs");
	}
	
	private final Set<URI>      overviewURIs = new HashSet<URI>();
	
	private NullProgressMonitor pm;
	
	private JiraRestClient      restClient;
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkRAW(java.lang .String)
	 */
	@Override
	@NoneNull
	public boolean checkRAW(final RawReport rawReport) {
		if (!super.checkRAW(rawReport)) {
			return false;
		}
		if (doesNotExistRegex.matches(rawReport.getContent())) {
			return false;
		}
		if (errorRegex.matches(rawReport.getContent())) {
			return false;
		}
		if (!(rawReport.getUri().toString().endsWith(".xml") || (rawReport.getUri().toString().endsWith(".xhtml")))) {
			if (Logger.logWarn()) {
				Logger.warn("The Jira rawRepo uri is not an xml file.");
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		if (!super.checkXML(xmlReport)) {
			return false;
		}
		
		final Element rootElement = xmlReport.getDocument().getRootElement();
		
		if (rootElement == null) {
			if (Logger.logError()) {
				Logger.error("Root element is <null>");
			}
			return false;
		}
		
		if (!rootElement.getName().equals("rss")) {
			if (Logger.logError()) {
				Logger.error("Name of root element is not `rss`");
			}
			return false;
		}
		
		@SuppressWarnings ("rawtypes")
		final List children = rootElement.getChildren("channel", rootElement.getNamespace());
		
		if (children == null) {
			if (Logger.logError()) {
				Logger.error("No `channel` children.");
			}
			return false;
		}
		
		if (children.size() != 1) {
			if (Logger.logError()) {
				Logger.error("No `channel` children.");
			}
			return false;
		}
		
		@SuppressWarnings ("unchecked")
		final List<Element> items = rootElement.getChildren("channel", rootElement.getNamespace());
		if (items.get(0).getChildren("item", rootElement.getNamespace()).size() != 1) {
			if (Logger.logError()) {
				Logger.error("No `item` children.");
			}
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	@NoneNull
	public XmlReport createDocument(final RawReport rawReport) {
		
		final BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		try {
			final SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
			final Document document = saxBuilder.build(reader);
			reader.close();
			return new XmlReport(rawReport, document);
		} catch (final TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#fetchSource(java. net.URI)
	 */
	@Override
	@NoneNull
	public RawReport fetchSource(final URI uri) throws FetchException, UnsupportedProtocolException {
		// FIXME
		return null;
	}
	
	@Override
	public Set<? extends URI> getBugURIs() {
		// PRECONDITIONS
		try {
			
			return this.overviewURIs;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public URI getLinkFromId(final Long bugId) {
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
			return searchJql.getIssues().iterator().next().getSelf();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public OverviewParser getOverviewParser(final RawContent overviewContent) {
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
	public Parser getParser(final XmlReport xmlReport) {
		// PRECONDITIONS
		
		try {
			return new JiraParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public boolean parseOverview(final RawContent content) {
		// PRECONDITIONS
		
		try {
			final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
			// TODO support PROXYs
			this.restClient = factory.createWithBasicHttpAuthentication(this.fetchURI, this.username, this.password);
			this.pm = new NullProgressMonitor();
			try {
				final SearchResult searchJql = this.restClient.getSearchClient().searchJql("project=" + this.pattern,
				                                                                           this.pm);
				for (final BasicIssue issue : searchJql.getIssues()) {
					this.overviewURIs.add(issue.getSelf());
				}
			} catch (final RestClientException e) {
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
}
