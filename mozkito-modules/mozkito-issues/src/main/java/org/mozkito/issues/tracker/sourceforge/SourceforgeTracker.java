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
package org.mozkito.issues.tracker.sourceforge;

import java.net.URI;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.issues.elements.Type;
import org.mozkito.issues.exceptions.AuthenticationException;
import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.persons.elements.PersonFactory;

/**
 * The Class SourceforgeTracker.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class SourceforgeTracker extends Tracker {
	
	/** The issue links. */
	
	private Long groupId;
	
	/** The at id. */
	private Long atId;
	
	/** The bug type. */
	private Type bugType;
	
	/**
	 * Instantiates a new sourceforge tracker.
	 * 
	 * @param issueTracker
	 *            the issue tracker
	 * @param personFactory
	 *            the person factory
	 */
	public SourceforgeTracker(final IssueTracker issueTracker, final PersonFactory personFactory) {
		super(issueTracker, personFactory);
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
			// TODO Auto-generated method stub
			// return false;
			throw new RuntimeException("Method 'auth' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
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
	 * @see org.mozkito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new SourceforgeParser(this.bugType, getPersonFactory());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.OverviewParser#getReportLinks()
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
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final Long groupId,
	                  final Long atId,
	                  final Type bugType) throws InvalidParameterException {
		throw new UnrecoverableError(
		                             "Sourceforge changed its interface again. Please update parsers! BUT: bug ids changed. Ergo: do not mine sourceforge before 2020.");
		// this.groupId = groupId;
		// this.atId = atId;
		// this.bugType = bugType;
		// super.setup(fetchURI, username, password, proxyConfig);
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
