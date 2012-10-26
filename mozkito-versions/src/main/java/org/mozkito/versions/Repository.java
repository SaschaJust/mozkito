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
package org.mozkito.versions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

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
import org.mozkito.exceptions.InvalidProtocolType;
import org.mozkito.exceptions.InvalidRepositoryURI;
import org.mozkito.exceptions.UnsupportedProtocolType;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.elements.LogIterator;
import org.mozkito.versions.mercurial.MercurialRepository;
import org.mozkito.versions.model.RCSTransaction;

import difflib.Delta;

/**
 * The Class Repository. Every repository connector that extends this class has to be named [Repotype]Repository. E.g.
 * DarksRepository. Additionally it is mandatory to add a new enum constant in {@link RepositoryType}.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class Repository {
	
	/** The uri. */
	private URI            uri;
	
	/** The start revision. */
	private String         startRevision;
	
	/** The end revision. */
	private String         endRevision;
	
	/** The start transaction. */
	private RCSTransaction startTransaction = null;
	
	/** The main branch name. */
	private String         mainBranchName;
	
	/**
	 * Instantiates a new repository.
	 */
	public Repository() {
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
	public abstract List<AnnotationEntry> annotate(String filePath,
	                                               String revision);
	
	/**
	 * Checks out the given relative path in repository and returns the file handle to the checked out file. If the path
	 * is a directory, the file handle will point to the specified directory. If the relative path points to a file, the
	 * file handle will do so too.
	 * 
	 * @param relativeRepoPath
	 *            the relative repository path
	 * @param revision
	 *            the revision
	 * @return The file handle to the checked out, corresponding file or directory.
	 */
	public abstract File checkoutPath(String relativeRepoPath,
	                                  String revision);
	
	/**
	 * Checks the repository for corruption.
	 * 
	 * @param logEntries
	 *            the log entries
	 * @param withInterface
	 *            the with interface
	 */
	public void consistencyCheck(@NotNull final List<LogEntry> logEntries,
	                             final boolean withInterface) {
		LogEntry previous = null;
		
		for (final LogEntry entry : logEntries) {
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
	 * Creates the file count per transaction.
	 * 
	 * @param entries
	 *            the entries
	 * @return the j free chart
	 */
	private JFreeChart createFileCountPerTransaction(final List<LogEntry> entries) {
		final List<Double> revisions = new ArrayList<Double>(entries.size());
		final List<Double> files = new ArrayList<Double>(entries.size());
		int i = 0;
		final double[][] datapoints = new double[6][entries.size()];
		
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		BarRenderer.setDefaultShadowsVisible(false);
		
		for (final LogEntry entry : entries) {
			revisions.add(Double.parseDouble(entry.getRevision()));
			final Map<String, ChangeType> changedPaths = getChangedPaths(entry.getRevision());
			files.add((double) changedPaths.size());
			
			datapoints[0][i] = revisions.get(i);
			datapoints[1][i] = revisions.get(i);
			datapoints[2][i] = revisions.get(i);
			datapoints[3][i] = files.get(i);
			datapoints[4][i] = files.get(i);
			datapoints[5][i] = files.get(i);
			
			++i;
		}
		
		final DefaultIntervalXYDataset idataset = new DefaultIntervalXYDataset();
		idataset.addSeries(new String("Files per revision"), datapoints);
		final JFreeChart chart = ChartFactory.createXYBarChart("Files per revision", "revisions", false, "files",
		                                                       idataset, PlotOrientation.VERTICAL, true, false, false);
		
		((XYBarRenderer) chart.getXYPlot().getRenderer()).setShadowVisible(false);
		
		return chart;
	}
	
	/**
	 * Creates the time per transaction.
	 * 
	 * @param entries
	 *            the entries
	 * @return the j free chart
	 */
	private JFreeChart createTimePerTransaction(final List<LogEntry> entries) {
		final DefaultXYDataset dataset = new DefaultXYDataset();
		final double[][] datapoints = new double[2][entries.size()];
		
		final List<Double> revisions = new ArrayList<Double>(entries.size());
		final List<Double> times = new ArrayList<Double>(entries.size());
		
		for (final LogEntry entry : entries) {
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
	 * Creates the transactions per author.
	 * 
	 * @param entries
	 *            the entries
	 * @param threshold
	 *            the threshold
	 * @return the j free chart
	 */
	private JFreeChart createTransactionsPerAuthor(final List<LogEntry> entries,
	                                               final double threshold) {
		final Map<String, Double> authors = new HashMap<String, Double>();
		
		for (final LogEntry entry : entries) {
			// commits
			final String author = entry.getAuthor().toString();
			if (authors.containsKey(author)) {
				authors.put(author, authors.get(author) + 1.0);
			} else {
				authors.put(author, 1.0);
			}
		}
		
		final DefaultCategoryDataset cdataset = new DefaultCategoryDataset();
		
		double others = 0.0d;
		int otherCount = 0;
		for (final String key : authors.keySet()) {
			if (authors.get(key) > (entries.size() / 35)) {
				cdataset.addValue(authors.get(key), key, new String("authors"));
			} else {
				others += authors.get(key);
				++otherCount;
			}
		}
		cdataset.addValue(others, "others (" + otherCount + ")", new String("authors"));
		
		return ChartFactory.createBarChart("Commits per Author (threshold " + ((100d * threshold) / entries.size())
		        + "%)", "history", "commits", cdataset, PlotOrientation.VERTICAL, true, false, false);
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
	public abstract Collection<Delta> diff(String filePath,
	                                       String baseRevision,
	                                       String revisedRevision);
	
	/**
	 * Gather tool information.
	 * 
	 * @return a string containing information about the instrumented library/tool (e.g. version, ...)
	 */
	public abstract String gatherToolInformation();
	
	/**
	 * Gets the files that changed within the corresponding transaction.
	 * 
	 * @param revision
	 *            the revision to be analyzed
	 * @return the changed paths
	 */
	public abstract Map<String, ChangeType> getChangedPaths(String revision);
	
	/**
	 * Get the last revision to be considered.
	 * 
	 * @return the endRevision
	 */
	public String getEndRevision() {
		return this.endRevision;
	}
	
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
	 * @return Returns the former path name iff the file/directory was renamed. Null otherwise.
	 */
	public abstract String getFormerPathName(String revision,
	                                         String pathName);
	
	/**
	 * Determines the simple class name of the object.
	 * 
	 * @return this.getClass().getSimpleName();
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Gets the hEAD.
	 * 
	 * @return the hEAD
	 */
	public String getHEAD() {
		return "HEAD";
	}
	
	/**
	 * Gets the last revision of the repository.
	 * 
	 * @return the last revision id
	 */
	public abstract String getHEADRevisionId();
	
	/**
	 * Gets the main branch name.
	 * 
	 * @return the main branch name
	 */
	public String getMainBranchName() {
		return this.mainBranchName;
	}
	
	/**
	 * Returns the relative transaction id to the given one. Result is bounded by startRevision and endRevision.
	 * 
	 * @param transactionId
	 *            the transaction id
	 * @param index
	 *            the index
	 * @return the relative transaction id
	 */
	public abstract String getRelativeTransactionId(String transactionId,
	                                                long index);
	
	/**
	 * Gets the repository type.
	 * 
	 * @return the {@link RepositoryType} of the connector class determined by naming convention. See the java-doc of
	 * @author Sascha Just <sascha.just@mozkito.org> {@link Repository} for details.
	 */
	public final RepositoryType getRepositoryType() {
		return RepositoryType.valueOf(this.getClass()
		                                  .getSimpleName()
		                                  .substring(0,
		                                             this.getClass().getSimpleName().length()
		                                                     - Repository.class.getSimpleName().length()).toUpperCase());
	}
	
	/**
	 * Gets the rev dependency graph.
	 * 
	 * @return the rev dependency graph
	 */
	public abstract IRevDependencyGraph getRevDependencyGraph();
	
	/**
	 * Gets the rev dependency graph.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the rev dependency graph
	 */
	public abstract IRevDependencyGraph getRevDependencyGraph(final PersistenceUtil persistenceUtil);
	
	/**
	 * Gets the start revision.
	 * 
	 * @return the startRevision
	 */
	public String getStartRevision() {
		return this.startRevision;
	}
	
	/**
	 * Gets the start transaction.
	 * 
	 * @return the startTransaction
	 */
	public RCSTransaction getStartTransaction() {
		return this.startTransaction;
	}
	
	/**
	 * Gets the transaction count.
	 * 
	 * @return the total number of revisions in the repository, -1 if error occured
	 */
	public abstract long getTransactionCount();
	
	/**
	 * Returns the transaction id string to the transaction determined by the given index.
	 * 
	 * @param index
	 *            Starts at 0
	 * @return the corresponding transaction id (e.g. for reposuite {@link MercurialRepository#getTransactionId(long)}
	 *         returns 021e7e97724b for 3.
	 */
	public abstract String getTransactionId(long index);
	
	/**
	 * Gets the uri.
	 * 
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}
	
	// /**
	// * This method extracts the fragment of the URI, saves the given uri
	// without
	// * fragment as repository uri and returns the fragment as string.
	// *
	// * @param address
	// * the address
	// * @return the string the extracted fragment section of the given address
	// * uri
	// */
	// protected String getUriFragment() {
	//
	// URI address = getUri();
	// String fragment = address.getFragment();
	// if ((fragment != null) && (!fragment.equals(""))) {
	// // need to reformat URI and gitName
	// StringBuilder uriBuilder = new StringBuilder();
	// uriBuilder.append(address.getScheme());
	// uriBuilder.append("://");
	// uriBuilder.append(address.getAuthority());
	// uriBuilder.append(address.getPath());
	// if ((address.getQuery() != null) && (!address.getQuery().equals(""))) {
	// uriBuilder.append("?");
	// uriBuilder.append(address.getQuery());
	// }
	// try {
	// uri = new URI(uriBuilder.toString());
	// } catch (URISyntaxException e1) {
	// if (Logger.logError()) {
	// Logger.error("Newly generated URI using the specified username cannot be parsed. URI = `"
	// + uriBuilder.toString() + "`");
	// }
	// }
	// }
	// return (fragment != null
	// ? fragment
	// : "");
	// }
	
	/**
	 * Returns the path of the directory that contains the local copy/clone/checkout of the repository (the working
	 * copy).
	 * 
	 * @return the woking copy location
	 */
	public abstract File getWokingCopyLocation();
	
	/**
	 * Extract a log from the repository.
	 * 
	 * @param fromRevision
	 *            the from revision
	 * @param toRevision
	 *            the to revision
	 * @return the list of log entries. The first entry is the oldest log entry.
	 */
	public abstract List<LogEntry> log(String fromRevision,
	                                   String toRevision);
	
	/**
	 * Extract a log from the repository spanning between two revisions.
	 * 
	 * @param fromRevision
	 *            the from revision
	 * @param toRevision
	 *            the to revision
	 * @param cacheSize
	 *            the cache size
	 * @return Iterator running from <code>fromRevisions</code> to <code>toRevision</code>
	 */
	public Iterator<LogEntry> log(final String fromRevision,
	                              final String toRevision,
	                              final int cacheSize) {
		return new LogIterator(this, fromRevision, toRevision, cacheSize);
	}
	
	/**
	 * Sets the end revision.
	 * 
	 * @param endRevision
	 *            the endRevision to set
	 */
	public void setEndRevision(final String endRevision) {
		this.endRevision = endRevision;
	}
	
	/**
	 * Sets the main branch name.
	 * 
	 * @param mainBranchName
	 *            the new main branch name
	 */
	public void setMainBranchName(final String mainBranchName) {
		this.mainBranchName = mainBranchName;
	}
	
	/**
	 * Sets the start revision.
	 * 
	 * @param startRevision
	 *            the startRevision to set
	 */
	public void setStartRevision(final String startRevision) {
		this.startRevision = startRevision;
	}
	
	/**
	 * Sets the start transaction.
	 * 
	 * @param startTransaction
	 *            the startTransaction to set
	 */
	public void setStartTransaction(final RCSTransaction startTransaction) {
		this.startTransaction = startTransaction;
	}
	
	/**
	 * Connect to repository at URI address.
	 * 
	 * @param address
	 *            the address the repository can be found
	 * @param branchFactory
	 *            the branch factory
	 * @param tmpDir
	 *            the tmp dir
	 * @param mainBranchName
	 *            the main branch name
	 * @throws MalformedURLException
	 *             the malformed URL exception
	 * @throws InvalidProtocolType
	 *             the invalid protocol type
	 * @throws InvalidRepositoryURI
	 *             the invalid repository URI
	 * @throws UnsupportedProtocolType
	 *             the unsupported protocol type
	 */
	public abstract void setup(URI address,
	                           BranchFactory branchFactory,
	                           File tmpDir,
	                           String mainBranchName) throws MalformedURLException,
	                                                 InvalidProtocolType,
	                                                 InvalidRepositoryURI,
	                                                 UnsupportedProtocolType;
	
	/**
	 * Connect to repository at URI address using user name and password.
	 * 
	 * @param address
	 *            the address
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param branchFactory
	 *            the branch factory
	 * @param tmpDir
	 *            the tmp dir
	 * @param mainBranchName
	 *            the main branch name
	 * @throws MalformedURLException
	 *             the malformed URL exception
	 * @throws InvalidProtocolType
	 *             the invalid protocol type
	 * @throws InvalidRepositoryURI
	 *             the invalid repository URI
	 * @throws UnsupportedProtocolType
	 *             the unsupported protocol type
	 */
	public abstract void setup(URI address,
	                           String username,
	                           String password,
	                           BranchFactory branchFactory,
	                           File tmpDir,
	                           String mainBranchName) throws MalformedURLException,
	                                                 InvalidProtocolType,
	                                                 InvalidRepositoryURI,
	                                                 UnsupportedProtocolType;
	
	/**
	 * Sets the uri.
	 * 
	 * @param uri
	 *            the new uri
	 */
	public void setUri(final URI uri) {
		this.uri = uri;
	}
	
}
