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
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.ioda.exceptions.LoadingException;
import net.ownhero.dev.ioda.exceptions.StoringException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.StringCondition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * {@link Tracker} is the super class all BTS classes have to extend. The {@link Tracker} handles all
 * mining/parsing/analyzing of a {@link Report}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Tracker {
	
	protected final TrackerType type             = TrackerType.valueOf(this.getClass()
	                                                                       .getSimpleName()
	                                                                       .substring(0,
	                                                                                  this.getClass().getSimpleName()
	                                                                                      .length()
	                                                                                          - Tracker.class.getSimpleName()
	                                                                                                         .length())
	                                                                       .toUpperCase());
	protected DateTime          lastUpdate;
	protected String            baseURL;
	protected String            pattern;
	protected URI               fetchURI;
	protected String            username;
	protected String            password;
	protected Long              startAt;
	protected Long              stopAt;
	protected boolean           initialized      = false;
	private URI                 overviewURI;
	private BlockingQueue<Long> bugIds           = new LinkedBlockingQueue<Long>();
	protected File              cacheDir;
	
	private static final String bugIdPlaceholder = "<BUGID>";
	private static final Regex  bugIdRegex       = new Regex("({bugid}<BUGID>)");
	
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
		Condition.notNull(this.bugIds, "The bugId container must be initialized.");
		Condition.notNull(bugIdPlaceholder, "bugIdPlaceholder must be set.");
		StringCondition.notEmpty(bugIdPlaceholder, "bugIdPlaceholder must not be empty");
		Condition.notNull(bugIdRegex, "bugIdRegex must be set.");
		StringCondition.notEmpty(bugIdRegex.getPattern(), "bugIdRegex must not be empty");
	}
	
	/**
	 * @param id
	 */
	public void addBugId(final Long id) {
		this.bugIds.add(id);
	}
	
	/**
	 * The method takes a string containing one bug report and analyzes its content. If this method returns false, the
	 * report will be dropped from the corresponding {@link RepoSuiteToolchain}. Applications are broken documents,
	 * etc...
	 * 
	 * @param rawString
	 *            the bug report without further processing
	 * @return true if no error occurred
	 */
	public boolean checkRAW(@NotNull final RawReport rawReport) {
		final boolean retval = true;
		return retval;
	}
	
	/**
	 * The method takes a XML document representing a bug report and checks this document for consistency, i.e. if all
	 * required nodes are available or if the document matches a given XML scheme. If this method returns false, the
	 * report will be dropped from the corresponding {@link RepoSuiteToolchain}. Applications are broken documents or
	 * unsupported versions.
	 * 
	 * @param xmlReport
	 *            the XML document representing a bug report
	 * @return true if no error occurred
	 */
	public boolean checkXML(@NotNull final XmlReport xmlReport) {
		return xmlReport.getDocument().getRootElement() != null;
	}
	
	/**
	 * The method takes a bug report in raw format and creates the corresponding XML document.
	 * 
	 * @param rawReport
	 *            the raw bug report
	 * @return the bug report as XML document
	 */
	public abstract XmlReport createDocument(RawReport rawReport);
	
	/**
	 * This is method takes a {@link URI} and fetches the content to a string.
	 * 
	 * @param fetchURI
	 *            the fetchURI to the bug report
	 * @return a {@link RawReport}
	 * @throws UnsupportedProtocolException
	 * @throws FetchException
	 */
	public RawReport fetchSource(final URI uri) throws FetchException, UnsupportedProtocolException {
		RawReport source = null;
		
		if (this.cacheDir != null) {
			
			String filename = uri.toString();
			final int index = filename.lastIndexOf('/');
			filename = filename.substring(index + 1);
			
			final File cacheFile = new File(this.cacheDir.getAbsolutePath() + FileUtils.fileSeparator + filename);
			if (cacheFile.exists()) {
				if (Logger.logInfo()) {
					Logger.info("Fetching report `" + uri.toString() + "` from cache directory ... ");
				}
				source = new RawReport(reverseURI(uri), fetchSource(cacheFile.toURI()));
				
			} else {
				
				source = new RawReport(reverseURI(uri), IOUtils.fetch(uri));
				writeContentToFile(source, filename);
			}
		} else {
			
			source = new RawReport(reverseURI(uri), IOUtils.fetch(uri));
		}
		return source;
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
	
	/**
	 * Creates an {@link URI} that corresponds to the given bugId. This method is used to create {@link URI}s for the
	 * {@link Tracker#fetchSource(URI)} method.
	 * 
	 * @param bugId
	 *            the id of the bug an URI shall be created to
	 * @return the URI to the bug report.
	 */
	public URI getLinkFromId(final Long bugId) {
		try {
			return new URI(Tracker.bugIdRegex.replaceAll(this.fetchURI.toString() + this.pattern, bugId + ""));
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
	}
	
	/**
	 * this method should be synchronized
	 * 
	 * @return the next id that hasn't been requested.
	 */
	public final synchronized Long getNextId() {
		if (!this.bugIds.isEmpty()) {
			return this.bugIds.poll();
		} else {
			return null;
		}
	}
	
	/**
	 * @return the overviewURI
	 */
	public URI getOverviewURI() {
		return this.overviewURI;
	}
	
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
	public abstract Report parse(XmlReport rawReport);
	
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
	                  final String cacheDirPath) throws InvalidParameterException {
		Condition.check((username == null) == (password == null),
		                "Either username and password are set or none at all. username = `%s`, password = `%s`",
		                username, password);
		Condition.check(((startAt == null) || ((startAt != null) && (startAt > 0))),
		                "`startAt` must be null or > 0, but is: %s", startAt);
		Condition.check(((stopAt == null) || ((stopAt != null) && (stopAt > 0))),
		                "[setup] `startAt` must be null or > 0, but is: %s", stopAt);
		
		if (!this.initialized) {
			this.fetchURI = fetchURI;
			this.overviewURI = overviewURI;
			this.pattern = pattern;
			this.username = username;
			this.password = password;
			this.startAt = startAt;
			this.stopAt = stopAt;
			this.initialized = true;
			if (cacheDirPath != null) {
				// FIXME use new IOUtils function
				this.cacheDir = new File(cacheDirPath);
				try {
					FileUtils.ensureFilePermissions(this.cacheDir, FileUtils.WRITABLE_DIR);
				} catch (final FilePermissionException e) {
					throw new InvalidParameterException("The cache directory is not valid. " + e.getMessage(), e);
				}
			}
		} else {
			if (Logger.logWarn()) {
				Logger.warn(getHandle() + " already initialized. Ignoring call to setup().");
			}
		}
		
		this.bugIds = new LinkedBlockingDeque<Long>();
		
		// TODO when this method ends, bugIds must be filled
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
