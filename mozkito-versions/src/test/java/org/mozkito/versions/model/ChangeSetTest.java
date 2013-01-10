/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.versions.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.RevDependencyGraph.EdgeType;
import org.mozkito.versions.elements.ChangeType;

/**
 * The Class ChangeSetTest.
 */
public class ChangeSetTest {
	
	/**
	 * Test get changed files.
	 */
	@Test
	public void testGetChangedFiles() {
		final Person person = new Person("kim", "", "");
		
		final ChangeSet t_0 = new ChangeSet("0", "", new DateTime(), person, "");
		
		final VersionArchive versionArchive = new VersionArchive() {
			
			/**
             * 
             */
			private static final long serialVersionUID = 8388504356360016697L;
			
			@Override
			public ChangeSet getTransactionById(final String id) {
				switch (id) {
					case "0":
						return t_0;
					default:
						return null;
				}
			}
		};
		try {
			final RevDependencyGraph revDepGraph = new RevDependencyGraph();
			revDepGraph.addBranch("master", "0");
			versionArchive.setRevDependencyGraph(revDepGraph);
			
			final Handle handle = new Handle(versionArchive);
			handle.assignRevision(new Revision(t_0, handle, ChangeType.Added), "public.java");
			
			final Collection<Handle> changedFiles = t_0.getChangedFiles();
			assertEquals(1, changedFiles.size());
			assertTrue(changedFiles.contains(handle));
		} catch (final IOException e1) {
			fail();
		}
	}
	
	/**
	 * Test get revision for path.
	 */
	@Test
	public void testGetRevisionForPath() {
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
		
		final Person person = new Person("kim", "", "");
		
		final ChangeSet t_0 = new ChangeSet("0", "", new DateTime(), person, "");
		final ChangeSet t_1 = new ChangeSet("1", "", new DateTime(), person, "");
		final ChangeSet t_2 = new ChangeSet("2", "", new DateTime(), person, "");
		final ChangeSet t_3 = new ChangeSet("3", "", new DateTime(), person, "");
		final ChangeSet t_4 = new ChangeSet("4", "", new DateTime(), person, "");
		final ChangeSet t_5 = new ChangeSet("5", "", new DateTime(), person, "");
		
		t_1.setBranchParent(t_0);
		t_2.setBranchParent(t_0);
		t_3.setBranchParent(t_2);
		t_4.setBranchParent(t_2);
		t_4.setMergeParent(t_3);
		t_5.setBranchParent(t_1);
		t_5.setMergeParent(t_4);
		
		final VersionArchive versionArchive = new VersionArchive() {
			
			/**
             * 
             */
			private static final long serialVersionUID = 8388504356360016697L;
			
			@Override
			public ChangeSet getTransactionById(final String id) {
				switch (id) {
					case "0":
						return t_0;
					case "1":
						return t_1;
					case "2":
						return t_2;
					case "3":
						return t_3;
					case "4":
						return t_4;
					case "5":
						return t_5;
					default:
						return null;
				}
			}
		};
		try {
			final RevDependencyGraph revDepGraph = new RevDependencyGraph();
			revDepGraph.addBranch("master", "5");
			revDepGraph.addEdge("4", "5", EdgeType.MERGE_EDGE);
			revDepGraph.addEdge("1", "5", EdgeType.BRANCH_EDGE);
			revDepGraph.addEdge("3", "4", EdgeType.MERGE_EDGE);
			revDepGraph.addEdge("2", "4", EdgeType.BRANCH_EDGE);
			revDepGraph.addEdge("2", "3", EdgeType.BRANCH_EDGE);
			revDepGraph.addEdge("0", "2", EdgeType.BRANCH_EDGE);
			revDepGraph.addEdge("0", "1", EdgeType.BRANCH_EDGE);
			versionArchive.setRevDependencyGraph(revDepGraph);
			
			final Handle handle = new Handle(versionArchive);
			handle.assignRevision(new Revision(t_0, handle, ChangeType.Added), "public.java");
			
			new Revision(t_1, handle, ChangeType.Modified);
			
			final Handle hiddenFile = new Handle(versionArchive);
			hiddenFile.assignRevision(new Revision(t_2, hiddenFile, ChangeType.Added), "hidden.java");
			
			final Revision rCSRevision = new Revision(t_3, hiddenFile, ChangeType.Renamed);
			hiddenFile.assignRevision(rCSRevision, "moreHidden.java");
			
			final Revision revision2 = new Revision(t_4, hiddenFile, ChangeType.Modified);
			
			new Revision(t_5, handle, ChangeType.Modified);
			
			assertEquals(null, t_4.getRevisionForPath("hubba"));
			assertEquals(revision2, t_4.getRevisionForPath("moreHidden.java"));
			assertEquals(revision2, t_4.getRevisionForPath("/moreHidden.java"));
			assertEquals(rCSRevision, t_3.getRevisionForPath("moreHidden.java"));
			assertEquals(rCSRevision, t_3.getRevisionForPath("/moreHidden.java"));
		} catch (final IOException e1) {
			fail();
		}
		
	}
}
