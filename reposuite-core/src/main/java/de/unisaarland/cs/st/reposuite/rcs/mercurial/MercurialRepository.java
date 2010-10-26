/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.mercurial;

import java.io.BufferedWriter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.CommandExecutor;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class MercurialRepository extends Repository {
	
	protected static SimpleDateFormat hgLogDateFormat      = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
	protected static String           regex                = "^\\s*([^ ]+)\\s+([^ ]+)\\s+([^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+\\+[0-9]{4})\\s+([^:]+):\\s(.*)$";
	protected static SimpleDateFormat hgAnnotateDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
	protected List<String>            hashes               = new ArrayList<String>();
	private URI                       uri;
	private File                      cloneDir;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(String filePath, String revision) {
		if ((filePath == null) && (revision == null)) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("filePath and revision must not be null. Abort.");
			}
		}
		String cmd = "hg annotate -cfud -r" + revision + " " + filePath;
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Annotating file `" + filePath + "` in revision `" + revision + "` returned no output.");
			}
			return null;
		}
		Pattern pattern = Pattern.compile(regex);
		List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		HashMap<String, String> hashCache = new HashMap<String, String>();
		
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Found line in annotation that cannot be parsed. Abort");
				}
				return null;
			}
			String author = matcher.group(1);
			String shortHash = matcher.group(2);
			String date = matcher.group(3);
			
			DateTime timestamp;
			try {
				timestamp = new DateTime(hgAnnotateDateFormat.parseObject(date));
			} catch (ParseException e) {
				if (RepoSuiteSettings.logError()) {
					Logger.error(e.getMessage());
				}
				return null;
			}
			
			String file = matcher.group(4);
			String codeLine = matcher.group(5);
			
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
					if (RepoSuiteSettings.logError()) {
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
		String cmd = "hg log --template '{node}\n'";
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			if (RepoSuiteSettings.logWarn()) {
				Logger.warn("Could not cache hashes");
			}
			return;
		}
		for (String line : response.getSecond()) {
			if (line.trim().equals("")) {
				continue;
			}
			this.hashes.add(line.trim());
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
	public File checkoutPath(String relativeRepoPath, String revision) {
		if ((relativeRepoPath == null) || (revision == null)) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Path and revision must not be null.");
			}
			return null;
		}
		String cmd = "hg update -C " + revision;
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		File file = new File(this.cloneDir, relativeRepoPath);
		if (!file.exists()) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Could not get requested path using command `" + cmd + "`. Abort.");
			}
			return null;
		}
		return file;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#diff(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision) {
		if ((filePath == null) || (baseRevision == null) || (revisedRevision == null)) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Path and revisions must not be null. Abort.");
			}
			return null;
		}
		String baseCMD = "hg cat -r";
		
		String cmd = baseCMD + baseRevision;
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> original = response.getSecond();
		
		cmd = baseCMD + revisedRevision;
		response = CommandExecutor.execute(cmd, this.cloneDir);
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
	public Map<String, ChangeType> getChangedPaths(String revision) {
		if (revision == null) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Revision must be null. Abort.");
			}
			return null;
		}
		String cmd = "hg log --style minerlog -r" + revision + ":" + revision;
		try {
			writeLogStyle(this.cloneDir);
		} catch (IOException e1) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Could not set log style `miner` in order to parse log. Abort.");
				Logger.error(e1.getMessage());
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() != 1) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Log returned " + lines.size() + " lines. Only one line expected. Abort.");
			}
			return null;
		}
		String line = lines.get(0);
		String[] lineParts = line.split("\\+~\\+");
		if (lineParts.length < 7) {
			if (RepoSuiteSettings.logError()) {
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
		return this.cloneDir;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		String cmd = "hg log -r0 --template \"{node}\"";
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Command `" + cmd + "` returned no output. Abort.");
			}
			return null;
		}
		return lines.get(0).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getLastRevisionId()
	 */
	@Override
	public String getLastRevisionId() {
		String cmd = "hg log -rtip --template \"{node}\"";
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		if (lines.size() < 1) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Command `" + cmd + "` returned no output. Abort.");
			}
			return null;
		}
		return lines.get(0).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<LogEntry> log(String fromRevision, String toRevision) {
		ArrayList<LogEntry> result = new ArrayList<LogEntry>();
		if ((toRevision == null) || (fromRevision == null)) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Cannot get log for null referenced revisions. Abort");
			}
			return null;
		}
		
		String cmd = "hg log --style minerlog -r" + toRevision + ":" + fromRevision;
		try {
			writeLogStyle(this.cloneDir);
		} catch (IOException e1) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Could not set log style `miner` in order to parse log. Abort.");
				Logger.error(e1.getMessage());
			}
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		
		for (String line : lines) {
			String[] lineParts = line.split("\\+~\\+");
			if (lineParts.length < 7) {
				if (RepoSuiteSettings.logError()) {
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
			String author = lineParts[1];
			DateTime date;
			try {
				date = new DateTime(hgLogDateFormat.parse(lineParts[2]));
			} catch (ParseException e) {
				if (RepoSuiteSettings.logError()) {
					Logger.error(e.getMessage());
				}
				return null;
			}
			
			LogEntry previous = null;
			if (result.size() > 0) {
				previous = result.get(result.size() - 1);
			}
			result.add(new LogEntry(revID, previous, author, lineParts[6], date));
		}
		return result;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(URI address) {
		this.uri = address;
		//clone  the remote repository
		
		String hgName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		        + DateTimeUtils.currentTimeMillis();
		StringBuilder cmd = new StringBuilder();
		cmd.append("hg clone -U ");
		cmd.append(this.uri);
		cmd.append(" ");
		cmd.append(hgName);
		
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute(cmd.toString(), this.cloneDir);
		if (returnValue.getFirst() == 0) {
			this.cloneDir = new File(hgName);
			if (!this.cloneDir.exists()) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Could not clone git repository `" + this.uri.toString() + "` to directory `" + hgName
					        + "`");
					Logger.error("Used command: " + cmd.toString());
				}
				return;
			}
			try {
				FileUtils.forceDeleteOnExit(this.cloneDir);
			} catch (IOException e) {
				if (RepoSuiteSettings.logError()) {
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
	public void setup(URI address, String username, String password) {
		this.uri = Repository.encodeUsername(address, username);
		String hgName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		        + DateTimeUtils.currentTimeMillis();
		StringBuilder cmd = new StringBuilder();
		cmd.append("hg clone -U ");
		cmd.append(this.uri);
		cmd.append(" ");
		cmd.append(hgName);
		
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute(cmd.toString(), this.cloneDir, password);
		if (returnValue.getFirst() == 0) {
			this.cloneDir = new File(hgName);
			if (!this.cloneDir.exists()) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Could not clone git repository `" + this.uri.toString() + "` to directory `" + hgName
					        + "`");
					Logger.error("Used command: " + cmd.toString());
				}
				return;
			}
			try {
				FileUtils.forceDeleteOnExit(this.cloneDir);
			} catch (IOException e) {
				if (RepoSuiteSettings.logError()) {
					Logger.error(e.getMessage());
				}
			}
		}
		cacheHashes();
	}
	
	private void writeLogStyle(File dir) throws IOException {
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
