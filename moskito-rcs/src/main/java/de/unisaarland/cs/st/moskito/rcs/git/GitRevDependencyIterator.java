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
package de.unisaarland.cs.st.moskito.rcs.git;

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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.apache.commons.io.LineIterator;

import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.elements.RevDependency;
import de.unisaarland.cs.st.moskito.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;

public class GitRevDependencyIterator implements RevDependencyIterator {
	
	protected static Regex               tagRegex = new Regex("\\(([^)]+)\\)");
	private Iterator<RevDependency>      depIter;
	private final File                   cloneDir;
	private final String                 revision;
	
	private final Map<String, RCSBranch> branches = new HashMap<String, RCSBranch>();
	
	public GitRevDependencyIterator(final File cloneDir, final String revision, final BranchFactory branchFactory) {
		
		this.cloneDir = cloneDir;
		this.revision = revision;
		try {
			final LineIterator revListFileIterator = FileUtils.getLineIterator(getRevListFile());
			final LineIterator decorateListIterator = FileUtils.getLineIterator(getDecorateListFile());
			
			final LineIterator lsRemoteListIterator = FileUtils.getLineIterator(getLsRemoteFile());
			while (lsRemoteListIterator.hasNext()) {
				final String[] lsRemote = lsRemoteListIterator.next().split("\\s+");
				String branchName = lsRemote[1];
				if (branchName.endsWith("^{}")) {
					continue;
				}
				if (branchName.startsWith("refs/heads/")) {
					branchName = branchName.substring(11);
					this.branches.put(lsRemote[0], branchFactory.getBranch(branchName));
					if (Logger.logDebug()) {
						Logger.debug("Storing branch reference " + branchName + " along with associated commit id "
						        + lsRemote[0]);
					}
				}
			}
			
			final List<String> mergeTransactions = getMerges();
			
			final List<RevDependency> depList = new LinkedList<RevDependency>();
			
			// merge files
			while ((revListFileIterator.hasNext()) && (decorateListIterator.hasNext())) {
				
				final String[] revFields = revListFileIterator.next().split("\\s");
				final String decoLine = decorateListIterator.next();
				final String decoId = decoLine.substring(0, 40).trim();
				final String decoration = decoLine.substring(40).trim();
				
				Condition.check(revFields.length > 0, "The fileds in the temporary csv filed should never be empty");
				Condition.check(revFields[0].trim().equals(decoId),
				                "the first field in the current temp csv line must equal the current tranaction id");
				
				// if decoration starts with "ref/remotes/" then it's a branch
				// name. Set branchName of current branch.
				// else if it starts with "refs/tags" it's a tag
				String branchName = null;
				final List<String> tagNames = new LinkedList<String>();
				if (!decoration.equals("")) {
					final List<RegexGroup> groups = tagRegex.find(decoration);
					if ((groups != null) && (groups.size() == 2)) {
						final String refString = groups.get(1).getMatch();
						final String[] refs = refString.split(",");
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
				
				final String revId = revFields[0].trim();
				final List<String> parents = new LinkedList<String>();
				for (int i = 1; i < revFields.length; ++i) {
					parents.add(revFields[i]);
				}
				
				if (!this.branches.containsKey(revId)) {
					if ((branchName == null) || branchName.equals("origin/HEAD") || branchName.equals("origin/master")) {
						this.branches.put(revId, branchFactory.getMasterBranch());
					} else {
						this.branches.put(revId, branchFactory.getBranch(branchName));
					}
				}
				
				Condition.check(this.branches.containsKey(revId),
				                "The current transaction id must be known and the branch it belongs to must be known too. If this is not the case somethig goes horribly wrong.");
				
				final RCSBranch commitBranch = this.branches.get(revId);
				this.branches.remove(revId);
				
				// // if this is a named branch change name of branch
				// if ((branchName != null) && (!commitBranch.isMasterBranch())) {
				// commitBranch.setName(branchName);
				// }
				
				if (parents.size() > 0) {
					final String parent = parents.get(0);
					if (!this.branches.containsKey(parent)) {
						this.branches.put(parent, commitBranch);
					} else {
						if (commitBranch.compareTo(this.branches.get(parent)) < 0) {
							this.branches.put(parent, commitBranch);
						}
					}
				}
				for (int i = 1; i < parents.size(); ++i) {
					final String parent = parents.get(i);
					
					if (!this.branches.containsKey(parent)) {
						final RCSBranch newBranch = branchFactory.getBranch(parent + "Branch");
						if (newBranch.getParent() == null) {
							newBranch.setParent(commitBranch);
						}
						this.branches.put(parent, newBranch);
					}
					
					// set transaction to be merge transaction for new
					// branch
					this.branches.get(parent).addMergedIn(revId);
				}
				
				// if transaction is a merge, mark it as a merge
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
		} catch (final Exception e) {
			throw new UnrecoverableError("Could not initialize DependencyIterator for Git repo."
			        + FileUtils.lineSeparator + e.getMessage(), e);
		}
	}
	
	private File getDecorateListFile() throws IOException {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
		                                                                              "--branches", "--decorate=full",
		                                                                              "--remotes", "--encoding=UTF-8",
		                                                                              "--pretty=format:%H %d",
		                                                                              "--topo-order", this.revision },
		                                                                      this.cloneDir, null,
		                                                                      new HashMap<String, String>(),
		                                                                      GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError(
			                             "Could not initialize DependencyIterator for Git repo. Could not get decorateList");
		}
		final File decorateListFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		final BufferedWriter decorateListWriter = new BufferedWriter(new FileWriter(decorateListFile));
		for (final String line : response.getSecond()) {
			decorateListWriter.write(line);
			decorateListWriter.write(FileUtils.lineSeparator);
		}
		decorateListWriter.close();
		return decorateListFile;
	}
	
	private File getLsRemoteFile() throws IOException {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "ls-remote", "." },
		                                                                      this.cloneDir, null,
		                                                                      new HashMap<String, String>(),
		                                                                      GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError(
			                             "Could not initialize DependencyIterator for Git repo: could not get ls-remote.");
		}
		final File revListFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		final BufferedWriter revListWriter = new BufferedWriter(new FileWriter(revListFile));
		for (final String line : response.getSecond()) {
			revListWriter.write(line);
			revListWriter.write(FileUtils.lineSeparator);
		}
		revListWriter.close();
		return revListFile;
	}
	
	private List<String> getMerges() {
		/*
		 * get merges using "git log --decorate=full --branches --remotes --pretty=format:"%H" --merges". Whenever you
		 * see such a merge, mark the merge as closing transaction iff not already set and branch gets closed at all
		 * (see above).
		 */
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "log",
		                                                                              "--branches", "--decorate=full",
		                                                                              "--remotes", "--encoding=UTF-8",
		                                                                              "--pretty=format:%H", "--merges",
		                                                                              "--topo-order", this.revision },
		                                                                      this.cloneDir, null,
		                                                                      new HashMap<String, String>(),
		                                                                      GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not fetch list of transactions merging branches.");
		}
		return response.getSecond();
	}
	
	private File getRevListFile() throws IOException {
		final Tuple<Integer, List<String>> response = CommandExecutor.execute("git", new String[] { "rev-list",
		                                                                              "--encoding=UTF-8", "--parents",
		                                                                              "--branches", "--remotes",
		                                                                              "--topo-order", this.revision },
		                                                                      this.cloneDir, null,
		                                                                      new HashMap<String, String>(),
		                                                                      GitRepository.charset);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError(
			                             "Could not initialize DependencyIterator for Git repo: could not get revList using revision"
			                                     + this.revision + ".");
		}
		final File revListFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		final BufferedWriter revListWriter = new BufferedWriter(new FileWriter(revListFile));
		for (final String line : response.getSecond()) {
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
