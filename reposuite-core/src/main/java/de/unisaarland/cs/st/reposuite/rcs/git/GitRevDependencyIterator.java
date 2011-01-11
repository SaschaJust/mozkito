package de.unisaarland.cs.st.reposuite.rcs.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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


public class GitRevDependencyIterator implements RevDependencyIterator {
	
	protected static Regex               tagRegex = new Regex("\\(([^)]+)\\)");
	
	private LineIterator      lineIter;
	private final File        cloneDir;
	private final String      revision;
	private final Set<String> tagNames = new HashSet<String>();
	private final Map<String, RCSBranch> branches = new HashMap<String, RCSBranch>();
	
	public GitRevDependencyIterator(final File cloneDir, final String revision) {
		Condition.notNull(cloneDir);
		Condition.notNull(revision);
		
		File depFile = FileUtils.createRandomFile();
		
		this.cloneDir = cloneDir;
		this.revision = revision;
		try {
			FileUtils.forceDeleteOnExit(depFile);
			LineIterator revListFileIterator = FileUtils.getLineIterator(this.getRevListFile());
			LineIterator decorateListIterator = FileUtils.getLineIterator(this.getDecorateListFile());
			
			// get tag names
			File tagDir = new File(cloneDir.getAbsolutePath() + ".git" + FileUtils.fileSeparator + "refs"
					+ FileUtils.fileSeparator + "tags");
			if(tagDir.exists()){
				for (File tagFile : FileUtils.listFiles(tagDir, null, false)){
					if(!tagFile.isDirectory()){
						this.tagNames.add(tagFile.getName());
					}
				}
			}
			
			// merge files
			BufferedWriter depFileWriter = new BufferedWriter(new FileWriter(depFile));
			while ((revListFileIterator.hasNext()) && (decorateListIterator.hasNext())) {
				StringBuilder lineToWrite = new StringBuilder();
				
				String[] revFields = revListFileIterator.next().split("\\s");
				String decoLine = decorateListIterator.next();
				String decoId = decoLine.substring(0, 40).trim();
				String decoration = decoLine.substring(40).trim();
				
				Condition.check(revFields.length > 0);
				Condition.check(revFields[0].trim().equals(decoId));
				String revId = revFields[0].trim();
				lineToWrite.append(revId.trim());
				lineToWrite.append("|");
				
				StringBuilder revBuilder = new StringBuilder();
				for (int i = 1; i < revFields.length; ++i) {
					revBuilder.append(revFields[i]);
					revBuilder.append(" ");
				}
				lineToWrite.append(revBuilder.toString().trim());
				lineToWrite.append("|");
				
				if (!decoration.equals("")) {
					StringBuilder decorateBuilder = new StringBuilder();
					List<RegexGroup> groups = tagRegex.find(decoration);
					if ((groups != null) && (groups.size() == 2)) {
						String tagNamesString = groups.get(1).getMatch();
						String[] tagNamesStrings = tagNamesString.split(",");
						for (String tagName : tagNamesStrings) {
							tagName = tagName.trim();
							if (tagName.equals("master") || tagName.equals("HEAD")) {
								continue;
							}
							decorateBuilder.append(tagName);
							decorateBuilder.append(" ");
						}
					}
					lineToWrite.append(decorateBuilder.toString().trim());
				}
				depFileWriter.write(lineToWrite.toString().trim());
				depFileWriter.write(FileUtils.lineSeparator);
			}
			if ((revListFileIterator.hasNext()) || (decorateListIterator.hasNext())) {
				throw new UnrecoverableError(
				"Could not initialize DependencyIterator for Git repo: revlist and taglist should have same length");
			}
			depFileWriter.close();
			this.lineIter = FileUtils.getLineIterator(depFile);
		} catch (Exception e) {
			throw new UnrecoverableError("Could not initialize DependencyIterator for Git repo."
					+ FileUtils.lineSeparator + e.getMessage(), e);
		}
	}
	
	private File getDecorateListFile() throws IOException {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
 "--reverse",
		        "--encoding=UTF-8", "--pretty=format:%H %d", this.revision }, this.cloneDir, null,
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
 "--reverse",
		        "--encoding=UTF-8", "--parents", this.revision }, this.cloneDir, null,
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
		return this.lineIter.hasNext();
	}
	
	@Override
	public RevDependency next() {
		if (!hasNext()) {
			return null;
		}
		String[] depLine = this.lineIter.next().split("\\|");
		Condition.check(depLine.length >= 1);
		Condition.check(depLine.length <= 3);
		String id = depLine[0];
		String[] parents = new String[0];
		if (this.branches.isEmpty()) {
			// found last commit
			this.branches.put(depLine[0], RCSBranch.MASTER);
		}
		if (depLine.length > 1) {
			parents = depLine[1].split(" ");
			Condition.check(parents.length > 0);
		}
		
		
		
		Condition.check(this.branches.containsKey(id));
		RCSBranch commitBranch = this.branches.get(id);
		this.branches.remove(id);
		
		Set<String> parentSet = new HashSet<String>();
		if (parents.length > 0) {
			String parent = parents[0];
			parentSet.add(parent);
			if (!this.branches.containsKey(parent)) {
				this.branches.put(parent, commitBranch);
			} else {
				if (commitBranch.compareTo(this.branches.get(parent)) < 0) {
					this.branches.put(parent, commitBranch);
				}
			}
		}
		for (int i = 1; i < parents.length; ++i) {
			String parent = parents[i];
			parentSet.add(parent);
			RCSBranch newBranch = new RCSBranch(parent + "Branch", commitBranch);
			if (!this.branches.containsKey(parent)) {
				this.branches.put(parent, newBranch);
			} else {
				if (newBranch.compareTo(this.branches.get(parent)) < 0) {
					this.branches.put(parent, newBranch);
				}
			}
		}
		
		String[] decos = new String[0];
		if (depLine.length == 3) {
			decos = depLine[2].split(" ");
		}
		String tagName = null;
		for (String deco : decos) {
			if (this.tagNames.contains(deco)) {
				tagName = deco;
			}
		}
		
		return new RevDependency(depLine[0], commitBranch, parentSet, tagName);
	}
	
	@Override
	public void remove() {
		this.lineIter.remove();
	}
	
}
