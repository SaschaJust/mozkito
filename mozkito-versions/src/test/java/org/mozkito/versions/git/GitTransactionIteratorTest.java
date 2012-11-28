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
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.IRevDependencyGraph;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.model.RCSBranch;
import org.mozkito.versions.model.RCSTransaction;

public class GitTransactionIteratorTest {
	
	private static BranchFactory               branchFactory;
	private static GitRepository               repo;
	private static Map<String, RCSTransaction> transactionMap = new HashMap<String, RCSTransaction>();
	private static Map<String, RCSBranch>      branchMap      = new HashMap<String, RCSBranch>();
	
	@BeforeClass
	public static void beforeClass() {
		try {
			final URL zipURL = GitRevDependencyGraphTest.class.getResource(FileUtils.fileSeparator + "testGit.zip");
			assert (zipURL != null);
			
			final File bareDir = new File(
			                              (new URL(zipURL.toString()
			                                             .substring(0,
			                                                        zipURL.toString()
			                                                              .lastIndexOf(FileUtils.fileSeparator)))).toURI());
			FileUtils.unzip(new File(zipURL.toURI()), bareDir);
			if ((!bareDir.exists()) || (!bareDir.isDirectory())) {
				fail();
			}
			branchFactory = new BranchFactory(null);
			repo = new GitRepository();
			repo.setup(new URI("file://" + bareDir.getAbsolutePath() + FileUtils.fileSeparator + "testGit"),
			           branchFactory, null, "master");
		} catch (final Exception e) {
			fail();
		}
		final Iterator<LogEntry> log = repo.log(repo.getFirstRevisionId(), repo.getEndRevision(), 100);
		
		while (log.hasNext()) {
			final LogEntry logEntry = log.next();
			final RCSTransaction rcsTransaction = new RCSTransaction(logEntry.getRevision(), logEntry.getMessage(),
			                                                         logEntry.getDateTime(), logEntry.getAuthor(),
			                                                         logEntry.getOriginalId());
			transactionMap.put(logEntry.getRevision(), rcsTransaction);
		}
		
		final IRevDependencyGraph revDepGraph = repo.getRevDependencyGraph();
		for (final RCSTransaction rcsTransaction : transactionMap.values()) {
			final String hash = rcsTransaction.getId();
			
			if (!revDepGraph.hasVertex(hash)) {
				throw new UnrecoverableError("RevDependencyGraph does not contain transaction " + hash);
			}
			
			// set parents
			final String branchParentHash = revDepGraph.getBranchParent(hash);
			if (branchParentHash != null) {
				final RCSTransaction branchParent = transactionMap.get(branchParentHash);
				rcsTransaction.setBranchParent(branchParent);
			}
			final String mergeParentHash = revDepGraph.getMergeParent(hash);
			if (mergeParentHash != null) {
				final RCSTransaction mergeParent = transactionMap.get(mergeParentHash);
				rcsTransaction.setMergeParent(mergeParent);
			}
			
			// set tags
			final Set<String> tags = revDepGraph.getTags(hash);
			if (tags != null) {
				rcsTransaction.addAllTags(tags);
			}
			
			// persist branches
			final String branchName = revDepGraph.isBranchHead(hash);
			if (branchName != null) {
				final RCSBranch branch = branchFactory.getBranch(branchName);
				branch.setHead(rcsTransaction);
				branchMap.put(branchName, branch);
			}
		}
		
	}
	
	@Test
	public void testMaintenanceBranch() {
		final Iterator<String> transactions = repo.getRevDependencyGraph().getBranchTransactions("origin/maintenance")
		                                          .iterator();
		
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
	
	@Test
	public void testMasterBranch() {
		final Iterator<String> transactions = repo.getRevDependencyGraph()
		                                          .getBranchTransactions(RCSBranch.MASTER_BRANCH_NAME).iterator();
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
