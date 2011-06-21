/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.rcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.git.GitRepository;
import de.unisaarland.cs.st.reposuite.rcs.mercurial.MercurialRepository;
import difflib.Delta;

public class RepositoryTest {
	
	// [scheme:][//authority][path][?query][#fragment]
	// [user-info@]host[:port]
	
	private static File             tmpDirectory;
	private static List<Repository> repositories = new LinkedList<Repository>();
	
	@AfterClass
	public static void afterClass() {
		
		URL repoURL = RepositoryTest.class.getResource(FileUtils.fileSeparator + "repotest.mercurial");
		if (repoURL != null) {
			try {
				File toDelete = new File(repoURL.toURI());
				FileUtils.deleteDirectory(toDelete);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				fail();
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
			
		}
		
		repoURL = RepositoryTest.class.getResource(FileUtils.fileSeparator + "repotest.git");
		if (repoURL != null) {
			try {
				File toDelete = new File(repoURL.toURI());
				FileUtils.deleteDirectory(toDelete);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				fail();
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		repoURL = RepositoryTest.class.getResource(FileUtils.fileSeparator + "testGit");
		if (repoURL != null) {
			try {
				File toDelete = new File(repoURL.toURI());
				FileUtils.deleteDirectory(toDelete);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				fail();
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}
		
		for (Repository repository : repositories) {
			if (repository.getRepositoryType().equals(RepositoryType.CVS)) {
				continue;
			} else if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				try {
					FileUtils.deleteDirectory(tmpDirectory);
				} catch (IOException e) {
					e.printStackTrace();
					fail();
				}
			} else if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				GitRepository gitRepo = (GitRepository) repository;
				if (gitRepo.getWokingCopyLocation() != null) {
					try {
						FileUtils.deleteDirectory(gitRepo.getWokingCopyLocation());
					} catch (IOException e) {
						e.printStackTrace();
						fail();
					}
				}
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				MercurialRepository hgRepo = (MercurialRepository) repository;
				if (hgRepo.getWokingCopyLocation() != null) {
					try {
						FileUtils.deleteDirectory(hgRepo.getWokingCopyLocation());
					} catch (IOException e) {
						e.printStackTrace();
						fail();
					}
				}
			}
		}
		
		// delete all reposuite directories and files
		Map<FileShutdownAction, Set<File>> openFiles = FileUtils.getManagedOpenFiles();
		Set<File> set = openFiles.get(FileShutdownAction.DELETE);
		if (set != null) {
			for (File f : set) {
				try {
					if (f.isFile()) {
						FileUtils.forceDelete(f);
					} else {
						FileUtils.deleteDirectory(f);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@BeforeClass
	public static void beforeClass() {
		
		// UNZIP mercurial repo
		URL zipURL = RepositoryTest.class.getResource(FileUtils.fileSeparator + "repotest.mercurial.zip");
		if (zipURL == null) {
			fail();
		}
		File baseDir = null;
		try {
			baseDir = new File(
			                   (new URL(zipURL.toString().substring(0,
			                                                        zipURL.toString()
			                                                              .lastIndexOf(FileUtils.fileSeparator)))).toURI());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			fail();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		if ((!baseDir.exists()) || (!baseDir.isDirectory())) {
			fail();
		}
		File zipFile = null;
		try {
			zipFile = new File(zipURL.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		// UNZIP END
		
		// UNZIP git repo
		zipURL = RepositoryTest.class.getResource(FileUtils.fileSeparator + "repotest.git.zip");
		if (zipURL == null) {
			fail();
		}
		try {
			zipFile = new File(zipURL.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		// UNZIP END
		
		for (RepositoryType type : RepositoryType.values()) {
			if (type.equals(RepositoryType.CVS)) {
				continue;
			}
			Repository repository = null;
			try {
				repository = RepositoryFactory.getRepositoryHandler(type).newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
				fail();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
				fail();
			} catch (UnregisteredRepositoryTypeException e1) {
				e1.printStackTrace();
				fail();
			}
			repositories.add(repository);
			URL url = RepositoryTest.class.getResource(FileUtils.fileSeparator + "repotest."
			        + type.toString().toLowerCase());
			File urlFile = null;
			try {
				urlFile = new File(url.toURI());
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
				fail();
			}
			
			if (type.equals(RepositoryType.SUBVERSION)) {
				tmpDirectory = FileUtils.createRandomDir("repotest_" + type.toString(), "", FileShutdownAction.DELETE);
				try {
					Integer returnValue = 0;
					if (Logger.logDebug()) {
						Logger.debug("Creating " + type.toString() + " repository at: "
						        + tmpDirectory.getAbsolutePath());
					}
					Tuple<Integer, List<String>> execute = CommandExecutor.execute("svnadmin", new String[] { "create",
					        "--config-dir", System.getProperty("user.home") + FileUtils.fileSeparator + ".subversion",
					        tmpDirectory.getAbsolutePath() }, tmpDirectory, null, null);
					returnValue += execute.getFirst();
					execute = CommandExecutor.execute("svnadmin",
					                                  new String[] { "load", tmpDirectory.getAbsolutePath() },
					                                  tmpDirectory, url.openStream(), null);
					returnValue += execute.getFirst();
				} catch (IOException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
				urlFile = tmpDirectory;
			}
			
			try {
				repository.setup(urlFile.toURI(), null, null);
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
		
	}
	
	private static DateTime getDateFromString(final String timestamp) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
		return dtf.parseDateTime(timestamp);
	}
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testAnnotate() {
		for (Repository repository : repositories) {
			List<AnnotationEntry> annotation = repository.annotate("dir_b/file_2_dir_a", repository.getHEAD());
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
	
	@Test
	public void testCheckout() {
		for (Repository repository : repositories) {
			File checkoutPath = repository.checkoutPath("/", repository.getHEAD());
			File dir_a = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_a");
			File dir_b = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_b");
			File file_1 = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "file_1");
			
			File dir_a_file_3 = new File(dir_a.getAbsolutePath() + FileUtils.fileSeparator + "file_3_dir_a");
			File dir_b_file_2 = new File(dir_b.getAbsolutePath() + FileUtils.fileSeparator + "file_2_dir_a");
			
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
	
	@Test
	public void testCheckoutDir() {
		for (Repository repository : repositories) {
			File checkoutPath = repository.checkoutPath("/dir_a", repository.getHEAD());
			if (Logger.logDebug()) {
				Logger.debug("Child entries of checkout path: " + JavaUtils.arrayToString(checkoutPath.list()));
			}
			
			File dir_a = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_a");
			File dir_b = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "dir_b");
			File file_1 = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "file_1");
			File dir_a_file_3 = new File(checkoutPath.getAbsolutePath() + FileUtils.fileSeparator + "file_3_dir_a");
			
			assertFalse(dir_a.exists());
			assertFalse(dir_b.exists());
			assertFalse(file_1.exists());
			assertTrue(dir_a_file_3.exists());
			assertTrue(dir_a_file_3.isFile());
		}
		
	}
	
	@Test
	public void testCheckoutFile() {
		for (Repository repository : repositories) {
			if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertFalse(repository.checkoutPath("/dir_b/file_2_dir_a", repository.getHEAD()) != null);
			} else {
				assertTrue(repository.checkoutPath("/dir_b/file_2_dir_a", repository.getHEAD()) != null);
			}
		}
	}
	
	@Test
	public void testDiff() {
		for (Repository repository : repositories) {
			String id = repository.getRelativeTransactionId(repository.getFirstRevisionId(), 11);
			String parent = repository.getRelativeTransactionId(repository.getFirstRevisionId(), 10);
			Collection<Delta> diff = repository.diff("file_1", parent, id);
			assertEquals(1, diff.size());
			Delta[] deltas = diff.toArray(new Delta[1]);
			assertEquals(0, deltas[0].getOriginal().getLines().size());
			assertEquals(9, deltas[0].getRevised().getLines().size());
		}
	}
	
	@Test
	public void testDiffMove() {
		for (Repository repository : repositories) {
			String id = repository.getRelativeTransactionId(repository.getFirstRevisionId(), 3);
			String parent = repository.getRelativeTransactionId(repository.getFirstRevisionId(), 2);
			Collection<Delta> diff = repository.diff("dir_a/file_2_dir_a", parent, id);
			assertEquals(1, diff.size());
			Delta[] deltas = diff.toArray(new Delta[1]);
			assertEquals(1, deltas[0].getOriginal().getLines().size());
			assertEquals(0, deltas[0].getRevised().getLines().size());
		}
	}
	
	@Test
	public void testGetChangedPaths() {
		for (Repository repository : repositories) {
			Map<String, ChangeType> changedPaths = repository.getChangedPaths(repository.getHEAD());
			Map<String, ChangeType> paths = new HashMap<String, ChangeType>();
			paths.put("/dir_b/file_2_dir_a", ChangeType.Modified);
			paths.put("/file_1", ChangeType.Added);
			
			assertEquals(paths.keySet().size(), changedPaths.keySet().size());
			assertTrue(CollectionUtils.isEqualCollection(paths.keySet(), changedPaths.keySet()));
			for (String key : changedPaths.keySet()) {
				assertEquals(paths.get(key), changedPaths.get(key));
			}
		}
	}
	
	@Test
	public void testGetFirstRevisionID() {
		for (Repository repository : repositories) {
			if (repository.getRepositoryType().equals(RepositoryType.CVS)) {
				
			} else if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertEquals("1", repository.getFirstRevisionId());
			} else if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("7b5b41fffc13fba4f2dbca350becc9bc27d2d311", repository.getFirstRevisionId());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("1510979776500f102ff503949ea34cdbf8c653d8", repository.getFirstRevisionId());
			}
		}
	}
	
	@Test
	public void testGetFormerPathName() {
		for (Repository repository : repositories) {
			String formerPathName = repository.getFormerPathName(repository.getRelativeTransactionId(repository.getFirstRevisionId(),
			                                                                                         3),
			                                                     "dir_b/file_2_dir_a");
			assertEquals("dir_a/file_2_dir_a", formerPathName);
		}
	}
	
	@Test
	public void testGetLastRevisionID() {
		for (Repository repository : repositories) {
			if (repository.getRepositoryType().equals(RepositoryType.CVS)) {
				
			} else if (repository.getRepositoryType().equals(RepositoryType.SUBVERSION)) {
				assertEquals("17", repository.getEndRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.GIT)) {
				assertEquals("a19f0b6e729adbf26b70a0e17f32453835fb50eb", repository.getEndRevision());
			} else if (repository.getRepositoryType().equals(RepositoryType.MERCURIAL)) {
				assertEquals("01bcd1a86fb7d47c977f41af6a3a8f2407ce9183", repository.getEndRevision());
			}
			
		}
	}
	
	@Test
	public void testGetRelativeTransactionId() {
		for (Repository repository : repositories) {
			String endRevision = repository.getEndRevision();
			assertEquals(endRevision, repository.getRelativeTransactionId(endRevision, 10));
		}
	}
	
	@Test
	public void testLog() {
		for (Repository repository : repositories) {
			List<LogEntry> log = repository.log(repository.getFirstRevisionId(), repository.getHEAD());
			
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
	
	@Test
	public void testMoveEdit() {
		for (Repository repository : repositories) {
			Map<String, ChangeType> changedPaths = repository.getChangedPaths(repository.getRelativeTransactionId(repository.getFirstRevisionId(),
			                                                                                                      3));
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
