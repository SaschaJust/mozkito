package de.unisaarland.cs.st.reposuite.rcs.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.LineIterator;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependency;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import de.unisaarland.cs.st.reposuite.utils.CommandExecutor;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;


public class GitRevDependencyIterator implements RevDependencyIterator {
	
	protected static Regex               tagRegex = new Regex("\\(([^)]+)\\)");
	private Iterator<RevDependency>      depIter;
	private final File        cloneDir;
	private final String      revision;
	private final Set<String> tagNames = new HashSet<String>();
	
	private final Map<String, RCSBranch> branches = new HashMap<String, RCSBranch>();
	
	@NoneNull
	public GitRevDependencyIterator(final File cloneDir, final String revision) {
		
		this.cloneDir = cloneDir;
		this.revision = revision;
		try {
			LineIterator revListFileIterator = FileUtils.getLineIterator(this.getRevListFile());
			LineIterator decorateListIterator = FileUtils.getLineIterator(this.getDecorateListFile());
			
			// get tag names
			File tagDir = new File(cloneDir.getAbsolutePath() + ".git" + FileUtils.fileSeparator + "refs"
					+ FileUtils.fileSeparator + "tags");
			if(tagDir.exists()){
				for (File tagFile : FileUtils.listFiles(tagDir, null, false)){
					if(!tagFile.isDirectory()){
						tagNames.add(tagFile.getName());
					}
				}
			}
			
			List<RevDependency> depList = new LinkedList<RevDependency>();
			
			// merge files
			while ((revListFileIterator.hasNext()) && (decorateListIterator.hasNext())) {
				
				
				
				String[] revFields = revListFileIterator.next().split("\\s");
				String decoLine = decorateListIterator.next();
				String decoId = decoLine.substring(0, 40).trim();
				String decoration = decoLine.substring(40).trim();
				
				Condition.check(revFields.length > 0, "The fileds in the temporary csv filed should never be empty");
				Condition.check(revFields[0].trim().equals(decoId),
				"the first field in the current temp csv line must equal the current tranaction id");
				
				String revId = revFields[0].trim();
				List<String> parents = new LinkedList<String>();
				List<String> branches = new LinkedList<String>();
				
				for (int i = 1; i < revFields.length; ++i) {
					parents.add(revFields[i]);
				}
				
				if (!decoration.equals("")) {
					List<RegexGroup> groups = tagRegex.find(decoration);
					if ((groups != null) && (groups.size() == 2)) {
						String tagNamesString = groups.get(1).getMatch();
						String[] tagNamesStrings = tagNamesString.split(",");
						for (String tagName : tagNamesStrings) {
							tagName = tagName.trim();
							if (tagName.equals("master") || tagName.equals("HEAD")) {
								continue;
							}
							branches.add(tagName);
						}
					}
				}
				
				if (this.branches.isEmpty()) {
					// found last commit
					this.branches.put(revId, RCSBranch.MASTER);
				}
				Condition
				        .check(this.branches.containsKey(revId),
				                "The current transaction id must be known and the branch it belongs to must be known too. If this is not the case somethig goes horribly wrong.");
				RCSBranch commitBranch = this.branches.get(revId);
				this.branches.remove(revId);
				
				if (parents.size() > 0) {
					String parent = parents.get(0);
					if (!this.branches.containsKey(parent)) {
						this.branches.put(parent, commitBranch);
					} else {
						if (commitBranch.compareTo(this.branches.get(parent)) < 0) {
							this.branches.put(parent, commitBranch);
						}
					}
				}
				for (int i = 1; i < parents.size(); ++i) {
					String parent = parents.get(i);
					RCSBranch newBranch = new RCSBranch(parent + "Branch", commitBranch);
					if (!this.branches.containsKey(parent)) {
						this.branches.put(parent, newBranch);
					} else {
						if (newBranch.compareTo(this.branches.get(parent)) < 0) {
							this.branches.put(parent, newBranch);
						}
					}
				}
				
				String tagName = null;
				for (String branch : branches) {
					if (tagNames.contains(branch)) {
						tagName = branch;
						break;
					}
				}
				depList.add(0, new RevDependency(revId, commitBranch, new HashSet<String>(parents), tagName));
			}
			if ((revListFileIterator.hasNext()) || (decorateListIterator.hasNext())) {
				throw new UnrecoverableError(
				"Could not initialize DependencyIterator for Git repo: revlist and taglist should have same length");
			}
			depIter = depList.iterator();
		} catch (Exception e) {
			throw new UnrecoverableError("Could not initialize DependencyIterator for Git repo."
					+ FileUtils.lineSeparator + e.getMessage(), e);
		}
	}
	
	private File getDecorateListFile() throws IOException {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
				"--encoding=UTF-8", "--pretty=format:%H %d", revision }, cloneDir, null,
				new HashMap<String, String>(), GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError(
			"Could not initialize DependencyIterator for Git repo. Could not get decorateList");
		}
		File decorateListFile = FileUtils.createRandomFile();
		BufferedWriter decorateListWriter = new BufferedWriter(new FileWriter(decorateListFile));
		for (String line : response.getSecond()) {
			decorateListWriter.write(line);
			decorateListWriter.write(FileUtils.lineSeparator);
		}
		decorateListWriter.close();
		return decorateListFile;
	}
	
	private File getRevListFile() throws IOException {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git",
				new String[] { "rev-list",
				"--encoding=UTF-8", "--parents", revision }, cloneDir, null,
				new HashMap<String, String>(), GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not initialize DependencyIterator for Git repo: could not get revList.");
		}
		File revListFile = FileUtils.createRandomFile();
		BufferedWriter revListWriter = new BufferedWriter(new FileWriter(revListFile));
		for(String line : response.getSecond()){
			revListWriter.write(line);
			revListWriter.write(FileUtils.lineSeparator);
		}
		revListWriter.close();
		return revListFile;
	}
	
	@Override
	public boolean hasNext() {
		return depIter.hasNext();
	}
	
	@Override
	public RevDependency next() {
		if (!hasNext()) {
			return null;
		}
		return depIter.next();
	}
	
	@Override
	public void remove() {
		depIter.remove();
	}
	
}
