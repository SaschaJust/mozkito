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
package org.mozkito.versions.git;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.MinLength;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.utilities.commons.URIUtils;
import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.utilities.execution.CommandExecutor;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.FileUtils.FileShutdownAction;
import org.mozkito.utilities.io.exceptions.ExternalExecutableException;
import org.mozkito.versions.DistributedCommandLineRepository;
import org.mozkito.versions.LogParser;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.elements.RevDependencyGraph.EdgeType;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.Branch;

/**
 * The Class GitRepository. This class is _not_ thread safe.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GitRepository extends DistributedCommandLineRepository {
	
	/** The Constant REFS_TAGS_LENGTH. */
	private static final int                 REFS_TAGS_LENGTH    = 10;
	
	/** The Constant REFS_PULL_LENGTH. */
	private static final int                 REFS_PULL_LENGTH    = 10;
	
	/** The Constant REFS_REMOTES_LENGTH. */
	private static final int                 REFS_REMOTES_LENGTH = 13;
	
	/** The Constant REFS_HEAD_LENGTH. */
	private static final int                 REFS_HEAD_LENGTH    = 11;
	
	/** The Constant GIT_HASH_LENGTH. */
	private static final int                 GIT_HASH_LENGTH     = 40;
	
	/** The current revision. */
	private String                           currentRevision     = null;
	
	/** The charset. */
	protected static Charset                 charset             = Charset.defaultCharset();
	static {
		if (Charset.isSupported("UTF8")) {
			GitRepository.charset = Charset.forName("UTF8");
		}
	}
	
	/** The Constant dtf. */
	protected static final DateTimeFormatter DTF                 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
	
	/** The Constant regex. */
	protected static final Regex             REGEX               = new Regex(
	                                                                         ".*\\(({author}.*)\\s+({date}\\d{4}-\\d{2}-\\d{2}\\s+[^ ]+\\s+[+-]\\d{4})\\s+[^)]*\\)\\s+({codeline}.*)");
	
	/** The Constant formerPathRegex. */
	protected static final Regex             FORMER_PATH_REGEX   = new Regex(
	                                                                         "^[^\\s]+\\s+({result}[^\\s]+)\\s+[^\\s]+.*");
	
	/** The clone dir. */
	private File                             cloneDir;
	
	/** The transaction i ds. */
	private final List<String>               changeSetIds        = new LinkedList<String>();
	
	/** The rev dep graph. */
	private RevDependencyGraph               revDepGraph;
	
	/**
	 * Instantiates a new git repository.
	 * 
	 * @param personFactory
	 *            the person factory
	 */
	public GitRepository(final PersonFactory personFactory) {
		super(personFactory);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(final String filePath,
	                                      final String revision) throws RepositoryOperationException {
		Condition.notNull(filePath, "Annotation of null path not possible");
		Condition.notNull(revision, "Annotation requires revision");
		
		final List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		final String firstRev = getFirstRevisionId();
		Tuple<Integer, List<String>> response;
		
		try {
			response = CommandExecutor.execute("git", new String[] { "blame", "-lf", revision, "--", filePath },
			                                   this.cloneDir, null, new HashMap<String, String>());
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			return null;
		}
		
		for (final String line : response.getSecond()) {
			String sha = line.substring(0, GitRepository.GIT_HASH_LENGTH);
			if (line.startsWith("^") && (firstRev.startsWith(line.substring(1, GitRepository.GIT_HASH_LENGTH)))) {
				sha = firstRev;
			}
			
			final String[] lineParts = line.split(" ");
			if (lineParts.length < 2) {
				throw new UnrecoverableError("Could not parse git blame output!");
			}
			final String fileName = lineParts[1];
			String author = "<unknown>";
			DateTime date = new DateTime();
			
			String lineContent = "<unkown>";
			if (GitRepository.REGEX.matchesFull(line)) {
				author = GitRepository.REGEX.getGroup("author");
				date = new DateTime(GitRepository.DTF.parseDateTime(GitRepository.REGEX.getGroup("date")));
				lineContent = GitRepository.REGEX.getGroup("codeline");
			} else {
				
				throw new UnrecoverableError("Could not extract author and date info from log entry for revision `"
				        + revision + "`");
			}
			
			if (fileName.equals(filePath)) {
				result.add(new AnnotationEntry(sha, author, date, lineContent));
			} else {
				result.add(new AnnotationEntry(sha, author, date, lineContent, fileName));
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#checkoutPath(java.lang. String, java.lang.String)
	 */
	@Override
	public File checkoutPath(final String relativeRepoPath,
	                         final String revision) throws RepositoryOperationException {
		Condition.notNull(relativeRepoPath, "Cannot check out NULL path");
		Condition.notNull(revision, "Checking ut requries revision");
		
		if ((this.currentRevision == null) || (!revision.equals(this.currentRevision))) {
			Tuple<Integer, List<String>> response;
			try {
				response = CommandExecutor.execute("git", new String[] { "checkout", revision }, this.cloneDir, null,
				                                   new HashMap<String, String>());
			} catch (final IOException e) {
				throw new RepositoryOperationException(e);
			}
			if (response.getFirst() != 0) {
				return null;
			}
			this.currentRevision = revision;
		}
		final File result = new File(this.cloneDir, relativeRepoPath);
		if (!result.exists()) {
			throw new RepositoryOperationException(
			                                       String.format("Could not get requested path using command `git checkout %s`. Abort.",
			                                                     revision));
		}
		return result;
	}
	
	/**
	 * Clone.
	 * 
	 * @param inputStream
	 *            the input stream
	 * @param destDir
	 *            the dest dir
	 * @return true, if successful
	 * @throws RepositoryOperationException
	 */
	private boolean clone(final InputStream inputStream,
	                      final String destDir) throws RepositoryOperationException {
		Condition.notNull(destDir, "[clone] `destDir` should not be null.");
		
		Tuple<Integer, List<String>> returnValue;
		try {
			if (Logger.logInfo()) {
				Logger.info("Cloning branch: %s. Executing: git clone -b %s -n -q %s %s", getMainBranchName(),
				            getMainBranchName(), URIUtils.uri2String(getUri()), destDir);
			}
			returnValue = CommandExecutor.execute("git", new String[] { "clone", "-b", getMainBranchName(), "-n", "-q",
			        URIUtils.uri2String(getUri()), destDir }, this.cloneDir, inputStream, new HashMap<String, String>());
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (returnValue.getFirst() == 0) {
			
			this.cloneDir = new File(destDir);
			if (!this.cloneDir.exists()) {
				final StringBuilder message = new StringBuilder();
				message.append("Could not clone git repository `");
				message.append(URIUtils.uri2String(getUri()));
				message.append("` to directory `");
				message.append(destDir);
				message.append("`");
				message.append(FileUtils.lineSeparator);
				message.append("Used command: `git clone -n -q ");
				message.append(URIUtils.uri2String(getUri()));
				message.append(" ");
				message.append(destDir);
				message.append("`.");
				message.append(FileUtils.lineSeparator);
				message.append("Got response:");
				message.append(FileUtils.lineSeparator);
				for (final String line : returnValue.getSecond()) {
					message.append(line);
					message.append(FileUtils.lineSeparator);
				}
				throw new UnrecoverableError(message.toString());
				
			}
			FileUtils.addToFileManager(this.cloneDir, FileShutdownAction.DELETE);
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#diff(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(final String filePath,
	                              final String baseRevision,
	                              final String revisedRevision) throws RepositoryOperationException {
		Condition.notNull(filePath, "Cannot diff NULL path");
		Condition.notNull(baseRevision, "cannot compare to NULL revision");
		Condition.notNull(revisedRevision, "cannot compare to NULL revision");
		
		String diffPath = filePath;
		if (diffPath.startsWith("/")) {
			diffPath = diffPath.substring(1);
		}
		
		// get the old version
		Tuple<Integer, List<String>> response;
		try {
			response = CommandExecutor.execute("git", new String[] { "show", baseRevision + ":" + diffPath },
			                                   this.cloneDir, null, new HashMap<String, String>());
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			return null;
		}
		final List<String> oldContent = response.getSecond();
		
		// get the new version
		List<String> newContent = new ArrayList<String>(0);
		
		try {
			response = CommandExecutor.execute("git", new String[] { "show", revisedRevision + ":" + diffPath },
			                                   this.cloneDir, null, new HashMap<String, String>());
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() == 0) {
			newContent = response.getSecond();
		}
		
		if ((!oldContent.isEmpty() && oldContent.get(0).startsWith("tree"))
		        || (!newContent.isEmpty() && newContent.get(0).startsWith("tree"))) {
			// filePath points to a directory
			throw new IllegalArgumentException(
			                                   String.format("Repository.diff() is only defined on file paths pointing to files not to directories. Supplied file path %s points to a directory.",
			                                                 filePath));
		}
		
		final Patch patch = DiffUtils.diff(oldContent, newContent);
		return patch.getDeltas();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.DistributedCommandLineRepository#executeLog(java.lang.String)
	 */
	@Override
	public Tuple<Integer, List<String>> executeLog(@MinLength (min = 4) @NotNull final String revision) throws RepositoryOperationException {
		return gitLog(revision);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.DistributedCommandLineRepository#executeLog(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public Tuple<Integer, List<String>> executeLog(@MinLength (min = 4) final String fromRevision,
	                                               @MinLength (min = 4) final String toRevision) throws RepositoryOperationException {
		final StringBuilder revisionSelectionBuilder = new StringBuilder();
		revisionSelectionBuilder.append(fromRevision);
		revisionSelectionBuilder.append("^..");
		revisionSelectionBuilder.append(toRevision);
		String revisionSelection = revisionSelectionBuilder.toString();
		if (fromRevision.equals(getFirstRevisionId())) {
			revisionSelection = toRevision;
		}
		
		return gitLog(revisionSelection);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#gatherToolInformation()
	 */
	@Override
	public String gatherToolInformation() {
		final StringBuilder builder = new StringBuilder();
		Tuple<Integer, List<String>> execute;
		try {
			execute = CommandExecutor.execute("git", new String[] { "--version" }, FileUtils.tmpDir, null, null);
		} catch (final IOException e) {
			execute = new Tuple<Integer, List<String>>(-1, new LinkedList<String>());
		}
		
		if (execute.getFirst() != 0) {
			builder.append(getClassName()).append(" could not determine `git` version. (Error code: ")
			       .append(execute.getFirst()).append(").");
			builder.append(FileUtils.lineSeparator);
			try {
				builder.append("Command was: ").append(FileUtils.checkExecutable("git")).append(" --version");
			} catch (final ExternalExecutableException e) {
				builder.append(e.getMessage());
			}
		} else {
			builder.append("Executable: ");
			try {
				builder.append(FileUtils.checkExecutable("git"));
			} catch (final ExternalExecutableException e) {
				builder.append(e.getMessage());
			}
			builder.append(FileUtils.lineSeparator);
			
			for (final String line : execute.getSecond()) {
				builder.append(line);
			}
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getChangedPaths()
	 */
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) throws RepositoryOperationException {
		Condition.notNull(revision, "Cannot get changed paths for null revision");
		
		Tuple<Integer, List<String>> response;
		try {
			response = CommandExecutor.execute("git", new String[] { "log", "--pretty=format:%H", "--name-status",
			        "-n1", revision }, this.cloneDir, null, new HashMap<String, String>());
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			return new HashMap<String, ChangeType>();
		}
		final List<String> lines = response.getSecond();
		
		// delete first line. Contains the SHA hash of the wanted transaction
		if (lines.size() < 1) {
			
			throw new UnrecoverableError(
			                             "Error while parsing GIT log to unveil changed paths for revision `"
			                                     + revision
			                                     + "`: git reported zero lines output. Abort parsing. Used command: git log --branches --remotes --pretty=format:%H --name-status -n1"
			                                     + revision);
		}
		final String removed = lines.remove(0);
		if ((!"HEAD".equals(revision.toUpperCase())) && (!removed.trim().equals(revision))) {
			throw new RepositoryOperationException(
			                                       String.format("Error while parsing GIT log to unveil changed paths for revision `%s`: wrong revision outputed. Abort parsing.",
			                                                     revision));
		}
		final Map<String, ChangeType> result = new HashMap<String, ChangeType>();
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				// found the end of the log entry.
				break;
			}
			line = line.replaceAll("\\s+", " ");
			final String[] lineParts = line.split(" ");
			if (lineParts.length < 2) {
				throw new RepositoryOperationException(
				                                       String.format("Error while parsing GIT log to unveil changed paths for revision `"
				                                               + revision
				                                               + "`: wrong line format detected. Abort parsing."
				                                               + FileUtils.lineSeparator + "Line:" + line));
			}
			final String type = lineParts[0];
			final String path = "/" + lineParts[1];
			if ("A".equals(type)) {
				result.put(path, ChangeType.Added);
			} else if ("C".equals(type)) {
				result.put(path, ChangeType.Modified);
			} else if ("D".equals(type)) {
				result.put(path, ChangeType.Deleted);
			} else if ("M".equals(type)) {
				result.put(path, ChangeType.Modified);
			} else if ("U".equals(type)) {
				result.put(path, ChangeType.Modified);
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getTransactionCount()
	 */
	@Override
	public long getChangeSetCount() throws RepositoryOperationException {
		final String[] args = new String[] { "log", "--branches", "--remotes", "--pretty=format:''" };
		Tuple<Integer, List<String>> response;
		
		try {
			response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			return -1;
		}
		return response.getSecond().size();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getChangeSetId(long)
	 */
	@Override
	public String getChangeSetId(@NotNegative ("Cannot get transaction id for revision number smaller than zero.") final long index) {
		// final String[] args = new String[] { "log", "--branches", "--remotes", "--pretty=format:%H", "--topo-order",
		// "--reverse" };
		// final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null,
		// null);
		// if (response.getFirst() != 0) {
		// return null;
		// }
		// return response.getSecond().get((int) index);
		return this.changeSetIds.get(Long.valueOf(index).intValue());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getTransactionIndex(java.lang.String)
	 */
	@Override
	public long getChangeSetIndex(final String changeSetId) throws RepositoryOperationException {
		if ("HEAD".equals(changeSetId.toUpperCase()) || "TIP".equals(changeSetId.toUpperCase())) {
			return this.changeSetIds.indexOf(getHEADRevisionId());
		}
		return this.changeSetIds.indexOf(changeSetId);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() throws RepositoryOperationException {
		if (getStartRevision() == null) {
			Tuple<Integer, List<String>> response;
			try {
				response = CommandExecutor.execute("git", new String[] { "log", "--branches", "--remotes",
				        "--pretty=format:%H", "--topo-order" }, this.cloneDir, null, new HashMap<String, String>());
			} catch (final IOException e) {
				throw new RepositoryOperationException(e);
			}
			
			if (response.getFirst() != 0) {
				throw new UnrecoverableError(
				                             "Getting the first revision ID failed during execution of the GIT command.");
			}
			if (response.getSecond().isEmpty()) {
				throw new UnrecoverableError(
				                             "Command ` git log --branches --remotes --pretty=format:%H --topo-order` did not produc any output!");
			}
			final List<String> lines = response.getSecond();
			return lines.get(lines.size() - 1).trim();
		}
		return getStartRevision();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getFormerPathName(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFormerPathName(final String revision,
	                                final String pathName) throws RepositoryOperationException {
		Condition.notNull(revision, "Cannot get former path name of null revision");
		Condition.notNull(pathName, "Cannot get former path name for null path");
		
		final String[] args = new String[] { "log", "--branches", "--remotes", "-r", revision + "^.." + revision, "-M",
		        "-C", "--name-status", "--diff-filter=RC" };
		Tuple<Integer, List<String>> response;
		
		try {
			response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			return null;
		}
		for (final String line : response.getSecond()) {
			if (((line.startsWith("R")) || (line.startsWith("C"))) && line.contains(pathName)) {
				final Match found = GitRepository.FORMER_PATH_REGEX.find(line);
				if (!found.hasGroups()) {
					if (Logger.logWarn()) {
						Logger.warn("Former path regex in Gitrepository did not match but should match.");
					}
					return null;
				}
				return GitRepository.FORMER_PATH_REGEX.getGroup("result");
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getLastRevisionId()
	 */
	@Override
	public String getHEADRevisionId() throws RepositoryOperationException {
		Tuple<Integer, List<String>> response;
		try {
			response = CommandExecutor.execute("git", new String[] { "rev-list", getMainBranchName(), "--branches",
			        "--remotes" }, this.cloneDir, null, new HashMap<String, String>());
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			throw new UnrecoverableError(
			                             String.format("Getting the HEAD revision ID failed while executing GIT command. Output: %s",
			                                           response.getSecond()));
		}
		if (response.getSecond().isEmpty()) {
			throw new UnrecoverableError(String.format("Command `git rev-parse %s` did not produce any output!",
			                                           getMainBranchName()));
		}
		return response.getSecond().get(0).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.DistributedCommandLineRepository#getLogParser()
	 */
	@Override
	protected LogParser getLogParser() {
		return new GitLogParser(getPersonFactory());
	}
	
	/**
	 * Gets the ls remote.
	 * 
	 * @return the ls remote
	 * @throws RepositoryOperationException
	 */
	public List<String> getLsRemote() throws RepositoryOperationException {
		Tuple<Integer, List<String>> response;
		try {
			response = CommandExecutor.execute("git", new String[] { "ls-remote", "." }, this.cloneDir, null,
			                                   new HashMap<String, String>(), GitRepository.charset);
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not get ls-remote.");
		}
		return response.getSecond();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getRevDependencyGraph()
	 */
	@Override
	public RevDependencyGraph getRevDependencyGraph() throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			try {
				if (this.revDepGraph == null) {
					this.revDepGraph = new RevDependencyGraph();
					
					// use `git ls-remote .` to get all branches and their HEADs
					final List<String> lsRemote = getLsRemote();
					for (final String line : lsRemote) {
						final String[] lineParts = line.split("\\s+");
						final String clHash = lineParts[0];
						String remoteName = lineParts[1];
						if (Logger.logDebug()) {
							Logger.debug("Found branch reference: " + remoteName);
						}
						if (remoteName.startsWith("refs/heads/")) {
							remoteName = remoteName.substring(REFS_HEAD_LENGTH);
							if ("master".equals(remoteName)) {
								continue;
							}
						} else if (remoteName.startsWith("refs/remotes/")) {
							remoteName = remoteName.substring(REFS_REMOTES_LENGTH);
							if ("origin/HEAD".equals(remoteName)) {
								continue;
							}
							if ("origin/master".equals(remoteName)) {
								remoteName = Branch.MASTER_BRANCH_NAME;
							}
						} else if (remoteName.startsWith("refs/pull/")) {
							remoteName = remoteName.substring(REFS_PULL_LENGTH);
						} else if (remoteName.startsWith("refs/tags/")) {
							remoteName = remoteName.substring(REFS_TAGS_LENGTH);
							if (remoteName.endsWith("^{}")) {
								remoteName = remoteName.replace("^{}", "");
								if (!this.revDepGraph.addTag(remoteName, clHash)) {
									final String hashForTag = this.revDepGraph.getHashForTag(remoteName);
									if (hashForTag != null) {
										this.revDepGraph.removeChangeSet(hashForTag);
									}
									this.revDepGraph.removeTag(remoteName);
									this.revDepGraph.addTag(remoteName, clHash);
								}
							} else {
								this.revDepGraph.addTag(remoteName, clHash);
							}
							continue;
						} else {
							continue;
						}
						if (Logger.logDebug()) {
							Logger.debug("Adding branch head for branch %s: %s.", remoteName, clHash);
						}
						this.revDepGraph.addBranch(remoteName, clHash);
					}
					
					// use `git rev-list` to get revs and their children: <commit> <branch child> <children ...>
					final List<String> revListParents = getRevListParents();
					for (final String line : revListParents) {
						final String[] lineParts = line.split("\\s+");
						if (lineParts.length < 1) {
							throw new UnrecoverableError(
							                             "Cannot process rev-list --parents. Detected line with no entires.");
						}
						final String child = lineParts[0];
						if (!this.revDepGraph.existsVertex(child)) {
							if (!this.revDepGraph.addChangeSet(child)) {
								if (Logger.logWarn()) {
									Logger.warn("Could not add change set %s.", child);
								}
							}
						}
						
						if (lineParts.length > 1) {
							final String branchParent = lineParts[1];
							if (!this.revDepGraph.addEdge(branchParent, child, EdgeType.BRANCH_EDGE)) {
								if (Logger.logWarn()) {
									Logger.warn("Could not add edge between %s -> %s. This might lead to inconsistent data. Please check earlier warnings and errors.",
									            branchParent, child);
								}
							}
							for (int i = 2; i < lineParts.length; ++i) {
								if (!this.revDepGraph.addEdge(lineParts[i], child, EdgeType.MERGE_EDGE)) {
									if (Logger.logWarn()) {
										Logger.warn("Could not add edge between %s -> %s. This might lead to inconsistent data. Please check earlier warnings and errors.",
										            lineParts[i], child);
									}
								}
							}
						}
					}
				}
				return this.revDepGraph;
			} catch (final IOException e) {
				throw new RepositoryOperationException(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the rev list parents.
	 * 
	 * @return the rev list parents
	 * @throws RepositoryOperationException
	 */
	public List<String> getRevListParents() throws RepositoryOperationException {
		Tuple<Integer, List<String>> response;
		try {
			response = CommandExecutor.execute("git", new String[] { "rev-list", "--encoding=UTF-8", "--parents",
			                                           "--branches", "--remotes", "--topo-order" }, this.cloneDir,
			                                   null, new HashMap<String, String>(),
			                                   GitRepository.charset);
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
		
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not get rev-list --children.");
		}
		return response.getSecond();
	}
	
	/*
	 * In case of git this method returns a file pointing to a bare repository mirror!
	 * 
	 * @see org.mozkito.versions.Repository#getWokingCopyLocation()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#getWokingCopyLocation()
	 */
	@Override
	public File getWorkingCopyLocation() {
		return this.cloneDir;
	}
	
	/**
	 * Git log.
	 * 
	 * @param revisionSelection
	 *            the revision selection
	 * @return the tuple
	 * @throws RepositoryOperationException
	 */
	private Tuple<Integer, List<String>> gitLog(@MinLength (min = 4) @NotNull final String revisionSelection) throws RepositoryOperationException {
		if (Logger.logDebug()) {
			Logger.debug("############# git log --pretty=fuller --branches --remotes --topo-order %s.",
			             revisionSelection);
		}
		
		try {
			return CommandExecutor.execute("git", new String[] { "log", "--pretty=fuller", "--branches", "--remotes",
			        "--topo-order", revisionSelection }, this.cloneDir, null, new HashMap<String, String>());
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  final File tmpDir,
	                  final String mainBranchName) throws RepositoryOperationException {
		Condition.notNull(address, "Setting up a repository without a corresponding address won't work.");
		
		setup(address, null, tmpDir, mainBranchName);
	}
	
	/**
	 * main setup method.
	 * 
	 * @param address
	 *            the address
	 * @param inputStream
	 *            the input stream
	 * @param branchFactory
	 *            the branch factory
	 * @param tmpDir
	 *            the tmp dir
	 * @param mainBranchName
	 *            the main branch name
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	private void setup(@NotNull final URI address,
	                   final InputStream inputStream,
	                   final File tmpDir,
	                   @NotNull final String mainBranchName) throws RepositoryOperationException {
		Condition.notNull(address, "Setting up a repository without a corresponding address won't work.");
		
		try {
			setMainBranchName(mainBranchName);
			setUri(address);
			
			File localCloneDir = null;
			if (tmpDir == null) {
				localCloneDir = FileUtils.createRandomDir("moskito_git_clone_",
				
				String.valueOf(DateTimeUtils.currentTimeMillis()), FileShutdownAction.DELETE);
			} else {
				localCloneDir = FileUtils.createRandomDir(tmpDir, "moskito_git_clone_",
				
				String.valueOf(DateTimeUtils.currentTimeMillis()), FileShutdownAction.DELETE);
			}
			
			if (!clone(inputStream, localCloneDir.getAbsolutePath())) {
				throw new UnrecoverableError("Failed to clone git repository from source: " + address.toString());
			}
			
			final String innerPath = ((getUri().getFragment()) != null)
			                                                           ? (getUri().getFragment())
			                                                           : "/";
			this.cloneDir = new File(localCloneDir.getAbsolutePath() + FileUtils.fileSeparator + innerPath);
			if (!this.cloneDir.exists()) {
				throw new UnrecoverableError("Could not access clone directory `" + this.cloneDir.getAbsolutePath()
				        + "`");
			}
			
			setStartRevision(getFirstRevisionId());
			setEndRevision(getHEADRevisionId());
			
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
			                                                                              "--pretty=format:%H",
			                                                                              "--branches", "--remotes",
			                                                                              "--topo-order" },
			                                                                      this.cloneDir, null,
			                                                                      new HashMap<String, String>());
			if (response.getFirst() != 0) {
				throw new UnrecoverableError("Could not fetch full list of revision IDs!");
			}
			if (Logger.logDebug()) {
				Logger.debug("############# git log --pretty=format:%H --branches --remotes --topo-order");
			}
			this.changeSetIds.clear();
			this.changeSetIds.addAll(response.getSecond());
			Collections.reverse(this.changeSetIds);
			
			if (!this.changeSetIds.isEmpty()) {
				Condition.check(getFirstRevisionId().equals(this.changeSetIds.get(0)),
				                "First revision ID and transaction ID list missmatch!");
				Condition.check(getHEADRevisionId().equals(this.changeSetIds.get(this.changeSetIds.size() - 1)),
				                "End revision ID and transaction ID list missmatch!");
			}
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  @NotNull final String username,
	                  @NotNull final String password,
	                  final File tmpDir,
	                  final String mainBranchName) throws RepositoryOperationException {
		Condition.notNull(address, "Setting up a repository without a corresponding address won't work.");
		Condition.notNull(username, "Calling this method requires user to be set.");
		Condition.notNull(password, "Calling this method requires password to be set.");
		setup(URIUtils.encodeUsername(address, username), new ByteArrayInputStream(password.getBytes()), tmpDir,
		      mainBranchName);
	}
}
