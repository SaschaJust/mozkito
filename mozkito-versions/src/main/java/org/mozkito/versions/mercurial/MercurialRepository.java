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
package org.mozkito.versions.mercurial;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.ioda.URIUtils;
import net.ownhero.dev.ioda.exceptions.ExternalExecutableException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.MinLength;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.DistributedCommandLineRepository;
import org.mozkito.versions.IRevDependencyGraph;
import org.mozkito.versions.LogParser;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;

/**
 * The Class MercurialRepository.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class MercurialRepository extends DistributedCommandLineRepository {
	
	/** The Constant HG_MODIFIED_PATHS_INDEX. */
	private static final int                 HG_MODIFIED_PATHS_INDEX  = 5;
	
	/** The Constant HG_DELETED_PATHS_INDEX. */
	private static final int                 HG_DELETED_PATHS_INDEX   = 4;
	
	/** The Constant HG_ADDED_PATHS_INDEX. */
	private static final int                 HG_ADDED_PATHS_INDEX     = 3;
	
	/** The Constant HG_MAX_LINE_PARTS_LENGTH. */
	protected static final int               HG_MAX_LINE_PARTS_LENGTH = 7;
	
	/** The Constant AUTHOR_REGEX. */
	protected static final Regex             AUTHOR_REGEX             = new Regex(
	                                                                              "^(({plain}[a-zA-Z]+)|({name}[^\\s<]+)?\\s*({lastname}[^\\s<]+\\s+)?(<({email}[^>]+)>)?)");
	
	/** The Constant HG_ANNOTATE_DATE_FORMAT. */
	protected static final DateTimeFormatter HG_ANNOTATE_DATE_FORMAT  = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z");
	// protected static DateTimeFormatter hgLogDateFormat =
	// DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");
	
	/** The Constant FORMER_PATH_REGEX. */
	protected static final Regex             FORMER_PATH_REGEX        = new Regex("[^(]*\\(({result}[^(]+)\\)");
	
	/** The Constant PATTERN. */
	protected static final String            PATTERN                  = "^\\s*({author}[^ ]+)\\s+({hash}[^ ]+)\\s+({date}[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+\\+[0-9]{4})\\s+({file}[^:]+):\\s({codeline}.*)$";
	
	/** The Constant REGEX. */
	protected static final Regex             REGEX                    = new Regex(MercurialRepository.PATTERN);
	
	/**
	 * Write a specific Mercurial log style into a temporary file. (should always be
	 * 
	 * @param dir
	 *            the directory the template file will be written to (
	 * @throws IOException
	 *             Signals that an I/O exception has occurred. {@link MercurialRepository#cloneDir}, not null)
	 */
	@NoneNull
	private static void writeLogStyle(final File dir) throws IOException {
		Condition.notNull(dir, "Cannot write content to NULL file");
		final File f = new File(dir + FileUtils.fileSeparator + "minerlog");
		if (f.exists()) {
			f.delete();
		}
		final BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write("changeset = \"{node}+~+{author}+~+{date|hgdate}+~+{file_adds}+~+{file_dels}+~+{file_mods}+~+{desc|addbreaks}\\n\"\n");
		writer.write("file_add = \"{file_add};\"\n");
		writer.write("file_del = \"{file_del};\"\n");
		writer.write("file_mod = \"{file_mod};\"\n");
		writer.close();
		
	}
	
	/** The clone dir. */
	private File               cloneDir;
	
	/** The hashes. */
	protected List<String>     hashes         = new ArrayList<String>();
	
	/** The transaction i ds. */
	private final List<String> transactionIDs = new LinkedList<String>();
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public List<AnnotationEntry> annotate(final String filePath,
	                                      final String revision) {
		Condition.notNull(filePath, "Annotation of null path not possible");
		Condition.notNull(revision, "Annotation requires revision");
		if ((filePath == null) || (revision == null)) {
			if (Logger.logError()) {
				Logger.error("filePath and revision must not be null. Abort.");
			}
			return new ArrayList<AnnotationEntry>(0);
		}
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "annotate", "-cfud",
		        "-r", revision, filePath }, this.cloneDir, null, null);
		
		if (response.getFirst() != 0) {
			return null;
		}
		final List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			if (Logger.logError()) {
				Logger.error("Annotating file `" + filePath + "` in revision `" + revision + "` returned no output.");
			}
			return null;
		}
		
		final List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		final HashMap<String, String> hashCache = new HashMap<String, String>();
		
		for (final String line : lines) {
			if (!MercurialRepository.REGEX.matchesFull(line)) {
				if (Logger.logError()) {
					Logger.error("Found line in annotation that cannot be parsed. Abort");
				}
				return null;
			}
			final String author = MercurialRepository.REGEX.getGroup("author");
			final String shortHash = MercurialRepository.REGEX.getGroup("hash");
			final String date = MercurialRepository.REGEX.getGroup("date");
			
			DateTime timestamp;
			timestamp = MercurialRepository.HG_ANNOTATE_DATE_FORMAT.parseDateTime(date);
			
			final String file = MercurialRepository.REGEX.getGroup("file");
			final String codeLine = MercurialRepository.REGEX.getGroup("codeline");
			
			if (!hashCache.containsKey(shortHash)) {
				boolean found = false;
				for (final String hash : this.hashes) {
					if (hash.startsWith(shortHash)) {
						hashCache.put(shortHash, hash);
						found = true;
						break;
					}
				}
				if (!found) {
					if (Logger.logError()) {
						Logger.error("Could not find a cached hash for short hash `" + shortHash + "`");
					}
				}
			}
			final String hash = hashCache.get(shortHash);
			if (filePath.equals(file)) {
				result.add(new AnnotationEntry(hash, author, timestamp, codeLine));
			} else {
				result.add(new AnnotationEntry(hash, author, timestamp, codeLine, file));
			}
			
		}
		return result;
	}
	
	/**
	 * Cache hashes. This is requries to identify short hashes output by hg log.
	 */
	private void cacheHashes() {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--template",
		        "'{node}\n'" }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			if (Logger.logWarn()) {
				Logger.warn("Could not cache hashes");
			}
			return;
		}
		for (final String line : response.getSecond()) {
			if (line.trim().isEmpty()) {
				continue;
			}
			this.hashes.add(line.trim().replaceAll("'", ""));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#checkoutPath(java.lang. String, java.lang.String)
	 */
	@Override
	@NoneNull
	public File checkoutPath(final String relativeRepoPath,
	                         final String revision) {
		Condition.notNull(relativeRepoPath, "Cannot check out NULL path");
		Condition.notNull(revision, "Checking ut requries revision");
		
		if ((relativeRepoPath == null) || (revision == null)) {
			if (Logger.logError()) {
				Logger.error("Path and revision must not be null.");
			}
			return null;
		}
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "update", "-C",
		        revision }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		final File file = new File(this.cloneDir, relativeRepoPath);
		if (!file.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not get requested path using command `hg update -C`. Abort.");
			}
			return null;
		}
		return file;
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
		final Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("hg", new String[] { "clone", "-U",
		        getUri().toString(), destDir }, this.cloneDir, inputStream, null);
		if (returnValue.getFirst() == 0) {
			this.cloneDir = new File(destDir);
			if (!this.cloneDir.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not clone git repository `" + getUri().toString() + "` to directory `"
					        + destDir + "`");
				}
				return false;
			}
			FileUtils.addToFileManager(this.cloneDir, FileShutdownAction.DELETE);
			cacheHashes();
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#diff(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public Collection<Delta> diff(final String filePath,
	                              final String baseRevision,
	                              final String revisedRevision) {
		Condition.notNull(filePath, "Cannot diff NULL path");
		Condition.notNull(baseRevision, "cannot compare to NULL revision");
		Condition.notNull(revisedRevision, "cannot compare to NULL revision");
		
		if ((filePath == null) || (baseRevision == null) || (revisedRevision == null)) {
			if (Logger.logError()) {
				Logger.error("Path and revisions must not be null. Abort.");
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "cat", "-r", baseRevision,
		        filePath }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		final List<String> original = response.getSecond();
		List<String> revised = new ArrayList<String>(0);
		response = CommandExecutor.execute("hg", new String[] { "cat", "-r", revisedRevision, filePath },
		                                   this.cloneDir, null, null);
		if (response.getFirst() == 0) {
			revised = response.getSecond();
		}
		
		final Patch patch = DiffUtils.diff(original, revised);
		return patch.getDeltas();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.DistributedCommandLineRepository#executeLog(java.lang.String)
	 */
	@Override
	public Tuple<Integer, List<String>> executeLog(@NotNull @MinLength (min = 1) final String revision) {
		return hgLog(revision);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.DistributedCommandLineRepository#executeLog(java.lang.String, java.lang.String)
	 */
	@Override
	public Tuple<Integer, List<String>> executeLog(@NotNull @MinLength (min = 1) final String fromRevision,
	                                               @NotNull @MinLength (min = 1) final String toRevision) {
		final StringBuilder revisionSelectionBuilder = new StringBuilder();
		revisionSelectionBuilder.append(fromRevision);
		revisionSelectionBuilder.append("::");
		revisionSelectionBuilder.append(toRevision);
		final String revisionSelection = revisionSelectionBuilder.toString();
		return hgLog(revisionSelection);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#gatherToolInformation()
	 */
	@Override
	public String gatherToolInformation() {
		final StringBuilder builder = new StringBuilder();
		final Tuple<Integer, List<String>> execute = CommandExecutor.execute("hg", new String[] { "--version" },
		                                                                     FileUtils.tmpDir, null, null);
		if (execute.getFirst() != 0) {
			builder.append(getHandle()).append(" could not determine `hg` version. (Error code: ")
			       .append(execute.getFirst()).append(").");
			builder.append(FileUtils.lineSeparator);
			try {
				builder.append("Command was: ").append(FileUtils.checkExecutable("hg")).append(" --version");
			} catch (final ExternalExecutableException e) {
				builder.append(e.getMessage());
			}
		} else {
			builder.append("Executable: ");
			try {
				builder.append(FileUtils.checkExecutable("hg"));
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
	 * @see org.mozkito.versions.Repository#getChangedPaths(java.lang .String)
	 */
	@Override
	@NoneNull
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		Condition.notNull(revision, "Cannot get changed paths for null revision");
		
		if (revision == null) {
			if (Logger.logError()) {
				Logger.error("Revision must be null. Abort.");
			}
			return null;
		}
		try {
			writeLogStyle(this.cloneDir);
		} catch (final IOException e1) {
			if (Logger.logError()) {
				Logger.error("Could not set log style `miner` in order to parse log. Abort.");
				Logger.error(e1.getMessage());
			}
			return null;
		}
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--style",
		        "minerlog", "-r", revision + ":" + revision }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		final List<String> lines = response.getSecond();
		if (lines.size() != 1) {
			if (Logger.logError()) {
				Logger.error("Log returned " + lines.size() + " lines. Only one line expected. Abort.");
			}
			return null;
		}
		final String line = lines.get(0);
		final String[] lineParts = line.split("\\+~\\+");
		if (lineParts.length < MercurialRepository.HG_MAX_LINE_PARTS_LENGTH) {
			if (Logger.logError()) {
				Logger.error("hg log could not be parsed. Too less columns in logfile.");
				return null;
			}
		}
		if (lineParts.length > MercurialRepository.HG_MAX_LINE_PARTS_LENGTH) {
			final StringBuilder s = new StringBuilder();
			s.append(lineParts[MercurialRepository.HG_MAX_LINE_PARTS_LENGTH - 1]);
			for (int i = MercurialRepository.HG_MAX_LINE_PARTS_LENGTH; i < lineParts.length; ++i) {
				s.append(":");
				s.append(lineParts[i]);
			}
			lineParts[MercurialRepository.HG_MAX_LINE_PARTS_LENGTH - 1] = s.toString();
		}
		final String[] addedPaths = lineParts[MercurialRepository.HG_ADDED_PATHS_INDEX].split(";");
		final String[] deletedPaths = lineParts[MercurialRepository.HG_DELETED_PATHS_INDEX].split(";");
		final String[] modifiedPaths = lineParts[MercurialRepository.HG_MODIFIED_PATHS_INDEX].split(";");
		
		final Map<String, ChangeType> result = new HashMap<String, ChangeType>();
		
		for (final String addedPath : addedPaths) {
			if (!addedPath.trim().isEmpty()) {
				result.put("/" + addedPath, ChangeType.Added);
			}
		}
		for (final String deletedPath : deletedPaths) {
			if (!deletedPath.trim().isEmpty()) {
				result.put("/" + deletedPath, ChangeType.Deleted);
			}
		}
		for (final String modifiedPath : modifiedPaths) {
			if (!modifiedPath.trim().isEmpty()) {
				result.put("/" + modifiedPath, ChangeType.Modified);
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		if (getStartRevision() == null) {
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r0",
			        "--template", "{node}" }, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			final List<String> lines = response.getSecond();
			if (lines.size() < 1) {
				if (Logger.logError()) {
					Logger.error("Command `hg log -r0 --template {node}` returned no output. Abort.");
				}
				return null;
			}
			return lines.get(0).trim();
		}
		return getStartRevision();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getFormerPathName(java. lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public String getFormerPathName(final String revision,
	                                final String pathName) {
		Condition.notNull(revision, "Cannot get former path name of null revision");
		Condition.notNull(pathName, "Cannot get former path name for null path");
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r",
		        revision, "--template", "{file_copies}" }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		String result = null;
		for (final String line : response.getSecond()) {
			if (line.trim().startsWith(pathName)) {
				MercurialRepository.FORMER_PATH_REGEX.find(line);
				result = MercurialRepository.FORMER_PATH_REGEX.getGroup("result").trim();
				break;
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getHEAD()
	 */
	@Override
	public String getHEAD() {
		return "tip";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getLastRevisionId()
	 */
	@Override
	public String getHEADRevisionId() {
		if (getEndRevision() == null) {
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-rtip",
			        "--template", "{node}" }, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			final List<String> lines = response.getSecond();
			if (lines.size() < 1) {
				if (Logger.logError()) {
					Logger.error("Command `hg log -rtip --template {node}` returned no output. Abort.");
				}
				return null;
			}
			return lines.get(0).trim();
		}
		return getEndRevision();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.DistributedCommandLineRepository#getLogParser()
	 */
	@Override
	protected LogParser getLogParser() {
		return new MercurialLogParser();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getRevDependencyGraph()
	 */
	@Override
	public IRevDependencyGraph getRevDependencyGraph() {
		// PRECONDITIONS
		
		try {
			throw new UnrecoverableError("Support hasn't been implemented yet. " + Settings.getReportThis());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getRevDependencyGraph(org.mozkito.persistence.PersistenceUtil)
	 */
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
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", "tip",
		        "--template", "{rev}\\n" }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return -1;
		}
		final String rev = response.getSecond().get(0).trim();
		Long result = Long.valueOf("-1");
		try {
			result = Long.valueOf(rev);
			result += 1;
		} catch (final NumberFormatException e) {
			if (Logger.logError()) {
				Logger.error("Could not interpret revision cound " + rev + " as Long.");
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getTransactionId(long)
	 */
	@Override
	public String getTransactionId(@NotNegative final long index) {
		
		final String[] args = new String[] { "log", "-r", String.valueOf(index), "--template={node}\\n" };
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		return response.getSecond().get(0).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getTransactionIndex(java.lang.String)
	 */
	@Override
	public long getTransactionIndex(final String transactionId) {
		if ("HEAD".equals(transactionId.toUpperCase()) || "TIP".equals(transactionId.toUpperCase())) {
			return this.transactionIDs.indexOf(getHEADRevisionId());
		}
		return this.transactionIDs.indexOf(transactionId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getWokingCopyLocation()
	 */
	@Override
	public File getWokingCopyLocation() {
		return this.cloneDir;
	}
	
	/**
	 * Hg log.
	 * 
	 * @param revisionSelection
	 *            the revision selection
	 * @return the tuple
	 */
	private Tuple<Integer, List<String>> hgLog(@NotNull @MinLength (min = 1) final String revisionSelection) {
		return CommandExecutor.execute("hg", new String[] { "log", "--style", "minerlog", "-r", revisionSelection },
		                               this.cloneDir, null, null);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) {
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
		
		setMainBranchName(mainBranchName);
		setUri(address);
		
		File localCloneDir = null;
		if (tmpDir == null) {
			localCloneDir = FileUtils.createRandomDir("moskito_hg_clone_",
			
			String.valueOf(DateTimeUtils.currentTimeMillis()), FileShutdownAction.DELETE);
		} else {
			localCloneDir = FileUtils.createRandomDir(tmpDir, "moskito_hg_clone_",
			
			String.valueOf(DateTimeUtils.currentTimeMillis()), FileShutdownAction.DELETE);
		}
		
		// clone the remote repository
		if (!clone(null, localCloneDir.getAbsolutePath())) {
			if (Logger.logError()) {
				Logger.error("Could not clone git repository `" + getUri().toString() + "` to directory `"
				        + localCloneDir.getAbsolutePath() + "`");
				throw new RuntimeException();
			}
		}
		
		try {
			writeLogStyle(this.cloneDir);
		} catch (final IOException e1) {
			throw new UnrecoverableError("Could not set log style `miner` in order to parse log. Abort.");
		}
		
		setStartRevision(getFirstRevisionId());
		setEndRevision(getHEADRevisionId());
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--template",
		        "{node}\n" }, this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not fetch full list of revision IDs!");
		}
		if (Logger.logDebug()) {
			Logger.debug("############# hg log --template '{node}\n'");
		}
		
		this.transactionIDs.clear();
		this.transactionIDs.addAll(response.getSecond());
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
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public void setup(@NotNull final URI address,
	                  @NotNull final String username,
	                  @NotNull final String password,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) {
		setup(URIUtils.encodeUsername(address, username), new ByteArrayInputStream(password.getBytes()), branchFactory,
		      tmpDir, mainBranchName);
	}
}
