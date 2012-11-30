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

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.persistence.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.RCSFileManager;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.File;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.Transaction;

/**
 * The Class OpenJPA_RCS_MozkitoTest.
 */
@DatabaseSettings (unit = "versions")
public class OpenJPA_RCS_MozkitoTest extends DatabaseTest {
	
	/** The branch factory. */
	private BranchFactory branchFactory;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		this.branchFactory = new BranchFactory(getPersistenceUtil());
	}
	
	/**
	 * Test rcs branch.
	 */
	@Test
	public void testRCSBranch() {
		
		final Branch branch = new Branch("testBranch");
		final Transaction beginTransaction = new Transaction("000000000000000", "committed begin",
		                                                           new DateTime(),
		                                                           new Person("just", "Sascha Just",
		                                                                      "sascha.just@mozkito.org"),
		                                                           "000000000000000");
		final Transaction endTransaction = new Transaction("0123456789abcde", "committed end", new DateTime(),
		                                                         new Person("just", "Sascha Just",
		                                                                    "sascha.just@mozkito.org"),
		                                                         "0123456789abcde");
		
		branch.setHead(endTransaction);
		
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().save(beginTransaction);
		getPersistenceUtil().save(endTransaction);
		getPersistenceUtil().save(branch);
		beginTransaction.addChild(endTransaction);
		endTransaction.setBranchParent(beginTransaction);
		getPersistenceUtil().commitTransaction();
		
		final List<Branch> list = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Branch.class));
		
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertTrue(list.contains(branch));
		for (final Branch b : list) {
			if (b.getName().equals(branch.getName())) {
				assertEquals(endTransaction, branch.getHead());
			}
		}
	}
	
	/**
	 * Test rcs revision.
	 */
	@Test
	public void testRCSRevision() {
		final Person person = new Person("just", null, null);
		final Transaction transaction = new Transaction("0", "", new DateTime(), person, "");
		final File file = new RCSFileManager().createFile("test.java", transaction);
		final Revision revision = new Revision(transaction, file, ChangeType.Added);
		
		assertTrue(transaction.getRevisions().contains(revision));
		this.branchFactory.getMasterBranch().setHead(transaction);
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().save(transaction);
		getPersistenceUtil().commitTransaction();
		
		assertTrue(transaction.getRevisions().contains(revision));
		
		// revision
		final List<Revision> revisionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Revision.class));
		assertFalse(revisionList.isEmpty());
		assertEquals(1, revisionList.size());
		assertEquals(revision, revisionList.get(0));
		assertEquals(transaction, revisionList.get(0).getTransaction());
		assertEquals(ChangeType.Added, revisionList.get(0).getChangeType());
		assertEquals(file, revisionList.get(0).getChangedFile());
		
		// file
		final List<File> fileList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(File.class));
		assertFalse(fileList.isEmpty());
		assertEquals(1, fileList.size());
		assertEquals(file, fileList.get(0));
		assertFalse(fileList.get(0).getChangedNames().isEmpty());
		assertEquals(1, fileList.get(0).getChangedNames().size());
		assertEquals("test.java", fileList.get(0).getLatestPath());
		
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
		final List<Transaction> transactionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Transaction.class));
		assertFalse(transactionList.isEmpty());
		assertEquals(1, transactionList.size());
		assertEquals(transaction, transactionList.get(0));
		assertEquals(person, transactionList.get(0).getAuthor());
		assertFalse(transactionList.get(0).getRevisions().isEmpty());
		assertEquals(1, transactionList.get(0).getRevisions().size());
	}
	
	/**
	 * Test save rcs file.
	 */
	@Test
	public void testSaveRCSFile() {
		final RCSFileManager fileManager = new RCSFileManager();
		final Person person = new Person("kim", null, null);
		final Transaction rcsTransaction = new Transaction("0", "", new DateTime(), person, "");
		
		final File file = fileManager.createFile("test.java", rcsTransaction);
		file.assignTransaction(rcsTransaction, "formerTest.java");
		final Revision revision = new Revision(rcsTransaction, file, ChangeType.Added);
		getPersistenceUtil().beginTransaction();
		
		this.branchFactory.getMasterBranch().setHead(rcsTransaction);
		
		getPersistenceUtil().saveOrUpdate(rcsTransaction);
		getPersistenceUtil().commitTransaction();
		
		final List<File> fileList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(File.class));
		assertEquals(1, fileList.size());
		assertEquals(file, fileList.get(0));
		
		final List<Person> personList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Person.class));
		assertFalse(personList.isEmpty());
		assertTrue(personList.contains(person));
		
		final List<Revision> revisionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Revision.class));
		assertEquals(1, revisionList.size());
		assertEquals(revision, revisionList.get(0));
		
		final List<Transaction> transactionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Transaction.class));
		assertFalse(transactionList.isEmpty());
		assertTrue(transactionList.contains(rcsTransaction));
	}
}
