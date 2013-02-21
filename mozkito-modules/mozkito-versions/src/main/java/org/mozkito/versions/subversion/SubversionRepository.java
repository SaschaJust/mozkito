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

package org.mozkito.versions.subversion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.URIUtils;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.io.output.NullOutputStream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.util.SVNDebugLog;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import org.mozkito.persistence.model.Person;
import org.mozkito.versions.ProtocolType;
import org.mozkito.versions.Repository;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.elements.RevDependencyGraph.EdgeType;
import org.mozkito.versions.exceptions.InvalidProtocolType;
import org.mozkito.versions.exceptions.InvalidRepositoryURI;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.exceptions.UnsupportedProtocolType;
import org.mozkito.versions.model.Branch;

/**
 * Subversion connector extending the {@link Repository} base class.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * @see Repository
 */
public class SubversionRepository extends Repository {
	
	/** The end revision. */
	private SVNRevision        endRevision;
	
	/** The initialized. */
	private boolean            initialized    = false;
	
	/** The password. */
	private String             password;
	
	/** The repository. */
	private SVNRepository      repository;
	
	/** The start revision. */
	private SVNRevision        startRevision;
	
	/** The svnurl. */
	private SVNURL             svnurl;
	
	/** The type. */
	private ProtocolType       type;
	
	/** The username. */
	private String             username;
	
	/** The working directory. */
	private File               workingDirectory;
	
	/** The tmp dir. */
	private File               tmpDir;
	
	/** The rev dep graph. */
	private RevDependencyGraph revDepGraph;
	
	/** The Constant BRANCH_PATTERN. */
	private static final Regex BRANCH_PATTERN = new Regex("\\/?branches/({branch_name}[^\\/]+)");
	
