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
package de.unisaarland.cs.st.mozkito.versions.git;

import java.io.ByteArrayInputStream;
import java.io.File;
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

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.ioda.URIUtils;
import net.ownhero.dev.ioda.exceptions.ExternalExecutableException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kanuni.annotations.string.MinLength;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.versions.BranchFactory;
import de.unisaarland.cs.st.mozkito.versions.IRevDependencyGraph;
import de.unisaarland.cs.st.mozkito.versions.Repository;
import de.unisaarland.cs.st.mozkito.versions.elements.AnnotationEntry;
import de.unisaarland.cs.st.mozkito.versions.elements.ChangeType;
import de.unisaarland.cs.st.mozkito.versions.elements.LogEntry;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * The Class GitRepository. This class is _not_ thread safe.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GitRepository extends Repository {
	
	/** The current revision. */
	private String                           currentRevision = null;
	
	/** The charset. */
	protected static Charset                 charset         = Charset.defaultCharset();
	static {
		if (Charset.isSupported("UTF8")) {
			charset = Charset.forName("UTF8");
		}
	}
	
	/** The Constant dtf. */
	protected static final DateTimeFormatter dtf             = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
	
	/** The Constant regex. */
	protected static final Regex             regex           = new Regex(
	                                                                     ".*\\(({author}.*)\\s+({date}\\d{4}-\\d{2}-\\d{2}\\s+[^ ]+\\s+[+-]\\d{4})\\s+[^)]*\\)\\s+({codeline}.*)");
	
	/** The Constant formerPathRegex. */
	protected static final Regex             formerPathRegex = new Regex("^[^\\s]+\\s+({result}[^\\s]+)\\s+[^\\s]+.*");
	
	/** The clone dir. */
	private File                             cloneDir;
	
	/** The transaction i ds. */
	private List<String>                     transactionIDs  = new LinkedList<String>();
	
	/** The log cache. */
	private final HashMap<String, LogEntry>  logCache        = new HashMap<String, LogEntry>();
	
	/** The branch factory. */
	private BranchFactory                    branchFactory;
	
	/** The rev dep graph. */
	private GitRevDependencyGraph            revDepGraph;
	
	/**
	 * Instantiates a new git repository.
	 */
	public GitRepository() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(final String filePath,
	                                      final String revision) {
		Condition.notNull(filePath, "Annotation of null path not possible");
		Condition.notNull(revision, "Annotation requires revision");
		
		final List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		final String firstRev = getFirstRevisionId();
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "blame", "-lf",
		        revision, "--", filePath }, this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		
		for (final String line : response.getSecond()) {
			String sha = line.substring(0, 40);
			if (line.startsWith("^") && (firstRev.startsWith(line.substring(1, 40)))) {
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
			if (regex.matchesFull(line)) {
				author = regex.getGroup("author");
				date = new DateTime(dtf.parseDateTime(regex.getGroup("date")));
				lineContent = regex.getGroup("codeline");
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
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#checkoutPath(java.lang. String, java.lang.String)
	 */
	@Override
	public File checkoutPath(final String relativeRepoPath,
	                         final String revision) {
		Condition.notNull(relativeRepoPath, "Cannot check out NULL path");
		Condition.notNull(revision, "Checking ut requries revision");
		
		if ((this.currentRevision == null) || (!revision.equals(this.currentRevision))) {
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "checkout",
			        revision }, this.cloneDir, null, new HashMap<String, String>());
			if (response.getFirst() != 0) {
				return null;
			}
			this.currentRevision = revision;
		}
		final File result = new File(this.cloneDir, relativeRepoPath);
		if (!result.exists()) {
			throw new UnrecoverableError("Could not checkout `" + relativeRepoPath + "` in revision `" + revision);
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
	 */
	private boolean clone(final InputStream inputStream,
	                      final String destDir) {
		Condition.notNull(destDir, "[clone] `destDir` should not be null.");
		
		final Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("git",
		                                                                         new String[] { "clone", "-n", "-q",
		                                                                                 URIUtils.Uri2String(getUri()),
		                                                                                 destDir }, this.cloneDir,
		                                                                         inputStream,
		                                                                         new HashMap<String, String>());
		if (returnValue.getFirst() == 0) {
			
			this.cloneDir = new File(destDir);
			if (!this.cloneDir.exists()) {
				final StringBuilder message = new StringBuilder();
				message.append("Could not clone git repository `");
				message.append(URIUtils.Uri2String(getUri()));
				message.append("` to directory `");
				message.append(destDir);
				message.append("`");
				message.append(FileUtils.lineSeparator);
				message.append("Used command: `git clone -n -q ");
				message.append(URIUtils.Uri2String(getUri()));
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
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#diff(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(final String filePath,
	                              final String baseRevision,
	                              final String revisedRevision) {
		Condition.notNull(filePath, "Cannot diff NULL path");
		Condition.notNull(baseRevision, "cannot compare to NULL revision");
		Condition.notNull(revisedRevision, "cannot compare to NULL revision");
		
		String diffPath = filePath;
		if (diffPath.startsWith("/")) {
			diffPath = diffPath.substring(1);
		}
		
		// get the old version
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "show",
		        baseRevision + ":" + diffPath }, this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		final List<String> oldContent = response.getSecond();
		
		// get the new version
		List<String> newContent = new ArrayList<String>(0);
		response = CommandExecutor.execute("git", new String[] { "show", revisedRevision + ":" + diffPath },
		                                   this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() == 0) {
			newContent = response.getSecond();
		}
		
		final Patch patch = DiffUtils.diff(oldContent, newContent);
		return patch.getDeltas();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#gatherToolInformation()
	 */
	@Override
	public String gatherToolInformation() {
		final StringBuilder builder = new StringBuilder();
		final Tuple<Integer, List<String>> execute = CommandExecutor.execute("git", new String[] { "--version" },
		                                                                     FileUtils.tmpDir, null, null);
		if (execute.getFirst() != 0) {
			builder.append(getHandle()).append(" could not determine `git` version. (Error code: ")
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
	
	/**
	 * Gets the branch factory.
	 * 
	 * @return the branch factory
	 */
	public BranchFactory getBranchFactory() {
		// PRECONDITIONS
		
		try {
			return this.branchFactory;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getChangedPaths()
	 */
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		Condition.notNull(revision, "Cannot get changed paths for null revision");
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git",
		                                                                      new String[] { "log",
		                                                                              "--pretty=format:%H",
		                                                                              "--name-status", "-n1", revision },
		                                                                      this.cloneDir, null,
		                                                                      new HashMap<String, String>());
		
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
		if ((!revision.toUpperCase().equals("HEAD")) && (!removed.trim().equals(revision))) {
			
			throw new UnrecoverableError("Error while parsing GIT log to unveil changed paths for revision `"
			        + revision + "`: wrong revision outputed. Abort parsing.");
			
		}
		final Map<String, ChangeType> result = new HashMap<String, ChangeType>();
		for (String line : lines) {
			if (line.trim().equals("")) {
				// found the end of the log entry.
				break;
			}
			line = line.replaceAll("\\s+", " ");
			final String[] lineParts = line.split(" ");
			if (lineParts.length < 2) {
				
				throw new UnrecoverableError("Error while parsing GIT log to unveil changed paths for revision `"
				        + revision + "`: wrong line format detected. Abort parsing." + FileUtils.lineSeparator
				        + "Line:" + line);
				
			}
			final String type = lineParts[0];
			final String path = "/" + lineParts[1];
			if (type.equals("A")) {
				result.put(path, ChangeType.Added);
			} else if (type.equals("C")) {
				result.put(path, ChangeType.Modified);
			} else if (type.equals("D")) {
				result.put(path, ChangeType.Deleted);
			} else if (type.equals("M")) {
				result.put(path, ChangeType.Modified);
			} else if (type.equals("U")) {
				result.put(path, ChangeType.Modified);
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		if (getStartRevision() == null) {
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
			                                                                              "--branches", "--remotes",
			                                                                              "--pretty=format:%H",
			                                                                              "--topo-order" },
			                                                                      this.cloneDir, null,
			                                                                      new HashMap<String, String>());
			if (response.getFirst() != 0) {
				return null;
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
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getFormerPathName(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFormerPathName(final String revision,
	                                final String pathName) {
		Condition.notNull(revision, "Cannot get former path name of null revision");
		Condition.notNull(pathName, "Cannot get former path name for null path");
		
		final String[] args = new String[] { "log", "--branches", "--remotes", "-r", revision + "^.." + revision, "-M",
		        "-C", "--name-status", "--diff-filter=R,C" };
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		for (final String line : response.getSecond()) {
			if (((line.startsWith("R")) || (line.startsWith("C"))) && line.contains(pathName)) {
				final Match found = formerPathRegex.find(line);
				if (!found.hasGroups()) {
					if (Logger.logWarn()) {
						Logger.warn("Former path regex in Gitrepository did not match but should match.");
					}
					return null;
				}
				return formerPathRegex.getGroup("result");
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getLastRevisionId()
	 */
	@Override
	public String getHEADRevisionId() {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "rev-list",
		        getMainBranchName(), "--branches", "--remotes" }, this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		if (response.getSecond().isEmpty()) {
			throw new UnrecoverableError(String.format("Command `git rev-parse %s` did not produc any output!",
			                                           getMainBranchName()));
		}
		return response.getSecond().get(0).trim();
	}
	
	/**
	 * Gets the ls remote.
	 * 
	 * @return the ls remote
	 */
	public List<String> getLsRemote() {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "ls-remote", "." },
		                                                                      this.cloneDir, null,
		                                                                      new HashMap<String, String>(),
		                                                                      GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not get ls-remote.");
		}
		return response.getSecond();
	}
	
	/**
	 * Gets the merges.
	 * 
	 * @return the merges
	 */
	public List<String> getMerges() {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "rev-list",
		                                                                              "--branches", "--remotes",
		                                                                              "--parents", "--merges" },
		                                                                      this.cloneDir, null,
		                                                                      new HashMap<String, String>(),
		                                                                      GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not get rev-list --merges.");
		}
		return response.getSecond();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getRelativeTransactionId(java.lang.String, long)
	 */
	@Override
	public String getRelativeTransactionId(final String transactionId,
	                                       final long index) {
		Condition.notNull(transactionId, "Cannot get relative revision to null revision");
		
		if (index == 0) {
			return transactionId;
		}
		
		final int fromIndex = this.transactionIDs.indexOf(transactionId);
		
		if (Logger.logDebug()) {
			Logger.debug("Requesting relative transaction id of " + transactionId + " (fromIdex=" + fromIndex
			        + ") and index " + index);
		}
		
		String result = null;
		if ((fromIndex < 0) || (this.transactionIDs.size() <= (fromIndex + index))) {
			result = this.transactionIDs.get(this.transactionIDs.size() - 1);
		} else {
			result = this.transactionIDs.get((int) (fromIndex + index));
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Returning: " + result);
		}
		
		return result;
		
		/*
		 * else if (index < 0) { String[] args = new String[] { "log", "--branches", "--remotes", "--pretty=format:%H",
		 * "-r", transactionId }; Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args,
		 * this.cloneDir, null, null); if (response.getFirst() != 0) { return null; } List<String> lines =
		 * response.getSecond(); if (lines.size() < index) { return lines.get(lines.size() - 1); } else { return
		 * lines.get((int) index); } } else { String[] args = new String[] { "log", "--branches", "--remotes",
		 * "--reverse", "--pretty=format:%H", "-r", transactionId + ".." + getHEAD() }; Tuple<Integer, List<String>>
		 * response = CommandExecutor.execute("git", args, this.cloneDir, null, null); if (response.getFirst() != 0) {
		 * return null; } List<String> lines = response.getSecond(); if (lines.isEmpty()) { return transactionId; } if
		 * (lines.size() < (index - 1)) { return lines.get(lines.size() - 1); } else { return lines.get((int) index -
		 * 1); } }
		 */
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getRevDependencyGraph()
	 */
	@Override
	public IRevDependencyGraph getRevDependencyGraph() {
		// PRECONDITIONS
		
		try {
			if (this.revDepGraph == null) {
				this.revDepGraph = new GitRevDependencyGraph(this);
				this.revDepGraph.createFromRepository();
			}
			return this.revDepGraph;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getRevDependencyGraph(de.unisaarland.cs.st.mozkito.persistence.
	 * PersistenceUtil)
	 */
	@Override
	public IRevDependencyGraph getRevDependencyGraph(final PersistenceUtil persistenceUtil) {
		// PRECONDITIONS
		
		try {
			if (this.revDepGraph == null) {
				this.revDepGraph = new GitRevDependencyGraph(this);
				this.revDepGraph.readFromDB(persistenceUtil);
			}
			return this.revDepGraph;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the rev list parents.
	 * 
	 * @return the rev list parents
	 */
	public List<String> getRevListParents() {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "rev-list",
		                                                                              "--encoding=UTF-8", "--parents",
		                                                                              "--branches", "--remotes",
		                                                                              "--topo-order" }, this.cloneDir,
		                                                                      null,
		                                                                      new HashMap<String, String>(),
		                                                                      GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not get rev-list --children.");
		}
		return response.getSecond();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getTransactionCount()
	 */
	@Override
	public long getTransactionCount() {
		final String[] args = new String[] { "log", "--branches", "--remotes", "--pretty=format:''" };
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return -1;
		}
		return response.getSecond().size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getTransactionId(long)
	 */
	@Override
	public String getTransactionId(@Positive ("Cannot get transaction id for revision number smaller than zero.") final long index) {
		final String[] args = new String[] { "log", "--branches", "--remotes", "--pretty=format:'%H'", "--reverse" };
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		return response.getSecond().get((int) index);
	}
	
	/*
	 * In case of git this method returns a file pointing to a bare repository mirror!
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getWokingCopyLocation()
	 */
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#getWokingCopyLocation()
	 */
	@Override
	public File getWokingCopyLocation() {
		return this.cloneDir;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#log(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public List<LogEntry> log(@MinLength (min = 1) final String fromRevision,
	                          @MinLength (min = 1) final String toRevision) {
		String toRev = toRevision;
		if (toRevision.equals("HEAD")) {
			toRev = getHEADRevisionId();
		}
		final int fromIndex = this.transactionIDs.indexOf(fromRevision);
		final int toIndex = this.transactionIDs.indexOf(toRev);
		
		Condition.check(fromIndex >= 0, "Start transaction for log() is unknown!");
		Condition.check(toIndex >= 0, "End transaction for log() is unknown!");
		Condition.check(fromIndex <= toIndex, "cannot log from later revision to earlier one!");
		
		final List<LogEntry> result = new LinkedList<LogEntry>();
		
		String revisionSelection = fromRevision + "^.." + toRevision;
		if (fromRevision.equals(getFirstRevisionId())) {
			revisionSelection = toRevision;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log", "--pretty=fuller",
		                                                                        "--branches", "--remotes",
		                                                                        "--topo-order", revisionSelection },
		                                                                this.cloneDir, null,
		                                                                new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		if (Logger.logDebug()) {
			Logger.debug("############# git log --pretty=fuller --branches --remotes --topo-order " + revisionSelection);
		}
		for (final LogEntry e : GitLogParser.parse(response.getSecond())) {
			this.logCache.put(e.getRevision(), e);
		}
		
		for (int i = fromIndex; i <= toIndex; ++i) {
			final String tId = this.transactionIDs.get(i);
			
			if (!this.logCache.containsKey(tId)) {
				revisionSelection = tId + "^.." + tId;
				if (i < 1) {
					revisionSelection = tId;
				}
				response = CommandExecutor.execute("git", new String[] { "log", "--pretty=fuller", "--branches",
				                                           "--remotes", "--topo-order", revisionSelection },
				                                   this.cloneDir, null,
				                                   new HashMap<String, String>());
				if (response.getFirst() != 0) {
					return null;
				}
				if (Logger.logDebug()) {
					Logger.debug("############# git log --pretty=fuller --branches --remotes --topo-order "
					        + revisionSelection);
				}
				result.addAll(GitLogParser.parse(response.getSecond()));
			} else {
				result.add(this.logCache.get(tId));
			}
			this.logCache.remove(tId);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  final String mainBranchName) {
		Condition.notNull(address, "Setting up a repository without a corresponding address won't work.");
		
		setup(address, null, branchFactory, tmpDir, mainBranchName);
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
	 */
	private void setup(@NotNull final URI address,
	                   final InputStream inputStream,
	                   @NotNull final BranchFactory branchFactory,
	                   final File tmpDir,
	                   @NotNull final String mainBranchName) {
		Condition.notNull(address, "Setting up a repository without a corresponding address won't work.");
		setMainBranchName(mainBranchName);
		setUri(address);
		this.branchFactory = branchFactory;
		
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
			throw new UnrecoverableError("Could not access clone directory `" + this.cloneDir.getAbsolutePath() + "`");
		}
		
		setStartRevision(getFirstRevisionId());
		setEndRevision(getHEADRevisionId());
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
		                                                                              "--pretty=format:%H",
		                                                                              "--branches", "--remotes",
		                                                                              "--topo-order" }, this.cloneDir,
		                                                                      null,
		                                                                      new HashMap<String, String>());
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not fetch full list of revision IDs!");
		}
		if (Logger.logDebug()) {
			Logger.debug("############# git log --pretty=format:%H --branches --remotes --topo-order");
		}
		this.transactionIDs = response.getSecond();
		Collections.reverse(this.transactionIDs);
		
		if (!this.transactionIDs.isEmpty()) {
			Condition.check(getFirstRevisionId().equals(this.transactionIDs.get(0)),
			                "First revision ID and transaction ID list missmatch!");
			Condition.check(getHEADRevisionId().equals(this.transactionIDs.get(this.transactionIDs.size() - 1)),
			                "End revision ID and transaction ID list missmatch!");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  @NotNull final String username,
	                  @NotNull final String password,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  final String mainBranchName) {
		Condition.notNull(address, "Setting up a repository without a corresponding address won't work.");
		Condition.notNull(username, "Calling this method requires user to be set.");
		Condition.notNull(password, "Calling this method requires password to be set.");
		setup(URIUtils.encodeUsername(address, username), new ByteArrayInputStream(password.getBytes()), branchFactory,
		      tmpDir, mainBranchName);
	}
}
