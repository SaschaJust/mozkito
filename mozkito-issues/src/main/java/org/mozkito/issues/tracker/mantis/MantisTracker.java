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
package org.mozkito.issues.tracker.mantis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class MantisTracker.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MantisTracker extends Tracker {
	
	// URL = https://issues.openbravo.com/print_bug_page.php?bug_id=19779
	
	private Collection<String> bugIds = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new MantisParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#getReportLinks()
	 */
	@Override
	public Collection<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			if (this.bugIds != null) {
				final Collection<ReportLink> links = new HashSet<>();
				for (final String id : this.bugIds) {
					try {
						links.add(MantisOverviewParser.getLinkFromId(getUri(), id));
					} catch (final URISyntaxException e) {
						if (Logger.logError()) {
							Logger.error(e);
						}
					}
				}
				return links;
			}
			final MantisOverviewParser overviewParser = new MantisOverviewParser(this);
			if (!overviewParser.parseOverview()) {
				throw new UnrecoverableError("Could not parse overview to extract bug report IDs. See earlier error.");
			}
			return overviewParser.getReportLinks();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public void setReportIds(final Collection<String> ids) {
		this.bugIds = ids;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#setup(java.net.URI, java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final ProxyConfig proxyConfig) throws InvalidParameterException {
		super.setup(fetchURI, username, password, proxyConfig);
	}
	
	/**
	 * Sets the uri.
	 * 
	 * @param uri
	 *            the new uri
	 */
	protected void setUri(final URI uri) {
		this.trackerURI = uri;
	}
	
}
