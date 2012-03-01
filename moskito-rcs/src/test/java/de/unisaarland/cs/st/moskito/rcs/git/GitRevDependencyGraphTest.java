package de.unisaarland.cs.st.moskito.rcs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph;

public class GitRevDependencyGraphTest {
	
	private static BranchFactory branchFactory;
	private static GitRepository repo;
	
	@BeforeClass
	public static void beforeClass() {
		try {
			final URL zipURL = GitRevDependencyGraphTest.class.getResource(FileUtils.fileSeparator + "testGit.zip");
			if (zipURL == null) {
				fail();
			}
			
			final File bareDir = new File(
			                              (new URL(zipURL.toString()
			                                             .substring(0,
			                                                        zipURL.toString()
			                                                              .lastIndexOf(FileUtils.fileSeparator)))).toURI());
			FileUtils.unzip(new File(zipURL.toURI()), bareDir);
			if ((!bareDir.exists()) || (!bareDir.isDirectory())) {
				fail();
			}
			branchFactory = new BranchFactory(null);
			repo = new GitRepository();
			repo.setup(new URI("file://" + bareDir.getAbsolutePath() + FileUtils.fileSeparator + "testGit"), null,
			           null, branchFactory, null);
		} catch (final Exception e) {
			fail();
		}
	}
	
	@Test
	public void test() {
		final IRevDependencyGraph graph = repo.getRevDependencyGraph();
		
		String hash = "e52def97ebc1f78c9286b1e7c36783aa67604439";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) == null);
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "19bc6c11d2d8cff62f911f26bad29690c3cee256";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "9d647acdef18e1bc6137354359ae75e490a7687d";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "98d5c40ef3c14503a472ba4133ae3529c7578e30";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "cbcc33d919a27b9450d117f211a5f4f45615cab9";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "ae94d7fa81437cbbd723049e3951f9daaa62a7c0";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) != null);
		assertEquals("98d5c40ef3c14503a472ba4133ae3529c7578e30", graph.getMergeParent(hash));
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "8273c1e51992a4d7a1da012dbb416864c2749a7f";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) != null);
		assertEquals("ae94d7fa81437cbbd723049e3951f9daaa62a7c0", graph.getMergeParent(hash));
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "927478915f2d8fb9135eb33d21cb8491c0e655be"; // tag_one
		assertTrue(graph.hasVertex(hash));
		assertEquals(1, graph.getTags(hash).size());
		assertTrue(graph.getTags(hash).contains("tag_one"));
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "1ac6aaa05eb6d55939b20e70ec818bb413417757";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "41a40fb23b54a49e91eb4cee510533eef810ec68";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "376adc0f9371129a76766f8030f2e576165358c1";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("1ac6aaa05eb6d55939b20e70ec818bb413417757", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "637acf68104e7bdff8235fb2e1a254300ffea3cb";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) != null);
		assertEquals("376adc0f9371129a76766f8030f2e576165358c1", graph.getMergeParent(hash));
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("376adc0f9371129a76766f8030f2e576165358c1", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "9be561b3657e2b1da2b09d675dddd5f45c47f57c";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("637acf68104e7bdff8235fb2e1a254300ffea3cb", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2";
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) != null);
		assertEquals("9be561b3657e2b1da2b09d675dddd5f45c47f57c", graph.getMergeParent(hash));
		assertTrue(graph.isBranchHead(hash) == null);
		
		hash = "fe56f365f798c3742bac5e56f5ff30eca4f622c6"; // HEAD master
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("9be561b3657e2b1da2b09d675dddd5f45c47f57c", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertEquals("master", graph.isBranchHead(hash));
		
		hash = "67635fe9efeb2fd3751df9ea67650c71e59e3df1"; // HEAD maintenance
		assertTrue(graph.hasVertex(hash));
		assertEquals(0, graph.getTags(hash).size());
		assertTrue(graph.getBranchParent(hash) != null);
		assertEquals("a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2", graph.getBranchParent(hash));
		assertTrue(graph.getMergeParent(hash) == null);
		assertEquals("origin/maintenance", graph.isBranchHead(hash));
		
	}
	
}
