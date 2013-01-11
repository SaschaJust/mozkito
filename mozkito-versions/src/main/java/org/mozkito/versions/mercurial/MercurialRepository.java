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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.DistributedCommandLineRepository;
import org.mozkito.versions.LogParser;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.RevDependencyGraph.EdgeType;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.Branch;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * The Class MercurialRepository.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class MercurialRepository extends DistributedCommandLineRepository {
	
	/** The Constant HG_MODIFIED_PATHS_INDEX. */
	private static final int                 HG_MODIFIED_PATHS_INDEX      = 5;
	
	/** The Constant HG_DELETED_PATHS_INDEX. */
	private static final int                 HG_DELETED_PATHS_INDEX       = 4;
	
	/** The Constant HG_ADDED_PATHS_INDEX. */
	private static final int                 HG_ADDED_PATHS_INDEX         = 3;
	
	/** The Constant HG_MAX_LINE_PARTS_LENGTH. */
	protected static final int               HG_MAX_LINE_PARTS_LENGTH     = 7;
	
	/** The Constant AUTHOR_REGEX. */
	protected static final Regex             AUTHOR_REGEX                 = new Regex(
	                                                                                  "^(({plain}[a-zA-Z]+)|({name}[^\\s<]+)?\\s*({lastname}[^\\s<]+\\s+)?(<({email}[^>]+)>)?)");
	
	/** The Constant HG_ANNOTATE_DATE_FORMAT. */
	protected static final DateTimeFormatter HG_ANNOTATE_DATE_FORMAT      = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z");
	// protected static DateTimeFormatter hgLogDateFormat =
	// DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");
	
	/** The Constant FORMER_PATH_REGEX. */
	protected static final Regex             FORMER_PATH_REGEX            = new Regex("[^(]*\\(({result}[^(]+)\\)");
	
	/** The Constant PATTERN. */
	protected static final String            PATTERN                      = "^\\s*({author}[^ ]+)\\s+({hash}[^ ]+)\\s+({date}[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+\\+[0-9]{4})\\s+({file}[^:]+):\\s({codeline}.*)$";
	
	/** The Constant REGEX. */
	protected static final Regex             REGEX                        = new Regex(MercurialRepository.PATTERN);
	
	/** The Constant FIELD_SPLITTER. */
	private static final String              FIELD_SPLITTER               = "%%%";
	
	/** The Constant UNNAMED_BRANCH_NAME_TEMPLATE. */
	protected static final String            UNNAMED_BRANCH_NAME_TEMPLATE = "branch_%s";
	
	/** The Constant REVISION_NODE_REGEX. */
	private static final Regex               REVISION_NODE_REGEX          = new Regex(
	                                                                                  "-?({revision}\\d+):({hash}[a-zA-Z0-9]{40})");
	
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
	protected List<String>     hashes       = new ArrayList<String>();
	
	/** The transaction i ds. */
	private final List<String> changeSetIds = new LinkedList<String>();
	
	/** The rev dep graph. */
	private RevDependencyGraph revDepGraph;
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public List<AnnotationEntry> annotate(final String filePath,
	                                      final String revision) throws RepositoryOperationException {
		Condition.notNull(filePath, "Annotation of null path not possible");
		Condition.notNull(revision, "Annotation requires revision");
		if ((filePath == null) || (revision == null)) {
			throw new RepositoryOperationException("filePath and revision must not be null. Abort.");
		}
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "annotate", "-cfud",
		        "-r", revision, filePath }, this.cloneDir, null, null);
		
		if (response.getFirst() != 0) {
			return null;
		}
		final List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			throw new RepositoryOperationException("Annotating file `" + filePath + "` in revision `" + revision
			        + "` returned no output.");
		}
		
		final List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		final HashMap<String, String> hashCache = new HashMap<String, String>();
		
		for (final String line : lines) {
			if (!MercurialRepository.REGEX.matchesFull(line)) {
				throw new RepositoryOperationException("Found line in annotation that cannot be parsed. Abort");
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
					throw new RepositoryOperationException("Could not find a cached hash for short hash `" + shortHash
					        + "`");
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
	                         final String revision) throws RepositoryOperationException {
		Condition.notNull(relativeRepoPath, "Cannot check out NULL path");
		Condition.notNull(revision, "Checking ut requries revision");
		
		if ((relativeRepoPath == null) || (revision == null)) {
			throw new RepositoryOperationException("Path and revision must not be null.");
		}
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "update", "-C",
		        revision }, this.cloneDir, null, null);
		
		if (response.getFirst() != 0) {
			return null;
		}
		
		final File file = new File(this.cloneDir, relativeRepoPath);
		
		if (!file.exists()) {
			throw new RepositoryOperationException("Could not get requested path using command `hg update -C`. Abort.");
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
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	private boolean clone(final InputStream inputStream,
	                      final String destDir) throws RepositoryOperationException {
		final Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("hg", new String[] { "clone", "-U",
		        getUri().toString(), destDir }, this.cloneDir, inputStream, null);
		if (returnValue.getFirst() == 0) {
			this.cloneDir = new File(destDir);
			if (!this.cloneDir.exists()) {
				throw new RepositoryOperationException("Could not clone git repository `" + getUri().toString()
				        + "` to directory `" + destDir + "`");
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
	                              final String revisedRevision) throws RepositoryOperationException {
		Condition.notNull(filePath, "Cannot diff NULL path");
		Condition.notNull(baseRevision, "cannot compare to NULL revision");
		Condition.notNull(revisedRevision, "cannot compare to NULL revision");
		
		if ((filePath == null) || (baseRevision == null) || (revisedRevision == null)) {
			throw new RepositoryOperationException("Path and revisions must not be null. Abort.");
		}
		
		final File filePathFileBase = checkoutPath(filePath, baseRevision);
		if ((filePathFileBase != null) && (filePathFileBase.isDirectory())) {
			throw new IllegalArgumentException(
			                                   String.format("Repository.diff() is only defined on file paths pointing to files not to directories. Supplied file path %s points to a directory.",
			                                                 filePath));
		} else {
			try {
				final File filePathFileRevised = checkoutPath(filePath, revisedRevision);
				if ((filePathFileRevised != null) && (filePathFileRevised.isDirectory())) {
					throw new IllegalArgumentException(
					                                   String.format("Repository.diff() is only defined on file paths pointing to files not to directories. Supplied file path %s points to a directory.",
					                                                 filePath));
				}
			} catch (final RepositoryOperationException e) {
				// ignore
			}
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
			builder.append(getClassName()).append(" could not determine `hg` version. (Error code: ")
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
	public Map<String, ChangeType> getChangedPaths(final String revision) throws RepositoryOperationException {
		Condition.notNull(revision, "Cannot get changed paths for null revision");
		
		if (revision == null) {
			throw new RepositoryOperationException("Revision must be null. Abort.");
		}
		
		try {
			writeLogStyle(this.cloneDir);
		} catch (final IOException e1) {
			throw new RepositoryOperationException("Could not set log style `miner` in order to parse log. Abort.", e1);
		}
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--style",
		        "minerlog", "-r", revision + ":" + revision }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		final List<String> lines = response.getSecond();
		if (lines.size() != 1) {
			throw new RepositoryOperationException("Log returned " + lines.size()
			        + " lines. Only one line expected. Abort.");
		}
		
		final String line = lines.get(0);
		final String[] lineParts = line.split("\\+~\\+");
		
		if (lineParts.length < MercurialRepository.HG_MAX_LINE_PARTS_LENGTH) {
			throw new RepositoryOperationException("hg log could not be parsed. Too less columns in logfile.");
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
	 * @see org.mozkito.versions.Repository#getTransactionCount()
	 */
	@Override
	public long getChangeSetCount() throws RepositoryOperationException {
		
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", "tip",
		        "--template", "{rev}" + FileUtils.lineSeparator }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return -1;
		}
		final String rev = response.getSecond().get(0).trim();
		Long result = Long.valueOf("-1");
		try {
			result = Long.valueOf(rev);
			result += 1;
		} catch (final NumberFormatException e) {
			throw new RepositoryOperationException("Could not interpret revision cound " + rev + " as Long.");
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getChangeSetId(long)
	 */
	@Override
	public String getChangeSetId(@NotNegative final long index) {
		
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
	public long getChangeSetIndex(final String changeSetId) throws RepositoryOperationException {
		if ("HEAD".equals(changeSetId.toUpperCase()) || "TIP".equals(changeSetId.toUpperCase())) {
			return this.changeSetIds.indexOf(getHEADRevisionId());
		}
		return this.changeSetIds.indexOf(changeSetId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() throws RepositoryOperationException {
		if (getStartRevision() == null) {
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r0",
			        "--template", "{node}" }, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			final List<String> lines = response.getSecond();
			
			if (lines.size() < 1) {
				throw new RepositoryOperationException(
				                                       "Command `hg log -r0 --template {node}` returned no output. Abort.");
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
	public String getHEADRevisionId() throws RepositoryOperationException {
		if (getEndRevision() == null) {
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-rtip",
			        "--template", "{node}" }, this.cloneDir, null, null);
			
			if (response.getFirst() != 0) {
				return null;
			}
			
			final List<String> lines = response.getSecond();
			
			if (lines.size() < 1) {
				throw new RepositoryOperationException(
				                                       "Command `hg log -rtip --template {node}` returned no output. Abort.");
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
	public RevDependencyGraph getRevDependencyGraph() throws RepositoryOperationException {
		try {
			if (this.revDepGraph == null) {
				this.revDepGraph = new RevDependencyGraph();
				
				// run hg tip --template {node}\n to get tip and to determine master branch
				String[] arguments = new String[] { "tip", "--template", "{node}" + FileUtils.lineSeparator };
				final Tuple<Integer, List<String>> tipResult = CommandExecutor.execute("hg", arguments, this.cloneDir,
				                                                                       null, null);
				if (tipResult.getFirst() != 0) {
					throw new RepositoryOperationException(String.format("Could not execute hg %s.",
					                                                     StringUtils.join(arguments, " ")));
					
				}
				
				final String tip = tipResult.getSecond().get(0);
				
				// run hg heads --template {node}:{branches}\n to retrieve the open branches.
				// if {branches} is empty, it means that the branch in not names. generate name using that head hash
				arguments = new String[] { "heads", "--template",
				        "{node}" + FIELD_SPLITTER + "{branches}" + FileUtils.lineSeparator };
				final Tuple<Integer, List<String>> headsResult = CommandExecutor.execute("hg", arguments,
				                                                                         this.cloneDir, null, null);
				if (headsResult.getFirst() != 0) {
					throw new RepositoryOperationException(String.format("Could not execute hg %s.",
					                                                     StringUtils.join(arguments, " ")));
				}
				for (final String line : headsResult.getSecond()) {
					final String[] lineParts = line.split(FIELD_SPLITTER);
					if (lineParts.length < 1) {
						throw new RepositoryOperationException(
						                                       String.format("Malformatted line in hg heads output: %s. Further parsing would lead to data inconsistency.",
						                                                     line));
					}
					String branchName = "";
					if (lineParts.length > 1) {
						branchName = lineParts[1];
					}
					final String node = lineParts[0];
					if (branchName.isEmpty()) {
						if (!node.equals(tip)) {
							// unnamed branch. Genrating a branch name using the head commit hash as name
							branchName = String.format(MercurialRepository.UNNAMED_BRANCH_NAME_TEMPLATE, node);
						} else {
							branchName = String.format(Branch.MASTER_BRANCH_NAME, node);
						}
					}
					this.revDepGraph.addBranch(branchName, node);
				}
				
				// run hg log --template {node}+~+{parents}+~+{tags}\n --debug to retrieve change sets, patents and tags
				arguments = new String[] { "log", "--template",
				        "{node}" + FIELD_SPLITTER + "{parents}" + FIELD_SPLITTER + "{tags}" + FileUtils.lineSeparator,
				        "--debug" };
				final Tuple<Integer, List<String>> logResult = CommandExecutor.execute("hg", arguments, this.cloneDir,
				                                                                       null, null);
				if (logResult.getFirst() != 0) {
					throw new RepositoryOperationException(String.format("Could not execute hg %s. Returning null!",
					                                                     StringUtils.join(arguments, " ")));
					
				}
				for (final String line : logResult.getSecond()) {
					final String[] lineParts = line.split(FIELD_SPLITTER);
					if (lineParts.length < 2) {
						throw new RepositoryOperationException(
						                                       String.format("Malformatted line in hg log output: %s. Further parsing might lead to data inconsistency.",
						                                                     line));
					}
					
					final String node = lineParts[0];
					final String parents = lineParts[1];
					
					final String[] parentsParts = parents.split(" ");
					if (parentsParts.length != 2) {
						throw new RepositoryOperationException(
						                                       String.format("A change set must have exactly TWO parents in hg log. Further parsing might lead to data inconsistency. Line: %s",
						                                                     line));
					}
					if (!REVISION_NODE_REGEX.matches(parentsParts[0])) {
						throw new RepositoryOperationException(
						                                       String.format("Found line in hg log that cannot be parsed. Further parsing might lead to data inconsistency. Line: %s",
						                                                     line));
					}
					final Tuple<String, String> parentNames = new Tuple<String, String>(
					                                                                    REVISION_NODE_REGEX.getGroup("hash"),
					                                                                    null);
					if (!REVISION_NODE_REGEX.matches(parentsParts[1])) {
						throw new RepositoryOperationException(
						                                       String.format("Found line in hg log that cannot be parsed. Further parsing might lead to data inconsistency. Line: %s",
						                                                     line));
					}
					
					final String mergeParentName = REVISION_NODE_REGEX.getGroup("hash");
					if (!"0000000000000000000000000000000000000000".equals(mergeParentName)) {
						parentNames.setSecond(mergeParentName);
					}
					
					String[] tagNames = new String[0];
					if (lineParts.length > 2) {
						tagNames = lineParts[2].split(" ");
					}
					
					if (this.revDepGraph.addChangeSet(node) == null) {
						throw new RepositoryOperationException(
						                                       "An error occured while adding a node to the graph DB. Please see earlier warning and error messages.");
					}
					
					for (final String tagName : tagNames) {
						this.revDepGraph.addTag(tagName, node);
					}
					
					if (!"0000000000000000000000000000000000000000".equals(parentNames.getFirst())) {
						this.revDepGraph.addEdge(parentNames.getFirst(), node, EdgeType.BRANCH_EDGE);
						if (parentNames.getSecond() != null) {
							this.revDepGraph.addEdge(parentNames.getSecond(), node, EdgeType.MERGE_EDGE);
						}
					}
				}
			}
			return this.revDepGraph;
		} catch (final IOException e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getWokingCopyLocation()
	 */
	@Override
	public File getWorkingCopyLocation() {
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
	                  @NotNull final String mainBranchName) throws RepositoryOperationException {
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
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	private void setup(@NotNull final URI address,
	                   final InputStream inputStream,
	                   @NotNull final BranchFactory branchFactory,
	                   final File tmpDir,
	                   @NotNull final String mainBranchName) throws RepositoryOperationException {
		try {
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
				throw new RepositoryOperationException("Could not clone git repository `" + getUri().toString()
				        + "` to directory `" + localCloneDir.getAbsolutePath() + "`");
			}
			
			try {
				writeLogStyle(this.cloneDir);
			} catch (final IOException e1) {
				throw new UnrecoverableError("Could not set log style `miner` in order to parse log. Abort.");
			}
			
			setStartRevision(getFirstRevisionId());
			setEndRevision(getHEADRevisionId());
			
			final Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log",
			        "--template", "{node}\n" }, this.cloneDir, null, new HashMap<String, String>());
			if (response.getFirst() != 0) {
				throw new UnrecoverableError("Could not fetch full list of revision IDs!");
			}
			if (Logger.logDebug()) {
				Logger.debug("############# hg log --template '{node}\n'");
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
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public void setup(@NotNull final URI address,
	                  @NotNull final String username,
	                  @NotNull final String password,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) throws RepositoryOperationException {
		setup(URIUtils.encodeUsername(address, username), new ByteArrayInputStream(password.getBytes()), branchFactory,
		      tmpDir, mainBranchName);
	}
}
