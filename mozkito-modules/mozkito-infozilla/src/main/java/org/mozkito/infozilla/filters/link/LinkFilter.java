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

package org.mozkito.infozilla.filters.link;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.Ostermiller.util.CSVParser;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.link.Link;

/**
 * The Class LinkFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class LinkFilter extends Filter<Link> {
	
	/** The Constant PATTERN_HTTP. */
	private static final String              SCHEME               = "({SCHEME}[0-9a-z.\\-+]+)";
	private static final String              PATTERN_URL          = "({LINK}" + LinkFilter.SCHEME + "://[^\\s\\)]+)";
	// private static final String PATTERN_FTP = "({LINK}ftps?://[^\\s\\)]+)";
	// assume no dots on scheme
	// private static final String PATTERN_URL =
	// "({LINK}(({scheme}(http|ftp)s?):)(//({authority}[^/?#\\n\\r]*?))({path}[^?#\\n\\r]*)(\\?({query}[^#\\n\\r]*))?(#({fragment}[\\n\\r]*))?)";
	
	/** The Constant VERIFY_URLS. */
	private static final boolean             VERIFY_URLS          = true;
	private static final int                 VERIFICATION_TIMEOUT = 30;
	private static final Map<String, String> schemeDefinitions    = new HashMap<>();
	private static final Map<URL, Boolean>   verificationCache    = new HashMap<>();
	
	static {
		readIanaPermanentUriSchemes();
		readIanaProvisionalUriSchemes();
	}
	
	/**
	 * Loading the URI schemes as defined in <a
	 * href="http://www.iana.org/assignments/uri-schemes/uri-schemes.xhtml">IANA - Uniform Resource Identifier (URI)
	 * Schemes</a>
	 */
	private static void readIanaPermanentUriSchemes() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			InputStream inputStream = null;
			inputStream = LinkFilter.class.getResourceAsStream("/IANA_permanent_schemes.csv");
			
			if (inputStream != null) {
				readIanaUriSchemes(inputStream);
			} else {
				// download
				try {
					final URL url = new URL("http://www.iana.org/assignments/uri-schemes/uri-schemes-1.csv");
					inputStream = url.openStream();
					if (inputStream != null) {
						readIanaUriSchemes(inputStream);
					}
				} catch (final IOException e) {
					throw new RuntimeException("Cannot fetch IANA permantn URI schemes.", e);
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	private static void readIanaProvisionalUriSchemes() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			InputStream inputStream = null;
			inputStream = LinkFilter.class.getResourceAsStream("/IANA_provisional_schemes.csv");
			
			if (inputStream != null) {
				readIanaUriSchemes(inputStream);
			} else {
				// download
				try {
					final URL url = new URL("http://www.iana.org/assignments/uri-schemes/uri-schemes-2.csv");
					inputStream = url.openStream();
					if (inputStream != null) {
						readIanaUriSchemes(inputStream);
					}
				} catch (final IOException e) {
					throw new RuntimeException("Cannot fetch IANA privisional URI schemes.", e);
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param inputStream
	 */
	private static void readIanaUriSchemes(final InputStream inputStream) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String inputLine = null;
				String[][] parsedLine = null;
				String scheme = null;
				String description = null;
				
				while ((inputLine = reader.readLine()) != null) {
					parsedLine = CSVParser.parse(inputLine);
					
					SANITY: {
						assert parsedLine != null;
						assert parsedLine.length > 0;
						assert parsedLine[0].length == 4;
					}
					
					scheme = parsedLine[0][0];
					description = parsedLine[0][2];
					
					LinkFilter.schemeDefinitions.put(scheme, description);
				}
			} catch (final IOException e) {
				throw new RuntimeException("Reading IANA URI definitions failed.", e);
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Instantiates a new link filter.
	 * 
	 */
	public LinkFilter() {
		super(Color.orange);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#apply(java.util.List, org.mozkito.infozilla.model.EnhancedReport)
	 */
	@Override
	protected void apply(final List<Link> results,
	                     final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			enhancedReport.setLinks(results);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param scheme2
	 * @return
	 */
	private String fixScheme(final String scheme2) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Comparator<String> schemeComparator = new Comparator<String>() {
				
				@Override
				public int compare(final String o1,
				                   final String o2) {
					PRECONDITIONS: {
						// none
					}
					
					try {
						if (o1.length() != o2.length()) {
							return Integer.compare(o2.length(), o1.length());
						} else {
							return o1.compareTo(o2);
						}
					} finally {
						POSTCONDITIONS: {
							// none
						}
					}
				}
			};
			
			final ArrayList<String> list = new ArrayList<>(LinkFilter.schemeDefinitions.keySet());
			Collections.sort(list, schemeComparator);
			
			for (final String scheme : list) {
				if (scheme2.endsWith(scheme)) {
					return scheme;
				}
			}
			
			return null;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#runFilter(java.lang.String)
	 */
	@Override
	protected List<Link> runFilter(final String inputText) {
		PRECONDITIONS: {
			if (inputText == null) {
				throw new NullPointerException();
			}
		}
		
		final List<Link> list = new LinkedList<>();
		
		try {
			final Regex regex = new Regex(LinkFilter.PATTERN_URL);
			
			final MultiMatch multiMatch = regex.findAll(inputText);
			if (multiMatch != null) {
				String urlString;
				String scheme;
				URL url;
				int start;
				int end;
				int offset = 0;
				
				MATCHES: for (final Match match : multiMatch) {
					SANITY: {
						assert match.hasNamedGroup("LINK");
						assert match.hasNamedGroup("SCHEME");
					}
					
					urlString = match.getGroup("LINK").getMatch();
					scheme = match.getGroup("SCHEME").getMatch();
					start = match.getGroup("LINK").start();
					end = match.getGroup("LINK").end();
					
					SANITY: {
						assert urlString != null;
						assert scheme != null;
						assert start >= 0;
						assert end <= inputText.length();
					}
					
					if (Logger.logDebug()) {
						Logger.debug("Analyzing URL: %s", urlString);
					}
					
					// fix SCHEME on overestimated matches
					if (!LinkFilter.schemeDefinitions.containsKey(scheme)) {
						final String fixScheme = fixScheme(scheme);
						
						if (fixScheme == null) {
							if (Logger.logWarn()) {
								Logger.warn("Regex '%s' for URL matched '%s' but the SCHEME '%s' is not valid.",
								            regex.getPattern(), urlString, scheme);
							}
							continue MATCHES;
						}
						
						if (!scheme.equals(fixScheme)) {
							if (Logger.logDebug()) {
								Logger.debug("fixing scheme to %s", fixScheme);
							}
							
							offset = scheme.length() - fixScheme.length();
							start += offset;
							urlString = urlString.substring(offset);
							scheme = fixScheme;
							
						}
					}
					
					try {
						url = new URL(urlString);
						final Link link = new Link(start, end, url, urlString, scheme,
						                           LinkFilter.schemeDefinitions.get(scheme));
						
						SANITY: {
							assert url != null;
						}
						
						if (LinkFilter.VERIFY_URLS && verifyURL(url)) {
							link.setVerified(true);
						}
						
						list.add(link);
					} catch (final MalformedURLException e) {
						if (Logger.logError()) {
							Logger.error("Found URL with valid SCHEME (%s) but intialization failed: %s", scheme,
							             urlString);
						}
						
					}
					
					// reset values
					start = 0;
					end = 0;
					urlString = null;
					url = null;
					scheme = null;
				}
			}
			
			return list;
		} finally {
			POSTCONDITIONS: {
				// non
			}
		}
	}
	
	/**
	 * Verify url.
	 * 
	 * @param url
	 *            the url
	 * @return true, if successful
	 */
	private boolean verifyURL(final URL url) {
		PRECONDITIONS: {
			if (url == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			if (!LinkFilter.verificationCache.containsKey(url)) {
				if (Logger.logDebug()) {
					Logger.debug("Trying to verify url '%s'.", url);
				}
				final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setAllowUserInteraction(false);
				connection.setConnectTimeout(LinkFilter.VERIFICATION_TIMEOUT * 1000);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("HEAD");
				connection.connect();
				final int responseCode = connection.getResponseCode();
				final boolean verified = (responseCode / 100) == 2; // SUCCESS CODES are 200-299
				LinkFilter.verificationCache.put(url, verified);
				connection.disconnect();
			}
			
			SANITY: {
				assert LinkFilter.verificationCache.containsKey(url);
			}
			
			return LinkFilter.verificationCache.get(url);
		} catch (final IOException | IllegalArgumentException e) {
			LinkFilter.verificationCache.put(url, false);
			return false;
		}
		
	}
	
}
