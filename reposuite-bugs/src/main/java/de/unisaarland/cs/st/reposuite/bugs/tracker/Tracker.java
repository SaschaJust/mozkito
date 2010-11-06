/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
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

import de.unisaarland.cs.st.reposuite.bugs.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
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
	protected FilenameFilter    filter;
	protected URI               uri;
	protected String            username;
	protected String            password;
	protected String            startAt;
	protected String            stopAt;
	protected boolean           initialized = false;
	
	/**
	 * 
	 */
	public Tracker() {
		
	}
	
	public abstract boolean checkRAW(String rawString);
	
	public abstract boolean checkXML(Document second);
	
	public abstract Document createDocument(String second);
	
	/**
	 * @return
	 */
	public abstract DocumentIterator fetch();
	
	/**
	 * @param id
	 * @return
	 */
	public abstract Document fetch(final String id);
	
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
	
	public URI getLinkFromId(final String bugId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public synchronized String getNextId() {
		// TODO Auto-generated method stub
		return RandomUtils.nextInt() + "";
	}
	
	public TrackerType getTrackerType() {
		return this.type;
	}
	
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}
	
	/**
	 * @return
	 */
	protected boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public BugReport loadReport(final String id) {
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
	 * This method mines and parses a bug tracker. If {@link Tracker#parse(URI)}
	 * is given a <code>file://</code>, all files in the specified directory
	 * (uri) are parsed that match the given filter. Otherwise the data is
	 * fetched directly from the the corresponding URI.
	 */
	public abstract BugReport parse(Document document);
	
	/**
	 * @param uri
	 * @param baseUrl
	 * @param new filter
	 * @param username
	 * @param password
	 * @param startAt
	 * @param stopAt
	 */
	public void setup(final URI uri, final String baseUrl, final String filter, final String username,
	        final String password, final String startAt, final String stopAt) {
		this.uri = uri;
		this.baseURL = baseUrl;
		this.filter = new FilenameFilter() {
			
			@Override
			public boolean accept(final File dir, final String name) {
				Regex regex = new Regex(filter);
				return regex.matches(name);
			}
		};
		this.username = username;
		this.password = password;
		this.startAt = startAt;
		this.stopAt = stopAt;
		
		this.initialized = true;
	}
	
	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(final URI uri) {
		this.uri = uri;
	}
}
