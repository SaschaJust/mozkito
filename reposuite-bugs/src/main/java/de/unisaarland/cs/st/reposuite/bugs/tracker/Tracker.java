/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jdom.Document;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import de.unisaarland.cs.st.reposuite.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * {@link Tracker} is the super class all BTS classes have to extend. The
 * {@link Tracker} handles all mining/parsing/analyzing of a {@link BugReport}.
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
	private BlockingQueue<Long> suspects         = new LinkedBlockingQueue<Long>();
	protected PersonManager     personManager    = new PersonManager();
	
	public static String        bugIdPlaceholder = "<BUGID>";
	public static Regex         bugIdRegex       = new Regex("({bugid}<BUGID>)");
	
	/**
	 * 
	 */
	public Tracker() {
		
	}
	
	public void addSuspect(final Long id) {
		this.suspects.add(id);
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
	public abstract boolean checkRAW(String rawReport);
	
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
	public abstract boolean checkXML(Document xmlReport);
	
	/**
	 * The method takes a bug report in raw format and creates the corresponding
	 * XML document.
	 * 
	 * @param rawReport
	 *            the raw bug report
	 * @return the bug report as XML document
	 */
	public abstract Document createDocument(String rawReport);
	
	/**
	 * This is method takes a {@link URI} and fetches the content to a string.
	 * 
	 * @param fetchURI
	 *            the fetchURI to the bug report
	 * @return a {@link Tuple} containing first the content type of the document
	 *         and second the content itself.
	 * @throws UnsupportedProtocolException
	 */
	
	public Tuple<String, String> fetchSource(final URI uri) throws UnsupportedProtocolException {
		assert (isInitialized());
		
		try {
			if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet(uri);
				HttpResponse response = httpClient.execute(request);
				HttpEntity entity = response.getEntity();
				
				return new Tuple<String, String>(response.getProtocolVersion().toString(), entity.toString());
			} else if (uri.getScheme().equals("file")) {
				StringBuilder builder = new StringBuilder();
				File file = new File(uri.getPath());
				if (file.exists() && file.isFile() && file.canRead()) {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
						builder.append(FileUtils.lineSeparator);
					}
					reader.close();
					
					// FIXME fix type determination
					return new Tuple<String, String>("XHTML", builder.toString());
				} else {
					
					if (Logger.logWarn()) {
						Logger.warn("Dropping: " + file.getAbsolutePath());
					}
					return null;
				}
			} else {
				throw new UnsupportedProtocolException(uri.getScheme());
			}
		} catch (ClientProtocolException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
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
		if (!this.suspects.isEmpty()) {
			return this.suspects.poll();
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
	 * This method is used to fetch persistent reports from the database
	 * 
	 * @param id
	 *            the id of the bug report
	 * @return the {@link BugReport}
	 */
	public BugReport loadReport(final Long id) {
		Criteria criteria;
		try {
			criteria = HibernateUtil.getInstance().createCriteria(BugReport.class);
			criteria.add(Restrictions.eq("id", id));
			@SuppressWarnings ("unchecked") List<BugReport> list = criteria.list();
			
			if (list.size() > 0) {
				BugReport bugReport = list.get(0);
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
	public abstract BugReport parse(Document document);
	
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
	        final String password, final Long startAt, final Long stopAt) throws InvalidParameterException {
		Preconditions.checkNotNull(fetchURI, "[setup] `fetchURI` should not be null.");
		Preconditions.checkArgument((username == null) == (password == null),
		        "[setup] Either username and password are set or none at all. username = `%s`, password = `%s`",
		        username, password);
		Preconditions.checkArgument(((startAt == null) || ((startAt != null) && (startAt > 0))),
		        "[setup] `startAt` must be null or > 0, but is: %s", startAt);
		Preconditions.checkArgument(((stopAt == null) || ((stopAt != null) && (stopAt > 0))),
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
		} else {
			if (Logger.logWarn()) {
				Logger.warn(getHandle() + " already initialized. Ignoring call to setup().");
			}
		}
		
		this.suspects = new LinkedBlockingDeque<Long>();
		
		// TODO when this method ends, suspects must be filled
	}
	
}
