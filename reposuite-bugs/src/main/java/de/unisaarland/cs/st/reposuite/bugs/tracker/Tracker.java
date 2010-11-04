/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
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
	
	protected Map<String, BugReport> bugReports = new HashMap<String, BugReport>();
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
	private URI                    uri;
	
	/**
	 * @param uri
	 *            the uri to the data
	 * @param url
	 *            the url to the tracker
	 * @param filter
	 *            the filename filter
	 */
	public Tracker(final URI uri, final URL url, final FilenameFilter filter) {
		this.setUri(uri);
		this.baseURL = url;
		this.filter = filter;
	}
	
	/**
	 * @param bugReport
	 */
	public void addBugReport(final BugReport bugReport) {
		this.bugReports.put(bugReport.getId() + "", bugReport);
	}
	
	/**
	 * @param id
	 * @return
	 */
	public BugReport getReport(final String id) {
		if (this.bugReports.containsKey(id)) {
			return this.bugReports.get(id);
		} else {
			
			Criteria criteria;
			try {
				criteria = HibernateUtil.getInstance().createCriteria(BugReport.class);
				
				// FIXME add criterion id = id
				// criteria.add();
				@SuppressWarnings ("unchecked") List<BugReport> list = criteria.list();
				
				if (list.size() > 0) {
					BugReport bugReport = list.get(0);
					addBugReport(bugReport);
					return bugReport;
				}
			} catch (UninitializedDatabaseException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
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
     * @param uri the uri to set
     */
    public void setUri(URI uri) {
	    this.uri = uri;
    }

	/**
     * @return the uri
     */
    public URI getUri() {
	    return uri;
    }
}
