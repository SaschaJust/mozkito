/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.git;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Preconditions;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.utils.CommandExecutor;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * The Class GitRepository. This class is _not_ thread safe.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GitRepository extends Repository {
	
	protected static DateTimeFormatter dtf             = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
	protected static Regex             regex           = new Regex(
	".*\\(({author}.*)\\s+({date}\\d{4}-\\d{2}-\\d{2}\\s+[^ ]+\\s+[+-]\\d{4})\\s+[^)]*\\)\\s+({codeline}.*)");
	protected static Regex             formerPathRegex = new Regex("^[^\\s]+\\s+({result}[^\\s]+)\\s+[^\\s]+.*");
	
	private File                       cloneDir;
	private URI                        uri;
	private String                     startRevision;
	private String                     endRevision;
	/**
	 * Instantiates a new git repository.
	 */
	public GitRepository() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(final String filePath, final String revision) {
		assert (filePath != null);
		assert (revision != null);
		Preconditions.checkNotNull(filePath, "Annotation of null path not possible");
		Preconditions.checkNotNull(revision, "Annotation requires revision");
		
		List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		String firstRev = getFirstRevisionId();
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "blame", "-lf", revision,
				"--", filePath }, this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		
		for (String line : response.getSecond()) {
			String sha = line.substring(0, 40);
			if (line.startsWith("^") && (firstRev.startsWith(line.substring(1, 40)))) {
				sha = firstRev;
			}
			
			String[] lineParts = line.split(" ");
			if (lineParts.length < 2) {
				if (Logger.logError()) {
					Logger.error("Could not parse git blame output!");
				}
				return null;
			}
			String fileName = lineParts[1];
			String author = "<unknown>";
			DateTime date = new DateTime();
			
			String lineContent = "<unkown>";
			if (regex.matchesFull(line)) {
				author = regex.getGroup("author");
				date = new DateTime(dtf.parseDateTime(regex.getGroup("date")));
				lineContent = regex.getGroup("codeline");
			} else {
				if (Logger.logWarn()) {
					Logger.error("Could not extract author and date info from log entry for revision `" + revision
							+ "`");
				}
				return null;
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
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#checkoutPath(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public File checkoutPath(final String relativeRepoPath, final String revision) {
		assert (relativeRepoPath != null);
		assert (revision != null);
		Preconditions.checkNotNull(relativeRepoPath, "Cannot check out NULL path");
		Preconditions.checkNotNull(revision, "Checking ut requries revision");
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "checkout", revision },
				this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		File result = new File(this.cloneDir, relativeRepoPath);
		if (!result.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not checkout `" + relativeRepoPath + "` in revision `" + revision);
			}
			return null;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#diff(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(final String filePath, final String baseRevision, final String revisedRevision) {
		assert (filePath != null);
		assert (baseRevision != null);
		assert (revisedRevision != null);
		Preconditions.checkNotNull(filePath, "Cannot diff NULL path");
		Preconditions.checkNotNull(baseRevision, "cannot compare to NULL revision");
		Preconditions.checkNotNull(revisedRevision, "cannot compare to NULL revision");
		
		// get the old version
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "show",
				baseRevision + ":" + filePath }, this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> oldContent = response.getSecond();
		
		// get the new version
		List<String> newContent = new ArrayList<String>(0);
		response = CommandExecutor.execute("git", new String[] { "show", revisedRevision + ":" + filePath }, this.cloneDir,
				null, new HashMap<String, String>());
		if (response.getFirst() == 0) {
			newContent = response.getSecond();
		}
		
		Patch patch = DiffUtils.diff(oldContent, newContent);
		return patch.getDeltas();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getChangedPaths()
	 */
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		assert (revision != null);
		Preconditions.checkNotNull(revision, "Cannot get changed paths for null revision");
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
				"--pretty=format:%H", "--name-status", revision }, this.cloneDir, null,
				new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> lines = response.getSecond();
		
		// delete first line. Contains the SHA hash of the wanted transaction
		if (lines.size() < 1) {
			if (Logger.logError()) {
				Logger.error("Error while parsing GIT log to unveil changed paths for revision `" + revision
						+ "`: git reported zero lines output. Abort parsing.");
			}
			return null;
		}
		lines.remove(0);
		Map<String, ChangeType> result = new HashMap<String, ChangeType>();
		for (String line : lines) {
			if (line.trim().equals("")) {
				// found the end of the log entry.
				break;
			}
			line = line.replaceAll("\\s+", " ");
			String[] lineParts = line.split(" ");
			if (lineParts.length < 2) {
				if (Logger.logError()) {
					Logger.error("Error while parsing GIT log to unveil changed paths for revision `" + revision
							+ "`: wrong line format detected. Abort parsing." + FileUtils.lineSeparator + "Line:"
							+ line);
				}
				return null;
			}
			String type = lineParts[0];
			String path = "/" + lineParts[1];
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
	
	/**
	 * Gets the clone dir.
	 * 
	 * @return the clone dir
	 */
	public File getCloneDir() {
		return this.cloneDir;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		if (this.startRevision == null) {
			Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
			"--pretty=format:%H" }, this.cloneDir, null, new HashMap<String, String>());
			if (response.getFirst() != 0) {
				return null;
			}
			if (response.getSecond().isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Command ` git --pretty=format:%H` did not produc any output!");
				}
				return null;
			}
			List<String> lines = response.getSecond();
			return lines.get(lines.size() - 1).trim();
		} else {
			return this.startRevision;
		}
	}
	
	@Override
	public String getFormerPathName(final String revision, final String pathName) {
		assert (revision != null);
		assert (pathName != null);
		Preconditions.checkNotNull(revision, "Cannot get former path name of null revision");
		Preconditions.checkNotNull(pathName, "Cannot get former path name for null path");
		
		String[] args = new String[] { "log", "-r", revision + "^.." + revision, "-M", "-C", "--name-status",
		"--diff-filter=R,C" };
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		for (String line : response.getSecond()) {
			if (((line.startsWith("R")) || (line.startsWith("C"))) && line.contains(pathName)) {
				List<RegexGroup> found = formerPathRegex.find(line);
				if (found.size() < 1) {
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
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getLastRevisionId()
	 */
	@Override
	public String getLastRevisionId() {
		if (this.endRevision == null) {
			Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "rev-parse", "master" },
					this.cloneDir, null, new HashMap<String, String>());
			if (response.getFirst() != 0) {
				return null;
			}
			if (response.getSecond().isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Command `git rev-parse master` did not produc any output!");
				}
				return null;
			}
			return response.getSecond().get(0).trim();
		} else {
			return this.endRevision;
		}
	}
	
	@Override
	public String getRelativeTransactionId(final String transactionId, final long index) {
		assert (transactionId != null);
		Preconditions.checkNotNull(transactionId, "Cannot get relative revision to null revision");
		
		if (index == 0) {
			return transactionId;
		} else if (index < 0) {
			String[] args = new String[] { "log", "--pretty=format:%H", "-r", transactionId };
			Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> lines = response.getSecond();
			if (lines.size() < index) {
				return lines.get(lines.size() - 1);
			} else {
				return lines.get((int) index);
			}
		} else {
			String[] args = new String[] { "log", "--reverse", "--pretty=format:%H", "-r",
					transactionId + ".." + getHEAD() };
			
			Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
			if (response.getFirst() != 0) {
				return null;
			}
			List<String> lines = response.getSecond();
			if (lines.size() < (index - 1)) {
				return lines.get(lines.size() - 1);
			} else {
				return lines.get((int) index - 1);
			}
		}
	}
	
	@Override
	public long getTransactionCount() {
		String[] args = new String[] { "log", "--pretty=format:''" };
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return -1;
		}
		return response.getSecond().size();
	}
	
	@Override
	public String getTransactionId(final long index) {
		assert (index >= 0);
		Preconditions.checkArgument(index >= 0, "Cannot get transaction id for revision number smaller than zero.");
		
		String[] args = new String[] { "log", "--pretty=format:'%H'", "--reverse" };
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", args, this.cloneDir, null, null);
		if (response.getFirst() != 0) {
			return null;
		}
		return response.getSecond().get((int) index);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#log(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<LogEntry> log(final String fromRevision, final String toRevision) {
		assert (fromRevision != null);
		assert (toRevision != null);
		Preconditions.checkNotNull(fromRevision, "Cannot get log info for NULL revision");
		Preconditions.checkNotNull(toRevision, "Cannot get log info for NULL revision");
		
		if ((fromRevision == null) || (toRevision == null)) {
			return null;
		}
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git",
				new String[] { "log", "--pretty=fuller" }, this.cloneDir, null, new HashMap<String, String>());
		if (response.getFirst() != 0) {
			return null;
		}
		return GitLogParser.parse(response.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI)
	 */
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision) {
		Preconditions.checkNotNull(address);
		
		this.uri = address;
		// clone the remote repository
		
		String gitName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		+ DateTimeUtils.currentTimeMillis();
		
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("git", new String[] { "clone", "-n", "-q",
				this.uri.toString(), gitName }, this.cloneDir, null, new HashMap<String, String>());
		if (returnValue.getFirst() == 0) {
			this.cloneDir = new File(gitName);
			if (!this.cloneDir.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not clone git repository `" + this.uri.toString() + "` to directory `" + gitName
							+ "`");
				}
				return;
			}
			try {
				FileUtils.forceDeleteOnExit(this.cloneDir);
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
			this.endRevision = endRevision;
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
		assert (address != null);
		assert (username != null);
		assert (password != null);
		Preconditions.checkNotNull(address);
		Preconditions.checkNotNull(username);
		Preconditions.checkNotNull(password);
		
		this.uri = Repository.encodeUsername(address, username);
		String gitName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		+ DateTimeUtils.currentTimeMillis();
		StringBuilder cmd = new StringBuilder();
		cmd.append("git clone -n -q ");
		cmd.append(this.uri);
		cmd.append(" ");
		cmd.append(gitName);
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(password.getBytes());
		Tuple<Integer, List<String>> returnValue = CommandExecutor.execute("git", new String[] { "clone", "-n", "-q",
				this.uri.toString(), gitName }, this.cloneDir, inputStream, new HashMap<String, String>());
		if (returnValue.getFirst() == 0) {
			this.cloneDir = new File(gitName);
			if (!this.cloneDir.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not clone git repository `" + this.uri.toString() + "` to directory `" + gitName
							+ "`");
					Logger.error("Used command: `git clone -n -q " + this.uri.toString() + " " + gitName + "`");
				}
				return;
			}
			try {
				FileUtils.forceDeleteOnExit(this.cloneDir);
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
			}
		}
		if (startRevision == null) {
			this.startRevision = this.getFirstRevisionId();
		}
		if (endRevision == null) {
			this.endRevision = this.getLastRevisionId();
		}
	}
}
