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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.regex.Match;

import org.junit.Before;
import org.junit.Test;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;

import difflib.Delta;

public class GitRepositoryTest {
	
	private GitRepository repo;
	
	@Before
	public void setup() {
		final URL zipURL = GitRepositoryTest.class.getResource(FileUtils.fileSeparator + "testGit.zip");
		assert (zipURL != null);
		try {
			final File bareDir = new File(
			                              (new URL(zipURL.toString()
			                                             .substring(0,
			                                                        zipURL.toString()
			                                                              .lastIndexOf(FileUtils.fileSeparator)))).toURI());
			FileUtils.unzip(new File(zipURL.toURI()), bareDir);
			if ((!bareDir.exists()) || (!bareDir.isDirectory())) {
				fail();
			}
			final BranchFactory branchFactory = new BranchFactory(null);
			this.repo = new GitRepository();
			this.repo.setup(new URI("file://" + bareDir.getAbsolutePath() + FileUtils.fileSeparator + "testGit"),
			                branchFactory, null, "master");
		} catch (final Exception e) {
			fail();
		}
	}
	
	@Test
	public void testAnnotate() {
		List<AnnotationEntry> annotate = this.repo.annotate("3.txt", "637acf68104e7bdff8235fb2e1a254300ffea3cb");
		
		assertEquals(3, annotate.size());
		AnnotationEntry line0 = annotate.get(0);
		assert (line0 != null);
		assertFalse(line0.hasAlternativePath());
		assert (line0.getAlternativeFilePath() == null);
		assertEquals("changing 3", line0.getLine());
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-11-22 20:30:52 +0100").isEqual(line0.getTimestamp()));
		assertEquals("Kim Herzig", line0.getUsername());
		
		final AnnotationEntry line1 = annotate.get(1);
		assert (line1 != null);
		assertFalse(line1.hasAlternativePath());
		assert (line1.getAlternativeFilePath() == null);
		assertEquals("changing 3", line0.getLine());
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", line1.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line1.getTimestamp()));
		assertEquals("Kim Herzig", line1.getUsername());
		
		final AnnotationEntry line2 = annotate.get(2);
		assert (line2 != null);
		assertFalse(line2.hasAlternativePath());
		assert (line2.getAlternativeFilePath() == null);
		assertEquals("changing 3", line2.getLine());
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", line2.getRevision());
		assertTrue(DateTimeUtils.parseDate("2011-01-20 12:03:24 +0100").isEqual(line2.getTimestamp()));
		assertEquals("Kim Herzig", line2.getUsername());
		
		annotate = this.repo.annotate("3_renamed.txt", "96a9f105774b50f1fa3361212c4d12ae057a4285");
		line0 = annotate.get(0);
		assert (line0 != null);
		assertTrue(line0.hasAlternativePath());
		assert (line0.getAlternativeFilePath() == "3.txt");
		assertEquals("changing 3", line0.getLine());
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", line0.getRevision());
		assertTrue(DateTimeUtils.parseDate("2010-11-22 20:30:52 +0100").isEqual(line0.getTimestamp()));
		assertEquals("Kim Herzig", line0.getUsername());
		
	}
	
	@Test
	public void testCheckoutPathFail() {
		try {
			this.repo.checkoutPath("3.txt", "96a9f105774b50f1fa3361212c4d12ae057a4285");
			fail();
		} catch (final UnrecoverableError e1) {
			
		}
		
	}
	
	@Test
	public void testCheckoutPathSuccess() {
		final File file = this.repo.checkoutPath("3.txt", "637acf68104e7bdff8235fb2e1a254300ffea3cb");
		assert (file != null);
		assert (file.exists());
	}
	
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
	
	@Test
	public void testFormerPathRegex() {
		final String line = "R100    hello.py        python.py";
		final Match found = GitRepository.FORMER_PATH_REGEX.find(line);
		assertEquals(1, found.getGroupCount());
		assertEquals("hello.py", GitRepository.FORMER_PATH_REGEX.getGroup("result"));
	}
	
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
	
	@Test
	public void testGetFormerPathName() {
		String formerPathName = this.repo.getFormerPathName("96a9f105774b50f1fa3361212c4d12ae057a4285", "3_renamed.txt");
		assertTrue(formerPathName != null);
		assertEquals("3.txt", formerPathName);
		
		formerPathName = this.repo.getFormerPathName("96a9f105774b50f1fa3361212c4d12ae057a4285", "1.txt");
		assertTrue(formerPathName == null);
	}
	
	@Test
	public void testGetTransactionCount() {
		assertEquals(20, this.repo.getTransactionCount());
	}
	
	@Test
	public void testGetTransactionId() {
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", this.repo.getTransactionId(0));
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", this.repo.getTransactionId(6));
		assertEquals("fe56f365f798c3742bac5e56f5ff30eca4f622c6", this.repo.getTransactionId(18));
		assertEquals("96a9f105774b50f1fa3361212c4d12ae057a4285", this.repo.getTransactionId(19));
	}
	
	@Test
	public void testSaschasAndererMegaRegex() {
		final String line = "^f554664a346629dc2b839f7292d06bad2db4aec hello.py (Mike Donaghy 2007-11-20 15:28:39 -0500 1) #!/usr/bin/env python";
		assertTrue(GitRepository.REGEX.matchesFull(line));
	}
	
}
