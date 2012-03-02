package de.unisaarland.cs.st.moskito.rcs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

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
	public void regressionTestRhino() {
		/* 
		 * @formatter:off
		 *       * 17d6198f9c31d608d985ff4c9ce1dcc162dc8133
         *       *   8222f6b8a291bf938f96d9560fd1d61638c1689c
         *       |\  
         *       | * 4a9e26c821ba113d9f36c95128b4184dc724750f
         *       * | 8e98f10673bae3345844c36eee2e9b21e8fed2d0
         *       * |   f51eba1d874d3fa5bb892d4dad8039d89dc8eee7
         *       |\ \  
         *       | |/  
         *       | * f5831260561a73cf26194eec3a3f8147050a2753
         *       * | 196075b582116f50b915e6e0508ec8812b92bcc6
         *       * |   2fe888ece150487c5ba43264094dcb696c800216
         *       |\ \  
         *       | * | 7ecdf2c2bd15e19704c7e4f6c4e128357aafdcd2
         *       * | | 1c0592306d9cfd1d19aa0133983c38448d167d69
         *       | |/  
         *       |/|   
         *       * | e4e75c53fd4f66c19c594aa3dfcc683407f44093
         *       * | c997bb0d0e45920993b576f83c657c4a532c9b95
         *       |/  
         *       * 34195982cb52661e5498ff880dd7b5b5b3230790
	     *
		 * 
		 * 
		 */
		
		//@formatter:on
		
		final GitRevDependencyGraph revGraph = new GitRevDependencyGraph(new GitRepository());
		revGraph.addEdge("8222f6b8a291bf938f96d9560fd1d61638c1689c", "17d6198f9c31d608d985ff4c9ce1dcc162dc8133",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("8e98f10673bae3345844c36eee2e9b21e8fed2d0", "8222f6b8a291bf938f96d9560fd1d61638c1689c",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("4a9e26c821ba113d9f36c95128b4184dc724750f", "8222f6b8a291bf938f96d9560fd1d61638c1689c",
		                 GitRevDependencyType.MERGE_EDGE);
		revGraph.addEdge("f5831260561a73cf26194eec3a3f8147050a2753", "4a9e26c821ba113d9f36c95128b4184dc724750f",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("f51eba1d874d3fa5bb892d4dad8039d89dc8eee7", "8e98f10673bae3345844c36eee2e9b21e8fed2d0",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("196075b582116f50b915e6e0508ec8812b92bcc6", "f51eba1d874d3fa5bb892d4dad8039d89dc8eee7",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("f5831260561a73cf26194eec3a3f8147050a2753", "f51eba1d874d3fa5bb892d4dad8039d89dc8eee7",
		                 GitRevDependencyType.MERGE_EDGE);
		revGraph.addEdge("e4e75c53fd4f66c19c594aa3dfcc683407f44093", "f5831260561a73cf26194eec3a3f8147050a2753",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("2fe888ece150487c5ba43264094dcb696c800216", "196075b582116f50b915e6e0508ec8812b92bcc6",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("1c0592306d9cfd1d19aa0133983c38448d167d69", "2fe888ece150487c5ba43264094dcb696c800216",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("7ecdf2c2bd15e19704c7e4f6c4e128357aafdcd2", "2fe888ece150487c5ba43264094dcb696c800216",
		                 GitRevDependencyType.MERGE_EDGE);
		revGraph.addEdge("34195982cb52661e5498ff880dd7b5b5b3230790", "7ecdf2c2bd15e19704c7e4f6c4e128357aafdcd2",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("e4e75c53fd4f66c19c594aa3dfcc683407f44093", "1c0592306d9cfd1d19aa0133983c38448d167d69",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("c997bb0d0e45920993b576f83c657c4a532c9b95", "e4e75c53fd4f66c19c594aa3dfcc683407f44093",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("34195982cb52661e5498ff880dd7b5b5b3230790", "c997bb0d0e45920993b576f83c657c4a532c9b95",
		                 GitRevDependencyType.BRANCH_EDGE);
		
		final Iterator<String> iterator = revGraph.getPreviousTransactions("17d6198f9c31d608d985ff4c9ce1dcc162dc8133")
		                                          .iterator();
		assertTrue(iterator.hasNext());
		assertEquals("8222f6b8a291bf938f96d9560fd1d61638c1689c", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("4a9e26c821ba113d9f36c95128b4184dc724750f", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("8e98f10673bae3345844c36eee2e9b21e8fed2d0", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("f51eba1d874d3fa5bb892d4dad8039d89dc8eee7", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("f5831260561a73cf26194eec3a3f8147050a2753", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("196075b582116f50b915e6e0508ec8812b92bcc6", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("2fe888ece150487c5ba43264094dcb696c800216", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("7ecdf2c2bd15e19704c7e4f6c4e128357aafdcd2", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("1c0592306d9cfd1d19aa0133983c38448d167d69", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("e4e75c53fd4f66c19c594aa3dfcc683407f44093", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("c997bb0d0e45920993b576f83c657c4a532c9b95", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("34195982cb52661e5498ff880dd7b5b5b3230790", iterator.next());
		assertFalse(iterator.hasNext());
		
	}
	
	@Test
	public void regressionTestRhino2() {
		/*
		 * @formatter:off
		 * 
		 *    *   280b1b8695286699770c5da85204e1718f7f4b66
         *    |\
         *    | * 702abfed3f8ca043b2636efd31c14ba7552603dd
         *    | *   cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2
         *    | |\
         *    | | * 94f8b9f16e9f3d423225b28619281a5ecf877275
         *    | * | 5813ab7d15c9c97ff45a44e051f8e9776a1f7e42
         *    | * | 8bc0679ca73760e68c0c27b54dc2855de34c1bdb
         *    | |/
         *    | *   9f6f106cdc16effd8c093defd47f1626195d03db
         *    | |\
         *    | | * 6bfee30b10fb0498f3d70f383814a669939bb1c7
         *    | |/
         *    | * 45702d2a094554789dc51bd23869ed5ddd8822a6
         *    * | 9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59
         *    |/
         *    * d522956171853fc2d7ca106d9c8d2b93e82df9d3
		 * 
		 */
		
		//@formatter:on
		
		final GitRevDependencyGraph revGraph = new GitRevDependencyGraph(new GitRepository());
		revGraph.addEdge("9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59", "280b1b8695286699770c5da85204e1718f7f4b66",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("702abfed3f8ca043b2636efd31c14ba7552603dd", "280b1b8695286699770c5da85204e1718f7f4b66",
		                 GitRevDependencyType.MERGE_EDGE);
		revGraph.addEdge("cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2", "702abfed3f8ca043b2636efd31c14ba7552603dd",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("5813ab7d15c9c97ff45a44e051f8e9776a1f7e42", "cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("94f8b9f16e9f3d423225b28619281a5ecf877275", "cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2",
		                 GitRevDependencyType.MERGE_EDGE);
		revGraph.addEdge("9f6f106cdc16effd8c093defd47f1626195d03db", "94f8b9f16e9f3d423225b28619281a5ecf877275",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("8bc0679ca73760e68c0c27b54dc2855de34c1bdb", "5813ab7d15c9c97ff45a44e051f8e9776a1f7e42",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("9f6f106cdc16effd8c093defd47f1626195d03db", "8bc0679ca73760e68c0c27b54dc2855de34c1bdb",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("45702d2a094554789dc51bd23869ed5ddd8822a6", "9f6f106cdc16effd8c093defd47f1626195d03db",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("6bfee30b10fb0498f3d70f383814a669939bb1c7", "9f6f106cdc16effd8c093defd47f1626195d03db",
		                 GitRevDependencyType.MERGE_EDGE);
		revGraph.addEdge("45702d2a094554789dc51bd23869ed5ddd8822a6", "6bfee30b10fb0498f3d70f383814a669939bb1c7",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("d522956171853fc2d7ca106d9c8d2b93e82df9d3", "45702d2a094554789dc51bd23869ed5ddd8822a6",
		                 GitRevDependencyType.BRANCH_EDGE);
		revGraph.addEdge("d522956171853fc2d7ca106d9c8d2b93e82df9d3", "9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59",
		                 GitRevDependencyType.BRANCH_EDGE);
		final Iterator<String> iterator = revGraph.getPreviousTransactions("280b1b8695286699770c5da85204e1718f7f4b66")
		                                          .iterator();
		
		assertTrue(iterator.hasNext());
		assertEquals("702abfed3f8ca043b2636efd31c14ba7552603dd", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("94f8b9f16e9f3d423225b28619281a5ecf877275", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("5813ab7d15c9c97ff45a44e051f8e9776a1f7e42", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("8bc0679ca73760e68c0c27b54dc2855de34c1bdb", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("9f6f106cdc16effd8c093defd47f1626195d03db", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("6bfee30b10fb0498f3d70f383814a669939bb1c7", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("45702d2a094554789dc51bd23869ed5ddd8822a6", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("d522956171853fc2d7ca106d9c8d2b93e82df9d3", iterator.next());
		assertFalse(iterator.hasNext());
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
		
		assertTrue(graph.existsPath("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6",
		                            "41a40fb23b54a49e91eb4cee510533eef810ec68"));
		assertFalse(graph.existsPath("cbcc33d919a27b9450d117f211a5f4f45615cab9",
		                             "d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6"));
		
	}
	
}
