/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.versions.subversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.git.GitRepositoryTest;
import org.mozkito.versions.model.RCSBranch;

import difflib.Delta;

/**
 * The Class SubversionRepositoryTest.
 */
public class SubversionRepositoryTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The repo. */
	private SubversionRepository repo;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		final URL zipURL = GitRepositoryTest.class.getResource(FileUtils.fileSeparator + "testSvn.zip");
		assertNotNull(zipURL);
		try {
			final File tmpDir = FileUtils.createRandomDir("mozkito", "testSvn", FileShutdownAction.DELETE);
			FileUtils.unzip(new File(zipURL.toURI()), tmpDir);
			if ((!tmpDir.exists()) || (!tmpDir.isDirectory())) {
				fail();
			}
			final BranchFactory branchFactory = new BranchFactory(null);
			this.repo = new SubversionRepository();
			this.repo.setup(new URI("file://" + tmpDir.getAbsolutePath() + FileUtils.fileSeparator + "testSvn"),
			                branchFactory, null, "master");
		} catch (final Exception e) {
			fail();
		}
	}
	
	/**
	 * Test annotate.
	 */
	@Test
	public void testAnnotate() {
		final List<AnnotationEntry> annotate = this.repo.annotate("file_1", "9");
		
		assertEquals(4, annotate.size());
		final AnnotationEntry line0 = annotate.get(0);
		assertNotNull(line0);
		assertFalse(line0.hasAlternativePath());
		assertEquals("file_1 content", line0.getLine());
		assertEquals("2", line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:35:15 +0200").isBefore(line0.getTimestamp()));
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:35:16 +0200").isAfter(line0.getTimestamp()));
		assertEquals("just", line0.getUsername());
		
		final AnnotationEntry line1 = annotate.get(1);
		assertNotNull(line1);
		assertFalse(line1.hasAlternativePath());
		assertEquals("Adding test change 1 to file_1", line1.getLine());
		assertEquals("7", line1.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:42:39 +0200").isBefore(line1.getTimestamp()));
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:42:40 +0200").isAfter(line1.getTimestamp()));
		assertEquals("just", line1.getUsername());
		
		final AnnotationEntry line2 = annotate.get(2);
		assertNotNull(line2);
		assertFalse(line2.hasAlternativePath());
		assertEquals("Adding test change 2 to file_1", line2.getLine());
		assertEquals("8", line2.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:42:40 +0200").isBefore(line2.getTimestamp()));
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:42:41 +0200").isAfter(line2.getTimestamp()));
		assertEquals("just", line2.getUsername());
		
		final AnnotationEntry line3 = annotate.get(3);
		assertNotNull(line3);
		assertFalse(line3.hasAlternativePath());
		assertEquals("Adding test change 3 to file_1", line3.getLine());
		assertEquals("9", line3.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:42:41 +0200").isBefore(line3.getTimestamp()));
		assertTrue(DateTimeUtils.parseDate("2010-10-22 16:42:42 +0200").isAfter(line3.getTimestamp()));
		assertEquals("just", line3.getUsername());
		
	}
	
	/**
	 * Test checkout path fail.
	 */
	@Test
	public void testCheckoutPathFail() {
		assertTrue(this.repo.checkoutPath("file_8989", "17") == null);
	}
	
	/**
	 * Test checkout path success.
	 */
	@Test
	public void testCheckoutPathSuccess() {
		final File file = this.repo.checkoutPath("dir_a/file_3_dir_a", "16");
		assertNotNull(file);
		assertTrue(file.exists());
	}
	
	/**
	 * Test diff.
	 */
	@Test
	public void testDiff() {
		final Collection<Delta> diff = this.repo.diff("file_1", "9", "11");
		assertEquals(1, diff.size());
		final Delta delta = diff.iterator().next();
		assertEquals(0, delta.getOriginal().getSize());
		assertEquals(2, delta.getRevised().getSize());
		@SuppressWarnings ("unchecked")
		final List<String> lines = (List<String>) delta.getRevised().getLines();
		assertEquals("Adding test change 4 to file_1", lines.get(0));
		assertEquals("Adding test change 5 to file_1", lines.get(1));
	}
	
	/**
	 * Test get changes paths.
	 */
	@Test
	public void testGetChangesPaths() {
		Map<String, ChangeType> changedPaths = this.repo.getChangedPaths("2");
		assertEquals(3, changedPaths.size());
		assertTrue(changedPaths.containsKey("/file_1"));
		assertEquals(ChangeType.Modified, changedPaths.get("/file_1"));
		assertTrue(changedPaths.containsKey("/file_2"));
		assertEquals(ChangeType.Added, changedPaths.get("/file_2"));
		assertTrue(changedPaths.containsKey("/file_3"));
		assertEquals(ChangeType.Added, changedPaths.get("/file_3"));
		
		changedPaths = this.repo.getChangedPaths("3");
		assertEquals(3, changedPaths.size());
		assertTrue(changedPaths.containsKey("/dir_a"));
		assertEquals(ChangeType.Added, changedPaths.get("/dir_a"));
		assertTrue(changedPaths.containsKey("/dir_a/file_2_dir_a"));
		assertEquals(ChangeType.Renamed, changedPaths.get("/dir_a/file_2_dir_a"));
		assertTrue(changedPaths.containsKey("/file_2"));
		assertEquals(ChangeType.Deleted, changedPaths.get("/file_2"));
		
		changedPaths = this.repo.getChangedPaths("18");
		assertEquals(2, changedPaths.size());
		assertTrue(changedPaths.containsKey("/file_1_renamed"));
		assertEquals(ChangeType.Renamed, changedPaths.get("/file_1_renamed"));
		assertTrue(changedPaths.containsKey("/file_1"));
		assertEquals(ChangeType.Deleted, changedPaths.get("/file_1"));
		
	}
	
	/**
	 * Test get end revision id.
	 */
	@Test
	public void testGetEndRevisionId() {
		assertEquals("18", this.repo.getEndRevision());
	}
	
	/**
	 * Test get first revision id.
	 */
	@Test
	public void testGetFirstRevisionId() {
		assertEquals("1", this.repo.getFirstRevisionId());
	}
	
	/**
	 * Test get former path name.
	 */
	@Test
	public void testGetFormerPathName() {
		String formerPathName = this.repo.getFormerPathName("3", "/dir_a/file_2_dir_a");
		assertNotNull(formerPathName);
		assertEquals("/file_2", formerPathName);
		
		formerPathName = this.repo.getFormerPathName("6", "file_3");
		assertNull(formerPathName);
		
		formerPathName = this.repo.getFormerPathName("18", "/file_1_renamed");
		assertNotNull(formerPathName);
		assertEquals("/file_1", formerPathName);
	}
	
	/**
	 * Test get log.
	 */
	@Test
	public void testGetLog() {
		final List<LogEntry> log = this.repo.log("6", "11");
		assertEquals(6, log.size());
		LogEntry logEntry = log.get(0);
		assertEquals("6", logEntry.getRevision());
		DateTime timestamp = DateTimeUtils.parseDate("2010-10-22 16:40:19 +0200");
		assertTrue(timestamp.isBefore(logEntry.getDateTime()));
		assertTrue(timestamp.plusSeconds(1).isAfter(logEntry.getDateTime()));
		assertEquals("moving file_3 to dir_a/file_3_dir_a" + FileUtils.lineSeparator
		        + "changing content of dir_a/file_3_dir_a to file_3 content changed", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(1);
		assertEquals("7", logEntry.getRevision());
		timestamp = DateTimeUtils.parseDate("2010-10-22 16:42:39 +0200");
		assertTrue(timestamp.isBefore(logEntry.getDateTime()));
		assertTrue(timestamp.plusSeconds(1).isAfter(logEntry.getDateTime()));
		assertEquals("applying change test 1 to file_1", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(2);
		assertEquals("8", logEntry.getRevision());
		timestamp = DateTimeUtils.parseDate("2010-10-22 16:42:40 +0200");
		assertTrue(timestamp.isBefore(logEntry.getDateTime()));
		assertTrue(timestamp.plusSeconds(1).isAfter(logEntry.getDateTime()));
		assertEquals("applying change test 2 to file_1", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(3);
		assertEquals("9", logEntry.getRevision());
		timestamp = DateTimeUtils.parseDate("2010-10-22 16:42:41 +0200");
		assertTrue(timestamp.isBefore(logEntry.getDateTime()));
		assertTrue(timestamp.plusSeconds(1).isAfter(logEntry.getDateTime()));
		assertEquals("applying change test 3 to file_1", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(4);
		assertEquals("10", logEntry.getRevision());
		timestamp = DateTimeUtils.parseDate("2010-10-22 16:42:42 +0200");
		assertTrue(timestamp.isBefore(logEntry.getDateTime()));
		assertTrue(timestamp.plusSeconds(1).isAfter(logEntry.getDateTime()));
		assertEquals("applying change test 4 to file_1", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(5);
		assertEquals("11", logEntry.getRevision());
		timestamp = DateTimeUtils.parseDate("2010-10-22 16:42:43 +0200");
		assertTrue(timestamp.isBefore(logEntry.getDateTime()));
		assertTrue(timestamp.plusSeconds(1).isAfter(logEntry.getDateTime()));
		assertEquals("applying change test 5 to file_1", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
	}
	
	/**
	 * Test get rev dependency graph.
	 */
	@Test
	public void testGetRevDependencyGraph() {
		final RevDependencyGraph revDepG = this.repo.getRevDependencyGraph();
		assertNotNull(revDepG);
		final Set<String> branches = revDepG.getBranches();
		assertEquals(1, branches.size());
		final Iterator<String> masterIter = revDepG.getBranchTransactions(RCSBranch.MASTER_BRANCH_NAME).iterator();
		assertTrue(masterIter.hasNext());
		assertEquals("18", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("17", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("16", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("15", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("14", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("13", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("12", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("11", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("10", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("9", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("8", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("7", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("6", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("5", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("4", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("3", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("2", masterIter.next());
		assertTrue(masterIter.hasNext());
		assertEquals("1", masterIter.next());
		assertFalse(masterIter.hasNext());
	}
	
	/**
	 * Test get transaction count.
	 */
	@Test
	public void testGetTransactionCount() {
		assertEquals(18, this.repo.getTransactionCount());
	}
	
	/**
	 * Test get transaction id.
	 */
	@Test
	public void testGetTransactionId() {
		assertEquals("1", this.repo.getTransactionId(0));
		assertEquals("7", this.repo.getTransactionId(6));
		assertEquals("12", this.repo.getTransactionId(11));
		assertEquals("17", this.repo.getTransactionId(16));
		assertTrue(this.repo.getTransactionId(18) == null);
	}
	
	/**
	 * Test get transaction index.
	 */
	@Test
	public void testGetTransactionIndex() {
		assertEquals(17, this.repo.getTransactionIndex("HEAD"));
		assertEquals(5, this.repo.getTransactionIndex("6"));
	}
}
