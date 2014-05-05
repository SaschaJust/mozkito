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
package org.mozkito.issues.tracker;

import java.net.URI;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.database.PersistenceUtil;
import org.mozkito.issues.exceptions.AuthenticationException;
import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.messages.Messages;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.persons.elements.PersonFactory;

/**
 * The Class Tracker.
 * 
 * {@link Tracker} is the super class all BTS classes have to extend. The {@link Tracker} handles all
 * mining/parsing/analyzing of a {@link Report}.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Tracker {
	
	/** The type. */
	protected final TrackerType       type        = TrackerType.valueOf(this.getClass()
	                                                                        .getSimpleName()
	                                                                        .substring(0,
	                                                                                   this.getClass().getSimpleName()
	                                                                                       .length()
	                                                                                           - Tracker.class.getSimpleName()
	                                                                                                          .length())
	                                                                        .toUpperCase());
	
	/** The tracker uri. */
	protected URI                     trackerURI;
	
	/** The username. */
	private String                    username;
	
	/** The password. */
	private String                    password;
	
	/** The report links. */
	private BlockingQueue<ReportLink> reportLinks = new LinkedBlockingQueue<ReportLink>();
	
	/** The issue tracker. */
	private final IssueTracker        issueTracker;
	
	/** The person factory. */
	private final PersonFactory       personFactory;
	
	/** The authenticated. */
	private boolean                   authenticated;
	
	/**
	 * Instantiates a new tracker.
	 * 
	 * @param issueTracker
	 *            the issue tracker
	 * @param personFactory
	 *            the person factory
	 */
	public Tracker(final IssueTracker issueTracker, final PersonFactory personFactory) {
		Condition.notNull(this.reportLinks, Messages.getString("Tracker.reportLinks_null")); //$NON-NLS-1$
		this.issueTracker = issueTracker;
		this.personFactory = personFactory;
	}
	
	/**
	 * Auth.
	 * 
	 * @return true, if successful
	 * @throws AuthenticationException
	 *             the authentication exception
	 */
	public abstract boolean auth() throws AuthenticationException;
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name of the current tracker instance
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
		
	}
	
	/**
	 * this method should be synchronized.
	 * 
	 * @return the next id that hasn't been requested.
	 */
	public final synchronized ReportLink getNextReportLink() {
		if (!this.reportLinks.isEmpty()) {
			final ReportLink next = this.reportLinks.poll();
			if (Logger.logTrace()) {
				Logger.trace("Providing next ReportLink %s", next); //$NON-NLS-1$
			}
			return next;
		}
		return null;
	}
	
	/**
	 * Gets the parser.
	 * 
	 * @return the parser
	 */
	public abstract Parser getParser();
	
	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Gets the person factory.
	 * 
	 * @return the personFactory
	 */
	public final PersonFactory getPersonFactory() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.personFactory;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.personFactory,
				                  "Field '%s' in '%s'.", "personFactory", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the report links.
	 * 
	 * @return the report links
	 */
	public abstract Collection<ReportLink> getReportLinks();
	
	/**
	 * This method returns the tracker type, determined by
	 * <code>TrackerType.valueOf(this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().length() - Tracker.class.getSimpleName().length()).toUpperCase());</code>
	 * 
	 * @return the type of the tracker
	 */
	public TrackerType getTrackerType() {
		return this.type;
	}
	
	/**
	 * Gets the uri.
	 * 
	 * @return the fetchURI
	 */
	public URI getUri() {
		return this.trackerURI;
	}
	
	/**
	 * Gets the username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		// PRECONDITIONS
		
		try {
			return this.username;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Checks if is authenticated.
	 * 
	 * @return true, if is authenticated
	 */
	public final boolean isAuthenticated() {
		return this.authenticated;
	}
	
	/**
	 * This method is used to fetch persistent reports from the database.
	 * 
	 * @param id
	 *            the id of the bug report
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the {@link Report}
	 */
	public Report loadReport(final Long id,
	                         final PersistenceUtil persistenceUtil) {
		PRECONDITIONS: {
			if (id == null) {
				throw new NullPointerException();
			}
			if (id <= 0) {
				throw new ArrayIndexOutOfBoundsException();
			}
			if (persistenceUtil == null) {
				throw new NullPointerException();
			}
		}
		
		return persistenceUtil.loadById(Report.class, id);
	}
	
	/**
	 * This method parses a XML document representing a bug report.
	 * 
	 * @param reportLink
	 *            the report link
	 * @return the report
	 */
	public final Report parse(final ReportLink reportLink) {
		final Parser parser = getParser();
		if (parser == null) {
			throw new UnrecoverableError(Messages.getString("Tracker.load_report_error")); //$NON-NLS-1$
		}
		
		if (Logger.logInfo()) {
			Logger.info("Parsing issue report %s ... ", reportLink.getBugId()); //$NON-NLS-1$
		}
		
		parser.setTracker(this);
		final Report report = parser.setContext(this.issueTracker, reportLink);
		if (report == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not parse report %s. See earlier error messages.", reportLink.toString()); //$NON-NLS-1$
			}
			return null;
		}
		
		report.setAttachmentEntries(parser.getAttachmentEntries());
		if (parser.getAssignedTo() != null) {
			report.setAssignedTo(parser.getAssignedTo());
		}
		report.setCategory(parser.getCategory());
		report.setComponent(parser.getComponent());
		
		for (final Comment comment : parser.getComments()) {
			report.addComment(comment);
		}
		report.setResolutionTimestamp(parser.getResolutionTimestamp());
		report.setCreationTimestamp(parser.getCreationTimestamp());
		report.setDescription(parser.getDescription());
		report.setHash(parser.getMd5());
		report.setLastFetch(parser.getFetchTime());
		report.setLastUpdateTimestamp(parser.getLastUpdateTimestamp());
		report.setPriority(parser.getPriority());
		report.setProduct(parser.getProduct());
		report.setResolution(parser.getResolution());
		if (parser.getResolver() != null) {
			report.setResolver(parser.getResolver());
		}
		report.setSiblings(new TreeSet<String>(parser.getSiblings()));
		report.setSeverity(parser.getSeverity());
		report.setStatus(parser.getStatus());
		report.setSubject(parser.getSubject());
		if (parser.getSubmitter() != null) {
			report.setSubmitter(parser.getSubmitter());
		}
		report.setSummary(parser.getSummary());
		report.setType(parser.getType());
		report.setVersion(parser.getVersion());
		report.setKeywords(parser.getKeywords());
		if (parser.getScmFixVersion() != null) {
			report.setScmFixVersion(parser.getScmFixVersion());
		}
		parser.parseHistoryElements(report.getHistory());
		return report;
	}
	
	/**
	 * Sets the authenticated.
	 * 
	 * @param value
	 *            the new authenticated
	 */
	protected final void setAuthenticated(final boolean value) {
		this.authenticated = value;
	}
	
	/**
	 * Sets the password.
	 * 
	 * @param password
	 *            the new password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}
	
	/**
	 * sets up the current tracker and fills the queue with the corresponding bug report ids.
	 * 
	 * @param fetchURI
	 *            The {@link URI} to be appended by the pattern filled with the bug id. If pattern is null, this is a
	 *            direct link to a site composing all reports in one document.
	 * @param username
	 *            The username to be used to login to a bug tracking system. May be null iff password is null.
	 * @param password
	 *            The password to be used to login to a bug tracking system. May be null iff username is null.
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password) throws InvalidParameterException {
		Condition.check((username == null) == (password == null),
		                "Either username and password are set or none at all. username = `%s`, password = `%s`", //$NON-NLS-1$
		                username, password);
		if (Logger.logTrace()) {
			Logger.trace("Setup"); //$NON-NLS-1$
		}
		
		this.trackerURI = fetchURI;
		setUsername(username);
		setPassword(password);
		
		// authenticate after setting auth info and before adding report links
		try {
			if (getPassword() != null) {
				auth();
			}
			
			if ((getPassword() != null) && !isAuthenticated()) {
				throw new InvalidParameterException("Password set but authentication failed.");
			}
		} catch (final AuthenticationException e) {
			throw new InvalidParameterException(e.getMessage(), e);
		}
		
		this.reportLinks = new LinkedBlockingDeque<ReportLink>();
		this.reportLinks.addAll(getReportLinks());
	}
	
	/**
	 * Sets the username.
	 * 
	 * @param username
	 *            the new username
	 */
	public void setUsername(final String username) {
		this.username = username;
	}
}
