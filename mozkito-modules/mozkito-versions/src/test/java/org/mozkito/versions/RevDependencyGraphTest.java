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
import java.util.Iterator;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.Test;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.elements.RevDependencyGraph.EdgeType;

/**
 * The Class RevDependencyGraphTest.
 */
public class RevDependencyGraphTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * MOZKIT o_101_test.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void MOZKITO_101_test() throws IOException {
		/* 
		 * @formatter:off
		 *        *---.   commit b072cf44964ebb6555ecc3cb1643065928129ca1
         *        |\ \ \  
         *        | | | |\
         *        | | | | |                   
         *        | | | | * commit 4f0200a06d1a57a908a37428cfb25ebc7f61fe69
         *        | | | | |                   
         *        | | | * | commit 3e56c3fc1884c8cdbfe9da55c689d26461592f55
         *        | | | |/  
         *        | | | |                  
         *        | | * | commit 1e19eb3ecece1931b5363a797fd2b30f8edc6180
         *        | | |/  
         *        | | | 
         *        | | |                 
         *        | * | commit 3d03990abc68f39e7a92ed9594abb658879a6d7b
         *        | | | 
         *        | | |                 
         *        | * | commit 3bbb65df3a5cb2cedb1d8080d6b70697c826a4a9
         *        | |/  
         *        | |   
         *        | |                
         *        * | commit 24c8b60f7ec2e92e1db69d3b11fb96ed4b6992d2
         *        |/  
         *        |
         *        * commit 64ff0470944f71d0e25d6ff2b02892a759d95a9e  
	     * 
		 */
		
		final RevDependencyGraph revGraph = new RevDependencyGraph();
		
