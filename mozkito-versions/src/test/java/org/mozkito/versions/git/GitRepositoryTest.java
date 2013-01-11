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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.regex.Match;

import org.junit.Before;
import org.junit.Test;
import org.mozkito.testing.VersionsTest;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.versions.RepositoryType;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.exceptions.RepositoryOperationException;

import difflib.Delta;

/**
 * The Class GitRepositoryTest.
 */
@RepositorySetting (id = "testGit", type = RepositoryType.GIT, uri = "testGit.zip")
public class GitRepositoryTest extends VersionsTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The repo. */
	private GitRepository repo;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		assertTrue(getRepositories().containsKey("testGit"));
		this.repo = (GitRepository) getRepositories().get("testGit");
	}
	
	/**
	 * Test annotate.
	 */
	@Test
	public void testAnnotate() {
		List<AnnotationEntry> annotate = this.repo.annotate("3.txt", "637acf68104e7bdff8235fb2e1a254300ffea3cb");
		
		assertEquals(3, annotate.size());
		AnnotationEntry line0 = annotate.get(0);
		assertNotNull(line0);
		assertFalse(line0.hasAlternativePath());
		assertNull(line0.getAlternativeFilePath());
		assertEquals("changing 3", line0.getLine());
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-11-22 20:30:52 +0100").isEqual(line0.getTimestamp()));
		assertEquals("Kim Herzig", line0.getUsername());
		
		final AnnotationEntry line1 = annotate.get(1);
		assertNotNull(line1);
		assertFalse(line1.hasAlternativePath());
		assertNull(line1.getAlternativeFilePath());
		assertEquals("changing 3", line1.getLine());
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", line1.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line1.getTimestamp()));
		assertEquals("Kim Herzig", line1.getUsername());
		
		final AnnotationEntry line2 = annotate.get(2);
		assertNotNull(line2);
		assertFalse(line2.hasAlternativePath());
		assertNull(line2.getAlternativeFilePath());
		assertEquals("changing 3", line2.getLine());
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", line2.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line2.getTimestamp()));
		assertEquals("Kim Herzig", line2.getUsername());
		
		annotate = this.repo.annotate("3_renamed.txt", "96a9f105774b50f1fa3361212c4d12ae057a4285");
		line0 = annotate.get(0);
		assertNotNull(line0);
		assertEquals(true, line0.hasAlternativePath());
		assertEquals("3.txt", line0.getAlternativeFilePath());
		assertEquals("changing 3", line0.getLine());
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-11-22 20:30:52 +0100").isEqual(line0.getTimestamp()));
		assertEquals("Kim Herzig", line0.getUsername());
		
	}
	
	/**
	 * Test checkout path fail.
	 */
	@Test
	public void testCheckoutPathFail() {
		assertTrue(this.repo.checkoutPath("3.txt", "96a9f105774b50f1fa3361212c4d12ae057a4285") == null);
	}
	
	/**
	 * Test checkout path success.
	 */
	@Test
	public void testCheckoutPathSuccess() {
		final File file = this.repo.checkoutPath("3.txt", "637acf68104e7bdff8235fb2e1a254300ffea3cb");
		assertNotNull(file);
		assertTrue(file.exists());
	}
	
	/**
	 * Test diff.
	 */
	@Test
	public void testDiff() {
		final Collection<Delta> diff = this.repo.diff("3.txt", "637acf68104e7bdff8235fb2e1a254300ffea3cb",
		                                              "9be561b3657e2b1da2b09d675dddd5f45c47f57c");
		assertEquals(1, diff.size());
		final Delta delta = diff.iterator().next();
		assertEquals(0, delta.getOriginal().getSize());
		assertEquals(3, delta.getRevised().getSize());
		for (final Object line : delta.getRevised().getLines()) {
			assertEquals("changing 3", line.toString());
		}
	}
	
	/**
	 * Test former path regex.
	 */
	@Test
	public void testFormerPathRegex() {
		final String line = "R100    hello.py        python.py";
		final Match found = GitRepository.FORMER_PATH_REGEX.find(line);
		assertEquals(1, found.getGroupCount());
		assertEquals("hello.py", GitRepository.FORMER_PATH_REGEX.getGroup("result"));
	}
	
	/**
	 * Test get transaction id.
	 */
	@Test
	public void testGetChangeSetId() {
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", this.repo.getChangeSetId(0));
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", this.repo.getChangeSetId(6));
		assertEquals("fe56f365f798c3742bac5e56f5ff30eca4f622c6", this.repo.getChangeSetId(18));
		assertEquals("96a9f105774b50f1fa3361212c4d12ae057a4285", this.repo.getChangeSetId(19));
	}
	
	/**
	 * Test get changes paths.
	 */
	@Test
	public void testGetChangesPaths() {
		Map<String, ChangeType> changedPaths = this.repo.getChangedPaths("376adc0f9371129a76766f8030f2e576165358c1");
		assertEquals(1, changedPaths.size());
		assertTrue(changedPaths.containsKey("/1.txt"));
		assertEquals(ChangeType.Modified, changedPaths.get("/1.txt"));
		
		changedPaths = this.repo.getChangedPaths("96a9f105774b50f1fa3361212c4d12ae057a4285");
		assertEquals(2, changedPaths.size());
		assertTrue(changedPaths.containsKey("/3.txt"));
		assertEquals(ChangeType.Deleted, changedPaths.get("/3.txt"));
		assertTrue(changedPaths.containsKey("/3_renamed.txt"));
		assertEquals(ChangeType.Added, changedPaths.get("/3_renamed.txt"));
	}
	
	/**
	 * Test get former path name.
	 */
	@Test
	public void testGetFormerPathName() {
		String formerPathName = this.repo.getFormerPathName("96a9f105774b50f1fa3361212c4d12ae057a4285", "3_renamed.txt");
		assertNotNull(formerPathName);
		assertEquals("3.txt", formerPathName);
		
		formerPathName = this.repo.getFormerPathName("96a9f105774b50f1fa3361212c4d12ae057a4285", "1.txt");
		assertNull(formerPathName);
	}
	
	/**
	 * Test get log.
	 *
	 * @throws RepositoryOperationException the repository operation exception
	 */
	@Test
	public void testGetLog() throws RepositoryOperationException {
		final List<LogEntry> log = this.repo.log("98d5c40ef3c14503a472ba4133ae3529c7578e30",
		                                         "376adc0f9371129a76766f8030f2e576165358c1");
		assertEquals(6, log.size());
		LogEntry logEntry = log.get(0);
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2010-11-22 20:26:24 +0100")));
		assertEquals("changing 1.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(1);
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0", logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2010-11-22 20:32:19 +0100")));
		assertEquals("Merge file:///tmp/testGit into testBranchName", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(2);
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2010-11-22 20:34:03 +0100")));
		assertEquals("Merge branch 'testBranchName'", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(3);
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2011-01-20 12:01:23 +0100")));
		assertEquals("changing 1.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(4);
		assertEquals("1ac6aaa05eb6d55939b20e70ec818bb413417757", logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2011-01-20 12:02:30 +0100")));
		assertEquals("chaging 2.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
		logEntry = log.get(5);
		assertEquals("376adc0f9371129a76766f8030f2e576165358c1", logEntry.getRevision());
		assertTrue(logEntry.getDateTime().isEqual(DateTimeUtils.parseDate("2011-01-20 12:03:59 +0100")));
		assertEquals("changing 1.txt", logEntry.getMessage());
		assertTrue(logEntry.getOriginalId().isEmpty());
		
	}
	
	/**
	 * Test get transaction count.
	 */
	@Test
	public void testGetTransactionCount() {
		assertEquals(20, this.repo.getChangeSetCount());
	}
	
	/**
	 * Test get transaction index.
	 */
	@Test
	public void testGetTransactionIndex() {
		assertEquals(19, this.repo.getChangeSetIndex("HEAD"));
		assertEquals(6, this.repo.getChangeSetIndex("98d5c40ef3c14503a472ba4133ae3529c7578e30"));
	}
	
	/**
	 * Test saschas anderer mega regex.
	 */
	@Test
	public void testSaschasAndererMegaRegex() {
		final String line = "^f554664a346629dc2b839f7292d06bad2db4aec hello.py (Mike Donaghy 2007-11-20 15:28:39 -0500 1) #!/usr/bin/env python";
		assertTrue(GitRepository.REGEX.matchesFull(line));
	}
	
}
