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

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.ioda.exceptions.LoadingException;
import net.ownhero.dev.ioda.exceptions.StoringException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.StringCondition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;

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
	
	protected final TrackerType       type             = TrackerType.valueOf(this.getClass()
	                                                                             .getSimpleName()
	                                                                             .substring(0,
	                                                                                        this.getClass()
	                                                                                            .getSimpleName()
	                                                                                            .length()
	                                                                                                - Tracker.class.getSimpleName()
	                                                                                                               .length())
	                                                                             .toUpperCase());
	protected DateTime                lastUpdate;
	protected String                  baseURL;
	protected String                  pattern;
	protected URI                     fetchURI;
	protected String                  username;
	protected String                  password;
	protected Long                    startAt;
	protected Long                    stopAt;
	protected boolean                 initialized      = false;
	private URI                       overviewURI;
	private BlockingQueue<ReportLink> reportLinks      = new LinkedBlockingQueue<ReportLink>();
	protected File                    cacheDir;
	
	protected static final String     bugIdPlaceholder = "<BUGID>";
	protected static final Regex      bugIdRegex       = new Regex("({bugid}<BUGID>)");
	
	public final static Person        unknownPerson    = new Person("<unknown>", null, null);
	
	/**
	 * @return
	 */
	public static String getBugidplaceholder() {
		return bugIdPlaceholder;
	}
	
	/**
	 * @return
	 */
	public static Regex getBugidregex() {
		return bugIdRegex;
	}
	
	/**
	 * 
	 */
	public Tracker() {
		Condition.check(!this.initialized, "The tracker must NOT be initialized at this point in time.");
		Condition.notNull(this.reportLinks, "The bugId container must be initialized.");
		Condition.notNull(bugIdPlaceholder, "bugIdPlaceholder must be set.");
		StringCondition.notEmpty(bugIdPlaceholder, "bugIdPlaceholder must not be empty");
		Condition.notNull(bugIdRegex, "bugIdRegex must be set.");
		StringCondition.notEmpty(bugIdRegex.getPattern(), "bugIdRegex must not be empty");
	}
	
	/**
	 * @param id
	 * @return
	 */
	public File getFileForContent(final long id) {
		return new File(this.cacheDir.getAbsolutePath() + FileUtils.fileSeparator + getHandle() + "_"
		        + this.fetchURI.getHost() + "_content_" + id);
	}
	
	/**
	 * @return the simple class name of the current tracker instance
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
		
	}
	
	public abstract ReportLink getLinkFromId(final String bugId);
	
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
	
	public abstract OverviewParser getOverviewParser();
	
	/**
	 * @return the overviewURI
	 */
	public URI getOverviewURI() {
		return this.overviewURI;
	}
	
	public abstract Parser getParser();
	
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
		return this.fetchURI;
	}
	
	/**
	 * @return true if the setup method had been called
	 */
	protected boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public RawContent loadContent(final long id) {
		try {
			return (RawContent) IOUtils.load(getFileForContent(id));
		} catch (final LoadingException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
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
	 * @param uri
	 * @return
	 */
	protected Long reverseURI(final URI uri) {
		// pattern = /bleh/<BUGID>3-blub/<BUGID>_3.xml
		final String[] split = this.pattern.split(Tracker.bugIdPlaceholder);
		final String uriString = uri.toString();
		
		final String tmpURI = uriString.substring(this.fetchURI.toString().length() + split[0].length(),
		                                          uriString.length());
		final String bugid = tmpURI.substring(0, split.length > 1
		                                                         ? tmpURI.indexOf(split[1])
		                                                         : tmpURI.length());
		
		try {
			return new Long(bugid);
		} catch (final NumberFormatException e) {
			return null;
		}
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
	                  final URI overviewURI,
	                  final String pattern,
	                  final String username,
	                  final String password,
	                  final Long startAt,
	                  final Long stopAt,
	                  final File cacheDir) throws InvalidParameterException {
		Condition.check((username == null) == (password == null),
		                "Either username and password are set or none at all. username = `%s`, password = `%s`",
		                username, password);
		// FIXME this should be handled at Settings level.
		Condition.check((overviewURI != null) || ((startAt != null) && (stopAt != null)),
		                "You must either specify a valid [startAt,stopAt] interval or provide a valid overviewURI. ");
		Condition.check(((startAt == null) || ((startAt != null) && (startAt > 0))),
		                "`startAt` must be null or > 0, but is: %s", startAt);
		Condition.check(((stopAt == null) || ((stopAt != null) && (stopAt > 0))),
		                "[setup] `startAt` must be null or > 0, but is: %s", stopAt);
		
		if (!this.initialized) {
			this.fetchURI = fetchURI;
			this.overviewURI = overviewURI;
			this.username = username;
			this.password = password;
			this.startAt = startAt;
			this.stopAt = stopAt;
			this.initialized = true;
			this.cacheDir = cacheDir;
			this.pattern = pattern;
			
			if (startAt == null) {
				this.startAt = 1l;
			}
			if (stopAt == null) {
				this.stopAt = Long.MAX_VALUE;
			}
		} else {
			if (Logger.logWarn()) {
				Logger.warn(getHandle() + " already initialized. Ignoring call to setup().");
			}
		}
		
		this.reportLinks = new LinkedBlockingDeque<ReportLink>();
		
		final OverviewParser overviewParser = getOverviewParser();
		if (overviewParser != null) {
			if (!overviewParser.parseOverview()) {
				throw new UnrecoverableError("Could not parse bug overview URI. See earlier errors.");
			}
			this.reportLinks.addAll(overviewParser.getReportLinks());
			if (Logger.logInfo()) {
				Logger.info("Added " + this.reportLinks.size() + " bug IDs while parsing overviewURI.");
			}
		} else {
			// what if no overviewParser?
			for (long i = startAt; i <= stopAt; ++i) {
				final ReportLink uri = getLinkFromId(String.valueOf(i));
				if (uri != null) {
					this.reportLinks.add(uri);
				}
			}
		}
	}
	
	/**
	 * @param content
	 * @return
	 */
	@NoneNull
	public boolean writeContentToFile(final RawContent content,
	                                  @NotEmpty final String fileName) {
		Condition.check(isInitialized(), "The tracker has to be initialized before using this method.");
		
		try {
			IOUtils.store(content, this.cacheDir, fileName, true);
			return true;
		} catch (final StoringException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return false;
	}
	
}
