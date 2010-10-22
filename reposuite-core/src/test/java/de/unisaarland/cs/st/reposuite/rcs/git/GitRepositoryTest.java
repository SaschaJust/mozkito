package de.unisaarland.cs.st.reposuite.rcs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;

public class GitRepositoryTest {
	
	private File          cloneDir;
	private URI           uri;
	private GitRepository repo;
	
	@Before
	public void setUp() throws Exception {
		
		//TODO replace this repo  and create an own repo on the fly 
		uri = new URI("git://github.com/git/hello-world.git");
	}
	
	@After
	public void tearDown() throws Exception {
		if (cloneDir != null) {
			File f = new File(cloneDir, ".git");
			FileUtils.forceDelete(f);
			FileUtils.forceDelete(cloneDir);
		}
	}
	
	@Test
	public void testAnnotate() {
		testClone();
		List<AnnotationEntry> annotate = repo.annotate("python.py", "c18877690322dfc6ae3e37bb7f7085a24e94e887");
		assertEquals(2, annotate.size());
		AnnotationEntry entry = annotate.get(0);
		assertEquals("f554664a346629dc2b839f7292d06bad2db4aece", entry.getRevision());
		assertTrue(entry.hasAlternativePath());
		assertEquals("hello.py", entry.getAlternativeFilePath());
		
		entry = annotate.get(1);
		assertEquals("f554664a346629dc2b839f7292d06bad2db4aece", entry.getRevision());
		assertTrue(entry.hasAlternativePath());
		assertEquals("hello.py", entry.getAlternativeFilePath());
		
		annotate = repo.annotate("focal.fc", "c18877690322dfc6ae3e37bb7f7085a24e94e887");
		assertEquals(1, annotate.size());
		entry = annotate.get(0);
		assertEquals("9dc30dd27d47d1d3f5bbc0ebb92daf8b4a6c812c", entry.getRevision());
		assertFalse(entry.hasAlternativePath());
		assertEquals(null, entry.getAlternativeFilePath());
	}
	
	@Test
	public void testCheckoutDir() {
		//TODO implement test
	}
	
	@Test
	public void testCheckoutFile() {
		//TODO implement test
	}
	
	@Test
	public void testClone() {
		repo = new GitRepository();
		repo.setup(uri);
		cloneDir = repo.getCloneDir();
		File DOT_GIT = new File(cloneDir, ".git");
		assertTrue(DOT_GIT.exists());
		assertTrue(DOT_GIT.isDirectory());
	}
	
	@Test
	public void testCloneUsername() {
		//TODO implements
	}
	
	@Test
	public void testDiff() {
		//TODO implement test
	}
	
	@Test
	public void testGetFirstRev() {
		testClone();
		assertEquals("f554664a346629dc2b839f7292d06bad2db4aece", repo.getFirstRevisionId());
	}
	
	@Test
	public void testGetLastRev() {
		testClone();
		assertEquals("3fa7c46d11b11d61f1cbadc6888be5d0eae21969", repo.getLastRevisionId());
	}
}
