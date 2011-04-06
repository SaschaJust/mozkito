package de.unisaarland.cs.st.reposuite.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

public class OpenJPATest {
	
	@AfterClass
	public static void afterClass() {
	}
	
	@BeforeClass
	public static void beforeClass() {
		
	}
	
	@Before
	public void setUp() throws Exception {
		// Logger.setLogLevel(LogLevel.OFF);
		Properties properties = new Properties();
		String url = "jdbc:postgresql://quentin.cs.uni-saarland.de/reposuiteTest";
		properties.put("openjpa.ConnectionURL", url);
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(SchemaAction='add,deleteTableContents')");
		properties.put("openjpa.ConnectionDriverName", "org.postgresql.Driver");
		properties.put("openjpa.ConnectionUserName", "miner");
		properties.put("openjpa.ConnectionPassword", "miner");
		
		OpenJPAUtil.createSessionFactory(properties);
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			OpenJPAUtil.getInstance().shutdown();
		} catch (UninitializedDatabaseException e) {
			
		}
	}
	
	@Test
	public void testPerson() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			Person person = new Person("username", "fullname", "em@i.l");
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(person);
			persistenceUtil.commitTransaction();
			
			List<Person> list = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
			
			assertFalse(list.isEmpty());
			assertEquals(1, list.size());
			assertEquals(person, list.get(0));
		} catch (UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPersonContainer() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			PersonContainer personContainer = new PersonContainer();
			Person person1 = new Person("username1", "full name1", "em1@i.l");
			Person person2 = new Person("username2", "full name2", "em21@i.l");
			Person person3 = new Person("username3", "full name3", "em3@i.l");
			
			personContainer.add("role1", person1);
			personContainer.add("role2", person2);
			personContainer.add("role3", person3);
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(personContainer);
			persistenceUtil.commitTransaction();
			
			List<PersonContainer> list = persistenceUtil.load(persistenceUtil.createCriteria(PersonContainer.class));
			
			assertFalse(list.isEmpty());
			assertEquals(1, list.size());
			assertEquals(personContainer, list.get(0));
			assertFalse(list.get(0).isEmpty());
			assertEquals(3, list.get(0).size());
			assertEquals(person1, list.get(0).get("role1"));
			assertEquals(person2, list.get(0).get("role2"));
			assertEquals(person3, list.get(0).get("role3"));
		} catch (UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRCSBranch() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			RCSBranch branch = new RCSBranch("testBranch");
			branch.setMergedIn("0123456789abcde");
			RCSTransaction beginTransaction = RCSTransaction.createTransaction("000000000000000",
			                                                                   "committed begin",
			                                                                   new DateTime(),
			                                                                   new Person("just", "Sascha Just",
			                                                                              "sascha.just@st.cs.uni-saarland.de"),
			                                                                   "000000000000000");
			RCSTransaction endTransaction = RCSTransaction.createTransaction("0123456789abcde",
			                                                                 "committed end",
			                                                                 new DateTime(),
			                                                                 new Person("just", "Sascha Just",
			                                                                            "sascha.just@st.cs.uni-saarland.de"),
			                                                                 "0123456789abcde");
			
			branch.setBegin(beginTransaction);
			branch.setEnd(endTransaction);
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(beginTransaction);
			persistenceUtil.save(endTransaction);
			persistenceUtil.save(branch);
			beginTransaction.setBranch(branch);
			endTransaction.setBranch(branch);
			beginTransaction.addChild(endTransaction);
			persistenceUtil.update(beginTransaction);
			persistenceUtil.update(endTransaction);
			persistenceUtil.commitTransaction();
			
			List<RCSBranch> list = persistenceUtil.load(persistenceUtil.createCriteria(RCSBranch.class));
			
			assertFalse(list.isEmpty());
			assertEquals(2, list.size());
			for (RCSBranch b : list) {
				if (b.getName().equals(RCSBranch.MASTER.getName())) {
					assertEquals(RCSBranch.MASTER, b);
				} else if (b.getName().equals(branch.getName())) {
					assertEquals(branch, b);
					assertEquals("0123456789abcde", b.getMergedIn());
				} else {
					fail("Invalid branch information loaded.");
				}
			}
		} catch (UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRCSRevision() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			
			Person person = new Person("just", null, null);
			RCSTransaction transaction = RCSTransaction.createTransaction("0", "", new DateTime(), person, "");
			RCSFile file = new RCSFileManager().createFile("test.java", transaction);
			RCSRevision revision = new RCSRevision(transaction, file, ChangeType.Added);
			
			assertTrue(transaction.getRevisions().contains(revision));
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(transaction);
			persistenceUtil.commitTransaction();
			
			assertTrue(transaction.getRevisions().contains(revision));
			
			// revision
			List<RCSRevision> revisionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSRevision.class));
			assertFalse(revisionList.isEmpty());
			assertEquals(1, revisionList.size());
			assertEquals(revision, revisionList.get(0));
			assertEquals(transaction, revisionList.get(0).getTransaction());
			assertEquals(ChangeType.Added, revisionList.get(0).getChangeType());
			assertEquals(file, revisionList.get(0).getChangedFile());
			
			// file
			List<RCSFile> fileList = persistenceUtil.load(persistenceUtil.createCriteria(RCSFile.class));
			assertFalse(fileList.isEmpty());
			assertEquals(1, fileList.size());
			assertEquals(file, fileList.get(0));
			assertFalse(fileList.get(0).getChangedNames().isEmpty());
			assertEquals(1, fileList.get(0).getChangedNames().size());
			assertEquals("test.java", fileList.get(0).getLatestPath());
			
			// person
			List<Person> personList = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
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
			List<RCSTransaction> transactionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSTransaction.class));
			assertFalse(transactionList.isEmpty());
			assertEquals(1, transactionList.size());
			assertEquals(transaction, transactionList.get(0));
			assertEquals(person, transactionList.get(0).getAuthor());
			assertFalse(transactionList.get(0).getRevisions().isEmpty());
			assertEquals(1, transactionList.get(0).getRevisions().size());
		} catch (UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSaveRCSFile() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			
			RCSFileManager fileManager = new RCSFileManager();
			Person person = new Person("kim", null, null);
			RCSTransaction rcsTransaction = RCSTransaction.createTransaction("0", "", new DateTime(), person, "");
			rcsTransaction.setBranch(RCSBranch.MASTER);
			RCSFile file = fileManager.createFile("test.java", rcsTransaction);
			file.assignTransaction(rcsTransaction, "formerTest.java");
			RCSRevision revision = new RCSRevision(rcsTransaction, file, ChangeType.Added);
			persistenceUtil.beginTransaction();
			persistenceUtil.saveOrUpdate(rcsTransaction);
			persistenceUtil.commitTransaction();
			
			List<RCSFile> fileList = persistenceUtil.load(persistenceUtil.createCriteria(RCSFile.class));
			assertEquals(1, fileList.size());
			assertEquals(file, fileList.get(0));
			
			List<Person> personList = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
			assertFalse(personList.isEmpty());
			assertTrue(personList.contains(person));
			
			List<RCSRevision> revisionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSRevision.class));
			assertEquals(1, revisionList.size());
			assertEquals(revision, revisionList.get(0));
			
			List<RCSTransaction> transactionList = persistenceUtil.load(persistenceUtil.createCriteria(RCSTransaction.class));
			assertFalse(transactionList.isEmpty());
			assertTrue(transactionList.contains(rcsTransaction));
		} catch (UninitializedDatabaseException e) {
			fail();
		}
		
	}
}
