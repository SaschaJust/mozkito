/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.versions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.testing.VersionsTest;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.testing.annotation.RepositorySettings;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.exceptions.RepositoryOperationException;

/**
 * The Class RevDependencyGraphTest.
 */
@RepositorySettings ({ @RepositorySetting (type = RepositoryType.GIT, uri = "testGit.zip", id = "testGit") })
public class RevDependencyGraphRepoTest extends VersionsTest {
	
	/** The repo. */
	private Repository         repo;
	
	/** The graph. */
	private RevDependencyGraph graph;
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * Gets the strange tag.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	@Test
	public void getStrangeTag() throws IOException, RepositoryOperationException {
		final RevDependencyGraph graph = this.repo.getRevDependencyGraph();
		assertEquals(1, graph.getTags("927478915f2d8fb9135eb33d21cb8491c0e655be").size());
		assertEquals(true, graph.getTags("927478915f2d8fb9135eb33d21cb8491c0e655be").contains("tag_one"));
	}
	
	/**
	 * Tear down.
	 * 
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	@Before
	public void setup() throws RepositoryOperationException {
		assertTrue(getRepositories().containsKey("testGit"));
		this.repo = getRepositories().get("testGit");
		this.graph = this.repo.getRevDependencyGraph();
	}
	
	/**
	 * After class.
	 */
	@After
	public void tearDown() {
		// ignore
	}
	
