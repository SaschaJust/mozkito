/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.mercurial;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.CommandExecutor;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class MercurialRepository extends Repository {
	
	protected static Regex            authorRegex          = new Regex(
	                                                               "^({name}[^\\s<]+)?\\s*({lastname}[^\\s<]+\\s+)?(<({email}[^>]+)>)?");
	
	protected static SimpleDateFormat hgAnnotateDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
	protected static SimpleDateFormat hgLogDateFormat      = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
	protected static Regex            formerPathRegex      = new Regex("[^(]*\\(({result}[^(]+)\\)");
	protected static String           pattern              = "^\\s*({author}[^ ]+)\\s+({hash}[^ ]+)\\s+({date}[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+\\+[0-9]{4})\\s+({file}[^:]+):\\s({codeline}.*)$";
	protected static Regex            regex                = new Regex(pattern);
	private File                      cloneDir;
	protected List<String>            hashes               = new ArrayList<String>();
	private final PersonManager       personManager        = new PersonManager();
	private URI                       uri;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(final String filePath, final String revision) {
		if ((filePath == null) && (revision == null)) {
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
			try {
				timestamp = new DateTime(hgAnnotateDateFormat.parseObject(date));
			} catch (ParseException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
				return null;
			}
			
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
	 * Cache hashes.
	 */
	private void cacheHashes() {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log",
		        "--template '{node}\n'" }, cloneDir, null, null);
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
			hashes.add(line.trim());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#checkoutPath(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public File checkoutPath(final String relativeRepoPath, final String revision) {
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
	
	@Override
	public void consistencyCheck(final List<LogEntry> logEntries) {
		//Will be implemented in super class
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#diff(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(final String filePath, final String baseRevision, final String revisedRevision) {
		if ((filePath == null) || (baseRevision == null) || (revisedRevision == null)) {
			if (Logger.logError()) {
				Logger.error("Path and revisions must not be null. Abort.");
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg",
		        new String[] { "cat", "-r", baseRevision }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> original = response.getSecond();
		
		response = CommandExecutor.execute("hg", new String[] { "cat", "-r", revisedRevision }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> revised = response.getSecond();
		Patch patch = DiffUtils.diff(original, revised);
		return patch.getDeltas();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#getChangedPaths(java.lang
	 * .String)
	 */
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
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
		        "minerlog", "-r", revision, ":", revision }, cloneDir, null, null);
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
		String[] addedPaths = lineParts[3].split(" ");
		String[] deletedPaths = lineParts[4].split(" ");
		String[] modifiedPaths = lineParts[5].split(" ");
		
		Map<String, ChangeType> result = new HashMap<String, ChangeType>();
		
		for (String addedPath : addedPaths) {
			result.put(addedPath, ChangeType.Added);
		}
		for (String deletedPath : deletedPaths) {
			result.put(deletedPath, ChangeType.Deleted);
		}
		for (String modifiedPath : modifiedPaths) {
			result.put(modifiedPath, ChangeType.Modified);
		}
		return result;
	}
	
	public File getCloneDir() {
		return cloneDir;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r0",
		        "--template", "\"{node}\"" }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			if (Logger.logError()) {
				Logger.error("Command `hg log -r0 --template \"{node}\"` returned no output. Abort.");
			}
			return null;
		}
		return lines.get(0).trim();
	}
	
	@Override
	public String getFormerPathName(final String revision, final String pathName) {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", revision,
		        "--template", "\"{file_copies%filecopy}\"" }, cloneDir, null, null);
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
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getLastRevisionId()
	 */
	@Override
	public String getLastRevisionId() {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-rtip",
		        "--template", "\"{node}\"" }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			if (Logger.logError()) {
				Logger.error("Command `hg log -rtip --template \"{node}\"` returned no output. Abort.");
			}
			return null;
		}
		return lines.get(0).trim();
	}
	
	@Override
	public String getRelativeTransactionId(final String transactionId, final long index) {
		if (index == 0) {
			return transactionId;
		} else if (index > 0) {
			String[] args = new String[] { "log", "-r", transactionId + ":tip", "--template=\"{node}\\n\"", "-l",
			        String.valueOf(index + 1) };
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> list = response.getSecond();
			return list.get(list.size() - 1).trim();
		} else {
			String[] args = new String[] { "log", "-r", transactionId + ":0", "--template=\"{node}\\n\"", "-l",
			        String.valueOf(index + 1) };
			Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> list = response.getSecond();
			return list.get(list.size() - 1).trim();
		}
	}
	
	@Override
	public long getTransactionCount() {
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", new String[] { "log", "-r", "tip",
		        "--template=\"{rev}\\n\"" }, cloneDir, null, null);
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
	
	@Override
	public String getTransactionId(final long index) {
		String[] args = new String[] { "log", "-r", String.valueOf(index), "--template=\"{node}\\n\"" };
		Tuple<Integer, List<String>> response = CommandExecutor.execute("hg", args, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		return response.getSecond().get(0).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<LogEntry> log(final String fromRevision, final String toRevision) {
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
		        "minerlog", "-r", fromRevision, ":", toRevision }, cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		
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
			if (regex.getGroupNames().contains("lastname") && regex.getGroupNames().contains("name")) {
				authorFullname = regex.getGroup("name") + " " + regex.getGroup("lastname");
			} else if ((!regex.getGroupNames().contains("lastname")) && regex.getGroupNames().contains("name")) {
				authorUsername = regex.getGroup("name");
			}
			if (regex.getGroupNames().contains("email")) {
				authorEmail = regex.getGroup("email");
			}
			Person author = new Person(authorUsername, authorFullname, authorEmail);
			
			DateTime date;
			try {
				date = new DateTime(hgLogDateFormat.parse(lineParts[2]));
			} catch (ParseException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
				return null;
			}
			
			LogEntry previous = null;
			if (result.size() > 0) {
				previous = result.get(result.size() - 1);
			}
			result.add(new LogEntry(revID, previous, personManager.getPerson((author != null ? new Person("<unknown>",
			        null, null) : null)), lineParts[6], date));
		}
		return result;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision) {
		uri = address;
		// clone the remote repository
		
		String hgName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		        + DateTimeUtils.currentTimeMillis();
		
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("hg",
		        new String[] { "clone", "-U", uri.toString(), hgName }, cloneDir, null, null);
		if (returnValue.getFirst() == 0) {
			cloneDir = new File(hgName);
			if (!cloneDir.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not clone git repository `" + uri.toString() + "` to directory `" + hgName
					        + "`");
				}
				return;
			}
			try {
				FileUtils.forceDeleteOnExit(cloneDir);
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
			}
		}
		cacheHashes();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision, final String username,
	        final String password) {
		uri = Repository.encodeUsername(address, username);
		String hgName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		        + DateTimeUtils.currentTimeMillis();
		StringBuilder cmd = new StringBuilder();
		cmd.append("hg clone -U ");
		cmd.append(uri);
		cmd.append(" ");
		cmd.append(hgName);
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(password.getBytes());
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("hg",
		        new String[] { "clone", "-U", uri.toString(), hgName }, cloneDir, inputStream, null);
		if (returnValue.getFirst() == 0) {
			cloneDir = new File(hgName);
			if (!cloneDir.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not clone git repository `" + uri.toString() + "` to directory `" + hgName
					        + "`");
					Logger.error("Used command: " + cmd.toString());
				}
				return;
			}
			try {
				FileUtils.forceDeleteOnExit(cloneDir);
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
			}
		}
		cacheHashes();
	}
	
	private void writeLogStyle(final File dir) throws IOException {
		File f = new File(dir + System.getProperty("file.separator") + "minerlog");
		if (f.exists()) {
			f.delete();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write("changeset = \"{node}+~+{author}+~+{date|isodate}+~+{file_adds}+~+{file_dels}+~+{file_mods}+~+{desc|addbreaks}\\n\"\n");
		writer.write("file_add = \"{file_add};\"\n");
		writer.write("file_del = \"{file_del};\"\n");
		writer.write("file_mod = \"{file_mod};\"\n");
		writer.close();
		
	}
}
