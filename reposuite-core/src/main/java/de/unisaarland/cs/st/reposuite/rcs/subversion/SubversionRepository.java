/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.NullOutputStream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.util.SVNDebugLog;

import de.unisaarland.cs.st.reposuite.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.reposuite.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;
import de.unisaarland.cs.st.reposuite.rcs.ProtocolType;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Preconditions;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * Subversion connector extending the {@link Repository} base class.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * @see Repository
 */
public class SubversionRepository extends Repository {
	
	private SVNRevision   endRevision;
	private boolean       initialized = false;
	private String        password;
	private PersonManager personManager;
	private SVNRepository repository;
	private SVNRevision   startRevision;
	private SVNURL        svnurl;
	private ProtocolType  type;
	private URI           uri;
	private String        username;
	
	/**
	 * Instantiates a new subversion repository.
	 */
	public SubversionRepository() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(final String filePath, final String revision) {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(filePath);
		Preconditions.checkNotNull(revision);
		Preconditions.checkGreater(filePath.length(), 0);;
		Preconditions.checkGreater(revision.length(), 0);;
		
		SVNURL relativePath;
		try {
			
			relativePath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			SVNLogClient logClient = new SVNLogClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			
			SVNRevision svnRevision = buildRevision(revision);
			
			// check out the svnurl recursively into the createDir visible from
			// revision 0 to given revision string
			SubversionAnnotationHandler annotateHandler = new SubversionAnnotationHandler();
			logClient.doAnnotate(relativePath, svnRevision, buildRevision("0"), svnRevision, annotateHandler);
			return annotateHandler.getResults();
		} catch (SVNException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/**
	 * Converts a given string to the corresponding SVNRevision. This requires
	 * {@link Repository#setup(URI, String, String)} to be executed.
	 * 
	 * @param revision
	 *            the string representing an SVN revision. This is either a
	 *            numeric of type long or a case insensitive version of the
	 *            alias string versions. This may not be null.
	 * @return the corresponding SVNRevision
	 */
	private SVNRevision buildRevision(final String revision) {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(revision);
		Preconditions.checkGreater(revision.length(), 0);;
		
		SVNRevision svnRevision;
		
		try {
			Long revisionNumber = Long.parseLong(revision);
			svnRevision = SVNRevision.create(Long.valueOf(revisionNumber));
		} catch (NumberFormatException e) {
			svnRevision = SVNRevision.parse(revision.toUpperCase());
		}
		
		Preconditions.checkNotNull(svnRevision);
		
		try {
			if (svnRevision.getNumber() < 0) {
				if (svnRevision.equals(SVNRevision.PREVIOUS)) {
					
					svnRevision = SVNRevision.create(this.repository.getLatestRevision() - 1);
				} else {
					svnRevision = SVNRevision.create(this.repository.getLatestRevision());
				}
			}
		} catch (SVNException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
		
		if (svnRevision.getNumber() < this.startRevision.getNumber()) {
			
			if (Logger.logWarn()) {
				Logger.warn("Revision " + svnRevision.getNumber() + " is before " + this.startRevision.getNumber()
				        + ". Corrected to start revision.");
			}
			return this.startRevision;
		} else if (svnRevision.getNumber() > this.endRevision.getNumber()) {
			if (Logger.logWarn()) {
				Logger.warn("Revision " + svnRevision.getNumber() + " is after " + this.endRevision.getNumber()
				        + ". Corrected to end revision.");
			}
			return this.endRevision;
		} else {
			return svnRevision;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#checkoutPath(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public File checkoutPath(final String relativeRepoPath, final String revision) {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(relativeRepoPath);
		Preconditions.checkNotNull(revision);
		Preconditions.checkGreater(relativeRepoPath.length(), 0);;
		Preconditions.checkGreater(revision.length(), 0);;
		
		File workingDirectory = FileUtils.createDir(FileUtils.tmpDir,
		        "reposuite_clone_" + DateTimeUtils.currentTimeMillis());
		
		Preconditions.checkNotNull(workingDirectory);
		
		try {
			FileUtils.forceDeleteOnExit(workingDirectory);
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			return null;
		}
		
		SVNURL checkoutPath;
		try {
			
			checkoutPath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + relativeRepoPath);
			SVNUpdateClient updateClient = new SVNUpdateClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			
			SVNRevision svnRevision = buildRevision(revision);
			// check out the svnurl recursively into the createDir visible from
			// revision 0 to given revision string
			updateClient.doCheckout(checkoutPath, workingDirectory, svnRevision, svnRevision, SVNDepth.INFINITY, false);
			
			return workingDirectory;
		} catch (SVNException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#diff(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(final String filePath, final String baseRevision, final String revisedRevision) {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(filePath);
		Preconditions.checkGreater(filePath.length(), 0);;
		Preconditions.checkNotNull(baseRevision);
		Preconditions.checkNotNull(revisedRevision);
		Preconditions.checkGreater(baseRevision.length(), 0);;
		Preconditions.checkGreater(revisedRevision.length(), 0);;
		
		try {
			SVNURL repoPath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			SVNRevision fromRevision = buildRevision(baseRevision);
			SVNRevision toRevision = buildRevision(revisedRevision);
			SVNDiffClient diffClient = new SVNDiffClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			diffClient.getDiffGenerator().setDiffDeleted(true);
			SubversionDiffParser diffParser = new SubversionDiffParser();
			diffClient.setDiffGenerator(diffParser);
			
			diffClient.doDiff(repoPath, toRevision, fromRevision, toRevision, SVNDepth.FILES, false,
			        new NullOutputStream());
			
			// delete tmp diff files
			File pwd = new File(System.getProperty("user.dir"));
			for (File file : FileUtils.listFiles(pwd, null, false)) {
				if (file.getName().startsWith(".diff.")) {
					file.delete();
				}
			}
			
			return diffParser.getDeltas();
			
		} catch (SVNException e) {
			// try to checkout file in first revision
			String parentPath = filePath.substring(0, filePath.lastIndexOf("/"));
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
			File parentDir = checkoutPath(parentPath, baseRevision);
			if ((parentDir == null) || (!parentDir.exists())) {
				// checkout failed too. Return null
				return null;
			}
			File checkedOutFile = new File(parentDir.getAbsolutePath() + FileUtils.fileSeparator + fileName);
			if (!checkedOutFile.exists()) {
				return null;
			}
			List<String> lines = FileUtils.fileToLines(checkedOutFile);
			Patch patch = DiffUtils.diff(lines, new ArrayList<String>(0));
			return patch.getDeltas();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getChangedPaths(java.lang
	 * .String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(revision);
		Preconditions.checkGreater(revision.length(), 0);;
		
		Long revisionNumber = buildRevision(revision).getNumber();
		Map<String, ChangeType> map = new HashMap<String, ChangeType>();
		Collection<SVNLogEntry> logs;
		
		try {
			logs = this.repository.log(new String[] { "" }, null, revisionNumber, revisionNumber, true, true);
			
			for (SVNLogEntry entry : logs) {
				Map<Object, SVNLogEntryPath> changedPaths = entry.getChangedPaths();
				for (Object o : changedPaths.keySet()) {
					switch (changedPaths.get(o).getType()) {
						case 'M':
							map.put(changedPaths.get(o).getPath(), ChangeType.Modified);
							break;
						case 'A':
							map.put(changedPaths.get(o).getPath(), ChangeType.Added);
							break;
						case 'D':
							map.put(changedPaths.get(o).getPath(), ChangeType.Deleted);
							break;
						case 'R':
							map.put(changedPaths.get(o).getPath(), ChangeType.Renamed);
							break;
						default:
							if (Logger.logError()) {
								Logger.error("Unsupported change type `" + changedPaths.get(o).getType() + "`. "
								        + RepoSuiteSettings.reportThis);
							}
					}
				}
			}
			return map;
		} catch (SVNException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(this.startRevision);
		Preconditions.checkGreater(this.startRevision.getNumber(), 0l);;
		
		return this.startRevision.toString();
	}
	
	@Override
	public String getFormerPathName(final String revision, final String pathName) {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(revision);
		Preconditions.checkGreater(revision.length(), 0);;
		Preconditions.checkNotNull(pathName);
		Preconditions.checkGreater(pathName.length(), 0);;
		
		Long revisionNumber = buildRevision(revision).getNumber();
		
		try {
			@SuppressWarnings ("unchecked") Collection<SVNLogEntry> logs = this.repository.log(new String[] { "" },
			        null, revisionNumber, revisionNumber, true, true);
			
			for (SVNLogEntry entry : logs) {
				@SuppressWarnings ("unchecked") Map<Object, SVNLogEntryPath> changedPaths = entry.getChangedPaths();
				for (Object o : changedPaths.keySet()) {
					SVNLogEntryPath logEntryPath = changedPaths.get(o);
					switch (logEntryPath.getType()) {
						case 'R':
						case 'A':
							if (logEntryPath.getCopyPath() == null) {
								continue;
							}
							if (logEntryPath.getPath().equals(pathName)) {
								return logEntryPath.getCopyPath();
							} else if (logEntryPath.getKind().equals(SVNNodeKind.DIR)
							        && pathName.startsWith(logEntryPath.getPath().substring(1))) {
								String copyPath = logEntryPath.getCopyPath().substring(1) + "/";
								return copyPath
								        + pathName.substring(logEntryPath.getPath().length(), pathName.length());
							}
					}
					
				}
			}
			
		} catch (SVNException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getLastRevisionId()
	 */
	@Override
	public String getLastRevisionId() {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(this.endRevision);
		Preconditions.checkGreater(this.endRevision.getNumber(), 0l);;
		
		try {
			return (this.repository.getLatestRevision() > this.endRevision.getNumber() ? this.endRevision.toString()
			        : this.repository.getLatestRevision() + "");
		} catch (SVNException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getRelativeTransactionId
	 * (java.lang.String, long)
	 */
	@Override
	public String getRelativeTransactionId(final String transactionId, final long index) {
		Preconditions.checkNotNull(transactionId);
		
		if (buildRevision(transactionId).getNumber() + index > buildRevision(getLastRevisionId()).getNumber()) {
			return getLastRevisionId();
		} else if (buildRevision(transactionId).getNumber() + index < buildRevision(getFirstRevisionId()).getNumber()) {
			return getFirstRevisionId();
		} else {
			return (buildRevision(transactionId).getNumber() + index) + "";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getTransactionCount()
	 */
	@Override
	public long getTransactionCount() {
		try {
			return this.repository.getLatestRevision();
		} catch (SVNException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getTransactionId(long)
	 */
	@Override
	public String getTransactionId(final long index) {
		return (1 + index) + "";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public List<LogEntry> log(final String fromRevision, final String toRevision) {
		Preconditions.checkArgument(this.initialized);
		Preconditions.checkNotNull(fromRevision);
		Preconditions.checkNotNull(toRevision);
		Preconditions.checkGreater(fromRevision.length(), 0);;
		Preconditions.checkGreater(toRevision.length(), 0);;
		
		SVNRevision fromSVNRevision = buildRevision(fromRevision);
		SVNRevision toSVNRevision = buildRevision(toRevision);
		
		List<LogEntry> list = new LinkedList<LogEntry>();
		
		Collection<SVNLogEntry> logs;
		try {
			logs = this.repository.log(new String[] { "" }, null, fromSVNRevision.getNumber(),
			        toSVNRevision.getNumber(), true, true);
			LogEntry buff = null;
			for (SVNLogEntry entry : logs) {
				
				LogEntry current = new LogEntry(entry.getRevision() + "", buff, this.personManager.getPerson((entry
				        .getAuthor() != null ? new Person(entry.getAuthor(), null, null) : null)), entry.getMessage(),
				        new DateTime(entry.getDate()));
				list.add(current);
				buff = current;
			}
			return list;
		} catch (SVNException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	@Override
	public Iterator<LogEntry> log(final String fromRevision, final String toRevision, final int cacheSize) {
		return new LogIterator(this, fromRevision, toRevision, cacheSize);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision)
	        throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI, UnsupportedProtocolType {
		setup(address, startRevision, endRevision, null, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision, final String username,
	        final String password) throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI,
	        UnsupportedProtocolType {
		Preconditions.checkNotNull(address);
		this.uri = address;
		this.username = username;
		this.password = password;
		
		if (RepoSuiteSettings.debug) {
			SVNDebugLog.setDefaultLog(new SubversionLogger());
		}
		
		this.type = ProtocolType.valueOf(this.uri.toURL().getProtocol().toUpperCase());
		if (this.type != null) {
			if (Logger.logInfo()) {
				Logger.info("Setting up in '" + this.type.name() + "' mode.");
			}
			switch (this.type) {
				case FILE:
					if (Logger.logDebug()) {
						Logger.debug("Using valid mode " + this.type.name() + ".");
					}
					FSRepositoryFactory.setup();
					if (Logger.logTrace()) {
						Logger.trace("Setup done for mode " + this.type.name() + ".");
					}
					break;
				case HTTP:
				case HTTPS:
					if (Logger.logDebug()) {
						Logger.debug("Using valid mode " + this.type.name() + ".");
					}
					DAVRepositoryFactory.setup();
					if (Logger.logTrace()) {
						Logger.trace("Setup done for mode " + this.type.name() + ".");
					}
					break;
				case SSH:
					if (Logger.logDebug()) {
						Logger.debug("Using valid mode " + this.type.name() + ".");
					}
					SVNRepositoryFactoryImpl.setup();
					if (Logger.logTrace()) {
						Logger.trace("Setup done for mode " + this.type.name() + ".");
					}
					break;
				default:
					if (Logger.logError()) {
						Logger.error("Failed to setup in '" + this.type.name() + "' mode. Unsupported at this time.");
					}
					throw new UnsupportedProtocolType(getHandle() + " does not support protocol " + this.type.name());
			}
			try {
				if (Logger.logInfo()) {
					Logger.info("Parsing URL: " + this.uri.toString());
				}
				this.svnurl = SVNURL.parseURIDecoded(this.uri.toString());
				if (Logger.logTrace()) {
					Logger.trace("Done parsing URL: " + this.uri.toString() + " resulting in: "
					        + this.svnurl.toString());
				}
				
				if (this.username != null) {
					ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.username,
					        this.password);
					this.repository.setAuthenticationManager(authManager);
				}
				
				this.repository = SVNRepositoryFactory.create(this.svnurl);
				
				this.startRevision = (startRevision != null ? SVNRevision.parse(startRevision) : SVNRevision.create(1));
				this.endRevision = (endRevision != null ? SVNRevision.parse(endRevision) : SVNRevision
				        .create(this.repository.getLatestRevision()));
				
				if (this.startRevision.getNumber() < 0) {
					if (this.startRevision.equals(SVNRevision.PREVIOUS)) {
						this.startRevision = SVNRevision.create(this.repository.getLatestRevision() - 1);
					} else {
						this.startRevision = SVNRevision.create(this.repository.getLatestRevision());
					}
				}
				
				if (this.endRevision.getNumber() < 0) {
					if (this.endRevision.equals(SVNRevision.PREVIOUS)) {
						this.endRevision = SVNRevision.create(this.repository.getLatestRevision() - 1);
					} else {
						this.endRevision = SVNRevision.create(this.repository.getLatestRevision());
					}
				}
				
				this.personManager = new PersonManager();
				this.initialized = true;
				
				if (Logger.logInfo()) {
					Logger.info("Setup repository: " + this);
				}
				
			} catch (SVNException e) {
				throw new InvalidRepositoryURI(e.getMessage());
			}
			
		} else {
			throw new InvalidProtocolType(this.uri.toURL().getProtocol().toUpperCase());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubversionRepository [password="
		        + (this.password != null ? this.password.replaceAll(".", "*") : "(unset)") + ", svnurl=" + this.svnurl
		        + ", type=" + this.type + ", uri=" + this.uri + ", username="
		        + (this.username != null ? this.username : "(unset)") + ", startRevision=" + this.startRevision
		        + ", endRevision=" + this.endRevision + "]";
	}
}
