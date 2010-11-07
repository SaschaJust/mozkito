/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import de.unisaarland.cs.st.reposuite.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.bugs.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * {@link Tracker} is the super class all BTS classes have to extend. The
 * {@link Tracker} handles all mining/parsing/analyzing of a {@link BugReport}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Tracker {
	
	protected final TrackerType type        = TrackerType.valueOf(this
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
	protected boolean           initialized = false;
	private URI                 overviewURI;
	
	/**
	 * 
	 */
	public Tracker() {
		
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
	 * The method creates a {@link DocumentIterator} to which provides all XML
	 * documents for the bug reports under subject.
	 * 
	 * @return the created {@link DocumentIterator}
	 */
	public abstract DocumentIterator fetch();
	
	/**
	 * The method takes a bug report id and fetches the content in a string.
	 * 
	 * @param id
	 *            the bug id under subject
	 * @return the content of the bug report
	 */
	public abstract String fetch(final Long id);
	
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
				BufferedReader reader = new BufferedReader(new FileReader(new File(uri.getPath())));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append(FileUtils.lineSeparator);
				}
				reader.close();
				// TODO fix type determination
				return new Tuple<String, String>("XHTML", builder.toString());
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
	private String getHandle() {
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
	public abstract URI getLinkFromId(final Long bugId);
	
	/**
	 * this method should be synchronized
	 * 
	 * @return the next id that hasn't been requested.
	 */
	public abstract Long getNextId();
	
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
	 * sets up the current tracker
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
	 */
	public void setup(final URI fetchURI, final URI overviewURI, final String pattern, final String username,
	        final String password, final Long startAt, final Long stopAt) {
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
	}
	
}
