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

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.reposuite.exceptions.ExternalExecutableException;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.CommandExecutor;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
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
	protected static Regex             regex                = new Regex(pattern);
	
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
	protected static List<String> preFilterLines(final List<String> lines) {
		Condition.notNull(lines);
		Condition.notNull(lines);
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
	private static void writeLogStyle(final File dir) throws IOException {
		Condition.notNull(dir);
		Condition.notNull(dir, "Cannot write content to NULL file");
		File f = new File(dir + System.getProperty("file.separator") + "minerlog");
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
	
	private String              startRevision;
	private String              endRevision;
	private File                cloneDir;
	protected List<String>      hashes        = new ArrayList<String>();
	
	private final PersonManager personManager = new PersonManager();
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(final String filePath, final String revision) {
		Condition.notNull(filePath);
		Condition.notNull(revision);
		Condition.notNull(filePath, "Annotation of null path not possible");
		Condition.notNull(revision, "Annotation requires revision");
		if ((filePath == null) || (revision == null)) {
			if (Logger.logError()) {
				Logger.error("filePath and revision must not be null. Abort.");
			}
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "annotate", "-cfud", "-r",
				revision, filePath }, cloneDir, null, null);
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
			if (!regex.matchesFull(line)) {
				if (Logger.logError()) {
					Logger.error("Found line in annotation that cannot be parsed. Abort");
				}
				return null;
			}
			String author = regex.getGroup("author");
			String shortHash = regex.getGroup("hash");
			String date = regex.getGroup("date");
			
			DateTime timestamp;
			timestamp = hgAnnotateDateFormat.parseDateTime(date);
			
			String file = regex.getGroup("file");
			String codeLine = regex.getGroup("codeline");
			
			if (!hashCache.containsKey(shortHash)) {
				boolean found = false;
				for (String hash : hashes) {
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
		"'{node}\n'" }, cloneDir, null, null);
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
			hashes.add(line.trim().replaceAll("'", ""));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#checkoutPath(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public File checkoutPath(final String relativeRepoPath, final String revision) {
		Condition.notNull(relativeRepoPath);
		Condition.notNull(revision);
		Condition.notNull(relativeRepoPath, "Cannot check out NULL path");
		Condition.notNull(revision, "Checking ut requries revision");
		
		if ((relativeRepoPath == null) || (revision == null)) {
			if (Logger.logError()) {
				Logger.error("Path and revision must not be null.");
			}
			return null;
		}
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg",
				new String[] { "update", "-C", revision }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		File file = new File(cloneDir, relativeRepoPath);
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
	private boolean clone(final InputStream inputStream, final String destDir) {
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("hg",
				new String[] { "clone", "-U", getUri().toString(), destDir }, cloneDir, inputStream, null);
		if (returnValue.getFirst() == 0) {
			cloneDir = new File(destDir);
			if (!cloneDir.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not clone git repository `" + getUri().toString() + "` to directory `" + destDir
							+ "`");
				}
				return false;
			}
			try {
				FileUtils.forceDeleteOnExit(cloneDir);
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
			}
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
	public Collection<Delta> diff(final String filePath, final String baseRevision, final String revisedRevision) {
		Condition.notNull(filePath);
		Condition.notNull(baseRevision);
		Condition.notNull(revisedRevision);
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
				filePath }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> original = response.getSecond();
		List<String> revised = new ArrayList<String>(0);
		response = CommandExecutor.execute("hg", new String[] { "cat", "-r", revisedRevision, filePath },
				cloneDir, null, null);
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
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		Condition.notNull(revision);
		Condition.notNull(revision, "Cannot get changed paths for null revision");
		
		if (revision == null) {
			if (Logger.logError()) {
				Logger.error("Revision must be null. Abort.");
			}
			return null;
		}
		try {
			writeLogStyle(cloneDir);
		} catch (IOException e1) {
			if (Logger.logError()) {
				Logger.error("Could not set log style `miner` in order to parse log. Abort.");
				Logger.error(e1.getMessage());
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--style",
				"minerlog", "-r", revision + ":" + revision }, cloneDir, null, null);
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
	
	/**
	 * Gets the clone dir.
	 * 
	 * @return the clone dir
	 */
	public File getCloneDir() {
		return cloneDir;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		if (startRevision == null) {
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r0",
					"--template", "{node}" }, cloneDir, null, null);
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
			return startRevision;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getFormerPathName(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public String getFormerPathName(final String revision, final String pathName) {
		Condition.notNull(revision);
		Condition.notNull(pathName);
		Condition.notNull(revision, "Cannot get former path name of null revision");
		Condition.notNull(pathName, "Cannot get former path name for null path");
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", revision,
				"--template", "{file_copies%filecopy}" }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		String result = null;
		for (String line : response.getSecond()) {
			if (line.trim().startsWith(pathName)) {
				formerPathRegex.find(line);
				result = formerPathRegex.getGroup("result").trim();
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
	public String getLastRevisionId() {
		if (endRevision == null) {
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-rtip",
					"--template", "{node}" }, cloneDir, null, null);
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
			return endRevision;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getRelativeTransactionId
	 * (java.lang.String, long)
	 */
	@Override
	public String getRelativeTransactionId(final String transactionId, final long index) {
		Condition.notNull(transactionId);
		Condition.notNull(transactionId, "Cannot get relative revision to null revision");
		
		if (index == 0) {
			return transactionId;
		} else if (index > 0) {
			String[] args = new String[] { "log", "-r", transactionId + ":tip", "--template", "{node}\\n", "-l",
					String.valueOf(index + 1) };
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> list = response.getSecond();
			return list.get(list.size() - 1).trim();
		} else {
			String[] args = new String[] { "log", "-r", transactionId + ":0", "--template", "{node}\\n", "-l",
					String.valueOf(index + 1) };
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> list = response.getSecond();
			return list.get(list.size() - 1).trim();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getTransactionCount()
	 */
	@Override
	public long getTransactionCount() {
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", "tip",
				"--template", "{rev}\\n" }, cloneDir, null, null);
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
	public String getTransactionId(final long index) {
		Condition.greaterOrEqual(index, 0l);
		Condition.check(index >= 0, "Cannot get transaction id for revision number smaller than zero.");
		
		String[] args = new String[] { "log", "-r", String.valueOf(index), "--template=\"{node}\\n\"" };
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		return response.getSecond().get(0).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<LogEntry> log(final String fromRevision, final String toRevision) {
		Condition.notNull(fromRevision);
		Condition.notNull(toRevision);
		Condition.notNull(fromRevision, "Cannot get log info for NULL revision");
		Condition.notNull(toRevision, "Cannot get log info for NULL revision");
		
		ArrayList<LogEntry> result = new ArrayList<LogEntry>();
		if ((toRevision == null) || (fromRevision == null)) {
			if (Logger.logError()) {
				Logger.error("Cannot get log for null referenced revisions. Abort");
			}
			return null;
		}
		
		try {
			writeLogStyle(cloneDir);
		} catch (IOException e1) {
			if (Logger.logError()) {
				Logger.error("Could not set log style `miner` in order to parse log. Abort.");
				Logger.error(e1.getMessage());
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "--style",
				"minerlog", "-r", fromRevision + ":" + toRevision }, cloneDir, null, null);
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
			
			authorRegex.find(authorString);
			authorRegex.getGroupNames();
			
			if (authorRegex.getGroup("plain") != null) {
				authorUsername = authorRegex.getGroup("plain").trim();
			} else if ((authorRegex.getGroup("lastname") != null) && (authorRegex.getGroup("name") != null)) {
				authorFullname = authorRegex.getGroup("name").trim() + " " + authorRegex.getGroup("lastname").trim();
			} else if (authorRegex.getGroup("name") != null) {
				authorUsername = authorRegex.getGroup("name").trim();
			}
			if (authorRegex.getGroup("email") != null) {
				authorEmail = authorRegex.getGroup("email").trim();
			}
			Person author = new Person(authorUsername, authorFullname, authorEmail);
			
			String[] dateString = lineParts[2].split(" ");
			
			DateTime date = new DateTime(Long.valueOf(dateString[0]).longValue() * 1000,
					DateTimeZone.forOffsetMillis(Integer.valueOf(dateString[1]).intValue() * 1000));
			
			LogEntry previous = null;
			if (result.size() > 0) {
				previous = result.get(result.size() - 1);
			}
			result.add(new LogEntry(revID, previous, personManager.getPerson((author != null ? author : null)),
					lineParts[6].replaceAll("<br/>", FileUtils.lineSeparator), date));
		}
		return result;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision) {
		Condition.notNull(address);
		
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
	private void setup(final URI address, final String startRevision, final String endRevision,
			final InputStream inputStream) {
		
		String innerRepoPath = setup(address);
		
		String hgName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		+ DateTimeUtils.currentTimeMillis();
		
		// clone the remote repository
		if (!clone(null, hgName)) {
			if (Logger.logError()) {
				Logger.error("Failed to clone mercurial repository!");
				throw new RuntimeException();
			}
		}
		
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("hg", new String[] { "clone", "-U",
				getUri().toString(), hgName }, cloneDir, null, null);
		if (returnValue.getFirst() == 0) {
			cloneDir = new File(hgName + FileUtils.fileSeparator + innerRepoPath);
			if (!cloneDir.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not clone git repository `" + getUri().toString() + "` to directory `" + hgName
							+ "`");
				}
				return;
			}
			cacheHashes();
			try {
				FileUtils.forceDeleteOnExit(cloneDir);
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
			}
		}
		if (startRevision == null) {
			this.startRevision = this.getFirstRevisionId();
		} else {
			this.startRevision = startRevision;
		}
		
		if (endRevision == null) {
			this.endRevision = this.getLastRevisionId();
		} else {
			this.endRevision = startRevision;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision, final String username,
			final String password) {
		Condition.notNull(address);
		Condition.notNull(username);
		Condition.notNull(password);
		Condition.notNull(address);
		Condition.notNull(username);
		Condition.notNull(password);
		
		setup(Repository.encodeUsername(address, username), startRevision, endRevision, new ByteArrayInputStream(
				password.getBytes()));
	}
}