	/**
	 * Test.
	 */
	@Test
	public void test() {
		
		String hash = "e52def97ebc1f78c9286b1e7c36783aa67604439";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) == null);
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "19bc6c11d2d8cff62f911f26bad29690c3cee256";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("e52def97ebc1f78c9286b1e7c36783aa67604439", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "9d647acdef18e1bc6137354359ae75e490a7687d";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "98d5c40ef3c14503a472ba4133ae3529c7578e30";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("19bc6c11d2d8cff62f911f26bad29690c3cee256", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("9d647acdef18e1bc6137354359ae75e490a7687d", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "cbcc33d919a27b9450d117f211a5f4f45615cab9";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "ae94d7fa81437cbbd723049e3951f9daaa62a7c0";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("cbcc33d919a27b9450d117f211a5f4f45615cab9", this.graph.getBranchParent(hash));
		assertFalse(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.getMergeParents(hash).contains("98d5c40ef3c14503a472ba4133ae3529c7578e30"));
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "8273c1e51992a4d7a1da012dbb416864c2749a7f";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8", this.graph.getBranchParent(hash));
		assertFalse(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.getMergeParents(hash).contains("ae94d7fa81437cbbd723049e3951f9daaa62a7c0"));
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "927478915f2d8fb9135eb33d21cb8491c0e655be"; // tag_one
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(1, this.graph.getTags(hash).size());
		assertTrue(this.graph.getTags(hash).contains("tag_one"));
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("8273c1e51992a4d7a1da012dbb416864c2749a7f", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "1ac6aaa05eb6d55939b20e70ec818bb413417757";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "41a40fb23b54a49e91eb4cee510533eef810ec68";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("927478915f2d8fb9135eb33d21cb8491c0e655be", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "376adc0f9371129a76766f8030f2e576165358c1";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("1ac6aaa05eb6d55939b20e70ec818bb413417757", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "637acf68104e7bdff8235fb2e1a254300ffea3cb";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("41a40fb23b54a49e91eb4cee510533eef810ec68", this.graph.getBranchParent(hash));
		assertFalse(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.getMergeParents(hash).contains("376adc0f9371129a76766f8030f2e576165358c1"));
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("376adc0f9371129a76766f8030f2e576165358c1", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "9be561b3657e2b1da2b09d675dddd5f45c47f57c";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("637acf68104e7bdff8235fb2e1a254300ffea3cb", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9", this.graph.getBranchParent(hash));
		assertFalse(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.getMergeParents(hash).contains("9be561b3657e2b1da2b09d675dddd5f45c47f57c"));
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "fe56f365f798c3742bac5e56f5ff30eca4f622c6";
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("9be561b3657e2b1da2b09d675dddd5f45c47f57c", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertTrue(this.graph.isBranchHead(hash) == null);
		
		hash = "96a9f105774b50f1fa3361212c4d12ae057a4285"; // HEAD master
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("fe56f365f798c3742bac5e56f5ff30eca4f622c6", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertEquals("master", this.graph.isBranchHead(hash));
		
		hash = "67635fe9efeb2fd3751df9ea67650c71e59e3df1"; // HEAD maintenance
		assertTrue(this.graph.existsVertex(hash));
		assertEquals(0, this.graph.getTags(hash).size());
		assertTrue(this.graph.getBranchParent(hash) != null);
		assertEquals("a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2", this.graph.getBranchParent(hash));
		assertTrue(this.graph.getMergeParents(hash).isEmpty());
		assertEquals("origin/maintenance", this.graph.isBranchHead(hash));
		
	}
	
	/**
	 * Test exist path success.
	 */
	@Test
	public void testExistPathFail() {
		
		assertEquals(false, this.graph.existsPath("cbcc33d919a27b9450d117f211a5f4f45615cab9",
		                                          "d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6"));
	}
	
	/**
	 * Test exist path success.
	 */
	@Test
	public void testExistPathSuccess() {
		
		assertEquals(true, this.graph.containsEdge("ae94d7fa81437cbbd723049e3951f9daaa62a7c0",
		                                           "cbcc33d919a27b9450d117f211a5f4f45615cab9"));
		assertEquals(false, this.graph.containsEdge("cbcc33d919a27b9450d117f211a5f4f45615cab9",
		                                            "ae94d7fa81437cbbd723049e3951f9daaa62a7c0"));
		assertEquals(true, this.graph.existsPath("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6",
		                                         "41a40fb23b54a49e91eb4cee510533eef810ec68"));
		assertEquals(false, this.graph.existsPath("41a40fb23b54a49e91eb4cee510533eef810ec68",
		                                          "d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6"));
	}
	
	/**
	 * Test exist path success.
	 */
	@Test
	public void testExistSimplePath() {
		assertEquals(true, this.graph.existsPath("cbcc33d919a27b9450d117f211a5f4f45615cab9",
		                                         "cbcc33d919a27b9450d117f211a5f4f45615cab9"));
	}
	
	/**
	 * Test get vertices.
	 * 
	 * @throws RepositoryOperationException
	 */
	@Test
	public void testGetVertices() throws RepositoryOperationException {
		final Set<String> chnageSetIds = new HashSet<>();
		for (final String v : this.repo.getRevDependencyGraph().getVertices()) {
			chnageSetIds.add(v);
		}
		assertEquals(20, chnageSetIds.size());
		assertEquals(true, chnageSetIds.contains("96a9f105774b50f1fa3361212c4d12ae057a4285"));
		assertEquals(true, chnageSetIds.contains("fe56f365f798c3742bac5e56f5ff30eca4f622c6"));
		assertEquals(true, chnageSetIds.contains("9be561b3657e2b1da2b09d675dddd5f45c47f57c"));
		assertEquals(true, chnageSetIds.contains("637acf68104e7bdff8235fb2e1a254300ffea3cb"));
		assertEquals(true, chnageSetIds.contains("376adc0f9371129a76766f8030f2e576165358c1"));
		assertEquals(true, chnageSetIds.contains("41a40fb23b54a49e91eb4cee510533eef810ec68"));
		assertEquals(true, chnageSetIds.contains("1ac6aaa05eb6d55939b20e70ec818bb413417757"));
		assertEquals(true, chnageSetIds.contains("927478915f2d8fb9135eb33d21cb8491c0e655be"));
		assertEquals(true, chnageSetIds.contains("8273c1e51992a4d7a1da012dbb416864c2749a7f"));
		assertEquals(true, chnageSetIds.contains("ae94d7fa81437cbbd723049e3951f9daaa62a7c0"));
		assertEquals(true, chnageSetIds.contains("cbcc33d919a27b9450d117f211a5f4f45615cab9"));
		assertEquals(true, chnageSetIds.contains("deeefc5f6ab45a88c568fc8f27ee6f42e4a191b8"));
		assertEquals(true, chnageSetIds.contains("d23c3c69e8b9b8d8c0ee6ef08ea6f1944e186df6"));
		assertEquals(true, chnageSetIds.contains("98d5c40ef3c14503a472ba4133ae3529c7578e30"));
		assertEquals(true, chnageSetIds.contains("9d647acdef18e1bc6137354359ae75e490a7687d"));
		assertEquals(true, chnageSetIds.contains("19bc6c11d2d8cff62f911f26bad29690c3cee256"));
		assertEquals(true, chnageSetIds.contains("e52def97ebc1f78c9286b1e7c36783aa67604439"));
		assertEquals(true, chnageSetIds.contains("d98b5a8740dbbe912b711e3a29dcc4fa3d3890e9"));
		assertEquals(true, chnageSetIds.contains("a92759a8824c8a13c60f9d1c04fb16bd7bb37cc2"));
		assertEquals(true, chnageSetIds.contains("67635fe9efeb2fd3751df9ea67650c71e59e3df1"));
		
	}
	
}
