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
package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * {@link Tracker} is the super class all BTS classes have to extend. The {@link Tracker} handles all
 * mining/parsing/analyzing of a {@link Report}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Tracker {
	
	protected final TrackerType       type          = TrackerType.valueOf(this.getClass()
	                                                                          .getSimpleName()
	                                                                          .substring(0,
	                                                                                     this.getClass()
	                                                                                         .getSimpleName().length()
	                                                                                             - Tracker.class.getSimpleName()
	                                                                                                            .length())
	                                                                          .toUpperCase());
	protected URI                     trackerURI;
	protected String                  username;
	protected String                  password;
	private BlockingQueue<ReportLink> reportLinks   = new LinkedBlockingQueue<ReportLink>();
	public final static Person        unknownPerson = new Person("<unknown>", null, null);
	
	/**
	 * 
	 */
	public Tracker() {
		Condition.notNull(this.reportLinks, "The bugId container must be initialized.");
	}
	
	/**
	 * @return the simple class name of the current tracker instance
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
		
	}
	
	/**
	 * this method should be synchronized
	 * 
	 * @return the next id that hasn't been requested.
	 */
	public final synchronized ReportLink getNextReportLink() {
		if (!this.reportLinks.isEmpty()) {
			return this.reportLinks.poll();
		} else {
			return null;
		}
	}
	
	public abstract Parser getParser();
	
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
	 * @return the fetchURI
	 */
	public URI getUri() {
		return this.trackerURI;
	}
	
	/**
	 * This method is used to fetch persistent reports from the database
	 * 
	 * @param id
	 *            the id of the bug report
	 * @return the {@link Report}
	 */
	public Report loadReport(final Long id,
	                         final PersistenceUtil persistenceUtil) {
		
		final Criteria<Report> criteria = persistenceUtil.createCriteria(Report.class).eq("id", id);
		final List<Report> list = persistenceUtil.load(criteria);
		if (list.size() > 0) {
			final Report bugReport = list.get(0);
			return bugReport;
		}
		return null;
	}
	
	/**
	 * This method parses a XML document representing a bug report.
	 */
	public final Report parse(final ReportLink reportLink) {
		final Parser parser = getParser();
		if (parser == null) {
			throw new UnrecoverableError(
			                             "Could not load bug report parser! Maybe your bug tracker version is not supported!");
		}
		
		if (Logger.logInfo()) {
			Logger.info("Parsing issue report " + reportLink.toString() + " ... ");
		}
		
		parser.setTracker(this);
		if (!parser.setURI(reportLink)) {
			if (Logger.logWarn()) {
				Logger.warn("Could not parse report " + reportLink.toString() + ". See earlier error messages.");
			}
			return null;
		}
		
		final String id = parser.getId();
		Condition.notNull(id, "The bug id returned by the parser may never be null.");
		
		final Report report = new Report(id);
		report.setAttachmentEntries(parser.getAttachmentEntries());
		if (parser.getAssignedTo() != null) {
			report.setAssignedTo(parser.getAssignedTo());
		}
		report.setCategory(parser.getCategory());
		report.setComponent(parser.getComponent());
		
		for (final Comment comment : parser.getComments()) {
			report.addComment(comment);
		}
		
		report.setCreationTimestamp(parser.getCreationTimestamp());
		report.setDescription(parser.getDescription());
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
		
		for (final HistoryElement helement : parser.getHistoryElements()) {
			report.addHistoryElement(helement);
		}
		
		return report;
	}
	
	/**
	 * sets up the current tracker and fills the queue with the corresponding bug report ids
	 * 
	 * @param fetchURI
	 *            The {@link URI} to be appended by the pattern filled with the bug id. If pattern is null, this is a
	 *            direct link to a site composing all reports in one document.
	 * @param overviewURI
	 *            The {@link URI} to an overview site where all bug ids can be found. May be null.
	 * @param pattern
	 *            The pattern to be appended to the {@link URI} when fetching bug reports. May be null.
	 * @param username
	 *            The username to be used to login to a bug tracking system. May be null iff password is null.
	 * @param password
	 *            The password to be used to login to a bug tracking system. May be null iff username is null.
	 * @param startAt
	 *            The first bug id to be mined. May be null.
	 * @param stopAt
	 *            The last bug id to be mined. May be null.
	 * @throws InvalidParameterException
	 */
	
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password) throws InvalidParameterException {
		Condition.check((username == null) == (password == null),
		                "Either username and password are set or none at all. username = `%s`, password = `%s`",
		                username, password);
		
		this.trackerURI = fetchURI;
		this.username = username;
		this.password = password;
		
		this.reportLinks = new LinkedBlockingDeque<ReportLink>();
		this.reportLinks.addAll(getReportLinks());
	}
}
