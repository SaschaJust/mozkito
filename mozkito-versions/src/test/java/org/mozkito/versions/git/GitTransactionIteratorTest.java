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
package org.mozkito.versions.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.Before;
import org.junit.Test;

import org.mozkito.testing.VersionsTest;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.versions.RepositoryType;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;

/**
 * @author "Kim Herzig <herzig@cs.uni-saarland.de>"
 * 
 */
@RepositorySetting (id = "testGit", type = RepositoryType.GIT, uri = "testGit.zip")
public class GitTransactionIteratorTest extends VersionsTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The repo. */
	private GitRepository                     repo;
	
	/** The transaction map. */
	private final Map<String, ChangeSet> transactionMap = new HashMap<String, ChangeSet>();
	
	/** The branch map. */
	private final Map<String, Branch>      branchMap      = new HashMap<String, Branch>();
	
	/**
	 * Before class.
	 * 
	 * @throws IOException
	 * @throws RepositoryOperationException
	 */
	@Before
	public void setup() throws IOException, RepositoryOperationException {
		assertTrue(getRepositories().containsKey("testGit"));
		this.repo = (GitRepository) getRepositories().get("testGit");
		this.transactionMap.clear();
		this.branchMap.clear();
		
		final Iterator<LogEntry> log = this.repo.log(this.repo.getFirstRevisionId(), this.repo.getEndRevision(), 100);
		
		while (log.hasNext()) {
			final LogEntry logEntry = log.next();
			final ChangeSet changeset = new ChangeSet(logEntry.getRevision(), logEntry.getMessage(),
			                                                         logEntry.getDateTime(), logEntry.getAuthor(),
			                                                         logEntry.getOriginalId());
			this.transactionMap.put(logEntry.getRevision(), changeset);
		}
		
		final RevDependencyGraph revDepGraph = this.repo.getRevDependencyGraph();
		for (final ChangeSet changeset : this.transactionMap.values()) {
			final String hash = changeset.getId();
			
			if (!revDepGraph.existsVertex(hash)) {
				throw new UnrecoverableError("RevDependencyGraph does not contain transaction " + hash);
			}
			
			// set parents
			final String branchParentHash = revDepGraph.getBranchParent(hash);
			if (branchParentHash != null) {
				final ChangeSet branchParent = this.transactionMap.get(branchParentHash);
				changeset.setBranchParent(branchParent);
			}
			final String mergeParentHash = revDepGraph.getMergeParent(hash);
			if (mergeParentHash != null) {
				final ChangeSet mergeParent = this.transactionMap.get(mergeParentHash);
				changeset.setMergeParent(mergeParent);
			}
			
			// set tags
			final Set<String> tags = revDepGraph.getTags(hash);
			if (tags != null) {
				changeset.addAllTags(tags);
			}
			
			// persist branches
			final String branchName = revDepGraph.isBranchHead(hash);
			if (branchName != null) {
				final Branch rCSBranch = this.repo.getBranchFactory().getBranch(branchName);
				rCSBranch.setHead(changeset);
				this.branchMap.put(branchName, rCSBranch);
			}
		}
		
	}
	
	/**
	 * Test maintenance branch.
	 * 
	 * @throws IOException
	 * @throws RepositoryOperationException
	 */
	@Test
	public void testMaintenanceBranch() throws IOException, RepositoryOperationException {
		final Iterator<String> transactions = this.repo.getRevDependencyGraph()
		                                               .getBranchTransactions("origin/maintenance").iterator();
		
		assertTrue(transactions.hasNext());
		assertEquals("67635fe9efeb2fd3751df9ea67650c71e59e3df1", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("9be561b3657e2b1da2b09d675dddd5f45c47f57c", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("637acf68104e7bdff8235fb2e1a254300ffea3cb", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("376adc0f9371129a76766f8030f2e576165358c1", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("1ac6aaa05eb6d55939b20e70ec818bb413417757", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", transactions.next());
		assertFalse(transactions.hasNext());
	}
	
	/**
	 * Test master branch.
	 * 
	 * @throws IOException
	 * @throws RepositoryOperationException
	 */
	@Test
	public void testMasterBranch() throws IOException, RepositoryOperationException {
		final Iterator<String> transactions = this.repo.getRevDependencyGraph()
		                                               .getBranchTransactions(Branch.MASTER_BRANCH_NAME).iterator();
		assertTrue(transactions.hasNext());
		assertEquals("96a9f105774b50f1fa3361212c4d12ae057a4285", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("fe56f365f798c3742bac5e56f5ff30eca4f622c6", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("9be561b3657e2b1da2b09d675dddd5f45c47f57c", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("637acf68104e7bdff8235fb2e1a254300ffea3cb", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("376adc0f9371129a76766f8030f2e576165358c1", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("1ac6aaa05eb6d55939b20e70ec818bb413417757", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", transactions.next());
		assertTrue(transactions.hasNext());
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", transactions.next());
		assertFalse(transactions.hasNext());
	}
}
