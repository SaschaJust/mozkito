/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.issues.tracker.jira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.mozkito.issues.exceptions.AuthenticationException;
import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.tracker.OverviewParser;
import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.TrackerType;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.utilities.datastructures.RawContent;
import org.mozkito.utilities.io.IOUtils;
import org.mozkito.utilities.io.exceptions.FetchException;
import org.mozkito.utilities.io.exceptions.UnsupportedProtocolException;

import sun.reflect.ReflectionFactory;

/**
 * The Class JiraTracker.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class JiraTracker extends Tracker implements OverviewParser {
	
	/** The overview ur is. */
	private final Set<ReportLink> overviewURIs = new HashSet<ReportLink>();
	
	/** The project key. */
	private String                projectKey;
	private DefaultHttpClient     httpClient   = null;
	private static final String   URL_SUFFIX   = "/login";
	
	/**
	 * Instantiates a new jira tracker.
	 * 
	 * @param issueTracker
	 *            the issue tracker
	 * @param personFactory
	 *            the person factory
	 */
	public JiraTracker(final IssueTracker issueTracker, final PersonFactory personFactory) {
		super(issueTracker, personFactory);
		if (Logger.logInfo()) {
			Logger.info("Setting up new HTTP connector.");
		}
		this.httpClient = new DefaultHttpClient();
		this.httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		this.httpClient.setCookieStore(new BasicCookieStore());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.issues.tracker.Tracker#auth()
	 */
	@Override
	public boolean auth() throws AuthenticationException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (getPassword() != null) {
				if (getUsername() == null) {
					throw new AuthenticationException("Password set, but no username given.");
				}
				final String authURL = getUri() + URL_SUFFIX;
				
				if (Logger.logInfo()) {
					Logger.info("Authenticating at: " + authURL);
				}
				
				final HttpPost post = new HttpPost(authURL);
				
				try {
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("os_username", getUsername()));
					nameValuePairs.add(new BasicNameValuePair("os_password", getPassword()));
					nameValuePairs.add(new BasicNameValuePair("username", getUsername()));
					nameValuePairs.add(new BasicNameValuePair("password", getPassword()));
					nameValuePairs.add(new BasicNameValuePair("os_cookie", "true"));
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					final HttpResponse response = this.httpClient.execute(post);
					final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity()
					                                                                           .getContent()));
					String line = null;
					while ((line = rd.readLine()) != null) {
						System.out.println(line);
					}
					
					final List<Cookie> cookies = this.httpClient.getCookieStore().getCookies();
					if (Logger.logInfo()) {
						Logger.info("Received %s cookies.", cookies.size());
					}
					
					if (Logger.logDebug()) {
						for (final Cookie cookie : cookies) {
							Logger.debug(cookie.toString());
						}
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
				
				setAuthenticated(true);
			}
			
			return isAuthenticated();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Fetch.
	 * 
	 * @param uri
	 *            the uri
	 * @return the raw content
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 * @throws FetchException
	 *             the fetch exception
	 */
	private RawContent fetch(final URI uri) throws UnsupportedProtocolException, FetchException {
		PRECONDITIONS: {
			assert this.httpClient != null;
		}
		
		return IOUtils.fetchHttp(uri, this.httpClient);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new JiraParser(getPersonFactory());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#getReportLinks()
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
	 * @see org.mozkito.bugs.tracker.OverviewParser#parseOverview()
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
				
				final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();
				try {
					final Constructor<TrackerType> constructor = TrackerType.class.getConstructor(new Class<?>[0]);
					reflection.newConstructorAccessor(constructor).newInstance(new Object[] { "HAPPY", 3 });
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalArgumentException
				        | InvocationTargetException e1) {
					// TODO Auto-generated catch block
					
				}
				
				if (Logger.logDebug()) {
					Logger.debug("Parsing overview URI %s", sb.toString());
				}
				
				final URI overviewUri = new URI(sb.toString());
				final RawContent overviewContent = fetch(overviewUri);
				
				final XMLReader parser = XMLReaderFactory.createXMLReader();
				final JiraIDExtractor handler = new JiraIDExtractor();
				parser.setContentHandler(handler);
				final InputSource inputSource = new InputSource(new StringReader(overviewContent.getContent()));
				
				try {
					parser.parse(inputSource);
				} catch (final SAXException e) {
					if (Logger.logError()) {
						Logger.error("Request: " + overviewUri.toASCIIString());
						Logger.error("Result: " + overviewContent.getContent());
						Logger.error(e);
					}
					return false;
				}
				
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
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final String projectKey) throws InvalidParameterException {
		
		if (Logger.logTrace()) {
			Logger.trace("Setting up JiraTracker with fetchURI=%s, username=%s, password=%s, projectKey=%s, proxyConfig=%s",
			             fetchURI == null
			                             ? "null"
			                             : fetchURI.toASCIIString(), username, password, projectKey);
		}
		
		this.projectKey = projectKey;
		super.setup(fetchURI, username, password);
	}
}
