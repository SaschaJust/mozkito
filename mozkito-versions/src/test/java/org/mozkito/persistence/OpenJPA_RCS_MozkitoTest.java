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
import org.junit.Test;

import org.mozkito.persistence.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.RCSFileManager;
import org.mozkito.versions.model.RCSBranch;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSRevision;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class OpenJPA_RCS_MozkitoTest.
 */
@DatabaseSettings (unit = "versions", options = ConnectOptions.DB_DROP_CREATE)
public class OpenJPA_RCS_MozkitoTest extends DatabaseTest {
	
	/** The branch factory. */
	private BranchFactory branchFactory;
	
	/**
	 * Test rcs branch.
	 */
	@Test
	public void testRCSBranch() {
		this.branchFactory = new BranchFactory(getPersistenceUtil());
		final RCSBranch rCSBranch = this.branchFactory.getBranch("testBranch");
		final RCSTransaction beginTransaction = new RCSTransaction("000000000000000", "committed begin", new DateTime(),
		                                                     new Person("just", "Sascha Just",
		                                                                "sascha.just@mozkito.org"), "000000000000000");
		final RCSTransaction endTransaction = new RCSTransaction(
		                                                   "0123456789abcde",
		                                                   "committed end",
		                                                   new DateTime(),
		                                                   new Person("just", "Sascha Just", "sascha.just@mozkito.org"),
		                                                   "0123456789abcde");
		
		rCSBranch.setHead(endTransaction);
		
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().save(beginTransaction);
		getPersistenceUtil().save(endTransaction);
		getPersistenceUtil().save(rCSBranch);
		beginTransaction.addChild(endTransaction);
		endTransaction.setBranchParent(beginTransaction);
		getPersistenceUtil().commitTransaction();
		
		final List<RCSBranch> list = getPersistenceUtil().load(getPersistenceUtil().createCriteria(RCSBranch.class));
		
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertTrue(list.contains(rCSBranch));
		for (final RCSBranch b : list) {
			if (b.getName().equals(rCSBranch.getName())) {
				assertEquals(endTransaction, rCSBranch.getHead());
			}
		}
	}
	
	/**
	 * Test rcs revision.
	 */
	@Test
	public void testRCSRevision() {
		this.branchFactory = new BranchFactory(getPersistenceUtil());
		final Person person = new Person("just", null, null);
		final RCSTransaction rCSTransaction = new RCSTransaction("0", "", new DateTime(), person, "");
		final RCSFile rCSFile = new RCSFileManager().createFile("test.java", rCSTransaction);
		final RCSRevision rCSRevision = new RCSRevision(rCSTransaction, rCSFile, ChangeType.Added);
		
		assertTrue(rCSTransaction.getRevisions().contains(rCSRevision));
		this.branchFactory.getMasterBranch().setHead(rCSTransaction);
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().save(rCSTransaction);
		getPersistenceUtil().commitTransaction();
		
		assertTrue(rCSTransaction.getRevisions().contains(rCSRevision));
		
		// revision
		final List<RCSRevision> revisionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(RCSRevision.class));
		assertFalse(revisionList.isEmpty());
		assertEquals(1, revisionList.size());
		assertEquals(rCSRevision, revisionList.get(0));
		assertEquals(rCSTransaction, revisionList.get(0).getTransaction());
		assertEquals(ChangeType.Added, revisionList.get(0).getChangeType());
		assertEquals(rCSFile, revisionList.get(0).getChangedFile());
		
		// file
		final List<RCSFile> fileList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(RCSFile.class));
		assertFalse(fileList.isEmpty());
		assertEquals(1, fileList.size());
		assertEquals(rCSFile, fileList.get(0));
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
		final List<RCSTransaction> transactionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(RCSTransaction.class));
		assertFalse(transactionList.isEmpty());
		assertEquals(1, transactionList.size());
		assertEquals(rCSTransaction, transactionList.get(0));
		assertEquals(person, transactionList.get(0).getAuthor());
		assertFalse(transactionList.get(0).getRevisions().isEmpty());
		assertEquals(1, transactionList.get(0).getRevisions().size());
	}
	
	/**
	 * Test save rcs file.
	 */
	@Test
	public void testSaveRCSFile() {
		this.branchFactory = new BranchFactory(getPersistenceUtil());
		final RCSFileManager fileManager = new RCSFileManager();
		final Person person = new Person("kim", null, null);
		final RCSTransaction rcsTransaction = new RCSTransaction("0", "", new DateTime(), person, "");
		
		final RCSFile rCSFile = fileManager.createFile("test.java", rcsTransaction);
		rCSFile.assignTransaction(rcsTransaction, "formerTest.java");
		final RCSRevision rCSRevision = new RCSRevision(rcsTransaction, rCSFile, ChangeType.Added);
		getPersistenceUtil().beginTransaction();
		
		this.branchFactory.getMasterBranch().setHead(rcsTransaction);
		
		getPersistenceUtil().saveOrUpdate(rcsTransaction);
		getPersistenceUtil().commitTransaction();
		
		final List<RCSFile> fileList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(RCSFile.class));
		assertEquals(1, fileList.size());
		assertEquals(rCSFile, fileList.get(0));
		
		final List<Person> personList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Person.class));
		assertFalse(personList.isEmpty());
		assertTrue(personList.contains(person));
		
		final List<RCSRevision> revisionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(RCSRevision.class));
		assertEquals(1, revisionList.size());
		assertEquals(rCSRevision, revisionList.get(0));
		
		final List<RCSTransaction> transactionList = getPersistenceUtil().load(getPersistenceUtil().createCriteria(RCSTransaction.class));
		assertFalse(transactionList.isEmpty());
		assertTrue(transactionList.contains(rcsTransaction));
	}
}
