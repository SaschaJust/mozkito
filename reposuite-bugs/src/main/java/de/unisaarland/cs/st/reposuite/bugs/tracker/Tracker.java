/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.FilePermissionException;
import de.unisaarland.cs.st.reposuite.exceptions.LoadingException;
import de.unisaarland.cs.st.reposuite.exceptions.StoringException;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.IOUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.RawContent;
import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * {@link Tracker} is the super class all BTS classes have to extend. The
 * {@link Tracker} handles all mining/parsing/analyzing of a {@link Report}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Tracker {
	
	protected final TrackerType type             = TrackerType.valueOf(this
	                                                     .getClass()
	                                                     .getSimpleName()
	                                                     .substring(
	                                                             0,
	                                                             this.getClass().getSimpleName().length()
	                                                                     - Tracker.class.getSimpleName().length())
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
	protected PersonManager     personManager    = new PersonManager();
	protected File              cacheDir;
	
	public static String        bugIdPlaceholder = "<BUGID>";
	public static Regex         bugIdRegex       = new Regex("({bugid}<BUGID>)");
	
	/**
	 * 
	 */
	public Tracker() {
		Condition.check(!this.initialized);
		Condition.notNull(this.bugIds);
		Condition.notNull(this.personManager);
		Condition.notNull(bugIdPlaceholder);
		Condition.greater(bugIdPlaceholder.length(), 0);
		Condition.notNull(bugIdRegex);
		Condition.greater(bugIdRegex.getPattern().length(), 0);
	}
	
	/**
	 * @param id
	 */
	public void addBugId(final Long id) {
		this.bugIds.add(id);
	}
	
	/**
	 * The method takes a string containing one bug report and analyzes its
	 * content. If this method returns false, the report will be dropped from
	 * the corresponding {@link RepoSuiteToolchain}. Applications are broken
	 * documents, etc...
	 * 
	 * @param rawString
	 *            the bug report without further processing
	 * @return true if no error occurred
	 */
	public boolean checkRAW(final RawReport rawReport) {
		Condition.notNull(rawReport);
		
		boolean retval = true;
		return retval;
	}
	
	/**
	 * The method takes a XML document representing a bug report and checks this
	 * document for consistency, i.e. if all required nodes are available or if
	 * the document matches a given XML scheme. If this method returns false,
	 * the report will be dropped from the corresponding
	 * {@link RepoSuiteToolchain}. Applications are broken documents or
	 * unsupported versions.
	 * 
	 * @param xmlReport
	 *            the XML document representing a bug report
	 * @return true if no error occurred
	 */
	public boolean checkXML(final XmlReport xmlReport) {
		Condition.notNull(xmlReport);
		
		return xmlReport.getDocument().getRootElement() != null;
	}
	
	/**
	 * The method takes a bug report in raw format and creates the corresponding
	 * XML document.
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
		return new RawReport(reverseURI(uri), IOUtils.fetch(uri));
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
	 * Creates an {@link URI} that corresponds to the given bugId. This method
	 * is used to create {@link URI}s for the {@link Tracker#fetchSource(URI)}
	 * method.
	 * 
	 * @param bugId
	 *            the id of the bug an URI shall be created to
	 * @return the URI to the bug report.
	 */
	public URI getLinkFromId(final Long bugId) {
		try {
			return new URI(Tracker.bugIdRegex.replaceAll(this.fetchURI.toString() + this.pattern, bugId + ""));
		} catch (URISyntaxException e) {
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
		} catch (LoadingException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (FilePermissionException e) {
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
	public Report loadReport(final Long id) {
		Criteria criteria;
		try {
			criteria = HibernateUtil.getInstance().createCriteria(Report.class);
			criteria.add(Restrictions.eq("id", id));
			@SuppressWarnings ("unchecked") List<Report> list = criteria.list();
			
			if (list.size() > 0) {
				Report bugReport = list.get(0);
				return bugReport;
			}
		} catch (UninitializedDatabaseException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
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
		String[] split = this.pattern.split(Tracker.bugIdPlaceholder);
		String uriString = uri.toString();
		
		String tmpURI = uriString.substring(this.fetchURI.toString().length() + split[0].length(), uriString.length());
		String bugid = tmpURI.substring(0, split.length > 1 ? tmpURI.indexOf(split[1]) : tmpURI.length());
		
		try {
			return new Long(bugid);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * sets up the current tracker and fills the queue with the corresponding
	 * bug report ids
	 * 
	 * @param fetchURI
	 *            The {@link URI} to be appended by the pattern filled with the
	 *            bug id. If pattern is null, this is a direct link to a site
	 *            composing all reports in one document.
	 * @param overviewURI
	 *            The {@link URI} to an overview site where all bug ids can be
	 *            found. May be null.
	 * @param pattern
	 *            The pattern to be appended to the {@link URI} when fetching
	 *            bug reports. May be null.
	 * @param username
	 *            The username to be used to login to a bug tracking system. May
	 *            be null iff password is null.
	 * @param password
	 *            The password to be used to login to a bug tracking system. May
	 *            be null iff username is null.
	 * @param startAt
	 *            The first bug id to be mined. May be null.
	 * @param stopAt
	 *            The last bug id to be mined. May be null.
	 * @throws InvalidParameterException
	 */
	
	public void setup(final URI fetchURI, final URI overviewURI, final String pattern, final String username,
	        final String password, final Long startAt, final Long stopAt, final String cacheDirPath)
	        throws InvalidParameterException {
		Condition.notNull(fetchURI);
		Condition.check((username == null) == (password == null),
		        "Either username and password are set or none at all. username = `%s`, password = `%s`", username,
		        password);
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
				this.cacheDir = new File(cacheDirPath);
				try {
					FileUtils.ensureFilePermissions(this.cacheDir, FileUtils.ACCESSIBLE_DIR);
				} catch (FilePermissionException e) {
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
	public boolean writeContentToFile(final RawContent content, final String fileName) {
		Condition.notNull(content);
		Condition.notNull(fileName);
		Condition.greater(fileName.length(), 0);
		Condition.notNull(this.cacheDir);
		Condition.check(isInitialized());
		
		try {
			IOUtils.store(content, this.cacheDir, fileName, true);
			return true;
		} catch (StoringException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return false;
	}
	
}
