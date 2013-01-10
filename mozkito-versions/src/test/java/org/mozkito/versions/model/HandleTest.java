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
package org.mozkito.versions.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.RevDependencyGraph.EdgeType;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.exceptions.NoSuchHandleException;

/**
 * The Class RCSFileTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class HandleTest {
	
	private Person             person;
	private RCSTransaction     t_0;
	private RCSTransaction     t_1;
	private RCSTransaction     t_2;
	private RCSTransaction     t_3;
	private RCSTransaction     t_4;
	private RCSTransaction     t_5;
	private VersionArchive     versionArchive;
	private RevDependencyGraph revDepGraph;
	
	/**
	 * After.
	 */
	@After
	public void after() {
		this.revDepGraph.close();
	}
	
	/**
	 * 
	 */
	@Before
	public void setup() {
		this.person = new Person("kim", "", "");
		
		this.t_0 = new RCSTransaction("0", "", new DateTime(), this.person, "");
		this.t_1 = new RCSTransaction("1", "", new DateTime(), this.person, "");
		this.t_2 = new RCSTransaction("2", "", new DateTime(), this.person, "");
		this.t_3 = new RCSTransaction("3", "", new DateTime(), this.person, "");
		this.t_4 = new RCSTransaction("4", "", new DateTime(), this.person, "");
		this.t_5 = new RCSTransaction("5", "", new DateTime(), this.person, "");
		
		this.t_1.setBranchParent(this.t_0);
		this.t_2.setBranchParent(this.t_0);
		this.t_3.setBranchParent(this.t_2);
		this.t_4.setBranchParent(this.t_2);
		this.t_4.setMergeParent(this.t_3);
		this.t_5.setBranchParent(this.t_1);
		this.t_5.setMergeParent(this.t_4);
		
		this.versionArchive = new VersionArchive() {
			
			/**
             * 
             */
			private static final long serialVersionUID = 8388504356360016697L;
			
			@Override
			public RCSTransaction getTransactionById(final String id) {
				switch (id) {
					case "0":
						return HandleTest.this.t_0;
					case "1":
						return HandleTest.this.t_1;
					case "2":
						return HandleTest.this.t_2;
					case "3":
						return HandleTest.this.t_3;
					case "4":
						return HandleTest.this.t_4;
					case "5":
						return HandleTest.this.t_5;
					default:
						return null;
				}
			}
		};
		this.revDepGraph = null;
		try {
			this.revDepGraph = new RevDependencyGraph();
		} catch (final IOException e1) {
			fail();
		}
		assertNotNull(this.revDepGraph);
		this.revDepGraph.addBranch("master", "5");
		this.revDepGraph.addEdge("4", "5", EdgeType.MERGE_EDGE);
		this.revDepGraph.addEdge("1", "5", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("3", "4", EdgeType.MERGE_EDGE);
		this.revDepGraph.addEdge("2", "4", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("2", "3", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("0", "2", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("0", "1", EdgeType.BRANCH_EDGE);
		this.versionArchive.setRevDependencyGraph(this.revDepGraph);
	}
	
	/**
	 * Test get path.
	 */
	@Test
	public void testGetPath() {
		/*
		 * @formatter:off
		 * 
		 *    *   5
	     *    |\
	     *    | *   4
	     *    | |\
	     *    | | * 3 <<---- Renaming file Hidden.java to moreHidden.java
	     *    | |/
	     *    | * 2 <<---- Adding file Hidden.java
	     *    * | 1
	     *    |/
	     *    * 0
		 * 
		 */
		
		//@formatter:on
		
		this.revDepGraph.addBranch("master", "5");
		this.revDepGraph.addEdge("4", "5", EdgeType.MERGE_EDGE);
		this.revDepGraph.addEdge("1", "5", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("3", "4", EdgeType.MERGE_EDGE);
		this.revDepGraph.addEdge("2", "4", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("2", "3", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("0", "2", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("0", "1", EdgeType.BRANCH_EDGE);
		this.versionArchive.setRevDependencyGraph(this.revDepGraph);
		
		final Handle handle = new Handle(this.versionArchive);
		handle.assignRevision(new RCSRevision(this.t_0, handle, ChangeType.Added), "public.java");
		handle.assignRevision(new RCSRevision(this.t_4, handle, ChangeType.Added), "new_name.java");
		
		this.t_1.setBranchParent(this.t_0);
		this.t_2.setBranchParent(this.t_1);
		this.t_3.setBranchParent(this.t_2);
		this.t_4.setBranchParent(this.t_2);
		this.t_4.setMergeParent(this.t_3);
		this.t_5.setBranchParent(this.t_1);
		this.t_5.setMergeParent(this.t_4);
		
		try {
			assertEquals("public.java", handle.getPath(this.t_0));
			assertEquals("new_name.java", handle.getPath(this.t_4));
			assertEquals("public.java", handle.getPath(this.t_3));
			assertEquals("public.java", handle.getPath(this.t_2));
		} catch (final NoSuchHandleException e) {
			fail();
		}
	}
	
	/**
	 * Test hidden file name.
	 */
	@Test
	public void testHiddenFileName() {
		/*
	 * @formatter:off
	 * 
	 *    *   5
     *    |\
     *    | *   4
     *    | |\
     *    | | * 3 <<---- Adding file Hidden.java
     *    | |/
     *    | * 2
     *    * | 1
     *    |/
     *    * 0
	 * 
	 */
	
	//@formatter:on
		
		final Handle handle = new Handle(this.versionArchive);
		handle.assignRevision(new RCSRevision(this.t_0, handle, ChangeType.Added), "public.java");
		
		new RCSRevision(this.t_1, handle, ChangeType.Modified);
		new RCSRevision(this.t_2, handle, ChangeType.Modified);
		
		final Handle hiddenFile = new Handle(this.versionArchive);
		hiddenFile.assignRevision(new RCSRevision(this.t_3, hiddenFile, ChangeType.Added), "hidden.java");
		
		new RCSRevision(this.t_4, hiddenFile, ChangeType.Modified);
		
		new RCSRevision(this.t_5, handle, ChangeType.Modified);
		
		try {
			assertEquals("hidden.java", hiddenFile.getPath(this.t_5));
		} catch (final NoSuchHandleException e) {
			fail();
		}
		
	}
	
	/**
	 * Test hidden file name2.
	 */
	@Test
	public void testHiddenFileName2() {
		/*
	 * @formatter:off
	 * 
	 *    *   5
     *    |\
     *    | *   4
     *    | |\
     *    | | * 3 <<---- Renaming file Hidden.java to moreHidden.java
     *    | |/
     *    | * 2 <<---- Adding file Hidden.java
     *    * | 1
     *    |/
     *    * 0
	 * 
	 */
	
	//@formatter:on
		
		final Handle rCSFile = new Handle(this.versionArchive);
		rCSFile.assignRevision(new RCSRevision(this.t_0, rCSFile, ChangeType.Added), "public.java");
		
		new RCSRevision(this.t_1, rCSFile, ChangeType.Modified);
		
		final Handle hiddenFile = new Handle(this.versionArchive);
		hiddenFile.assignRevision(new RCSRevision(this.t_2, hiddenFile, ChangeType.Added), "hidden.java");
		hiddenFile.assignRevision(new RCSRevision(this.t_3, hiddenFile, ChangeType.Renamed), "moreHidden.java");
		
		new RCSRevision(this.t_4, hiddenFile, ChangeType.Modified);
		new RCSRevision(this.t_5, rCSFile, ChangeType.Modified);
		
		try {
			assertEquals("moreHidden.java", hiddenFile.getPath(this.t_5));
		} catch (final NoSuchHandleException e) {
			fail();
		}
		
	}
}
