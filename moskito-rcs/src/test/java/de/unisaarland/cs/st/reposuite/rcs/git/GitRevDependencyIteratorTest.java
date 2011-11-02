/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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

import net.ownhero.dev.regex.RegexGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependency;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import net.ownhero.dev.ioda.FileUtils;


public class GitRevDependencyIteratorTest {
	
	private static GitRepository repo;
	
	@BeforeClass
	public static void beforeClass() {
		try {
			URL zipURL = GitRevDependencyIteratorTest.class.getResource(FileUtils.fileSeparator
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
		GitRevDependencyIterator iter = new GitRevDependencyIterator(repo.getWokingCopyLocation(),
		"67635fe9efeb2fd3751df9ea67650c71e59e3df1");
		
		assertTrue(iter.hasNext());
		RevDependency dep = iter.next();
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_e52 = dep.getCommitBranch();
		Set<String> parents = dep.getParents();
		assertEquals(0, parents.size());
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_19b = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("e52def97ebc1f78c9286b1e7c36783aa67604439"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_9d6 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("19bc6c11d2d8cff62f911f26bad29690c3cee256"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_dee = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("9d647acdef18e1bc6137354359ae75e490a7687d"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_d23 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("9d647acdef18e1bc6137354359ae75e490a7687d"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_cbc = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_98d = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("19bc6c11d2d8cff62f911f26bad29690c3cee256"));
		assertFalse(dep.isMerge());
		
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_ae9 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(2, parents.size());
		assertTrue(parents.contains("cbcc33d919a27b9450d117f211a5f4f45615cab9"));
		assertTrue(parents.contains("98d5c40ef3c14503a472ba4133ae3529c7578e30"));
		assertTrue(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_827 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(2, parents.size());
		assertTrue(parents.contains("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8"));
		assertTrue(parents.contains("ae94d7fa81437cbbd723049e3951f9daaa62a7c0"));
		assertTrue(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", dep.getId());
		assertEquals(1, dep.getTagNames().size());
		assertTrue(dep.getTagNames().contains("tag_one"));
		RCSBranch branch_927 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("8273c1e51992a4d7a1da012dbb416864c2749a7f"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_41a = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("927478915f2d8fb9135eb33d21cb8491c0e655be"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("1ac6aaa05eb6d55939b20e70ec818bb413417757", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_1ac = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("927478915f2d8fb9135eb33d21cb8491c0e655be"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("376adc0f9371129a76766f8030f2e576165358c1", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_376 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("1ac6aaa05eb6d55939b20e70ec818bb413417757"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("637acf68104e7bdff8235fb2e1a254300ffea3cb", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_637 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(2, parents.size());
		assertTrue(parents.contains("376adc0f9371129a76766f8030f2e576165358c1"));
		assertTrue(parents.contains("41a40fb23b54a49e91eb4cee510533eef810ec68"));
		assertTrue(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("9be561b3657e2b1da2b09d675dddd5f45c47f57c", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_9be = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("637acf68104e7bdff8235fb2e1a254300ffea3cb"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("fe56f365f798c3742bac5e56f5ff30eca4f622c6", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_fe5 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("9be561b3657e2b1da2b09d675dddd5f45c47f57c"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_d98 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("376adc0f9371129a76766f8030f2e576165358c1"));
		assertFalse(dep.isMerge());
		
		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_a92 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(2, parents.size());
		assertTrue(parents.contains("d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9"));
		assertTrue(parents.contains("9be561b3657e2b1da2b09d675dddd5f45c47f57c"));
		assertTrue(dep.isMerge());

		assertTrue(iter.hasNext());
		dep = iter.next();
		assertEquals("67635fe9efeb2fd3751df9ea67650c71e59e3df1", dep.getId());
		assertEquals(0, dep.getTagNames().size());
		RCSBranch branch_676 = dep.getCommitBranch();
		parents = dep.getParents();
		assertEquals(1, parents.size());
		assertTrue(parents.contains("a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2"));
		assertFalse(dep.isMerge());
		
		assertFalse(iter.hasNext());
		
		
		//check braches and branch hierarchy
		assertFalse(branch_e52.hasParent());
		assertEquals(RCSBranch.MASTER, branch_e52);
		assertEquals(null, branch_e52.getMergedIn());
		assertTrue(branch_e52.isOpen());
		
		assertFalse(branch_19b.hasParent());
		assertEquals(RCSBranch.MASTER, branch_19b);
		
		assertFalse(branch_9d6.hasParent());
		assertEquals(RCSBranch.MASTER, branch_9d6);
		
		assertFalse(branch_dee.hasParent());
		assertEquals(RCSBranch.MASTER, branch_dee);
		
		assertTrue(branch_d23.hasParent());
		assertEquals(branch_ae9, branch_cbc);
		assertEquals(RCSBranch.MASTER, branch_d23.getParent());
		
		assertTrue(branch_cbc.hasParent());
		assertEquals(branch_ae9, branch_cbc);
		assertEquals(RCSBranch.MASTER, branch_cbc.getParent());
		
		assertTrue(branch_98d.hasParent());
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30Branch", branch_98d.getName());
		assertEquals(branch_ae9, branch_98d.getParent());
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0", branch_98d.getMergedIn());
		assertFalse(branch_98d.isOpen());
		
		assertTrue(branch_ae9.hasParent());
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0Branch", branch_ae9.getName());
		assertEquals(RCSBranch.MASTER, branch_ae9.getParent());
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", branch_ae9.getMergedIn());
		assertFalse(branch_ae9.isOpen());
		
		assertFalse(branch_827.hasParent());
		assertEquals(RCSBranch.MASTER, branch_827);
		
		assertFalse(branch_927.hasParent());
		assertEquals(RCSBranch.MASTER, branch_927);
		
		assertFalse(branch_1ac.hasParent());
		assertEquals("origin/maintenance", branch_1ac.getName());
		assertEquals(null, branch_e52.getMergedIn());
		assertTrue(branch_e52.isOpen());
		
		assertFalse(branch_41a.hasParent());
		assertEquals(RCSBranch.MASTER, branch_41a);
		
		assertFalse(branch_376.hasParent());
		assertEquals(branch_1ac, branch_376);
		
		assertFalse(branch_637.hasParent());
		assertEquals(RCSBranch.MASTER, branch_637);
		
		assertFalse(branch_d98.hasParent());
		assertEquals(branch_1ac, branch_d98);
		
		assertFalse(branch_9be.hasParent());
		assertEquals(RCSBranch.MASTER, branch_9be);
		
		assertFalse(branch_a92.hasParent());
		assertEquals(branch_1ac, branch_a92);
		
		assertFalse(branch_fe5.hasParent());
		assertEquals(RCSBranch.MASTER, branch_fe5);
		
		assertFalse(branch_676.hasParent());
		assertEquals(branch_1ac, branch_676);
		
		
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
