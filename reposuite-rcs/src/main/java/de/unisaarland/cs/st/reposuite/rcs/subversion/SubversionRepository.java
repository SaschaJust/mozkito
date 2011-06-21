/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

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
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.ProtocolType;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.ioda.URIUtils;
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
	private SVNRepository repository;
	private SVNRevision   startRevision;
	private SVNURL        svnurl;
	private ProtocolType  type;
	private String        username;
	private File          workingDirectory;
	
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
	@NoneNull
	public List<AnnotationEntry> annotate(@NotEmpty final String filePath,
	                                      @NotEmpty final String revision) {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		SVNURL relativePath;
		try {
			
			relativePath = SVNURL.parseURIDecoded(repository.getRepositoryRoot(true) + "/" + filePath);
			SVNLogClient logClient = new SVNLogClient(repository.getAuthenticationManager(),
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
	private SVNRevision buildRevision(@NotNull @NotEmpty final String revision) {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		
		SVNRevision svnRevision;
		
		try {
			Long revisionNumber = Long.parseLong(revision);
			svnRevision = SVNRevision.create(Long.valueOf(revisionNumber));
		} catch (NumberFormatException e) {
			svnRevision = SVNRevision.parse(revision.toUpperCase());
		}
		
		Condition.notNull(svnRevision, "SVNRevision may never be null at this point.");
		
		try {
			if (svnRevision.getNumber() < 0) {
				if (svnRevision.equals(SVNRevision.PREVIOUS)) {
					
					svnRevision = SVNRevision.create(repository.getLatestRevision() - 1);
				} else {
					svnRevision = SVNRevision.create(repository.getLatestRevision());
				}
			}
		} catch (SVNException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
		
		if (svnRevision.getNumber() < startRevision.getNumber()) {
			
			if (Logger.logWarn()) {
				Logger.warn("Revision " + svnRevision.getNumber() + " is before " + startRevision.getNumber()
				        + ". Corrected to start revision.");
			}
			return startRevision;
		} else if (svnRevision.getNumber() > endRevision.getNumber()) {
			if (Logger.logWarn()) {
				Logger.warn("Revision " + svnRevision.getNumber() + " is after " + endRevision.getNumber()
				        + ". Corrected to end revision.");
			}
			return endRevision;
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
	@NoneNull
	public File checkoutPath(@NotEmpty final String relativeRepoPath,
	                         @NotEmpty final String revision) {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		
		workingDirectory = FileUtils.createDir(FileUtils.tmpDir,

		"reposuite_clone_" + DateTimeUtils.currentTimeMillis(), FileShutdownAction.DELETE);
		
		Condition.notNull(workingDirectory, "Cannot operate on working directory that is set to Null");
		
		SVNURL checkoutPath;
		try {
			
			checkoutPath = SVNURL.parseURIDecoded(repository.getRepositoryRoot(true) + "/" + relativeRepoPath);
			SVNUpdateClient updateClient = new SVNUpdateClient(repository.getAuthenticationManager(),
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
	@NoneNull
	public Collection<Delta> diff(@NotEmpty final String filePath,
	                              @NotEmpty final String baseRevision,
	                              @NotEmpty final String revisedRevision) {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		
		try {
			SVNURL repoPath = SVNURL.parseURIDecoded(repository.getRepositoryRoot(true) + "/" + filePath);
			SVNRevision fromRevision = buildRevision(baseRevision);
			SVNRevision toRevision = buildRevision(revisedRevision);
			SVNDiffClient diffClient = new SVNDiffClient(repository.getAuthenticationManager(),
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
	
	@Override
	public String gatherToolInformation() {
		StringBuilder builder = new StringBuilder();
		CodeSource codeSource = SVNRepository.class.getProtectionDomain().getCodeSource();
		builder.append(getHandle()).append(" is using SVNKit from: ").append(codeSource.getLocation().getPath());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getChangedPaths(java.lang
	 * .String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public Map<String, ChangeType> getChangedPaths(@NotNull @NotEmpty final String revision) {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		
		Long revisionNumber = buildRevision(revision).getNumber();
		Map<String, ChangeType> map = new HashMap<String, ChangeType>();
		Collection<SVNLogEntry> logs;
		
		try {
			logs = repository.log(new String[] { "" }, null, revisionNumber, revisionNumber, true, true);
			
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
								        + RepositorySettings.reportThis);
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
	
	@Override
	public String getEndRevision() {
		return endRevision.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		Condition.notNull(startRevision, "startRevision must not be null at this point.");
		CompareCondition.greater(startRevision.getNumber(), 0l, "startRevision must be positive at this point.");
		
		return startRevision.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getFormerPathName(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public String getFormerPathName(@NotEmpty final String revision,
	                                @NotEmpty final String pathName) {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		
		Long revisionNumber = buildRevision(revision).getNumber();
		
		try {
			@SuppressWarnings ("unchecked")
			Collection<SVNLogEntry> logs = repository.log(new String[] { "" }, null, revisionNumber, revisionNumber,
			                                              true, true);
			
			for (SVNLogEntry entry : logs) {
				@SuppressWarnings ("unchecked")
				Map<Object, SVNLogEntryPath> changedPaths = entry.getChangedPaths();
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
	public String getHEADRevisionId() {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		
		Condition.notNull(endRevision, "endRevision must not be null at this point.");
		CompareCondition.greater(endRevision.getNumber(), 0l, "endRevision must be positive at this point");
		
		try {
			return (repository.getLatestRevision() > endRevision.getNumber()
			                                                                ? endRevision.toString()
			                                                                : repository.getLatestRevision() + "");
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
	public String getRelativeTransactionId(@NotNull final String transactionId,
	                                       final long index) {
		if (buildRevision(transactionId).getNumber() + index > buildRevision(getEndRevision()).getNumber()) {
			return getEndRevision();
		} else if (buildRevision(transactionId).getNumber() + index < buildRevision(getFirstRevisionId()).getNumber()) {
			return getFirstRevisionId();
		} else {
			return (buildRevision(transactionId).getNumber() + index) + "";
		}
	}
	
	@Override
	public RevDependencyIterator getRevDependencyIterator() {
		if (Logger.logError()) {
			Logger.error("Support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getTransactionCount()
	 */
	@Override
	public long getTransactionCount() {
		try {
			return repository.getLatestRevision();
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
	
	@Override
	public File getWokingCopyLocation() {
		return workingDirectory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	@NoneNull
	public List<LogEntry> log(@NotEmpty final String fromRevision,
	                          @NotEmpty final String toRevision) {
		Condition.check(initialized, "Repository has to be initialized before calling this method.");
		
		SVNRevision fromSVNRevision = buildRevision(fromRevision);
		SVNRevision toSVNRevision = buildRevision(toRevision);
		
		List<LogEntry> list = new LinkedList<LogEntry>();
		
		Collection<SVNLogEntry> logs;
		try {
			logs = repository.log(new String[] { "" }, null, fromSVNRevision.getNumber(), toSVNRevision.getNumber(),
			                      true, true);
			LogEntry buff = null;
			for (SVNLogEntry entry : logs) {
				LogEntry current = new LogEntry(entry.getRevision() + "", buff,
				                                (entry.getAuthor() != null
				                                                          ? new Person(entry.getAuthor(), null, null)
				                                                          : null), entry.getMessage(),
				                                new DateTime(entry.getDate()), "");
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
	public Iterator<LogEntry> log(final String fromRevision,
	                              final String toRevision,
	                              final int cacheSize) {
		return new LogIterator(this, fromRevision, toRevision, cacheSize);
	}
	
	@Override
	public void setEndRevision(final String endRevision) {
		try {
			this.endRevision = (endRevision != null
			                                       ? SVNRevision.parse(endRevision)
			                                       : SVNRevision.create(repository.getLatestRevision()));
		} catch (SVNException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown();
		}
	}
	
	@Override
	public void setStartRevision(final String startRevision) {
		this.startRevision = (startRevision != null
		                                           ? SVNRevision.parse(startRevision)
		                                           : SVNRevision.create(1));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(final URI address,
	                  final String startRevision,
	                  final String endRevision) throws MalformedURLException,
	                                           InvalidProtocolType,
	                                           InvalidRepositoryURI,
	                                           UnsupportedProtocolType {
		setup(address, startRevision, endRevision, null, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  final String startRevision,
	                  final String endRevision,
	                  final String username,
	                  final String password) throws MalformedURLException,
	                                        InvalidProtocolType,
	                                        InvalidRepositoryURI,
	                                        UnsupportedProtocolType {
		setUri(address);
		this.username = username;
		this.password = password;
		
		if (RepositorySettings.debug) {
			SVNDebugLog.setDefaultLog(new SubversionLogger());
		}
		
		type = ProtocolType.valueOf(getUri().toURL().getProtocol().toUpperCase());
		if (type != null) {
			if (Logger.logInfo()) {
				Logger.info("Setting up in '" + type.name() + "' mode.");
			}
			switch (type) {
				case FILE:
					if (Logger.logDebug()) {
						Logger.debug("Using valid mode " + type.name() + ".");
					}
					FSRepositoryFactory.setup();
					if (Logger.logTrace()) {
						Logger.trace("Setup done for mode " + type.name() + ".");
					}
					break;
				case HTTP:
				case HTTPS:
					if (Logger.logDebug()) {
						Logger.debug("Using valid mode " + type.name() + ".");
					}
					DAVRepositoryFactory.setup();
					if (Logger.logTrace()) {
						Logger.trace("Setup done for mode " + type.name() + ".");
					}
					break;
				case SSH:
					if (Logger.logDebug()) {
						Logger.debug("Using valid mode " + type.name() + ".");
					}
					SVNRepositoryFactoryImpl.setup();
					if (Logger.logTrace()) {
						Logger.trace("Setup done for mode " + type.name() + ".");
					}
					break;
				default:
					if (Logger.logError()) {
						Logger.error("Failed to setup in '" + type.name() + "' mode. Unsupported at this time.");
					}
					throw new UnsupportedProtocolType(getHandle() + " does not support protocol " + type.name());
			}
			try {
				if (Logger.logInfo()) {
					Logger.info("Parsing URL: " + URIUtils.Uri2String(getUri()));
				}
				svnurl = SVNURL.parseURIDecoded(URIUtils.Uri2String(getUri()));
				if (Logger.logTrace()) {
					Logger.trace("Done parsing URL: " + getUri().toString() + " resulting in: " + svnurl.toString());
				}
				
				if (this.username != null) {
					ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.username,
					                                                                                     this.password);
					repository.setAuthenticationManager(authManager);
				}
				
				repository = SVNRepositoryFactory.create(svnurl);
				
				this.startRevision = (startRevision != null
				                                           ? SVNRevision.parse(startRevision)
				                                           : SVNRevision.create(1));
				this.endRevision = (endRevision != null
				                                       ? SVNRevision.parse(endRevision)
				                                       : SVNRevision.create(repository.getLatestRevision()));
				
				if (this.startRevision.getNumber() < 0) {
					if (this.startRevision.equals(SVNRevision.PREVIOUS)) {
						this.startRevision = SVNRevision.create(repository.getLatestRevision() - 1);
					} else {
						this.startRevision = SVNRevision.create(repository.getLatestRevision());
					}
				}
				
				if (this.endRevision.getNumber() < 0) {
					if (this.endRevision.equals(SVNRevision.PREVIOUS)) {
						this.endRevision = SVNRevision.create(repository.getLatestRevision() - 1);
					} else {
						this.endRevision = SVNRevision.create(repository.getLatestRevision());
					}
				}
				
				initialized = true;
				
				if (Logger.logInfo()) {
					Logger.info("Setup repository: " + this);
				}
				
			} catch (SVNException e) {
				throw new InvalidRepositoryURI(e.getMessage());
			}
			
		} else {
			throw new InvalidProtocolType(getUri().toURL().getProtocol().toUpperCase());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubversionRepository [password=" + (password != null
		                                                            ? password.replaceAll(".", "*")
		                                                            : "(unset)") + ", svnurl=" + svnurl + ", type="
		        + type + ", uri=" + getUri() + ", username=" + (username != null
		                                                                        ? username
		                                                                        : "(unset)") + ", startRevision="
		        + startRevision + ", endRevision=" + endRevision + "]";
	}
}