	/**
	 * Instantiates a new subversion repository.
	 */
	public SubversionRepository() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public List<AnnotationEntry> annotate(@NotEmpty final String filePath,
	                                      @NotEmpty final String revision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		SVNURL relativePath;
		try {
			
			relativePath = SVNURL.parseURIEncoded(SVNEncodingUtil.autoURIEncode(this.svnurl + "/" + filePath));
			final SVNLogClient logClient = new SVNLogClient(this.repository.getAuthenticationManager(),
			                                                SVNWCUtil.createDefaultOptions(true));
			
			final SVNRevision svnRevision = buildRevision(revision);
			
			// check out the svnurl recursively into the createDir visible from
			// revision 0 to given revision string
			final SubversionAnnotationHandler annotateHandler = new SubversionAnnotationHandler();
			logClient.doAnnotate(relativePath, svnRevision, buildRevision("0"), svnRevision, annotateHandler);
			return annotateHandler.getResults();
		} catch (final SVNException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * Converts a given string to the corresponding SVNRevision. This requires
	 * 
	 * @param revision
	 *            the string representing an SVN revision. This is either a numeric of type long or a case insensitive
	 *            version of the alias string versions. This may not be null.
	 * @return the corresponding SVNRevision {@link Repository#setup(URI, String, String)} to be executed.
	 * @throws SVNException
	 *             the sVN exception
	 */
	private SVNRevision buildRevision(@NotNull @NotEmpty final String revision) throws SVNException {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		SVNRevision svnRevision;
		
		try {
			final Long revisionNumber = Long.parseLong(revision);
			svnRevision = SVNRevision.create(Long.valueOf(revisionNumber));
		} catch (final NumberFormatException e) {
			svnRevision = SVNRevision.parse(revision.toUpperCase());
		}
		
		Condition.notNull(svnRevision, "SVNRevision may never be null at this point.");
		
		if (svnRevision.getNumber() < 0) {
			if (svnRevision.equals(SVNRevision.PREVIOUS)) {
				
				svnRevision = SVNRevision.create(this.repository.getLatestRevision() - 1);
			} else {
				svnRevision = SVNRevision.create(this.repository.getLatestRevision());
			}
		}
		
		return svnRevision;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#checkoutPath(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public File checkoutPath(@NotEmpty final String relativeRepoPath,
	                         @NotEmpty final String revision) throws RepositoryOperationException {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		try {
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
			
			final SVNUpdateClient updateClient = new SVNUpdateClient(this.repository.getAuthenticationManager(),
			                                                         SVNWCUtil.createDefaultOptions(true));
			
			final SVNRevision svnRevision = buildRevision(revision);
			// check out the svnurl recursively into the createDir visible from
			// revision 0 to given revision string
			updateClient.doCheckout(this.repository.getRepositoryRoot(true), this.workingDirectory, svnRevision,
			                        svnRevision, SVNDepth.INFINITY, false);
			final File result = new File(this.workingDirectory.getAbsolutePath() + "/" + relativeRepoPath);
			if (!result.exists()) {
				return null;
			}
			return result;
		} catch (final SVNException | FilePermissionException e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#diff(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public Collection<Delta> diff(@NotEmpty final String filePath,
	                              @NotEmpty final String baseRevision,
	                              @NotEmpty final String revisedRevision) throws RepositoryOperationException {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		try {
			final String uriEncode = SVNEncodingUtil.autoURIEncode(this.repository.getRepositoryRoot(true) + "/"
			        + filePath);
			final SVNURL repoPath = SVNURL.parseURIEncoded(uriEncode);
			final SVNRevision fromRevision = buildRevision(baseRevision);
			final SVNRevision toRevision = buildRevision(revisedRevision);
			final SVNDiffClient diffClient = new SVNDiffClient(this.repository.getAuthenticationManager(),
			                                                   SVNWCUtil.createDefaultOptions(true));
			
			diffClient.getDiffGenerator().setDiffDeleted(true);
			final SubversionDiffParser diffParser = new SubversionDiffParser();
			diffClient.setDiffGenerator(diffParser);
			
			SVNNodeKind nodeKind = this.repository.checkPath(filePath, fromRevision.getNumber());
			if (nodeKind.equals(SVNNodeKind.NONE)) {
				nodeKind = this.repository.checkPath(filePath, toRevision.getNumber());
			}
			if (!nodeKind.equals(SVNNodeKind.FILE)) {
				throw new IllegalArgumentException(
				                                   String.format("Repository.diff() is only defined on file paths pointing to files not to directories. Supplied file path %s points to a directory.",
				                                                 filePath));
			}
			
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
			try {
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
			} catch (final IOException e1) {
				throw new RepositoryOperationException(e1);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#gatherToolInformation()
	 */
	@Override
	public String gatherToolInformation() {
		final StringBuilder builder = new StringBuilder();
		final CodeSource codeSource = SVNRepository.class.getProtectionDomain().getCodeSource();
		
		builder.append(getClassName()).append(" is using SVNKit from: ").append(codeSource.getLocation().getPath());
		builder.append(FileUtils.lineSeparator);
		
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getChangedPaths(java.lang.String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public Map<String, ChangeType> getChangedPaths(@NotNull @NotEmpty final String revision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		try {
			final Long revisionNumber = buildRevision(revision).getNumber();
			final Map<String, ChangeType> map = new HashMap<String, ChangeType>();
			Collection<SVNLogEntry> logs;
			
			logs = this.repository.log(new String[] { "" }, null, revisionNumber, revisionNumber, true, true);
			
			for (final SVNLogEntry entry : logs) {
				final Map<String, SVNLogEntryPath> changedPaths = entry.getChangedPaths();
				for (final String o : changedPaths.keySet()) {
					switch (changedPaths.get(o).getType()) {
						case SVNLogEntryPath.TYPE_MODIFIED:
							map.put(changedPaths.get(o).getPath(), ChangeType.Modified);
							break;
						case SVNLogEntryPath.TYPE_ADDED:
							final String copyPath = changedPaths.get(o).getCopyPath();
							if (copyPath != null) {
								map.put(changedPaths.get(o).getPath(), ChangeType.Renamed);
							} else {
								map.put(changedPaths.get(o).getPath(), ChangeType.Added);
							}
							break;
						case SVNLogEntryPath.TYPE_DELETED:
							map.put(changedPaths.get(o).getPath(), ChangeType.Deleted);
							break;
						case SVNLogEntryPath.TYPE_REPLACED:
							map.put(changedPaths.get(o).getPath(), ChangeType.Renamed);
							break;
						default:
							throw UnrecoverableError.format("Unsupported change type '%s'. %s", changedPaths.get(o)
							                                                                                .getType(),
							                                Settings.getReportThis());
							
					}
				}
			}
			
			return map;
		} catch (final SVNException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getChangeSetCount()
	 */
	@Override
	public long getChangeSetCount() {
		try {
			return this.repository.getLatestRevision();
		} catch (final SVNException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getChangeSetId(long)
	 */
	@Override
	public String getChangeSetId(final long index) {
		if (index < getChangeSetCount()) {
			return String.valueOf(1 + index);
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getChangeSetIndex(java.lang.String)
	 */
	@Override
	public long getChangeSetIndex(final String changeSetId) {
		String searchRev = changeSetId;
		
		if ("HEAD".equals(changeSetId.toUpperCase()) || ("TIP".equals(changeSetId.toUpperCase()))) {
			searchRev = getHEADRevisionId();
		}
		
		final long index = Long.valueOf(searchRev).longValue() - 1;
		
		if (index < 0) {
			return -1;
		}
		
		return index;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getEndRevision()
	 */
	@Override
	public String getEndRevision() {
		return this.endRevision.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		Condition.notNull(this.startRevision, "startRevision must not be null at this point.");
		CompareCondition.greater(this.startRevision.getNumber(), 0l, "startRevision must be positive at this point.");
		
		return this.startRevision.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getFormerPathName(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public String getFormerPathName(@NotEmpty final String revision,
	                                @NotEmpty final String pathName) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		try {
			final Long revisionNumber = buildRevision(revision).getNumber();
			
			@SuppressWarnings ("unchecked")
			final Collection<SVNLogEntry> logs = this.repository.log(new String[] { "" }, null, revisionNumber,
			                                                         revisionNumber, true, true);
			
			for (final SVNLogEntry entry : logs) {
				
				final Map<String, SVNLogEntryPath> changedPaths = entry.getChangedPaths();
				for (final String o : changedPaths.keySet()) {
					final SVNLogEntryPath logEntryPath = changedPaths.get(o);
					switch (logEntryPath.getType()) {
						case SVNLogEntryPath.TYPE_REPLACED:
						case SVNLogEntryPath.TYPE_ADDED:
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
							break;
						default:
							// here no renaming can have happend. Simply do nothing which means return null.
							break;
					}
					
				}
			}
			
		} catch (final SVNException e) {
			throw new UnrecoverableError(e);
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getHEADRevisionId()
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
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getRevDependencyGraph()
	 */
	@Override
	public RevDependencyGraph getRevDependencyGraph() throws RepositoryOperationException {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		try {
			if (this.revDepGraph == null) {
				
				final String repoPath = this.svnurl.getPath();
				String branchName = Branch.MASTER_BRANCH_NAME;
				if (BRANCH_PATTERN.matches(repoPath)) {
					branchName = BRANCH_PATTERN.getGroup("branch_name");
				}
				
				this.revDepGraph = new RevDependencyGraph();
				this.revDepGraph.addBranch(branchName, getHEADRevisionId());
				
				for (final LogEntry logEntry : this.log("1", "HEAD")) {
					this.revDepGraph.addChangeSet(logEntry.getRevision());
					try {
						if (!logEntry.getRevision().equals(getHEADRevisionId())) {
							Long revNum = Long.valueOf(logEntry.getRevision());
							++revNum;
							this.revDepGraph.addEdge(logEntry.getRevision(), revNum.toString(), EdgeType.BRANCH_EDGE);
						}
					} catch (final NumberFormatException e) {
						throw UnrecoverableError.format(e,
						                                "Could not interpret revision ID %s as Long value. Returning NULL!",
						                                logEntry.getRevision());
					}
				}
			}
			return this.revDepGraph;
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getWorkingCopyLocation()
	 */
	@Override
	public File getWorkingCopyLocation() {
		return this.workingDirectory;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#log(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	@NoneNull
	public List<LogEntry> log(@NotEmpty final String fromRevision,
	                          @NotEmpty final String toRevision) {
		Condition.check(this.initialized, "Repository has to be initialized before calling this method.");
		
		try {
			final SVNRevision fromSVNRevision = buildRevision(fromRevision);
			final SVNRevision toSVNRevision = buildRevision(toRevision);
			
			final List<LogEntry> list = new LinkedList<LogEntry>();
			
			final Collection<SVNLogEntry> logs = this.repository.log(new String[] { "" }, null,
			                                                         fromSVNRevision.getNumber(),
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
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#setEndRevision(java.lang.String)
	 */
	@Override
	protected void setEndRevision(final String endRevision) {
		try {
			this.endRevision = (endRevision != null
			                                       ? SVNRevision.parse(endRevision)
			                                       : SVNRevision.create(this.repository.getLatestRevision()));
		} catch (final SVNException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#setStartRevision(java.lang.String)
	 */
	@Override
	protected void setStartRevision(final String startRevision) {
		this.startRevision = (startRevision != null
		                                           ? SVNRevision.parse(startRevision)
		                                           : SVNRevision.create(1));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.io.File, java.lang.String)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) throws RepositoryOperationException {
		setup(address, null, null, tmpDir, mainBranchName);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String, java.io.File,
	 *      java.lang.String)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  final String username,
	                  final String password,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) throws RepositoryOperationException {
		try {
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
						throw new UnsupportedProtocolType("Failed to setup in '" + this.type.name()
						        + "' mode. Unsupported at this time. " + getClassName() + " does not support protocol "
						        + this.type.name());
				}
				try {
					if (Logger.logInfo()) {
						Logger.info("Parsing URL: " + URIUtils.Uri2String(getUri()));
					}
					this.svnurl = SVNURL.parseURIEncoded(SVNEncodingUtil.autoURIEncode(URIUtils.Uri2String(getUri())));
					if (Logger.logTrace()) {
						Logger.trace("Done parsing URL: " + getUri().toString() + " resulting in: "
						        + this.svnurl.toString());
					}
					
					if (this.username != null) {
						final ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.username,
						                                                                                           this.password);
						this.repository.setAuthenticationManager(authManager);
					}
					
					this.repository = SVNClientManager.newInstance().createRepository(this.svnurl, true);
					
					this.startRevision = (SVNRevision.create(1));
					final SVNDirEntry entry = this.repository.info("/", -1);
					this.endRevision = SVNRevision.create(entry.getRevision());
					
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
		} catch (final InvalidRepositoryURI | UnsupportedProtocolType | MalformedURLException | InvalidProtocolType e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("SubversionRepository [endRevision=");
		builder.append(this.endRevision);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append(", password=");
		builder.append((this.password != null
		                                     ? "*************"
		                                     : "(unset)"));
		builder.append(", repository=");
		builder.append(this.repository);
		builder.append(", startRevision=");
		builder.append(this.startRevision);
		builder.append(", svnurl=");
		builder.append(this.svnurl);
		builder.append(", type=");
		builder.append(this.type);
		builder.append(", username=");
		builder.append((this.username != null
		                                     ? "*************"
		                                     : "(unset)"));
		builder.append(", workingDirectory=");
		builder.append(this.workingDirectory);
		builder.append(", tmpDir=");
		builder.append(this.tmpDir);
		builder.append("]");
		
		return builder.toString();
	}
	
}
