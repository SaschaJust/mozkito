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
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.LogLevel;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class OpenJPATest {
	
	@AfterClass
	public static void afterClass() {
		try {
			OpenJPAUtil.getInstance().shutdown();
		} catch (UninitializedDatabaseException e) {
			
		}
	}
	
	@BeforeClass
	public static void beforeClass() {
		Logger.setLogLevel(LogLevel.OFF);
		Properties properties = new Properties();
		String url = "jdbc:postgresql://quentin.cs.uni-saarland.de/reposuiteTest";
		properties.put("openjpa.ConnectionURL", url);
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(SchemaAction='add,deleteTableContents')");
		properties.put("openjpa.ConnectionDriverName", "org.postgresql.Driver");
		properties.put("openjpa.ConnectionUserName", "miner");
		properties.put("openjpa.ConnectionPassword", "miner");
		
		OpenJPAUtil.createSessionFactory(properties);
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
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