		revGraph.addEdge("24c8b60f7ec2e92e1db69d3b11fb96ed4b6992d2", "b072cf44964ebb6555ecc3cb1643065928129ca1",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("64ff0470944f71d0e25d6ff2b02892a759d95a9e", "24c8b60f7ec2e92e1db69d3b11fb96ed4b6992d2",
		                 EdgeType.BRANCH_EDGE);
		
		revGraph.addEdge("4f0200a06d1a57a908a37428cfb25ebc7f61fe69", "b072cf44964ebb6555ecc3cb1643065928129ca1",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("64ff0470944f71d0e25d6ff2b02892a759d95a9e", "4f0200a06d1a57a908a37428cfb25ebc7f61fe69",
		                 EdgeType.BRANCH_EDGE);
		
		revGraph.addEdge("3e56c3fc1884c8cdbfe9da55c689d26461592f55", "b072cf44964ebb6555ecc3cb1643065928129ca1",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("64ff0470944f71d0e25d6ff2b02892a759d95a9e", "3e56c3fc1884c8cdbfe9da55c689d26461592f55",
		                 EdgeType.BRANCH_EDGE);
		
		revGraph.addEdge("1e19eb3ecece1931b5363a797fd2b30f8edc6180", "b072cf44964ebb6555ecc3cb1643065928129ca1",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("64ff0470944f71d0e25d6ff2b02892a759d95a9e", "1e19eb3ecece1931b5363a797fd2b30f8edc6180",
		                 EdgeType.BRANCH_EDGE);
		
		
		revGraph.addEdge("3d03990abc68f39e7a92ed9594abb658879a6d7b", "b072cf44964ebb6555ecc3cb1643065928129ca1",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("3bbb65df3a5cb2cedb1d8080d6b70697c826a4a9", "3d03990abc68f39e7a92ed9594abb658879a6d7b",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("64ff0470944f71d0e25d6ff2b02892a759d95a9e", "3bbb65df3a5cb2cedb1d8080d6b70697c826a4a9",
		                 EdgeType.BRANCH_EDGE);
		
		
		final Iterator<String> iterator = revGraph.getPreviousTransactions("b072cf44964ebb6555ecc3cb1643065928129ca1")
		                                          .iterator();
		assertTrue(iterator.hasNext());
		assertEquals("4f0200a06d1a57a908a37428cfb25ebc7f61fe69",iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("3e56c3fc1884c8cdbfe9da55c689d26461592f55",iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("1e19eb3ecece1931b5363a797fd2b30f8edc6180",iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("3d03990abc68f39e7a92ed9594abb658879a6d7b",iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("3bbb65df3a5cb2cedb1d8080d6b70697c826a4a9",iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("24c8b60f7ec2e92e1db69d3b11fb96ed4b6992d2",iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("64ff0470944f71d0e25d6ff2b02892a759d95a9e",iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	/**
	 * Regression test rhino.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void regressionTestRhino() throws IOException {
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
		
		final RevDependencyGraph revGraph = new RevDependencyGraph();
		
		revGraph.addEdge("8222f6b8a291bf938f96d9560fd1d61638c1689c", "17d6198f9c31d608d985ff4c9ce1dcc162dc8133",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("8e98f10673bae3345844c36eee2e9b21e8fed2d0", "8222f6b8a291bf938f96d9560fd1d61638c1689c",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("4a9e26c821ba113d9f36c95128b4184dc724750f", "8222f6b8a291bf938f96d9560fd1d61638c1689c",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("f5831260561a73cf26194eec3a3f8147050a2753", "4a9e26c821ba113d9f36c95128b4184dc724750f",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("f51eba1d874d3fa5bb892d4dad8039d89dc8eee7", "8e98f10673bae3345844c36eee2e9b21e8fed2d0",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("196075b582116f50b915e6e0508ec8812b92bcc6", "f51eba1d874d3fa5bb892d4dad8039d89dc8eee7",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("f5831260561a73cf26194eec3a3f8147050a2753", "f51eba1d874d3fa5bb892d4dad8039d89dc8eee7",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("e4e75c53fd4f66c19c594aa3dfcc683407f44093", "f5831260561a73cf26194eec3a3f8147050a2753",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("2fe888ece150487c5ba43264094dcb696c800216", "196075b582116f50b915e6e0508ec8812b92bcc6",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("1c0592306d9cfd1d19aa0133983c38448d167d69", "2fe888ece150487c5ba43264094dcb696c800216",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("7ecdf2c2bd15e19704c7e4f6c4e128357aafdcd2", "2fe888ece150487c5ba43264094dcb696c800216",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("34195982cb52661e5498ff880dd7b5b5b3230790", "7ecdf2c2bd15e19704c7e4f6c4e128357aafdcd2",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("e4e75c53fd4f66c19c594aa3dfcc683407f44093", "1c0592306d9cfd1d19aa0133983c38448d167d69",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("c997bb0d0e45920993b576f83c657c4a532c9b95", "e4e75c53fd4f66c19c594aa3dfcc683407f44093",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("34195982cb52661e5498ff880dd7b5b5b3230790", "c997bb0d0e45920993b576f83c657c4a532c9b95",
		                 EdgeType.BRANCH_EDGE);
		
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
	
	/**
	 * Regression test rhino2.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void regressionTestRhino2() throws IOException {
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
		
		final RevDependencyGraph revGraph = new RevDependencyGraph();
		revGraph.addEdge("9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59", "280b1b8695286699770c5da85204e1718f7f4b66",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("702abfed3f8ca043b2636efd31c14ba7552603dd", "280b1b8695286699770c5da85204e1718f7f4b66",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2", "702abfed3f8ca043b2636efd31c14ba7552603dd",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("5813ab7d15c9c97ff45a44e051f8e9776a1f7e42", "cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("94f8b9f16e9f3d423225b28619281a5ecf877275", "cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("9f6f106cdc16effd8c093defd47f1626195d03db", "94f8b9f16e9f3d423225b28619281a5ecf877275",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("8bc0679ca73760e68c0c27b54dc2855de34c1bdb", "5813ab7d15c9c97ff45a44e051f8e9776a1f7e42",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("9f6f106cdc16effd8c093defd47f1626195d03db", "8bc0679ca73760e68c0c27b54dc2855de34c1bdb",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("45702d2a094554789dc51bd23869ed5ddd8822a6", "9f6f106cdc16effd8c093defd47f1626195d03db",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("6bfee30b10fb0498f3d70f383814a669939bb1c7", "9f6f106cdc16effd8c093defd47f1626195d03db",
		                 EdgeType.MERGE_EDGE);
		revGraph.addEdge("45702d2a094554789dc51bd23869ed5ddd8822a6", "6bfee30b10fb0498f3d70f383814a669939bb1c7",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("d522956171853fc2d7ca106d9c8d2b93e82df9d3", "45702d2a094554789dc51bd23869ed5ddd8822a6",
		                 EdgeType.BRANCH_EDGE);
		revGraph.addEdge("d522956171853fc2d7ca106d9c8d2b93e82df9d3", "9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59",
		                 EdgeType.BRANCH_EDGE);
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
	
	/**
	 * Test add change set twice.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testAddBranchTwice() throws IOException {
		final RevDependencyGraph graph = new RevDependencyGraph();
		assertTrue(graph.addBranch("hubba", "changeSet"));
		assertFalse(graph.addBranch("hubba", "changeSet2"));
	}
	
	/**
	 * Test add change set twice.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testAddChangeSetTwice() throws IOException {
		final RevDependencyGraph graph = new RevDependencyGraph();
		assertTrue(graph.addChangeSet("hubba"));
		assertFalse(graph.addChangeSet("hubba"));
	}
	
	/**
	 * Test add edge twice.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testAddEdgeTwice() throws IOException {
		final RevDependencyGraph graph = new RevDependencyGraph();
		assertEquals(true, graph.addEdge("a", "b", EdgeType.BRANCH_EDGE));
		assertEquals(false, graph.addEdge("a", "b", EdgeType.BRANCH_EDGE));
	}
}
