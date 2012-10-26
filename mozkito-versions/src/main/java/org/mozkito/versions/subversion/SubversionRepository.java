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
/**
 * 
 */
package org.mozkito.versions.subversion;

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

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.URIUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.output.NullOutputStream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.mozkito.exceptions.InvalidProtocolType;
import org.mozkito.exceptions.InvalidRepositoryURI;
import org.mozkito.exceptions.UnsupportedProtocolType;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.IRevDependencyGraph;
import org.mozkito.versions.ProtocolType;
import org.mozkito.versions.Repository;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.elements.LogIterator;
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

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * Subversion connector extending the {@link Repository} base class.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
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
	private File          tmpDir;
	
	/**
	 * Instantiates a new subversion repository.
	 */
	public SubversionRepository() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public List<AnnotationEntry> annotate(@NotEmpty final String filePath,
	                                      @NotEmpty final String revision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		SVNURL relativePath;
		try {
			
			relativePath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			final SVNLogClient logClient = new SVNLogClient(this.repository.getAuthenticationManager(),
			                                                SVNWCUtil.createDefaultOptions(true));
			
			final SVNRevision svnRevision = buildRevision(revision);
			
			// check out the svnurl recursively into the createDir visible from
			// revision 0 to given revision string
			final SubversionAnnotationHandler annotateHandler = new SubversionAnnotationHandler();
			logClient.doAnnotate(relativePath, svnRevision, buildRevision("0"), svnRevision, annotateHandler);
			return annotateHandler.getResults();
		} catch (final SVNException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new RuntimeException();
		}
	}
	
	/**
	 * Converts a given string to the corresponding SVNRevision. This requires
	 * {@link Repository#setup(URI, String, String)} to be executed.
	 * 
	 * @param revision
	 *            the string representing an SVN revision. This is either a numeric of type long or a case insensitive
	 *            version of the alias string versions. This may not be null.
	 * @return the corresponding SVNRevision
	 */
	private SVNRevision buildRevision(@NotNull @NotEmpty final String revision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		SVNRevision svnRevision;
		
		try {
			final Long revisionNumber = Long.parseLong(revision);
			svnRevision = SVNRevision.create(Long.valueOf(revisionNumber));
		} catch (final NumberFormatException e) {
			svnRevision = SVNRevision.parse(revision.toUpperCase());
		}
		
		Condition.notNull(svnRevision, "SVNRevision may never be null at this point.");
		
		try {
			if (svnRevision.getNumber() < 0) {
				if (svnRevision.equals(SVNRevision.PREVIOUS)) {
					
					svnRevision = SVNRevision.create(this.repository.getLatestRevision() - 1);
				} else {
					svnRevision = SVNRevision.create(this.repository.getLatestRevision());
				}
			}
		} catch (final SVNException e) {
			
			if (Logger.logError()) {
				Logger.error(e);
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
	 * @see org.mozkito.versions.Repository#checkoutPath(java.lang. String, java.lang.String)
	 */
	@Override
	@NoneNull
	public File checkoutPath(@NotEmpty final String relativeRepoPath,
	                         @NotEmpty final String revision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		if (this.tmpDir == null) {
			this.workingDirectory = FileUtils.createDir(FileUtils.tmpDir,
			                                            "moskito_clone_" + DateTimeUtils.currentTimeMillis(),
			                                            FileShutdownAction.DELETE);
		} else {
			this.workingDirectory = FileUtils.createDir(this.tmpDir,
			                                            "moskito_clone_" + DateTimeUtils.currentTimeMillis(),
			                                            FileShutdownAction.DELETE);
		}
		
		Condition.notNull(this.workingDirectory, "Cannot operate on working directory that is set to Null");
		
		SVNURL checkoutPath;
		try {
			
			checkoutPath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + relativeRepoPath);
			final SVNUpdateClient updateClient = new SVNUpdateClient(this.repository.getAuthenticationManager(),
			                                                         SVNWCUtil.createDefaultOptions(true));
			
			final SVNRevision svnRevision = buildRevision(revision);
			// check out the svnurl recursively into the createDir visible from
			// revision 0 to given revision string
			updateClient.doCheckout(checkoutPath, this.workingDirectory, svnRevision, svnRevision, SVNDepth.INFINITY,
			                        false);
			
			return this.workingDirectory;
		} catch (final SVNException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#diff(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public Collection<Delta> diff(@NotEmpty final String filePath,
	                              @NotEmpty final String baseRevision,
	                              @NotEmpty final String revisedRevision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		try {
			final SVNURL repoPath = SVNURL.parseURIDecoded(this.repository.getRepositoryRoot(true) + "/" + filePath);
			final SVNRevision fromRevision = buildRevision(baseRevision);
			final SVNRevision toRevision = buildRevision(revisedRevision);
			final SVNDiffClient diffClient = new SVNDiffClient(this.repository.getAuthenticationManager(),
			                                                   SVNWCUtil.createDefaultOptions(true));
			diffClient.getDiffGenerator().setDiffDeleted(true);
			final SubversionDiffParser diffParser = new SubversionDiffParser();
			diffClient.setDiffGenerator(diffParser);
			
			diffClient.doDiff(repoPath, toRevision, fromRevision, toRevision, SVNDepth.FILES, false,
			                  new NullOutputStream());
			
			// delete tmp diff files
			final File pwd = new File(System.getProperty("user.dir"));
			for (final File file : FileUtils.listFiles(pwd, null, false)) {
				if (file.getName().startsWith(".diff.")) {
					file.delete();
				}
			}
			
			return diffParser.getDeltas();
			
		} catch (final SVNException e) {
			// try to checkout file in first revision
			final String parentPath = filePath.substring(0, filePath.lastIndexOf("/"));
			final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
			final File parentDir = checkoutPath(parentPath, baseRevision);
			if ((parentDir == null) || (!parentDir.exists())) {
				// checkout failed too. Return null
				return null;
			}
			final File checkedOutFile = new File(parentDir.getAbsolutePath() + FileUtils.fileSeparator + fileName);
			if (!checkedOutFile.exists()) {
				return null;
			}
			final List<String> lines = FileUtils.fileToLines(checkedOutFile);
			final Patch patch = DiffUtils.diff(lines, new ArrayList<String>(0));
			return patch.getDeltas();
		}
	}
	
	@Override
	public String gatherToolInformation() {
		final StringBuilder builder = new StringBuilder();
		final CodeSource codeSource = SVNRepository.class.getProtectionDomain().getCodeSource();
		builder.append(getHandle()).append(" is using SVNKit from: ").append(codeSource.getLocation().getPath());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getChangedPaths(java.lang .String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public Map<String, ChangeType> getChangedPaths(@NotNull @NotEmpty final String revision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		final Long revisionNumber = buildRevision(revision).getNumber();
		final Map<String, ChangeType> map = new HashMap<String, ChangeType>();
		Collection<SVNLogEntry> logs;
		
		try {
			logs = this.repository.log(new String[] { "" }, null, revisionNumber, revisionNumber, true, true);
			
			for (final SVNLogEntry entry : logs) {
				final Map<Object, SVNLogEntryPath> changedPaths = entry.getChangedPaths();
				for (final Object o : changedPaths.keySet()) {
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
								        + Settings.getReportThis());
							}
					}
				}
			}
			return map;
		} catch (final SVNException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new RuntimeException();
		}
	}
	
	@Override
	public String getEndRevision() {
		return this.endRevision.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		Condition.notNull(this.startRevision, "startRevision must not be null at this point.");
		CompareCondition.greater(this.startRevision.getNumber(), 0l, "startRevision must be positive at this point.");
		
		return this.startRevision.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getFormerPathName(java. lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public String getFormerPathName(@NotEmpty final String revision,
	                                @NotEmpty final String pathName) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		final Long revisionNumber = buildRevision(revision).getNumber();
		
		try {
			@SuppressWarnings ("unchecked")
			final Collection<SVNLogEntry> logs = this.repository.log(new String[] { "" }, null, revisionNumber,
			                                                         revisionNumber, true, true);
			
			for (final SVNLogEntry entry : logs) {
				@SuppressWarnings ("unchecked")
				final Map<Object, SVNLogEntryPath> changedPaths = entry.getChangedPaths();
				for (final Object o : changedPaths.keySet()) {
					final SVNLogEntryPath logEntryPath = changedPaths.get(o);
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
								final String copyPath = logEntryPath.getCopyPath().substring(1) + "/";
								return copyPath
								        + pathName.substring(logEntryPath.getPath().length(), pathName.length());
							}
					}
					
				}
			}
			
		} catch (final SVNException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new RuntimeException();
		}
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getLastRevisionId()
	 */
	@Override
	public String getHEADRevisionId() {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		Condition.notNull(this.endRevision, "endRevision must not be null at this point.");
		CompareCondition.greater(this.endRevision.getNumber(), 0l, "endRevision must be positive at this point");
		
		try {
			return (this.repository.getLatestRevision() > this.endRevision.getNumber()
			                                                                          ? this.endRevision.toString()
			                                                                          : this.repository.getLatestRevision()
			                                                                                  + "");
		} catch (final SVNException e) {
			
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getRelativeTransactionId (java.lang.String, long)
	 */
	@Override
	public String getRelativeTransactionId(@NotNull final String transactionId,
	                                       final long index) {
		if ((buildRevision(transactionId).getNumber() + index) > buildRevision(getEndRevision()).getNumber()) {
			return getEndRevision();
		} else if ((buildRevision(transactionId).getNumber() + index) < buildRevision(getFirstRevisionId()).getNumber()) {
			return getFirstRevisionId();
		} else {
			return (buildRevision(transactionId).getNumber() + index) + "";
		}
	}
	
	@Override
	public IRevDependencyGraph getRevDependencyGraph() {
		// PRECONDITIONS
		
		try {
			throw new UnrecoverableError("Support hasn't been implemented yet. " + Settings.getReportThis());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public IRevDependencyGraph getRevDependencyGraph(final PersistenceUtil persistenceUtil) {
		// PRECONDITIONS
		
		try {
			throw new UnrecoverableError("Support hasn't been implemented yet. " + Settings.getReportThis());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getTransactionCount()
	 */
	@Override
	public long getTransactionCount() {
		try {
			return this.repository.getLatestRevision();
		} catch (final SVNException e) {
			
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new RuntimeException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getTransactionId(long)
	 */
	@Override
	public String getTransactionId(final long index) {
		return (1 + index) + "";
	}
	
	@Override
	public File getWokingCopyLocation() {
		return this.workingDirectory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#log(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	@NoneNull
	public List<LogEntry> log(@NotEmpty final String fromRevision,
	                          @NotEmpty final String toRevision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		final SVNRevision fromSVNRevision = buildRevision(fromRevision);
		final SVNRevision toSVNRevision = buildRevision(toRevision);
		
		final List<LogEntry> list = new LinkedList<LogEntry>();
		
		Collection<SVNLogEntry> logs;
		try {
			logs = this.repository.log(new String[] { "" }, null, fromSVNRevision.getNumber(),
			                           toSVNRevision.getNumber(), true, true);
			LogEntry buff = null;
			for (final SVNLogEntry entry : logs) {
				final LogEntry current = new LogEntry(entry.getRevision() + "", buff,
				                                      (entry.getAuthor() != null
				                                                                ? new Person(entry.getAuthor(), null,
				                                                                             null)
				                                                                : null), entry.getMessage(),
				                                      new DateTime(entry.getDate()), "");
				list.add(current);
				buff = current;
			}
			return list;
		} catch (final SVNException e) {
			if (Logger.logError()) {
				Logger.error(e);
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
			                                       : SVNRevision.create(this.repository.getLatestRevision()));
		} catch (final SVNException e) {
			if (Logger.logError()) {
				Logger.error(e);
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
	 * @see org.mozkito.versions.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) throws MalformedURLException,
	                                                       InvalidProtocolType,
	                                                       InvalidRepositoryURI,
	                                                       UnsupportedProtocolType {
		setup(address, null, null, branchFactory, tmpDir, mainBranchName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  final String username,
	                  final String password,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) throws MalformedURLException,
	                                                       InvalidProtocolType,
	                                                       InvalidRepositoryURI,
	                                                       UnsupportedProtocolType {
		setMainBranchName(mainBranchName);
		setUri(address);
		this.username = username;
		this.password = password;
		
		if (Logger.logDebug()) {
			SVNDebugLog.setDefaultLog(new SubversionLogger());
		}
		
		this.tmpDir = tmpDir;
		
		this.type = ProtocolType.valueOf(getUri().toURL().getProtocol().toUpperCase());
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
					Logger.info("Parsing URL: " + URIUtils.Uri2String(getUri()));
				}
				this.svnurl = SVNURL.parseURIDecoded(URIUtils.Uri2String(getUri()));
				if (Logger.logTrace()) {
					Logger.trace("Done parsing URL: " + getUri().toString() + " resulting in: "
					        + this.svnurl.toString());
				}
				
				if (this.username != null) {
					final ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.username,
					                                                                                           this.password);
					this.repository.setAuthenticationManager(authManager);
				}
				
				this.repository = SVNRepositoryFactory.create(this.svnurl);
				
				this.startRevision = (SVNRevision.create(1));
				this.endRevision = (SVNRevision.create(this.repository.getLatestRevision()));
				
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
				
				this.initialized = true;
				
				if (Logger.logInfo()) {
					Logger.info("Setup repository: " + this);
				}
				
			} catch (final SVNException e) {
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
		return "SubversionRepository [password=" + (this.password != null
		                                                                 ? this.password.replaceAll(".", "*")
		                                                                 : "(unset)") + ", svnurl=" + this.svnurl
		        + ", type=" + this.type + ", uri=" + getUri() + ", username=" + (this.username != null
		                                                                                              ? this.username
		                                                                                              : "(unset)")
		        + ", startRevision=" + this.startRevision + ", endRevision=" + this.endRevision + "]";
	}
}
