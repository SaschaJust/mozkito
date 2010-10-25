/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
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
import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.ProtocolType;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import difflib.Delta;

/**
 * Subversion connector extending the {@link Repository} base class.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * @see Repository
 */
public class SubversionRepository extends Repository {
	
	private static final RepositoryType REPOSITORY_TYPE = RepositoryType.SUBVERSION;
	private String                      password;
	private SVNRepository               repository;
	private SVNURL                      svnurl;
	private ProtocolType                type;
	private URI                         uri;
	private String                      username;
	
	/**
	 * Instantiates a new subversion repository.
	 */
	public SubversionRepository() {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(String filePath, String revision) {
		SVNURL relativePath;
		try {
			
			relativePath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			SVNLogClient logClient = new SVNLogClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			
			SVNRevision svnRevision = buildRevision(revision);
			
			// check out the svnurl recursively into the createDir visible from revision 0 to given revision string
			SubversionAnnotationHandler annotateHandler = new SubversionAnnotationHandler();
			logClient.doAnnotate(relativePath, svnRevision, buildRevision("0"), svnRevision, annotateHandler);
			return annotateHandler.getResults();
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/**
	 * Converts a given string to the corresponding SVNRevision.
	 * 
	 * @param revision
	 *            the string representing an SVN revision. This is either a
	 *            numeric of type long or a case insensitive version of the
	 *            alias string versions. This may not be null.
	 * @return the corresponding SVNRevision
	 */
	private SVNRevision buildRevision(String revision) {
		assert (revision != null);
		
		SVNRevision svnRevision;
		
		try {
			Long revisionNumber = Long.parseLong(revision);
			svnRevision = SVNRevision.create(Long.valueOf(revisionNumber));
		} catch (NumberFormatException e) {
			svnRevision = SVNRevision.parse(revision.toUpperCase());
		}
		
		assert (svnRevision != null);
		
		return svnRevision;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#checkoutPath(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public File checkoutPath(String relativeRepoPath, String revision) {
		File workingDirectory = FileUtils.createDir(FileUtils.tmpDir,
		        "reposuite_clone_" + DateTimeUtils.currentTimeMillis());
		try {
			FileUtils.forceDeleteOnExit(workingDirectory);
		} catch (IOException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		}
		
		SVNURL checkoutPath;
		try {
			
			checkoutPath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + relativeRepoPath);
			SVNUpdateClient updateClient = new SVNUpdateClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			
			SVNRevision svnRevision = buildRevision(revision);
			// check out the svnurl recursively into the createDir visible from revision 0 to given revision string 
			updateClient.doCheckout(checkoutPath, workingDirectory, svnRevision, svnRevision, SVNDepth.INFINITY, false);
			
			return workingDirectory;
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#diff(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision) {
		try {
			SVNURL repoPath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			SVNRevision fromRevision = buildRevision(baseRevision);
			SVNRevision toRevision = buildRevision(revisedRevision);
			SVNDiffClient diffClient = new SVNDiffClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			SubversionDiffParser diffParser = new SubversionDiffParser();
			diffClient.setDiffGenerator(diffParser);
			diffClient.doDiff(repoPath, toRevision, fromRevision, toRevision, SVNDepth.FILES, false,
			        new NullOutputStream());
			return diffParser.getDeltas();
			
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getChangedPaths(java.lang
	 * .String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ChangeType> getChangedPaths(String revision) {
		assert (revision != null);
		
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
							map.put(changedPaths.get(o).getPath(), ChangeType.Replaced);
							break;
						default:
							if (RepoSuiteSettings.logError()) {
								Logger.error("Unsupported change type `" + changedPaths.get(o).getType() + "`. "
								        + RepoSuiteSettings.reportThis);
							}
					}
				}
			}
			return map;
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		return "1";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getLastRevisionId()
	 */
	@Override
	public String getLastRevisionId() {
		try {
			return this.repository.getLatestRevision() + "";
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	@Override
	public RepositoryType getRepositoryType() {
		return REPOSITORY_TYPE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LogEntry> log(String fromRevision, String toRevision) {
		SVNRevision fromSVNRevision = buildRevision(fromRevision);
		SVNRevision toSVNRevision = buildRevision(toRevision);
		
		List<LogEntry> list = new LinkedList<LogEntry>();
		
		Collection<SVNLogEntry> logs;
		try {
			logs = this.repository.log(new String[] { "" }, null, fromSVNRevision.getNumber(),
			        toSVNRevision.getNumber(), true, true);
			LogEntry buff = null;
			for (SVNLogEntry entry : logs) {
				LogEntry current = new LogEntry(entry.getRevision() + "", buff, entry.getAuthor(), entry.getMessage(),
				        new DateTime(entry.getDate()));
				list.add(current);
				buff = current;
			}
			return list;
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(URI address) throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI,
	        UnsupportedProtocolType {
		setup(address, null, null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(URI address, String username, String password) throws MalformedURLException, InvalidProtocolType,
	        InvalidRepositoryURI, UnsupportedProtocolType {
		assert (address != null);
		this.uri = address;
		this.username = username;
		this.password = password;
		
		if (RepoSuiteSettings.debug) {
			SVNDebugLog.setDefaultLog(new SubversionLogger());
		}
		
		this.type = ProtocolType.valueOf(this.uri.toURL().getProtocol().toUpperCase());
		if (this.type != null) {
			if (RepoSuiteSettings.logInfo()) {
				Logger.info("Setting up in '" + this.type.name() + "' mode.");
			}
			switch (this.type) {
				case FILE:
					if (RepoSuiteSettings.logDebug()) {
						Logger.debug("Using valid mode " + this.type.name() + ".");
					}
					FSRepositoryFactory.setup();
					if (RepoSuiteSettings.logTrace()) {
						Logger.trace("Setup done for mode " + this.type.name() + ".");
					}
					break;
				case HTTP:
				case HTTPS:
					if (RepoSuiteSettings.logDebug()) {
						Logger.debug("Using valid mode " + this.type.name() + ".");
					}
					DAVRepositoryFactory.setup();
					if (RepoSuiteSettings.logTrace()) {
						Logger.trace("Setup done for mode " + this.type.name() + ".");
					}
					break;
				case SSH:
					if (RepoSuiteSettings.logDebug()) {
						Logger.debug("Using valid mode " + this.type.name() + ".");
					}
					SVNRepositoryFactoryImpl.setup();
					if (RepoSuiteSettings.logTrace()) {
						Logger.trace("Setup done for mode " + this.type.name() + ".");
					}
					break;
				default:
					if (RepoSuiteSettings.logError()) {
						Logger.error("Failed to setup in '" + this.type.name() + "' mode. Unsupported at this time.");
					}
					throw new UnsupportedProtocolType(getHandle() + " does not support protocol " + this.type.name());
			}
			try {
				if (RepoSuiteSettings.logInfo()) {
					Logger.info("Parsing URL: " + this.uri.toString());
				}
				this.svnurl = SVNURL.parseURIDecoded(this.uri.toString());
				if (RepoSuiteSettings.logTrace()) {
					Logger.trace("Done parsing URL: " + this.uri.toString() + " resulting in: "
					        + this.svnurl.toString());
				}
				this.repository = SVNRepositoryFactory.create(this.svnurl);
				
				if (this.username != null) {
					ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.username,
					        this.password);
					this.repository.setAuthenticationManager(authManager);
				}
			} catch (SVNException e) {
				throw new InvalidRepositoryURI(e.getMessage());
			}
			
		} else {
			throw new InvalidProtocolType(this.uri.toURL().getProtocol().toUpperCase());
		}
	}
}
