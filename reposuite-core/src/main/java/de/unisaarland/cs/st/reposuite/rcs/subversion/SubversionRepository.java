/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTimeUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
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
		// TODO Auto-generated method stub
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
		
		Long revisionNumber = Long.parseLong(revision);
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		SVNClientManager clientManager = SVNClientManager.newInstance(options, authManager);
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		updateClient.setIgnoreExternals(true);
		
		try {
			// check out the svnurl recursively into the createDir visible from revision 0 to given revision string 
			long doCheckout = updateClient.doCheckout(svnurl, workingDirectory, SVNRevision.create(revisionNumber),
			        SVNRevision.create(revisionNumber), SVNDepth.INFINITY, false);
			if (doCheckout != revisionNumber) {
				
			} else {
				return workingDirectory;
			}
		} catch (SVNException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		}
		
		return null;
	}
	
	@Override
	public Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<String, ChangeType> getChangedPaths() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getFirstRevisionId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getLastRevisionId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<LogEntry> log(String fromRevision, String toRevision) {
		// TODO Auto-generated method stub
		return null;
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
		uri = address;
		this.username = username;
		this.password = password;
		
		if (RepoSuiteSettings.debug) {
			SVNDebugLog.setDefaultLog(new SubversionLogger());
		}
		
		type = ProtocolType.valueOf(uri.toURL().getProtocol().toUpperCase());
		if (type != null) {
			try {
				if (RepoSuiteSettings.logInfo()) {
					Logger.info("Parsing URL: " + uri.toString());
				}
				svnurl = SVNURL.parseURIEncoded(uri.toString());
			} catch (SVNException e) {
				throw new InvalidRepositoryURI(e.getMessage());
			}
			Logger.info("Setting up in '" + type.name() + "' mode.");
			switch (type) {
				case FILE:
				case HTTP:
				case HTTPS:
				case SSH:
					if (RepoSuiteSettings.logTrace()) {
						Logger.trace("Using valid mode " + type.name() + ".");
					}
					break;
				default:
					if (RepoSuiteSettings.logError()) {
						Logger.error("Failed to setup in '" + type.name() + "' mode. Unsupported at this time.");
					}
					throw new UnsupportedProtocolType(getHandle() + " does not support protocol " + type.name());
			}
		} else {
			throw new InvalidProtocolType(uri.toURL().getProtocol().toUpperCase());
		}
	}
	
}
