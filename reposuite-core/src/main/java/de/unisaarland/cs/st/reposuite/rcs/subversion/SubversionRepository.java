/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
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
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
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
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import difflib.Delta;

/**
 * @author just
 * 
 */
public class SubversionRepository extends Repository {
	
	private URI           uri;
	private String        password;
	private String        username;
	private ProtocolType  type;
	private SVNRepository repository;
	private SVNURL        svnurl;
	
	/**
	 * Instantiates a new subversion repository.
	 */
	public SubversionRepository() {
	}
	
	@Override
	public List<AnnotationEntry> annotate(String filePath, String revision) {
		SVNURL relativePath;
		try {
			
			relativePath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			Long revisionNumber = Long.parseLong(revision);
			SVNLogClient logClient = new SVNLogClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			
			SVNRevision svnRevision = SVNRevision.create(Long.valueOf(revisionNumber));
			
			// check out the svnurl recursively into the createDir visible from revision 0 to given revision string
			ISVNAnnotateHandler annotateHandler = new ISVNAnnotateHandler() {
				
				private final List<AnnotationEntry> list = new LinkedList<AnnotationEntry>();
				
				@Override
				public void handleEOF() {
				}
				
				@Override
				public void handleLine(Date date, long revision, String author, String line) throws SVNException {
					AnnotationEntry annotationEntry = new AnnotationEntry(revision + "", author, new DateTime(date),
					        line);
					this.list.add(annotationEntry);
				}
				
				@Override
				public void handleLine(Date date, long revision, String author, String line, Date mergedDate,
				        long mergedRevision, String mergedAuthor, String mergedPath, int lineNumber)
				        throws SVNException {
				}
				
				@Override
				public boolean handleRevision(Date date, long revision, String author, File contents)
				        throws SVNException {
					return false;
				}
			};
			logClient.doAnnotate(relativePath, svnRevision, svnRevision, svnRevision, annotateHandler);
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
		return null;
	}
	
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
			Long revisionNumber = Long.parseLong(revision);
			SVNUpdateClient updateClient = new SVNUpdateClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			
			SVNRevision svnRevision = SVNRevision.create(Long.valueOf(revisionNumber));
			
			// check out the svnurl recursively into the createDir visible from revision 0 to given revision string 
			long checkout = updateClient.doCheckout(checkoutPath, workingDirectory, svnRevision, svnRevision,
			        SVNDepth.FILES, false);
			
			if (checkout != revisionNumber) {
				// TODO handle error
				return null;
			} else {
				return workingDirectory;
			}
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	@Override
	public Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision) {
		try {
			SVNURL repoPath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			Long revisionNumberFrom = Long.parseLong(baseRevision);
			SVNRevision fromRevision = SVNRevision.create(revisionNumberFrom);
			Long revisionNumberTo = Long.parseLong(revisedRevision);
			SVNRevision toRevision = SVNRevision.create(revisionNumberTo);
			SVNDiffClient diffClient = new SVNDiffClient(this.repository.getAuthenticationManager(),
			        SVNWCUtil.createDefaultOptions(true));
			SVNDiffParser diffParser = new SVNDiffParser();
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ChangeType> getChangedPaths(String revision) {
		Long revisionNumber = Long.parseLong(revision);
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
						default: // TODO handle unsupported ChangeType 
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
	
	@Override
	public String getFirstRevisionId() {
		return "0";
	}
	
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
	public List<LogEntry> log(String fromRevision, String toRevision) {
		Long revisionFromNumber = Long.parseLong(fromRevision);
		Long revisionToNumber = Long.parseLong(toRevision);
		List<LogEntry> list = new LinkedList<LogEntry>();
		
		Collection<SVNLogEntry> logs;
		try {
			logs = this.repository.log(new String[] { "" }, null, revisionFromNumber, revisionToNumber, true, true);
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
	
	@Override
	public void setup(URI address) throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI,
	        UnsupportedProtocolType {
		setup(address, null, null);
	}
	
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
			Logger.info("Setting up in '" + this.type.name() + "' mode.");
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
