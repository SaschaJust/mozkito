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
package org.mozkito.issues.tracker.bugzilla;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Collection;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.persons.elements.PersonFactory;

/**
 * The Class BugzillaTracker.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class BugzillaTracker extends Tracker {
	
	// @Override
	// public ReportLink getLinkFromId(final String bugId) {
	// // PRECONDITIONS
	//
	// try {
	// try {
	// return new ReportLink(new URI(Tracker.bugIdRegex.replaceAll(this.trackerURI.toString() + this.pattern,
	// bugId + "")), bugId);
	// } catch (final URISyntaxException e) {
	// if (Logger.logError()) {
	// Logger.error(e);
	// }
	// return null;
	// }
	// } finally {
	// // POSTCONDITIONS
	// }
	// }
	
	/** The overview uri. */
	private URI    overviewURI;
	
	/** The bugzilla version. */
	private String bugzillaVersion;
	
	/**
	 * Instantiates a new bugzilla tracker.
	 * 
	 * @param issueTracker
	 *            the issue tracker
	 * @param personFactory
	 *            the person factory
	 */
	public BugzillaTracker(final IssueTracker issueTracker, final PersonFactory personFactory) {
		super(issueTracker, personFactory);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			
			// load all BugzillaParsers
			try {
				final Collection<Class<? extends BugzillaParser>> parserClasses = ClassFinder.getClassesExtendingClass(BugzillaParser.class.getPackage(),
				                                                                                                       BugzillaParser.class,
				                                                                                                       Modifier.ABSTRACT
				                                                                                                               | Modifier.INTERFACE
				                                                                                                               | Modifier.PRIVATE);
				for (final Class<? extends BugzillaParser> parserClass : parserClasses) {
					if (!Modifier.isAbstract(parserClass.getModifiers())) {
						parserClass.newInstance();
					}
				}
			} catch (final Exception e) {
				throw new UnrecoverableError(e);
			}
			
			// get the correct parser and set tracker.
			return BugzillaParser.getParser(this.bugzillaVersion, getPersonFactory());
			
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
			final BugzillaOverviewParser overviewParser = new BugzillaOverviewParser(this.trackerURI, this.overviewURI);
			if (!overviewParser.parseOverview()) {
				throw new UnrecoverableError("Could not parse overview URI.");
			}
			final Set<ReportLink> links = overviewParser.getReportLinks();
			if (Logger.logTrace()) {
				Logger.trace("Fetched %d report links.", links.size());
			}
			return links;
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
	 * @param overviewURI
	 *            the overview uri
	 * @param bugzillaVersion
	 *            the bugzilla version
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final URI overviewURI,
	                  final String bugzillaVersion) throws InvalidParameterException {
		if (Logger.logTrace()) {
			Logger.trace("Setup"); //$NON-NLS-1$
		}
		this.overviewURI = overviewURI;
		this.bugzillaVersion = bugzillaVersion;
		super.setup(fetchURI, username, password);
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
