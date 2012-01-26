/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class OpenJPA_RCS_NetTest {
	
	@AfterClass
	public static void afterClass() {
	}
	
	@BeforeClass
	public static void beforeClass() {
		
	}
	
	@Before
	public void setUp() throws Exception {
		System.err.println("Creating new test session factory.");
		OpenJPAUtil.createTestSessionFactory("rcs");
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			System.err.println("Executing global shutdown.");
			OpenJPAUtil.getInstance().globalShutdown();
		} catch (final UninitializedDatabaseException e) {
			System.err.println("Caught exception when trying to execute global shutdown.");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRCSBranch() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			final RCSBranch branch = new RCSBranch("testBranch");
			branch.setMergedIn("0123456789abcde");
			final RCSTransaction beginTransaction = RCSTransaction.createTransaction("000000000000000",
					"committed begin",
					new DateTime(),
					new Person("just", "Sascha Just",
							"sascha.just@st.cs.uni-saarland.de"),
					"000000000000000");
			final RCSTransaction endTransaction = RCSTransaction.createTransaction("0123456789abcde",
					"committed end",
					new DateTime(),
					new Person("just", "Sascha Just",
							"sascha.just@st.cs.uni-saarland.de"),
					"0123456789abcde");
			
			beginTransaction.setBranch(branch);
			endTransaction.setBranch(branch);
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(beginTransaction);
			persistenceUtil.save(endTransaction);
			branch.setBegin(beginTransaction);
			branch.setEnd(endTransaction);
			beginTransaction.addChild(endTransaction);
			persistenceUtil.commitTransaction();
			
			final List<RCSBranch> list = persistenceUtil.load(persistenceUtil.createCriteria(RCSBranch.class));
			
			assertFalse(list.isEmpty());
			assertEquals(1, list.size());
			for (final RCSBranch b : list) {
				assertEquals(branch, b);
				assertEquals("0123456789abcde", b.getMergedIn());
			}
		} catch (final UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRCSRevision() {
		System.err.println("TEST testRCSRevision");
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			
			final Person person = new Person("just", null, null);
			final RCSTransaction transaction = RCSTransaction.createTransaction("0", "", new DateTime(), person, "");
			final RCSFile file = new RCSFileManager().createFile("test.java", transaction);
			final RCSRevision revision = new RCSRevision(transaction, file, ChangeType.Added);
			
			assertTrue(transaction.getRevisions().contains(revision));
			transaction.setBranch(new RCSBranch("master"));
			persistenceUtil.beginTransaction();
			persistenceUtil.save(transaction);
			persistenceUtil.commitTransaction();
			
			assertTrue(transaction.getRevisions().contains(revision));
			
			// revision
			final List<RCSRevision> revisionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSRevision.class));
			assertFalse(revisionList.isEmpty());
			assertEquals(1, revisionList.size());
			assertEquals(revision, revisionList.get(0));
			assertEquals(transaction, revisionList.get(0).getTransaction());
			assertEquals(ChangeType.Added, revisionList.get(0).getChangeType());
			assertEquals(file, revisionList.get(0).getChangedFile());
			
			// file
			final List<RCSFile> fileList = persistenceUtil.load(persistenceUtil.createCriteria(RCSFile.class));
			assertFalse(fileList.isEmpty());
			assertEquals(1, fileList.size());
			assertEquals(file, fileList.get(0));
			assertFalse(fileList.get(0).getChangedNames().isEmpty());
			assertEquals(1, fileList.get(0).getChangedNames().size());
			assertEquals("test.java", fileList.get(0).getLatestPath());
			
			// person
			final List<Person> personList = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
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
			final List<RCSTransaction> transactionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSTransaction.class));
			assertFalse(transactionList.isEmpty());
			assertEquals(1, transactionList.size());
			assertEquals(transaction, transactionList.get(0));
			assertEquals(person, transactionList.get(0).getAuthor());
			assertFalse(transactionList.get(0).getRevisions().isEmpty());
			assertEquals(1, transactionList.get(0).getRevisions().size());
		} catch (final UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSaveRCSFile() {
		System.err.println("TEST testSaveRCSFile");
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			
			final RCSFileManager fileManager = new RCSFileManager();
			final Person person = new Person("kim", null, null);
			final RCSTransaction rcsTransaction = RCSTransaction.createTransaction("0", "", new DateTime(), person, "");
			
			final RCSFile file = fileManager.createFile("test.java", rcsTransaction);
			file.assignTransaction(rcsTransaction, "formerTest.java");
			final RCSRevision revision = new RCSRevision(rcsTransaction, file, ChangeType.Added);
			persistenceUtil.beginTransaction();
			rcsTransaction.setBranch(BranchFactory.getMasterBranch());
			persistenceUtil.saveOrUpdate(rcsTransaction);
			persistenceUtil.commitTransaction();
			
			final List<RCSFile> fileList = persistenceUtil.load(persistenceUtil.createCriteria(RCSFile.class));
			assertEquals(1, fileList.size());
			assertEquals(file, fileList.get(0));
			
			final List<Person> personList = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
			assertFalse(personList.isEmpty());
			assertTrue(personList.contains(person));
			
			final List<RCSRevision> revisionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSRevision.class));
			assertEquals(1, revisionList.size());
			assertEquals(revision, revisionList.get(0));
			
			final List<RCSTransaction> transactionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSTransaction.class));
			assertFalse(transactionList.isEmpty());
			assertTrue(transactionList.contains(rcsTransaction));
		} catch (final UninitializedDatabaseException e) {
			fail();
		}
		
	}
}
