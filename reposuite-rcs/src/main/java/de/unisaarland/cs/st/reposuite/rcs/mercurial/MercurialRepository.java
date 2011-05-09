/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.mercurial;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.ioda.URIUtils;
import net.ownhero.dev.ioda.exceptions.ExternalExecutableException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * The Class MercurialRepository.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class MercurialRepository extends Repository {
	
	protected static Regex             authorRegex          = new Regex(
	                                                                    "^(({plain}[a-zA-Z]+)|({name}[^\\s<]+)?\\s*({lastname}[^\\s<]+\\s+)?(<({email}[^>]+)>)?)");
	
	protected static DateTimeFormatter hgAnnotateDateFormat = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z");
	// protected static DateTimeFormatter hgLogDateFormat =
	// DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");
	
	protected static Regex             formerPathRegex      = new Regex("[^(]*\\(({result}[^(]+)\\)");
	protected static String            pattern              = "^\\s*({author}[^ ]+)\\s+({hash}[^ ]+)\\s+({date}[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+\\+[0-9]{4})\\s+({file}[^:]+):\\s({codeline}.*)$";
	protected static Regex             regex                = new Regex(MercurialRepository.pattern);
	
	/**
	 * Pre-filters log lines. Mercurial cannot replace newlines in the log
	 * messages. This method replaces newlines marked with br-tags by br-tags
	 * only. This way, each entry in the returned list of strings represents a
	 * single, atomic log entry.
	 * 
	 * @param lines
	 *            the lines (not null)
	 * @return the list
	 */
	@NoneNull
	protected static List<String> preFilterLines(final List<String> lines) {
		List<String> completeLines = new LinkedList<String>();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < lines.size(); ++i) {
			String line = lines.get(i);
			if (line.endsWith("<br/>") && (lines.get(i + 1).split("\\+~\\+").length < 7)) {
				stringBuilder.append(line);
			} else {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(line);
					completeLines.add(stringBuilder.toString());
					stringBuilder = new StringBuilder();
				} else {
					completeLines.add(line);
				}
			}
		}
		return completeLines;
	}
	
	/**
	 * Write a specific Mercurial log style into a temporary file. (should
	 * always be {@link MercurialRepository#cloneDir}, not null)
	 * 
	 * @param dir
	 *            the directory the template file will be written to (
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@NoneNull
	private static void writeLogStyle(final File dir) throws IOException {
		Condition.notNull(dir, "Cannot write content to NULL file");
		File f = new File(dir + FileUtils.fileSeparator + "minerlog");
		if (f.exists()) {
			f.delete();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write("changeset = \"{node}+~+{author}+~+{date|hgdate}+~+{file_adds}+~+{file_dels}+~+{file_mods}+~+{desc|addbreaks}\\n\"\n");
		writer.write("file_add = \"{file_add};\"\n");
		writer.write("file_del = \"{file_del};\"\n");
		writer.write("file_mod = \"{file_mod};\"\n");
		writer.close();
		
	}
	
	private File           cloneDir;
	protected List<String> hashes = new ArrayList<String>();
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
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
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "annotate", "-cfud", "-r",
		        revision, filePath }, this.cloneDir, null, null);
		
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			if (Logger.logError()) {
				Logger.error("Annotating file `" + filePath + "` in revision `" + revision + "` returned no output.");
			}
			return null;
		}
		
		List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		HashMap<String, String> hashCache = new HashMap<String, String>();
		
		for (String line : lines) {
			if (!MercurialRepository.regex.matchesFull(line)) {
				if (Logger.logError()) {
					Logger.error("Found line in annotation that cannot be parsed. Abort");
				}
				return null;
			}
			String author = MercurialRepository.regex.getGroup("author");
			String shortHash = MercurialRepository.regex.getGroup("hash");
			String date = MercurialRepository.regex.getGroup("date");
			
			DateTime timestamp;
			timestamp = MercurialRepository.hgAnnotateDateFormat.parseDateTime(date);
			
			String file = MercurialRepository.regex.getGroup("file");
			String codeLine = MercurialRepository.regex.getGroup("codeline");
			
			if (!hashCache.containsKey(shortHash)) {
				boolean found = false;
				for (String hash : this.hashes) {
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
			String hash = hashCache.get(shortHash);
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
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--template",
		        "'{node}\n'" }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			if (Logger.logWarn()) {
				Logger.warn("Could not cache hashes");
			}
			return;
		}
		for (String line : response.getSecond()) {
			if (line.trim().equals("")) {
				continue;
			}
			this.hashes.add(line.trim().replaceAll("'", ""));
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
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg",
		                                                                new String[] { "update", "-C", revision },
		                                                                this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		File file = new File(this.cloneDir, relativeRepoPath);
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
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("hg", new String[] { "clone", "-U",
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
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#diff(java.lang.String,
	 * java.lang.String, java.lang.String)
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
		List<String> original = response.getSecond();
		List<String> revised = new ArrayList<String>(0);
		response = CommandExecutor.execute("hg", new String[] { "cat", "-r", revisedRevision, filePath },
		                                   this.cloneDir, null, null);
		if (response.getFirst() == 0) {
			revised = response.getSecond();
		}
		
		Patch patch = DiffUtils.diff(original, revised);
		return patch.getDeltas();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#gatherToolInformation()
	 */
	@Override
	public String gatherToolInformation() {
		StringBuilder builder = new StringBuilder();
		Tuple<Integer, List<String>> execute = CommandExecutor.execute("hg", new String[] { "--version" },
		                                                               FileUtils.tmpDir, null, null);
		if (execute.getFirst() != 0) {
			builder.append(getHandle()).append(" could not determine `hg` version. (Error code: ")
			       .append(execute.getFirst()).append(").");
			builder.append(FileUtils.lineSeparator);
			try {
				builder.append("Command was: ").append(FileUtils.checkExecutable("hg")).append(" --version");
			} catch (ExternalExecutableException e) {
				builder.append(e.getMessage());
			}
		} else {
			builder.append("Executable: ");
			try {
				builder.append(FileUtils.checkExecutable("hg"));
			} catch (ExternalExecutableException e) {
				builder.append(e.getMessage());
			}
			builder.append(FileUtils.lineSeparator);
			
			for (String line : execute.getSecond()) {
				builder.append(line);
			}
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getChangedPaths(java.lang
	 * .String)
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
		} catch (IOException e1) {
			if (Logger.logError()) {
				Logger.error("Could not set log style `miner` in order to parse log. Abort.");
				Logger.error(e1.getMessage());
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--style",
		        "minerlog", "-r", revision + ":" + revision }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() != 1) {
			if (Logger.logError()) {
				Logger.error("Log returned " + lines.size() + " lines. Only one line expected. Abort.");
			}
			return null;
		}
		String line = lines.get(0);
		String[] lineParts = line.split("\\+~\\+");
		if (lineParts.length < 7) {
			if (Logger.logError()) {
				Logger.error("hg log could not be parsed. Too less columns in logfile.");
				return null;
			}
		}
		if (lineParts.length > 7) {
			StringBuilder s = new StringBuilder();
			s.append(lineParts[6]);
			for (int i = 7; i < lineParts.length; ++i) {
				s.append(":");
				s.append(lineParts[i]);
			}
			lineParts[6] = s.toString();
		}
		String[] addedPaths = lineParts[3].split(";");
		String[] deletedPaths = lineParts[4].split(";");
		String[] modifiedPaths = lineParts[5].split(";");
		
		Map<String, ChangeType> result = new HashMap<String, ChangeType>();
		
		for (String addedPath : addedPaths) {
			if (!addedPath.trim().equals("")) {
				result.put("/" + addedPath, ChangeType.Added);
			}
		}
		for (String deletedPath : deletedPaths) {
			if (!deletedPath.trim().equals("")) {
				result.put("/" + deletedPath, ChangeType.Deleted);
			}
		}
		for (String modifiedPath : modifiedPaths) {
			if (!modifiedPath.trim().equals("")) {
				result.put("/" + modifiedPath, ChangeType.Modified);
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		if (getStartRevision() == null) {
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r0",
			        "--template", "{node}" }, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> lines = response.getSecond();
			if (lines.size() < 1) {
				if (Logger.logError()) {
					Logger.error("Command `hg log -r0 --template {node}` returned no output. Abort.");
				}
				return null;
			}
			return lines.get(0).trim();
		} else {
			return getStartRevision();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getFormerPathName(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public String getFormerPathName(final String revision,
	                                final String pathName) {
		Condition.notNull(revision, "Cannot get former path name of null revision");
		Condition.notNull(pathName, "Cannot get former path name for null path");
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", revision,
		        "--template", "{file_copies%filecopy}" }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		String result = null;
		for (String line : response.getSecond()) {
			if (line.trim().startsWith(pathName)) {
				MercurialRepository.formerPathRegex.find(line);
				result = MercurialRepository.formerPathRegex.getGroup("result").trim();
				break;
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getHEAD()
	 */
	@Override
	public String getHEAD() {
		return "tip";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getLastRevisionId()
	 */
	@Override
	public String getHEADRevisionId() {
		if (getEndRevision() == null) {
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-rtip",
			        "--template", "{node}" }, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> lines = response.getSecond();
			if (lines.size() < 1) {
				if (Logger.logError()) {
					Logger.error("Command `hg log -rtip --template {node}` returned no output. Abort.");
				}
				return null;
			}
			return lines.get(0).trim();
		} else {
			return getEndRevision();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getRelativeTransactionId
	 * (java.lang.String, long)
	 */
	@Override
	public String getRelativeTransactionId(final String transactionId,
	                                       final long index) {
		Condition.notNull(transactionId, "Cannot get relative revision to null revision");
		
		if (index == 0) {
			return transactionId;
		} else if (index > 0) {
			String[] args = new String[] { "log", "-r", transactionId + ":tip", "--template", "{node}\\n", "-l",
			        String.valueOf(index + 1) };
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> list = response.getSecond();
			return list.get(list.size() - 1).trim();
		} else {
			String[] args = new String[] { "log", "-r", transactionId + ":0", "--template", "{node}\\n", "-l",
			        String.valueOf(index + 1) };
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> list = response.getSecond();
			return list.get(list.size() - 1).trim();
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
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", "tip",
		        "--template", "{rev}\\n" }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return -1;
		}
		String rev = response.getSecond().get(0).trim();
		Long result = Long.valueOf("-1");
		try {
			result = Long.valueOf(rev);
			result += 1;
		} catch (NumberFormatException e) {
			if (Logger.logError()) {
				Logger.error("Could not interpret revision cound " + rev + " as Long.");
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getTransactionId(long)
	 */
	@Override
	public String getTransactionId(@NotNegative final long index) {
		
		String[] args = new String[] { "log", "-r", String.valueOf(index), "--template=\"{node}\\n\"" };
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		return response.getSecond().get(0).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getWokingCopyLocation()
	 */
	@Override
	public File getWokingCopyLocation() {
		return this.cloneDir;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@NoneNull
	public List<LogEntry> log(final String fromRevision,
	                          final String toRevision) {
		
		ArrayList<LogEntry> result = new ArrayList<LogEntry>();
		if ((toRevision == null) || (fromRevision == null)) {
			if (Logger.logError()) {
				Logger.error("Cannot get log for null referenced revisions. Abort");
			}
			return null;
		}
		
		try {
			writeLogStyle(this.cloneDir);
		} catch (IOException e1) {
			if (Logger.logError()) {
				Logger.error("Could not set log style `miner` in order to parse log. Abort.");
				Logger.error(e1.getMessage());
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--style",
		        "minerlog", "-r", fromRevision + ":" + toRevision }, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		
		// pre-filter lines. hg log might have some entries spanning multiple
		// lines.
		lines = preFilterLines(lines);
		
		for (String line : lines) {
			String[] lineParts = line.split("\\+~\\+");
			if (lineParts.length < 7) {
				if (Logger.logError()) {
					Logger.error("hg log could not be parsed. Too less columns in logfile.");
					return null;
				}
			}
			if (lineParts.length > 7) {
				StringBuilder s = new StringBuilder();
				s.append(lineParts[6]);
				for (int i = 7; i < lineParts.length; ++i) {
					s.append(":");
					s.append(lineParts[i]);
				}
				lineParts[6] = s.toString();
			}
			String revID = lineParts[0];
			String authorString = lineParts[1];
			
			String authorFullname = null;
			String authorUsername = null;
			String authorEmail = null;
			
			MercurialRepository.authorRegex.find(authorString);
			MercurialRepository.authorRegex.getGroupNames();
			
			if (MercurialRepository.authorRegex.getGroup("plain") != null) {
				authorUsername = MercurialRepository.authorRegex.getGroup("plain").trim();
			} else if ((MercurialRepository.authorRegex.getGroup("lastname") != null)
			        && (MercurialRepository.authorRegex.getGroup("name") != null)) {
				authorFullname = MercurialRepository.authorRegex.getGroup("name").trim() + " "
				        + MercurialRepository.authorRegex.getGroup("lastname").trim();
			} else if (MercurialRepository.authorRegex.getGroup("name") != null) {
				authorUsername = MercurialRepository.authorRegex.getGroup("name").trim();
			}
			if (MercurialRepository.authorRegex.getGroup("email") != null) {
				authorEmail = MercurialRepository.authorRegex.getGroup("email").trim();
			}
			Person author = new Person(authorUsername, authorFullname, authorEmail);
			
			String[] dateString = lineParts[2].split(" ");
			
			DateTime date = new DateTime(Long.valueOf(dateString[0]).longValue() * 1000,
			                             DateTimeZone.forOffsetMillis(Integer.valueOf(dateString[1]).intValue() * 1000));
			
			LogEntry previous = null;
			if (result.size() > 0) {
				previous = result.get(result.size() - 1);
			}
			result.add(new LogEntry(revID, previous, author, lineParts[6].replaceAll("<br/>", FileUtils.lineSeparator),
			                        date, ""));
		}
		return result;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(@NotNull final URI address,
	                  final String startRevision,
	                  final String endRevision) {
		setup(address, startRevision, endRevision, null);
	}
	
	/**
	 * main setup method.
	 * 
	 * @param address
	 *            the address
	 * @param startRevision
	 *            the start revision
	 * @param endRevision
	 *            the end revision
	 * @param inputStream
	 *            the input stream
	 */
	private void setup(final URI address,
	                   final String startRevision,
	                   final String endRevision,
	                   final InputStream inputStream) {
		
		setUri(address);
		
		String hgName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_hg_clone_"
		        + DateTimeUtils.currentTimeMillis();
		
		// clone the remote repository
		if (!clone(null, hgName)) {
			if (Logger.logError()) {
				Logger.error("Could not clone git repository `" + getUri().toString() + "` to directory `" + hgName
				        + "`");
				throw new RuntimeException();
			}
		}
		
		if (startRevision == null) {
			setStartRevision(getFirstRevisionId());
		} else {
			setStartRevision(startRevision);
		}
		
		if (endRevision == null) {
			setEndRevision(getHEADRevisionId());
		} else {
			setEndRevision(startRevision);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@NoneNull
	public void setup(final URI address,
	                  final String startRevision,
	                  final String endRevision,
	                  final String username,
	                  final String password) {
		setup(URIUtils.encodeUsername(address, username), startRevision, endRevision,
		      new ByteArrayInputStream(password.getBytes()));
	}
}
