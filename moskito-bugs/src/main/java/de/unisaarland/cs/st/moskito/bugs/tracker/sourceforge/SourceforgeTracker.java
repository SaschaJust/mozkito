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
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import java.net.URI;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;

/**
 * The Class SourceforgeTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SourceforgeTracker extends Tracker {
	
	/** The issue links. */
	
	private Long groupId;
	
	/** The at id. */
	private Long atId;
	
	/** The bug type. */
	private Type bugType;
	
	/**
	 * Gets the at id.
	 * 
	 * @return the at id
	 */
	public Long getAtId() {
		return this.atId;
	}
	
	/**
	 * Gets the group id.
	 * 
	 * @return the group id
	 */
	public Long getGroupId() {
		return this.groupId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new SourceforgeParser(this.bugType);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser#getReportLinks()
	 */
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			final SourceforgeOverviewParser overviewParser = new SourceforgeOverviewParser(this);
			if (!overviewParser.parseOverview()) {
				throw new UnrecoverableError(
				                             "Could not parse sourceforge overview to extract report IDs. See earlier errors.");
			}
			return overviewParser.getReportLinks();
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
	 * @param groupId
	 *            the group id
	 * @param atId
	 *            the at id
	 * @param bugType
	 *            the bug type
	 * @param proxyConfig
	 *            the proxy config
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final Long groupId,
	                  final Long atId,
	                  final Type bugType,
	                  final ProxyConfig proxyConfig) throws InvalidParameterException {
		this.groupId = groupId;
		this.atId = atId;
		this.bugType = bugType;
		super.setup(fetchURI, username, password, proxyConfig);
	}
	
	protected void setUri(final URI uri) {
		this.trackerURI = uri;
	}
}
