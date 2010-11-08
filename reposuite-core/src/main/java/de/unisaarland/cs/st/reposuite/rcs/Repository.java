package de.unisaarland.cs.st.reposuite.rcs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.DefaultXYDataset;

import de.unisaarland.cs.st.reposuite.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.reposuite.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.rcs.mercurial.MercurialRepository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Preconditions;
import difflib.Delta;

/**
 * The Class Repository. Every repository connector that extends this class has
 * to be named [Repotype]Repository. E.g. DarksRepository. Additionally it is
 * mandatory to add a new enum constant in {@link RepositoryType}.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class Repository {
	
	/**
	 * Check if in the given URI the user name is set to the
	 * <code>username</code> argument. If this is not the case, try to replace
	 * the user name info in the authority part with the specified user name.
	 * 
	 * @param address
	 *            The original URI to be checked and modified if necessary
	 * @param username
	 *            the user name to be encoded into the URI
	 * @return the URI with encoded user name. If the encoding fails, the
	 *         original URI will be returned.
	 */
	public static URI encodeUsername(final URI address, final String username) {
		// [scheme:][//authority][path][?query][#fragment]
		// [user-info@]host[:port]
		
		if (username == null) {
			return address;
		}
		
		URI uri = address;
		String authority = address.getAuthority();
		if ((address.getUserInfo() == null) || (!address.getUserInfo().equals(username))) {
			if (Logger.logWarn()) {
				Logger.warn("Username provided and username specified in URI are not equal. Using username explicitely provided by method argument.");
			}
			authority = username + "@" + address.getHost();
			if (address.getPort() > -1) {
				authority += ":" + address.getPort();
			}
			StringBuilder uriString = new StringBuilder();
			uriString.append(address.getScheme());
			uriString.append("://");
			uriString.append(authority);
			uriString.append(address.getPath());
			if ((address.getQuery() != null) && (!address.getQuery().equals(""))) {
				uriString.append("?");
				uriString.append(address.getQuery());
			}
			if ((address.getFragment() != null) && (!address.getFragment().equals(""))) {
				uriString.append("#");
				uriString.append(address.getFragment());
			}
			try {
				uri = new URI(uriString.toString());
			} catch (URISyntaxException e1) {
				if (Logger.logError()) {
					Logger.error("Newly generated URI using the specified username cannot be parsed. URI = `"
					        + uriString.toString() + "`");
				}
				if (Logger.logWarn()) {
					Logger.warn("Falling back original URI.");
				}
				uri = address;
			}
		}
		return uri;
	}
	
	/**
	 * Annotate file specified by file path at given revision.
	 * 
	 * @param filePath
	 *            the file path to be annotated
	 * @param revision
	 *            the revision the file path will be annotated in
	 * @return List of AnnotationEntry for all lines starting by first line
	 */
	public abstract List<AnnotationEntry> annotate(String filePath, String revision);
	
	/**
	 * Checks out the given relative path in repository and returns the file
	 * handle to the checked out file. If the path is a directory, the file
	 * handle will point to the specified directory. If the relative path points
	 * to a file, the file handle will do so too.
	 * 
	 * @param relativeRepoPath
	 *            the relative repository path
	 * @param revision
	 *            the revision
	 * @return The file handle to the checked out, corresponding file or
	 *         directory.
	 */
	public abstract File checkoutPath(String relativeRepoPath, String revision);
	
	/**
	 * Checks the repository for corruption.
	 * 
	 * @param withInterface
	 */
	public void consistencyCheck(final List<LogEntry> logEntries, final boolean withInterface) {
		Preconditions.checkNotNull(logEntries);
		Preconditions.checkGreater(logEntries.size(), 0);
		
		LogEntry previous = null;
		
		for (LogEntry entry : logEntries) {
			// check monotonic timestamp property
			if (previous != null) {
				if (entry.getDateTime().isBefore(previous.getDateTime())) {
					System.out.println("Transaction " + entry.getRevision()
					        + " has timestamp before previous transaction: current " + entry + " vs. previous "
					        + previous);
				}
			}
			
			previous = entry;
		}
		
		if (withInterface) {
			ChartFrame frame;
			JFreeChart chart;
			chart = createTransactionsPerAuthor(logEntries, logEntries.size() / 35);
			frame = new ChartFrame("Bar Chart/Timestamp per transaction", chart);
			frame.pack();
			frame.setVisible(true);
			
			chart = createTimePerTransaction(logEntries);
			frame = new ChartFrame("Scatterplot/Timestamp per transaction", chart);
			frame.pack();
			frame.setVisible(true);
			
			chart = createFileCountPerTransaction(logEntries);
			frame = new ChartFrame("Histogram/Files per transaction", chart);
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	/**
	 * @param entries
	 * @return
	 */
	private JFreeChart createFileCountPerTransaction(final List<LogEntry> entries) {
		List<Double> revisions = new ArrayList<Double>(entries.size());
		List<Double> files = new ArrayList<Double>(entries.size());
		int i = 0;
		double[][] datapoints = new double[6][entries.size()];
		
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		BarRenderer.setDefaultShadowsVisible(false);
		
		for (LogEntry entry : entries) {
			revisions.add(Double.parseDouble(entry.getRevision()));
			Map<String, ChangeType> changedPaths = getChangedPaths(entry.getRevision());
			files.add((double) changedPaths.size());
			
			datapoints[0][i] = revisions.get(i);
			datapoints[1][i] = revisions.get(i);
			datapoints[2][i] = revisions.get(i);
			datapoints[3][i] = files.get(i);
			datapoints[4][i] = files.get(i);
			datapoints[5][i] = files.get(i);
			
			++i;
		}
		
		DefaultIntervalXYDataset idataset = new DefaultIntervalXYDataset();
		idataset.addSeries(new String("Files per revision"), datapoints);
		JFreeChart chart = ChartFactory.createXYBarChart("Files per revision", "revisions", false, "files", idataset,
		        PlotOrientation.VERTICAL, true, false, false);
		
		((XYBarRenderer) chart.getXYPlot().getRenderer()).setShadowVisible(false);
		
		return chart;
	}
	
	/**
	 * @param entries
	 * @return
	 */
	private JFreeChart createTimePerTransaction(final List<LogEntry> entries) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		double[][] datapoints = new double[2][entries.size()];
		
		List<Double> revisions = new ArrayList<Double>(entries.size());
		List<Double> times = new ArrayList<Double>(entries.size());
		
		for (LogEntry entry : entries) {
			// timestamp per revision
			revisions.add(Double.parseDouble(entry.getRevision()));
			times.add((double) entry.getDateTime().getMillis() / (1000));
		}
		
		for (int i = 0; i < revisions.size(); ++i) {
			datapoints[0][i] = revisions.get(i);
		}
		for (int i = 0; i < times.size(); ++i) {
			datapoints[1][i] = times.get(i);
		}
		
		dataset.addSeries(new String("time per revision"), datapoints);
		return ChartFactory.createScatterPlot("Timestamp Analysis of Repository", "time in s", "revisions", dataset,
		        PlotOrientation.VERTICAL, true, false, false);
	}
	
	/**
	 * @param entries
	 * @param threshold
	 * @return
	 */
	private JFreeChart createTransactionsPerAuthor(final List<LogEntry> entries, final double threshold) {
		Map<String, Double> authors = new HashMap<String, Double>();
		
		for (LogEntry entry : entries) {
			// commits
			String author = (entry.getAuthor().getUsername() != null ? entry.getAuthor().getUsername() : entry
			        .getAuthor().getFullname());
			if (authors.containsKey(author)) {
				authors.put(author, authors.get(author) + 1.0);
			} else {
				authors.put(author, 1.0);
			}
		}
		
		DefaultCategoryDataset cdataset = new DefaultCategoryDataset();
		
		double others = 0.0d;
		int otherCount = 0;
		for (String key : authors.keySet()) {
			if (authors.get(key) > entries.size() / 35) {
				cdataset.addValue(authors.get(key), key, new String("authors"));
			} else {
				others += authors.get(key);
				++otherCount;
			}
		}
		cdataset.addValue(others, "others (" + otherCount + ")", new String("authors"));
		
		return ChartFactory.createBarChart("Commits per Author (threshold " + 100d * threshold / entries.size() + "%)",
		        "history", "commits", cdataset, PlotOrientation.VERTICAL, true, false, false);
	}
	
	/**
	 * Diff the file in the repository specified by filePath.
	 * 
	 * @param filePath
	 *            the file path to be analyzed. This path must be relative.
	 * @param baseRevision
	 *            the revision used as basis for comparison.
	 * @param revisedRevision
	 *            the revised revision
	 * @return Collection of deltas found between two revision
	 */
	public abstract Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision);
	
	/**
	 * Gets the files that changed within the corresponding transaction.
	 * 
	 * @param revision
	 *            the revision to be analyzed
	 * @return the changed paths
	 */
	public abstract Map<String, ChangeType> getChangedPaths(String revision);
	
	/**
	 * Gets the first revision of the repository.
	 * 
	 * @return the first revision id
	 */
	public abstract String getFirstRevisionId();
	
	/**
	 * Determines the former path name of the file/directory.
	 * 
	 * @param revision
	 *            (not null)
	 * @param pathName
	 *            (not null)
	 * @return Returns the former path name iff the file/directory was renamed.
	 *         Null otherwise.
	 */
	public abstract String getFormerPathName(String revision, String pathName);
	
	/**
	 * Determines the simple class name of the object.
	 * 
	 * @return this.getClass().getSimpleName();
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	public String getHEAD() {
		return "HEAD";
	}
	
	/**
	 * Gets the last revision of the repository.
	 * 
	 * @return the last revision id
	 */
	public abstract String getLastRevisionId();
	
	/**
	 * Returns the relative transaction id to the given one. Result is bounded
	 * by startRevision and endRevision.
	 * 
	 * @param transactionId
	 * @param index
	 * @return
	 */
	public abstract String getRelativeTransactionId(String transactionId, long index);
	
	/**
	 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
	 * @return the {@link RepositoryType} of the connector class determined by
	 *         naming convention. See the java-doc of {@link Repository} for
	 *         details.
	 */
	public final RepositoryType getRepositoryType() {
		return RepositoryType.valueOf(this.getClass().getSimpleName()
		        .substring(0, this.getClass().getSimpleName().length() - Repository.class.getSimpleName().length())
		        .toUpperCase());
	}
	
	/**
	 * @return the total number of revisions in the repository, -1 if error
	 *         occured
	 */
	public abstract long getTransactionCount();
	
	/**
	 * Returns the transaction id string to the transaction determined by the
	 * given index.
	 * 
	 * @param index
	 *            Starts at 0
	 * @return the corresponding transaction id (e.g. for reposuite
	 *         {@link MercurialRepository#getTransactionId(long)} returns
	 *         021e7e97724b for 3.
	 */
	public abstract String getTransactionId(long index);
	
	/**
	 * @param revision
	 * @return
	 */
	public RCSTransaction loadTransaction(final String revision) {
		try {
			HibernateUtil hibernateUtil = HibernateUtil.getInstance();
			Criteria criteria = hibernateUtil.createCriteria(RCSTransaction.class);
			criteria.add(Restrictions.eq("id", revision));
			@SuppressWarnings ("unchecked") List<RCSTransaction> results = criteria.list();
			if ((results != null) && (results.size() > 0)) {
				return results.get(0);
			}
		} catch (UninitializedDatabaseException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	/**
	 * Extract a log from the repository.
	 * 
	 * @param fromRevision
	 *            the from revision
	 * @param toRevision
	 *            the to revision
	 * @return the list of log entries. The first entry is the oldest log entry.
	 */
	public abstract List<LogEntry> log(String fromRevision, String toRevision);
	
	/**
	 * Extract a log from the repository spanning between two revisions.
	 * 
	 * @param fromRevision
	 *            the from revision
	 * @param toRevision
	 *            the to revision
	 * @param cacheSize
	 *            the cache size
	 * @return Iterator running from <code>fromRevisions</code> to
	 *         <code>toRevision</code>
	 */
	public Iterator<LogEntry> log(final String fromRevision, final String toRevision, final int cacheSize) {
		return new LogIterator(this, fromRevision, toRevision, cacheSize);
	}
	
	/**
	 * Connect to repository at URI address.
	 * 
	 * @param address
	 *            the address the repository can be found
	 * @param startRevision
	 *            first revision to take into account (may be null)
	 * @param endRevision
	 *            last revision to take into account (may be null)
	 * @throws MalformedURLException
	 *             the malformed URL exception
	 * @throws InvalidProtocolType
	 *             the invalid protocol type
	 * @throws InvalidRepositoryURI
	 *             the invalid repository URI
	 * @throws UnsupportedProtocolType
	 *             the unsupported protocol type
	 */
	public abstract void setup(URI address, String startRevision, String endRevision) throws MalformedURLException,
	        InvalidProtocolType, InvalidRepositoryURI, UnsupportedProtocolType;
	
	/**
	 * Connect to repository at URI address using user name and password.
	 * 
	 * @param address
	 *            the address
	 * @param startRevision
	 *            first revision to take into account (may be null)
	 * @param endRevision
	 *            last revision to take into account (may be null)
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @throws MalformedURLException
	 *             the malformed URL exception
	 * @throws InvalidProtocolType
	 *             the invalid protocol type
	 * @throws InvalidRepositoryURI
	 *             the invalid repository URI
	 * @throws UnsupportedProtocolType
	 *             the unsupported protocol type
	 */
	public abstract void setup(URI address, String startRevision, String endRevision, String username, String password)
	        throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI, UnsupportedProtocolType;
}
