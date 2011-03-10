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
	private final File                   cloneDir;
	private final String                 revision;
	
	private final Map<String, RCSBranch> branches = new HashMap<String, RCSBranch>();
	
	@NoneNull
	public GitRevDependencyIterator(final File cloneDir, final String revision) {
		
		this.cloneDir = cloneDir;
		this.revision = revision;
		try {
			LineIterator revListFileIterator = FileUtils.getLineIterator(this.getRevListFile());
			LineIterator decorateListIterator = FileUtils.getLineIterator(this.getDecorateListFile());
			
			List<String> nonMergedBranches = getNonMergedBranches();
			List<String> mergeTransactions = getMerges();
			
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
				
				//if decoration starts with "ref/remotes/" then it's a branch name. Set branchName of current branch.
				//else if it starts with "refs/tags" it's a tag
				String branchName = null;
				List<String> tagNames = new LinkedList<String>();
				if (!decoration.equals("")) {
					List<RegexGroup> groups = tagRegex.find(decoration);
					if ((groups != null) && (groups.size() == 2)) {
						String refString = groups.get(1).getMatch();
						String[] refs = refString.split(",");
						for (String ref : refs) {
							ref = ref.trim();
							if (ref.startsWith("refs/remotes/")) {
								branchName = ref.substring(13);
							} else if (ref.startsWith("refs/tags/")) {
								tagNames.add(ref.substring(10));
							}
						}
					}
				}
				
				String revId = revFields[0].trim();
				List<String> parents = new LinkedList<String>();
				for (int i = 1; i < revFields.length; ++i) {
					parents.add(revFields[i]);
				}
				
				if (!this.branches.containsKey(revId)) {
					if ((branchName == null) || branchName.equals("origin/HEAD") || branchName.equals("origin/master")) {
						this.branches.put(revId, RCSBranch.MASTER);
					} else {
						this.branches.put(revId, new RCSBranch(branchName));
					}
				}
				
				Condition
				.check(this.branches.containsKey(revId),
				"The current transaction id must be known and the branch it belongs to must be known too. If this is not the case somethig goes horribly wrong.");
				
				RCSBranch commitBranch = this.branches.get(revId);
				this.branches.remove(revId);
				
				//if this is a named branch change name of branch
				if ((branchName != null) && (!commitBranch.equals(RCSBranch.MASTER))) {
					commitBranch.setName(branchName);
					
					//if branch is non-merged, make it as such
					if (nonMergedBranches.contains("remotes/" + branchName)) {
						commitBranch.markOpen();
					}
				}
				
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
						//there is no later transaction within this new branch . Otherwise we would have seen the parent already
						this.branches.put(parent, newBranch);
						//set transaction to be merge transaction for new branch
						newBranch.setMergedIn(revId);
					}
				}
				
				//if transaction is a merge, mark it as a merge
				boolean isMerge = false;
				if (mergeTransactions.contains(revId)) {
					isMerge = true;
				}
				depList.add(0, new RevDependency(revId, commitBranch, new HashSet<String>(parents), tagNames, isMerge));
			}
			if ((revListFileIterator.hasNext()) || (decorateListIterator.hasNext())) {
				throw new UnrecoverableError(
				"Could not initialize DependencyIterator for Git repo: revlist and taglist should have same length");
			}
			this.depIter = depList.iterator();
		} catch (Exception e) {
			throw new UnrecoverableError("Could not initialize DependencyIterator for Git repo."
					+ FileUtils.lineSeparator + e.getMessage(), e);
		}
	}
	
	private File getDecorateListFile() throws IOException {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log", "--branches",
				"--decorate=full", "--remotes", "--encoding=UTF-8", "--pretty=format:%H %d", "--topo-order",
				this.revision }, this.cloneDir, null, new HashMap<String, String>(), GitRepository.charset);
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
	
	private List<String> getMerges() {
		/*
		 * get merges using "git log --decorate=full --branches --remotes
		 * --pretty=format:"%H" --merges". Whenever you see such a merge, mark
		 * the merge as closing transaction iff not already set and branch gets
		 * closed at all (see above).
		 */
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log", "--branches",
				"--decorate=full", "--remotes", "--encoding=UTF-8", "--pretty=format:%H", "--merges", "--topo-order",
				this.revision }, this.cloneDir, null, new HashMap<String, String>(), GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not fetch list of transactions merging branches.");
		}
		return response.getSecond();
	}
	
	private List<String> getNonMergedBranches() {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "branch", "-a", "-l",
				"--no-merged", this.revision }, this.cloneDir, null, new HashMap<String, String>(),
				GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not fetch the non-merged branches for Git repo.");
		}
		return response.getSecond();
	}
	
	private File getRevListFile() throws IOException {
		Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "rev-list",
		        "--encoding=UTF-8", "--parents", "--branches", "--remotes", "--topo-order", this.revision },
		        this.cloneDir, null,
				new HashMap<String, String>(), GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError(
					"Could not initialize DependencyIterator for Git repo: could not get revList using revision"
					+ this.revision + ".");
		}
		File revListFile = FileUtils.createRandomFile();
		BufferedWriter revListWriter = new BufferedWriter(new FileWriter(revListFile));
		for (String line : response.getSecond()) {
			revListWriter.write(line);
			revListWriter.write(FileUtils.lineSeparator);
		}
		revListWriter.close();
		return revListFile;
	}
	
	@Override
	public boolean hasNext() {
		return this.depIter.hasNext();
	}
	
	@Override
	public RevDependency next() {
		if (!hasNext()) {
			return null;
		}
		return this.depIter.next();
	}
	
	@Override
	public void remove() {
		this.depIter.remove();
	}
	
}
