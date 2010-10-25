/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;
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
	public void testAnnotate() {
		List<AnnotationEntry> annotate = repository.annotate("/dir_b/file_2_dir_a", "HEAD");
		System.err.println(JavaUtils.collectionToString(annotate));
		for (AnnotationEntry entry : annotate) {
			entry.getLine();
		}
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
		
	}
	
	@Test
	public void testGetFirstRevisionID() {
		assertEquals("0", repository.getFirstRevisionId());
	}
	
	@Test
	public void testGetLastRevisionID() {
		assertEquals("17", repository.getLastRevisionId());
	}
	
	@Test
	public void testLog() {
		
	}
	
	@Test
	public void testRenameEdit() {
		
	}
}
