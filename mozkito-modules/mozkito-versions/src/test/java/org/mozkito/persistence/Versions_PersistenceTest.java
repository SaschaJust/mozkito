/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.elements.RevDependencyGraph.EdgeType;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class Versions_PersistenceTest.
 */
@DatabaseSettings (unit = "versions", options = ConnectOptions.DROP_AND_CREATE_DATABASE)
public class Versions_PersistenceTest extends DatabaseTest {
	
	private PersonFactory personFactory;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		this.personFactory = new PersonFactory();
		
	}
	
	/**
	 * Test rcs branch.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DatabaseException
	 */
	@Test
	public void testBranch() throws IOException, DatabaseException {
		
		final RevDependencyGraph revDepGraph = new RevDependencyGraph();
		revDepGraph.addBranch("testBranch", "0123456789abcde");
		revDepGraph.addEdge("000000000000000", "0123456789abcde", EdgeType.BRANCH_EDGE);
		
		final VersionArchive versionArchive = new VersionArchive(revDepGraph);
		
		final ChangeSet beginTransaction = new ChangeSet(versionArchive, "000000000000000", "committed begin",
		                                                 new DateTime(),
		                                                 this.personFactory.get("just", "Sascha Just",
		                                                                        "sascha.just@mozkito.org"),
		                                                 "000000000000000");
		final ChangeSet endTransaction = new ChangeSet(versionArchive, "0123456789abcde", "committed end",
		                                               new DateTime(),
		                                               this.personFactory.get("just", "Sascha Just",
		                                                                      "sascha.just@mozkito.org"),
		                                               "0123456789abcde");
		final Branch testBranch = versionArchive.getBranch(null, "testBranch");
		testBranch.setHead(endTransaction);
		
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().save(beginTransaction);
		getPersistenceUtil().save(endTransaction);
		getPersistenceUtil().save(testBranch);
		beginTransaction.addChild(endTransaction);
		endTransaction.setBranchParent(beginTransaction);
		getPersistenceUtil().commitTransaction();
		
		final List<Branch> list = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Branch.class));
		
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertTrue(list.contains(testBranch));
		for (final Branch b : list) {
			if (b.getName().equals(testBranch.getName())) {
				assertEquals(endTransaction, testBranch.getHead());
			}
		}
	}
	
	/**
	 * Test rcs revision.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DatabaseException
	 */
	@Test
	public void testRevision() throws IOException, DatabaseException {
		
		final RevDependencyGraph revDepGraph = new RevDependencyGraph();
		revDepGraph.addBranch(Branch.MASTER_BRANCH_NAME, "0");
		
		final VersionArchive versionArchive = new VersionArchive(revDepGraph);
		
		final Person person = this.personFactory.get("just", null, null);
		final ChangeSet changeset = new ChangeSet(versionArchive, "0", "", new DateTime(), person, "");
		final Handle handle = new Handle(versionArchive);
		final Revision revision = new Revision(changeset, handle, ChangeType.Added);
		handle.assignRevision(revision, "test.java");
		
		assertTrue(changeset.getRevisions().contains(revision));
		versionArchive.getMasterBranch(null).setHead(changeset);
		
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().save(changeset);
		getPersistenceUtil().commitTransaction();
		
		assertTrue(changeset.getRevisions().contains(revision));
		
		// revision
		final List<Revision> revisionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Revision.class));
		assertFalse(revisionList.isEmpty());
		assertEquals(1, revisionList.size());
		assertEquals(revision, revisionList.get(0));
		assertEquals(changeset, revisionList.get(0).getChangeSet());
		assertEquals(ChangeType.Added, revisionList.get(0).getChangeType());
		assertEquals(handle, revisionList.get(0).getChangedFile());
		
		// file
		final List<Handle> fileList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Handle.class));
		assertFalse(fileList.isEmpty());
		assertEquals(1, fileList.size());
		assertEquals(handle, fileList.get(0));
		assertFalse(fileList.get(0).getChangedNames().isEmpty());
		assertEquals(1, fileList.get(0).getChangedNames().size());
		try {
			assertEquals("test.java", fileList.get(0).getLatestPath(getPersistenceUtil()));
		} catch (final NoSuchHandleException e) {
			fail();
		}
		
		// person
		final List<Person> personList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Person.class));
		assertFalse(personList.isEmpty());
		assertEquals(1, personList.size());
		assertEquals(person, personList.get(0));
		assertFalse(personList.get(0).getUsernames().isEmpty());
		assertEquals(1, personList.get(0).getUsernames().size());
		assertEquals("just", personList.get(0).getUsernames().iterator().next());
		assertEquals("just", personList.get(0).getUsernames().iterator().next());
		assertEquals("just", personList.get(0).getUsernames().iterator().next());
		assertTrue(personList.get(0).getEmailAddresses().isEmpty());
		assertTrue(personList.get(0).getFullnames().isEmpty());
		
		// transaction
		final List<ChangeSet> transactionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(ChangeSet.class));
		assertFalse(transactionList.isEmpty());
		assertEquals(1, transactionList.size());
		assertEquals(changeset, transactionList.get(0));
		assertEquals(person, transactionList.get(0).getAuthor());
		assertFalse(transactionList.get(0).getRevisions().isEmpty());
		assertEquals(1, transactionList.get(0).getRevisions().size());
	}
	
	/**
	 * Test save rcs file.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DatabaseException
	 */
	@Test
	public void testSaveHandle() throws IOException, DatabaseException {
		
		final RevDependencyGraph revDepGraph = new RevDependencyGraph();
		revDepGraph.addBranch(Branch.MASTER_BRANCH_NAME, "0");
		
		final VersionArchive versionArchive = new VersionArchive(revDepGraph);
		
		final Person person = this.personFactory.get("kim", null, null);
		final ChangeSet changeset = new ChangeSet(versionArchive, "0", "", new DateTime(), person, "");
		
		final Handle handle = new Handle(versionArchive);
		final Revision revision = new Revision(changeset, handle, ChangeType.Added);
		handle.assignRevision(revision, "formerTest.java");
		getPersistenceUtil().beginTransaction();
		
		versionArchive.getMasterBranch(getPersistenceUtil()).setHead(changeset);
		
		getPersistenceUtil().saveOrUpdate(changeset);
		getPersistenceUtil().commitTransaction();
		
		final List<Handle> fileList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Handle.class));
		assertEquals(1, fileList.size());
		assertEquals(handle, fileList.get(0));
		
		final List<Person> personList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Person.class));
		assertFalse(personList.isEmpty());
		assertTrue(personList.contains(person));
		
		final List<Revision> revisionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Revision.class));
		assertEquals(1, revisionList.size());
		assertEquals(revision, revisionList.get(0));
		
		final List<ChangeSet> transactionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(ChangeSet.class));
		assertFalse(transactionList.isEmpty());
		assertTrue(transactionList.contains(changeset));
	}
}
