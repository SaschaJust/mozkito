/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTimeUtils;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.CMDExecutor;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
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
	
	private URI    uri;
	@SuppressWarnings("unused")
	private String username;
	@SuppressWarnings("unused")
	private String password;
	private File   cloneDir;
	
	/**
	 * Instantiates a new git repository.
	 */
	public GitRepository() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.rcs.Repository#annotate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(String filePath, String revision) {
		List<AnnotationEntry> result = new ArrayList<AnnotationEntry>();
		String firstRev = getFirstRevisionId();
		String cmd = "git blame -lf " + revision + " -- " + filePath;
		Tuple<Integer, List<String>> response = CMDExecutor.execute(cmd, this.cloneDir);
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
				if (RepoSuiteSettings.logError()) {
					Logger.error("Could not parse git blame output!");
				}
				return null;
			}
			String fileName = lineParts[1];
			if (fileName.equals(filePath)) {
				result.add(new AnnotationEntry(sha));
			} else {
				result.add(new AnnotationEntry(sha, fileName));
			}
		}
		return result;
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
		String cmd = "git checkout " + revision;
		Tuple<Integer, List<String>> response = CMDExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		File result = new File(this.cloneDir, relativeRepoPath);
		if (!result.exists()) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Could not checkout `" + relativeRepoPath + "` in revision `" + revision);
			}
			return null;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#diff(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision) {
		String baseCMD = "git show ";
		
		//get the old version
		String cmd = baseCMD + baseRevision + ":" + filePath;
		Tuple<Integer, List<String>> response = CMDExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		List<String> oldContent = response.getSecond();
		
		//get the old version
		cmd = baseCMD + revisedRevision + ":" + filePath;
		response = CMDExecutor.execute(cmd, this.cloneDir);
		
		List<String> newContent = response.getSecond();
		
		Patch patch = DiffUtils.diff(oldContent, newContent);
		return patch.getDeltas();
	}
	
	/**
	 * Gets the clone dir.
	 * 
	 * @return the clone dir
	 */
	protected File getCloneDir() {
		return this.cloneDir;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() {
		String cmd = "git log --pretty=format:%H";
		Tuple<Integer, List<String>> response = CMDExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		if (response.getSecond().isEmpty()) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Command `" + cmd + "` did not produc any output!");
			}
			return null;
		}
		List<String> lines = response.getSecond();
		return lines.get(lines.size() - 1).trim();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#getLastRevisionId()
	 */
	@Override
	public String getLastRevisionId() {
		String cmd = "git rev-parse master";
		Tuple<Integer, List<String>> response = CMDExecutor.execute(cmd, this.cloneDir);
		if (response.getFirst() != 0) {
			return null;
		}
		if (response.getSecond().isEmpty()) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Command `" + cmd + "` did not produc any output!");
			}
			return null;
		}
		return response.getSecond().get(0).trim();
	}
	
	@Override
	public List<LogEntry> log() {
		// TODO Auto-generated method stub
		return null;
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
		
		String gitName = FileUtils.tmpDir + FileUtils.fileSeparator + "reposuite_clone_"
		        + DateTimeUtils.currentTimeMillis();
		StringBuilder cmd = new StringBuilder();
		cmd.append("git clone -n -q ");
		cmd.append(this.uri);
		cmd.append(" ");
		cmd.append(gitName);
		
		Tuple<Integer, List<String>> returnValue = CMDExecutor.execute(cmd.toString(), this.cloneDir);
		if (returnValue.getFirst() == 0) {
			this.cloneDir = new File(gitName);
			if (!this.cloneDir.exists()) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Could not clone git repository `" + this.uri.toString() + "` to directory `"
					        + gitName + "`");
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
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.rcs.Repository#setup(java.net.URI,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(URI address, String username, String password) {
		//TODO implement this method
	}
}
