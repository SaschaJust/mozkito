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
package org.mozkito.versions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozkito.exceptions.UnregisteredRepositoryTypeException;
import org.mozkito.testing.MozkitoTest;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.testing.annotation.RepositorySettings;
import org.mozkito.testing.annotation.processors.RepositorySettingsProcessor;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;

import difflib.Delta;

/**
 * The Class RepositoryTest.
 */
@RepositorySettings ({ @RepositorySetting (type = RepositoryType.GIT, uri = "repotest.git.zip"),
        @RepositorySetting (type = RepositoryType.MERCURIAL, uri = "repotest.mercurial.zip"),
        @RepositorySetting (type = RepositoryType.SUBVERSION, uri = "repotest.subversion") })
public class Repository_MozkitoTest extends MozkitoTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The repositories. */
	private static List<Repository>         repositories = new LinkedList<Repository>();
	
	/** The repo map. */
	private static Map<RepositoryType, URI> repoMap;
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		repoMap = new HashMap<RepositoryType, URI>();
		
		for (final RepositoryType type : RepositoryType.values()) {
			final String pathName = RepositorySettingsProcessor.getPathName(Repository_MozkitoTest.class, type);
			if (pathName != null) {
				try {
					repoMap.put(type, new URI("file://" + pathName + File.separator + "repotest."
					        + type.name().toLowerCase()));
				} catch (final URISyntaxException e) {
					fail(e.getMessage());
				}
			} else {
				fail();
			}
			
			Repository repository = null;
			try {
				repository = RepositoryFactory.getRepositoryHandler(type).newInstance();
			} catch (final InstantiationException e1) {
				e1.printStackTrace();
				fail();
			} catch (final IllegalAccessException e1) {
				e1.printStackTrace();
				fail();
			} catch (final UnregisteredRepositoryTypeException e1) {
				e1.printStackTrace();
				fail();
			}
			assert (repository != null);
			repositories.add(repository);
			
			final File urlFile = new File(repoMap.get(type));
			
			try {
				repository.setup(urlFile.toURI(), new BranchFactory(null), null, "master");
			} catch (final Exception e) {
				System.err.println(e.getMessage());
				fail(e.getMessage());
			}
		}
		
	}
	
	/**
	 * Gets the date from string.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the date from string
	 */
	private static DateTime getDateFromString(final String timestamp) {
		final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
		return dtf.parseDateTime(timestamp);
	}
	
	/**
	 * Test annotate.
	 */
	@Test
	public void testAnnotate() {
		for (final Repository repository : repositories) {
			final List<AnnotationEntry> annotation = repository.annotate("dir_b/file_2_dir_a", repository.getHEAD());
			assertEquals(2, annotation.size());
			if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertEquals("2", annotation.get(0).getRevision());
				assertEquals("17", annotation.get(1).getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("b9aff3c08f90cbd42361da158fbbe979405fba70", annotation.get(0).getRevision());
				assertEquals("01bcd1a86fb7d47c977f41af6a3a8f2407ce9183", annotation.get(1).getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("7f1d2e6e6cffca9a8360af777254d05d9a26bc11", annotation.get(0).getRevision());
				assertEquals("a19f0b6e729adbf26b70a0e17f32453835fb50eb", annotation.get(1).getRevision());
			}
			assertEquals("file_2 content", annotation.get(0).getLine());
			assertEquals("test", annotation.get(1).getLine());
			
			assertEquals(getDateFromString("2010-10-22 14:35:15 +0000").getMillis(),
			             annotation.get(0).getTimestamp()
			                       .minusMillis(annotation.get(0).getTimestamp().getMillisOfSecond()).getMillis());
			assertEquals(getDateFromString("2010-10-22 14:53:06 +0000").getMillis(),
			             annotation.get(1).getTimestamp()
			                       .minusMillis(annotation.get(1).getTimestamp().getMillisOfSecond()).getMillis());
			
			assertEquals("just", annotation.get(0).getUsername());
			assertEquals("just", annotation.get(1).getUsername());
			
			if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertEquals(false, annotation.get(0).hasAlternativePath());
			} else {
				assertEquals(true, annotation.get(0).hasAlternativePath());
			}
			assertEquals(false, annotation.get(1).hasAlternativePath());
			
			if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertEquals(null, annotation.get(0).getAlternativeFilePath());
			} else {
				assertEquals("file_2", annotation.get(0).getAlternativeFilePath());
			}
			assertEquals(null, annotation.get(1).getAlternativeFilePath());
		}
	}
	
	/**
	 * Test checkout.
	 */
	@Test
	public void testCheckout() {
		for (final Repository repository : repositories) {
			final File checkoutPath = repository.checkoutPath("/", repository.getHEAD());
			
			assert (checkoutPath != null);
			
			final File dir_a = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_a");
			final File dir_b = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_b");
			final File file_1 = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "file_1");
			
			final File dir_a_file_3 = new File(dir_a.getAbsolutePath() + FileUtils.fileSeparator + "file_3_dir_a");
			final File dir_b_file_2 = new File(dir_b.getAbsolutePath() + FileUtils.fileSeparator + "file_2_dir_a");
			
			assertTrue(dir_a.exists());
			assertTrue(dir_b.exists());
			assertTrue(file_1.exists());
			
			assertTrue(dir_a.isDirectory());
			assertTrue(dir_b.isDirectory());
			assertTrue(file_1.isFile());
			
			assertTrue(dir_a_file_3.exists());
			assertTrue(dir_b_file_2.exists());
			
			assertTrue(dir_a_file_3.isFile());
			assertTrue(dir_b_file_2.isFile());
		}
	}
	
	/**
	 * Test checkout dir.
	 */
	@Test
	public void testCheckoutDir() {
		for (final Repository repository : repositories) {
			final File checkoutPath = repository.checkoutPath("/dir_a", repository.getHEAD());
			if (Logger.logDebug()) {
				Logger.debug("Child entries of checkout path: " + JavaUtils.arrayToString(checkoutPath.list()));
			}
			assertTrue(checkoutPath.exists());
			assertTrue(checkoutPath.isDirectory());
			
			final File dir_a = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_a");
			final File dir_b = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_b");
			final File file_1 = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "file_1");
			final File dir_a_file_3 = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator
			        + "file_3_dir_a");
			
			assertFalse(dir_a.exists());
			assertFalse(dir_b.exists());
			assertFalse(file_1.exists());
			assertTrue(dir_a_file_3.exists());
			assertTrue(dir_a_file_3.isFile());
		}
		
	}
	
	/**
	 * Test checkout file.
	 */
	@Test
	public void testCheckoutFile() {
		for (final Repository repository : repositories) {
			final File f = repository.checkoutPath("/dir_b/file_2_dir_a", repository.getHEAD());
			assertTrue(f != null);
			assertTrue(f.exists());
			assertTrue(f.isFile());
		}
	}
	
	/**
	 * Test diff.
	 */
	@Test
	public void testDiff() {
		for (final Repository repository : repositories) {
			final String id = repository.getTransactionId(11);
			final String parent = repository.getTransactionId(10);
			final Collection<Delta> diff = repository.diff("file_1", parent, id);
			assertEquals(1, diff.size());
			final Delta[] deltas = diff.toArray(new Delta[1]);
			assertEquals(0, deltas[0].getOriginal().getLines().size());
			assertEquals(9, deltas[0].getRevised().getLines().size());
		}
	}
	
	/**
	 * Test diff move.
	 */
	@Test
	public void testDiffMove() {
		for (final Repository repository : repositories) {
			final String id = repository.getTransactionId(3);
			final String parent = repository.getTransactionId(2);
			final Collection<Delta> diff = repository.diff("dir_a/file_2_dir_a", parent, id);
			assertEquals(1, diff.size());
			final Delta[] deltas = diff.toArray(new Delta[1]);
			assertEquals(1, deltas[0].getOriginal().getLines().size());
			assertEquals(0, deltas[0].getRevised().getLines().size());
		}
	}
	
	/**
	 * Test get changed paths.
	 */
	@Test
	public void testGetChangedPaths() {
		for (final Repository repository : repositories) {
			final Map<String, ChangeType> changedPaths = repository.getChangedPaths(repository.getHEAD());
			final Map<String, ChangeType> paths = new HashMap<String, ChangeType>();
			paths.put("/dir_b/file_2_dir_a", ChangeType.Modified);
			paths.put("/file_1", ChangeType.Added);
			
			assertEquals(paths.keySet().size(), changedPaths.keySet().size());
			assertTrue(CollectionUtils.isEqualCollection(paths.keySet(), changedPaths.keySet()));
			for (final String key : changedPaths.keySet()) {
				assertEquals(paths.get(key), changedPaths.get(key));
			}
		}
	}
	
	/**
	 * Test get first revision id.
	 */
	@Test
	public void testGetFirstRevisionID() {
		for (final Repository repository : repositories) {
			if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertEquals("1", repository.getFirstRevisionId());
			} else if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("7b5b41fffc13fba4f2dbca350becc9bc27d2d311", repository.getFirstRevisionId());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("1510979776500f102ff503949ea34cdbf8c653d8", repository.getFirstRevisionId());
			}
		}
	}
	
	/**
	 * Test get former path name.
	 */
	@Test
	public void testGetFormerPathName() {
		for (final Repository repository : repositories) {
			final String formerPathName = repository.getFormerPathName(repository.getTransactionId(3),
			                                                           "dir_b/file_2_dir_a");
			try {
				assertEquals("dir_a/file_2_dir_a", formerPathName);
			} catch (final AssertionError e) {
				fail(String.format("Failed for repository '%s'. Expected '%s' but got '%s'.", repository.getHandle(),
				                   "dir_a/file_2_dir_a", formerPathName));
			}
		}
	}
	
	/**
	 * Test get last revision id.
	 */
	@Test
	public void testGetLastRevisionID() {
		for (final Repository repository : repositories) {
			if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertEquals("17", repository.getEndRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("a19f0b6e729adbf26b70a0e17f32453835fb50eb", repository.getEndRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("01bcd1a86fb7d47c977f41af6a3a8f2407ce9183", repository.getEndRevision());
			}
			
		}
	}
	
	/**
	 * Test log.
	 */
	@Test
	public void testLog() {
		for (final Repository repository : repositories) {
			final List<LogEntry> log = repository.log(repository.getFirstRevisionId(), repository.getHEAD());
			
			// -- Rev 1 -- //
			LogEntry entry = log.get(0);
			assertEquals("just", entry.getAuthor().getUsernames().iterator().next());
			assertEquals(getDateFromString("2010-10-22 16:33:44 +0200").getMillis(),
			             entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()).getMillis());
			if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("7b5b41fffc13fba4f2dbca350becc9bc27d2d311", entry.getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("1510979776500f102ff503949ea34cdbf8c653d8", entry.getRevision());
			} else {
				assertEquals("1", entry.getRevision());
			}
			
			assertEquals("creating file_1", entry.getMessage());
			
			// -- Rev 2 -- //
			entry = log.get(1);
			assertEquals("just", entry.getAuthor().getUsernames().iterator().next());
			assertEquals(getDateFromString("2010-10-22 16:35:15 +0200").getMillis(),
			             entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()).getMillis());
			if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("7f1d2e6e6cffca9a8360af777254d05d9a26bc11", entry.getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("b9aff3c08f90cbd42361da158fbbe979405fba70", entry.getRevision());
			} else {
				assertEquals("2", entry.getRevision());
			}
			assertEquals("adding file_2" + FileUtils.lineSeparator + "adding file_3" + FileUtils.lineSeparator
			        + "setting content of file_* to: file_* content", entry.getMessage().trim());
			
			// -- Rev 3 -- //
			entry = log.get(2);
			assertEquals("just", entry.getAuthor().getUsernames().iterator().next());
			assertEquals(getDateFromString("2010-10-22 16:36:05 +0200").getMillis(),
			             entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()).getMillis());
			if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("d0b5c4888aabfdcc524c10967e5fdea92dd33081", entry.getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("c09ba4fd1259c2421331b20dea435d414d2ab6b2", entry.getRevision());
			} else {
				assertEquals("3", entry.getRevision());
			}
			assertEquals("moving file_2 to dir_a/file_2_dir_a", entry.getMessage().trim());
			
			// -- Rev 4 -- //
			entry = log.get(3);
			assertEquals("just", entry.getAuthor().getUsernames().iterator().next());
			assertEquals(getDateFromString("2010-10-22 16:36:46 +0200").getMillis(),
			             entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()).getMillis());
			if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("63cddef94239aae861c474480a834c95df719c65", entry.getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("5a61f0f67642e577f814650bc4507543153b1b22", entry.getRevision());
			} else {
				assertEquals("4", entry.getRevision());
			}
			assertEquals("moving dir_a to dir_b", entry.getMessage().trim());
			
			// -- Rev 5 -- //
			entry = log.get(4);
			assertEquals("just", entry.getAuthor().getUsernames().iterator().next());
			assertEquals(getDateFromString("2010-10-22 16:37:07 +0200").getMillis(),
			             entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()).getMillis());
			if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("ea6878a36dc3b644f45ac93b095896bf6d68597d", entry.getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("42aa307236637be938f4126328234b5264af8bf8", entry.getRevision());
			} else {
				assertEquals("5", entry.getRevision());
			}
			assertEquals("adding new dir_a", entry.getMessage().trim());
			
			// -- Rev 6 -- //
			entry = log.get(5);
			assertEquals("just", entry.getAuthor().getUsernames().iterator().next());
			assertEquals(getDateFromString("2010-10-22 16:40:19 +0200").getMillis(),
			             entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()).getMillis());
			if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("a4769ec81d251b333ab668c013a30df8a6d92bdc", entry.getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("7abcf1545dba655579d6d8a775ebd6e245441962", entry.getRevision());
			} else {
				assertEquals("6", entry.getRevision());
			}
			assertEquals("moving file_3 to dir_a/file_3_dir_a" + FileUtils.lineSeparator
			        + "changing content of dir_a/file_3_dir_a to file_3 content changed", entry.getMessage().trim());
			
			// ............ //
			
			// -- Rev 17 -- //
			entry = log.get(16);
			assertEquals("just", entry.getAuthor().getUsernames().iterator().next());
			assertEquals(getDateFromString("2010-10-22 16:53:06 +0200").getMillis(),
			             entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()).getMillis());
			if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("a19f0b6e729adbf26b70a0e17f32453835fb50eb", entry.getRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("01bcd1a86fb7d47c977f41af6a3a8f2407ce9183", entry.getRevision());
			} else {
				assertEquals("17", entry.getRevision());
			}
			assertEquals("adding fake file_1 and modifying file_2_dir_a", entry.getMessage().trim());
		}
	}
	
	/**
	 * Test move edit.
	 */
	@Test
	public void testMoveEdit() {
		for (final Repository repository : repositories) {
			final Map<String, ChangeType> changedPaths = repository.getChangedPaths(repository.getTransactionId(3));
			assertEquals(2, changedPaths.size());
			if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertTrue(changedPaths.containsKey("/dir_a"));
				assertTrue(changedPaths.containsKey("/dir_b"));
				assertEquals(ChangeType.Deleted, changedPaths.get("/dir_a"));
				assertEquals(ChangeType.Added, changedPaths.get("/dir_b"));
			} else {
				assertTrue(changedPaths.containsKey("/dir_a/file_2_dir_a"));
				assertTrue(changedPaths.containsKey("/dir_b/file_2_dir_a"));
				assertEquals(ChangeType.Deleted, changedPaths.get("/dir_a/file_2_dir_a"));
				assertEquals(ChangeType.Added, changedPaths.get("/dir_b/file_2_dir_a"));
			}
			
		}
	}
	
}
