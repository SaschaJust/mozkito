/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.CommandExecutor;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SubversionRepositoryTest {
	
	private static SubversionRepository repository;
	
	private static DateTime getDateFromSVNString(String timestamp) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
		return dtf.parseDateTime(timestamp);
	}
	
	@Before
	public void setup() {
		if (repository == null) {
			repository = new SubversionRepository();
			URL url = SubversionRepositoryTest.class.getResource(System.getProperty("file.separator") + "repotest.svn");
			File tmpDirectory = FileUtils.createRandomDir("repotest_svn", "");
			try {
				Integer returnValue = 0;
				FileUtils.forceDeleteOnExit(tmpDirectory);
				if (RepoSuiteSettings.logDebug()) {
					Logger.debug("Creating SVN repository at: " + tmpDirectory.getAbsolutePath());
				}
				Tuple<Integer, List<String>> execute = CommandExecutor.execute("svnadmin", new String[] { "create",
				        tmpDirectory.getAbsolutePath() }, tmpDirectory, null, null);
				returnValue += execute.getFirst();
				execute = CommandExecutor.execute("svnadmin", new String[] { "load", tmpDirectory.getAbsolutePath() },
				        tmpDirectory, url.openStream(), null);
				returnValue += execute.getFirst();
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			
			try {
				repository.setup(new URI("file://" + tmpDirectory));
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
		
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void testAnnotateNoMerge() {
		List<AnnotationEntry> annotate = repository.annotate("/dir_b/file_2_dir_a", "HEAD");
		
		assertEquals("2", annotate.get(0).getRevision());
		assertEquals("just", annotate.get(0).getUsername());
		assertEquals("file_2 content", annotate.get(0).getLine().trim());
		// exact match besides millis
		assertEquals(getDateFromSVNString("2010-10-22 16:35:15 +0200"),
		        annotate.get(0).getTimestamp().minusMillis(annotate.get(0).getTimestamp().getMillisOfSecond()));
		assertNull(annotate.get(0).getAlternativeFilePath());
		
		assertEquals("17", annotate.get(1).getRevision());
		assertEquals("just", annotate.get(1).getUsername());
		assertEquals("test", annotate.get(1).getLine().trim());
		// exact match besides millis
		assertEquals(getDateFromSVNString("2010-10-22 16:53:06 +0200"),
		        annotate.get(1).getTimestamp().minusMillis(annotate.get(1).getTimestamp().getMillisOfSecond()));
		assertNull(annotate.get(1).getAlternativeFilePath());
	}
	
	@Test
	public void testCheckout() {
		File checkoutPath = repository.checkoutPath("/", "HEAD");
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
	
	@Test
	public void testCheckoutDir() {
		File checkoutPath = repository.checkoutPath("/dir_a", "HEAD");
		if (RepoSuiteSettings.logDebug()) {
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
	
	@Test
	public void testCheckoutFile() {
		try {
			repository.checkoutPath("/dir_b/file_2_dir_a", "HEAD");
			fail("Checking out a file should cause a RuntimeException to be thrown.");
		} catch (RuntimeException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetChangedPaths() {
		Map<String, ChangeType> changedPaths = repository.getChangedPaths("17");
		Map<String, ChangeType> paths = new HashMap<String, ChangeType>();
		paths.put("/dir_b/file_2_dir_a", ChangeType.Modified);
		paths.put("file_1", ChangeType.Added);
		
		assertEquals(paths.keySet().size(), changedPaths.keySet().size());
		assertTrue(CollectionUtils.isEqualCollection(paths.keySet(), changedPaths.keySet()));
		for (String key : changedPaths.keySet()) {
			assertEquals(paths.get(key), changedPaths.get(key));
		}
	}
	
	@Test
	public void testGetFirstRevisionID() {
		assertEquals("1", repository.getFirstRevisionId());
	}
	
	@Test
	public void testGetLastRevisionID() {
		assertEquals("17", repository.getLastRevisionId());
	}
	
	@Test
	public void testLog() {
		List<LogEntry> log = repository.log("0", "HEAD");
		
		// -- Rev 1 -- //
		LogEntry entry = log.get(1);
		assertEquals("just", entry.getAuthor());
		assertEquals(getDateFromSVNString("2010-10-22 16:33:44 +0200"),
		        entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()));
		assertEquals("1", entry.getRevision());
		assertEquals("creating file_1", entry.getMessage());
		
		// -- Rev 2 -- //
		entry = log.get(2);
		assertEquals("just", entry.getAuthor());
		assertEquals(getDateFromSVNString("2010-10-22 16:35:15 +0200"),
		        entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()));
		assertEquals("2", entry.getRevision());
		assertEquals("adding file_2" + FileUtils.lineSeparator + "adding file_3" + FileUtils.lineSeparator
		        + "setting content of file_* to: file_* content", entry.getMessage().trim());
		
		// -- Rev 3 -- //
		entry = log.get(3);
		assertEquals("just", entry.getAuthor());
		assertEquals(getDateFromSVNString("2010-10-22 16:36:05 +0200"),
		        entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()));
		assertEquals("3", entry.getRevision());
		assertEquals("moving file_2 to dir_a/file_2_dir_a", entry.getMessage().trim());
		
		// -- Rev 4 -- //
		entry = log.get(4);
		assertEquals("just", entry.getAuthor());
		assertEquals(getDateFromSVNString("2010-10-22 16:36:46 +0200"),
		        entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()));
		assertEquals("4", entry.getRevision());
		assertEquals("moving dir_a to dir_b", entry.getMessage().trim());
		
		// -- Rev 5 -- //
		entry = log.get(5);
		assertEquals("just", entry.getAuthor());
		assertEquals(getDateFromSVNString("2010-10-22 16:37:07 +0200"),
		        entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()));
		assertEquals("5", entry.getRevision());
		assertEquals("adding new dir_a", entry.getMessage().trim());
		
		// -- Rev 6 -- //
		entry = log.get(6);
		assertEquals("just", entry.getAuthor());
		assertEquals(getDateFromSVNString("2010-10-22 16:40:19 +0200"),
		        entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()));
		assertEquals("6", entry.getRevision());
		assertEquals("moving file_3 to dir_a/file_3_dir_a" + FileUtils.lineSeparator
		        + "changing content of dir_a/file_3_dir_a to file_3 content changed", entry.getMessage().trim());
		
		// ............ //
		
		// -- Rev 17 -- //
		entry = log.get(17);
		assertEquals("just", entry.getAuthor());
		assertEquals(getDateFromSVNString("2010-10-22 16:53:06 +0200"),
		        entry.getDateTime().minusMillis(entry.getDateTime().getMillisOfSecond()));
		assertEquals("17", entry.getRevision());
		assertEquals("adding fake file_1 and modifying file_2_dir_a", entry.getMessage().trim());
	}
	
	@Test
	public void testRenameEdit() {
		
	}
}
