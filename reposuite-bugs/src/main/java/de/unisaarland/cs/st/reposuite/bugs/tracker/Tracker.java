/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Tracker {
	protected final TrackerType      type       = TrackerType.valueOf(this
			.getClass()
			.getSimpleName()
			.substring(
					0,
					this.getClass().getSimpleName().length()
					- Tracker.class.getSimpleName().length())
					.toUpperCase());
	protected DateTime               lastUpdate;
	protected URL                    baseURL;
	protected FilenameFilter         filter;
	protected URI               uri;
	protected String            username;
	protected String            password;
	protected String            startAt;
	protected String            stopAt;
	protected boolean           initialized = false;
	
	
	public Tracker() {
		
	}
	
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
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
	public abstract void parse();
	
	/**
	 * @param uri
	 * @param url
	 * @param filter
	 * @param username
	 * @param password
	 * @param startAt
	 * @param stopAt
	 */
	public abstract void setup(final URI uri, final URL url, final FilenameFilter filter, final String username,
			final String password, final String startAt, final String stopAt);
	
	/**
	 * @param uri the uri to set
	 */
	public void setUri(final URI uri) {
		this.uri = uri;
	}
}
