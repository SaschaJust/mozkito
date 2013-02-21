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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.persistence.model.Person;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.elements.RevDependencyGraph.EdgeType;
import org.mozkito.versions.exceptions.NoSuchHandleException;

/**
 * The Class HandleTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class HandleTest {
	
	/** The person. */
	private Person             person;
	
	/** The t_0. */
	private ChangeSet          t_0;
	
	/** The t_1. */
	private ChangeSet          t_1;
	
	/** The t_2. */
	private ChangeSet          t_2;
	
	/** The t_3. */
	private ChangeSet          t_3;
	
	/** The t_4. */
	private ChangeSet          t_4;
	
	/** The t_5. */
	private ChangeSet          t_5;
	
	/** The version archive. */
	private VersionArchive     versionArchive;
	
	/** The rev dep graph. */
	private RevDependencyGraph revDepGraph;
	
	/**
	 * After.
	 */
	@After
	public void after() {
		// ignore
	}
	
	/**
	 * Setup.
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		this.person = new Person("kim", "", "");
		
		this.revDepGraph = new RevDependencyGraph();
		this.revDepGraph.addBranch(Branch.MASTER_BRANCH_NAME, "5");
		this.revDepGraph.addEdge("4", "5", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("3", "4", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("2", "3", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("1", "2", EdgeType.BRANCH_EDGE);
		this.revDepGraph.addEdge("0", "1", EdgeType.BRANCH_EDGE);
		this.versionArchive = new VersionArchive(this.revDepGraph);
		
		this.t_0 = new ChangeSet(this.versionArchive, "0", "", new DateTime(), this.person, "");
		this.t_1 = new ChangeSet(this.versionArchive, "1", "", new DateTime(), this.person, "");
		this.t_2 = new ChangeSet(this.versionArchive, "2", "", new DateTime(), this.person, "");
		this.t_3 = new ChangeSet(this.versionArchive, "3", "", new DateTime(), this.person, "");
		this.t_4 = new ChangeSet(this.versionArchive, "4", "", new DateTime(), this.person, "");
		this.t_5 = new ChangeSet(this.versionArchive, "5", "", new DateTime(), this.person, "");
		
		this.t_1.setBranchParent(this.t_0);
		this.t_2.setBranchParent(this.t_0);
		this.t_3.setBranchParent(this.t_2);
		this.t_4.setBranchParent(this.t_2);
		this.t_4.setMergeParent(this.t_3);
		this.t_5.setBranchParent(this.t_1);
		this.t_5.setMergeParent(this.t_4);
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
		
		final Handle handle = new Handle(this.versionArchive);
		handle.assignRevision(new Revision(this.t_0, handle, ChangeType.Added), "public.java");
		handle.assignRevision(new Revision(this.t_4, handle, ChangeType.Added), "new_name.java");
		
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
		handle.assignRevision(new Revision(this.t_0, handle, ChangeType.Added), "public.java");
		
		new Revision(this.t_1, handle, ChangeType.Modified);
		new Revision(this.t_2, handle, ChangeType.Modified);
		
		final Handle hiddenFile = new Handle(this.versionArchive);
		hiddenFile.assignRevision(new Revision(this.t_3, hiddenFile, ChangeType.Added), "hidden.java");
		
		new Revision(this.t_4, hiddenFile, ChangeType.Modified);
		
		new Revision(this.t_5, handle, ChangeType.Modified);
		
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
		
		final Handle handle = new Handle(this.versionArchive);
		handle.assignRevision(new Revision(this.t_0, handle, ChangeType.Added), "public.java");
		
		new Revision(this.t_1, handle, ChangeType.Modified);
		
		final Handle hiddenFile = new Handle(this.versionArchive);
		hiddenFile.assignRevision(new Revision(this.t_2, hiddenFile, ChangeType.Added), "hidden.java");
		hiddenFile.assignRevision(new Revision(this.t_3, hiddenFile, ChangeType.Renamed), "moreHidden.java");
		
		new Revision(this.t_4, hiddenFile, ChangeType.Modified);
		new Revision(this.t_5, handle, ChangeType.Modified);
		
		try {
			assertEquals("moreHidden.java", hiddenFile.getPath(this.t_5));
		} catch (final NoSuchHandleException e) {
			fail();
		}
		
	}
}
