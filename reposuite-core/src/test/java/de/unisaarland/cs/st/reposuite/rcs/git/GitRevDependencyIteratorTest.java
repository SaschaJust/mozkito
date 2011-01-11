package de.unisaarland.cs.st.reposuite.rcs.git;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependency;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;


public class GitRevDependencyIteratorTest {
	
	private static GitRepository repo;
	
	@BeforeClass
	public static void beforeClass() {
		try {
			URL zipURL = GitRevDependencyIteratorTest.class.getResource(System.getProperty("file.separator")
					+ "testGit.zip");
			if (zipURL == null) {
				fail();
			}
			
			File bareDir = new File((new URL(zipURL.toString().substring(0,
					zipURL.toString().lastIndexOf(FileUtils.fileSeparator)))).toURI());
			FileUtils.unzip(new File(zipURL.toURI()), bareDir);
			if ((!bareDir.exists()) || (!bareDir.isDirectory())) {
				fail();
			}
			
			repo = new GitRepository();
			repo.setup(new URI("file://" + bareDir.getAbsolutePath() + FileUtils.fileSeparator + "testGit"), null, null);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testIter() {
		GitRevDependencyIterator iter = new GitRevDependencyIterator(repo.getCloneDir(),
		"8273c1e51992a4d7a1da012dbb416864c2749a7f");
		
		
		assertTrue(iter.hasNext());
		RevDependency dep = iter.next();
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", dep.getId());
		assertEquals(RCSBranch.MASTER, dep.getCommitBranch());
		assertEquals(null, dep.getTagName());
		Set<String> parents = dep.getParents();
		assertEquals(0, parents.size());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", dep.getId());
		assertEquals(RCSBranch.MASTER, dep.getCommitBranch());
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("e52def97ebc1f78c9286b1e7c36783aa67604439"));
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", dep.getId());
		assertEquals(RCSBranch.MASTER, dep.getCommitBranch());
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("19bc6c11d2d8cff62f911f26bad29690c3cee256"));
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", dep.getId());
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30Branch", dep.getCommitBranch().getName());
		assertTrue(dep.getCommitBranch().hasParent());
		RCSBranch previousBranch = dep.getCommitBranch();
		assertEquals(previousBranch, dep.getCommitBranch().getParent());
		previousBranch = dep.getCommitBranch();
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("19bc6c11d2d8cff62f911f26bad29690c3cee256"));
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6", dep.getId());
		assertEquals(previousBranch, dep.getCommitBranch());
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("9d647acdef18e1bc6137354359ae75e490a7687d"));
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8", dep.getId());
		assertEquals(RCSBranch.MASTER, dep.getCommitBranch());
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("9d647acdef18e1bc6137354359ae75e490a7687d"));
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", dep.getId());
		assertEquals(previousBranch, dep.getCommitBranch());
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6"));
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0", dep.getId());
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0Branch", dep.getCommitBranch().getName());
		assertTrue(dep.getCommitBranch().hasParent());
		previousBranch = dep.getCommitBranch();
		assertEquals(RCSBranch.MASTER, dep.getCommitBranch().getParent());
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(2, parents.size());
		assertTrue(parents.contains("cbcc33d919a27b9450d117f211a5f4f45615cab9"));
		assertTrue(parents.contains("98d5c40ef3c14503a472ba4133ae3529c7578e30"));
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", dep.getId());
		assertEquals(RCSBranch.MASTER, dep.getCommitBranch());
		assertEquals(null, dep.getTagName());
		parents = dep.getParents();
		assertEquals(2, parents.size());
		assertTrue(parents.contains("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8"));
		assertTrue(parents.contains("ae94d7fa81437cbbd723049e3951f9daaa62a7c0"));
		
	}
	
	@Test
	public void testRegEx() {
		String line1 = "0cc858f14daa9750596051cb5b92c317ed17c401  (hudson-whatever)";
		String line2 = "ba4fb16f3057524353e5159e505b2f8d8405f38a  (HEAD, origin/master, origin/HEAD, master)";
		
		List<RegexGroup> groups = GitRevDependencyIterator.tagRegex.find(line1);
		assertFalse(groups == null);
		assertEquals(2, groups.size());
		assertEquals("hudson-whatever", groups.get(1).getMatch());
		
		groups = GitRevDependencyIterator.tagRegex.find(line2);
		assertFalse(groups == null);
		assertEquals(2, groups.size());
		assertEquals("HEAD, origin/master, origin/HEAD, master", groups.get(1).getMatch());
	}
	
}
