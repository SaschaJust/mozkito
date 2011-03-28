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
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.utils.LogLevel;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class HibernateTest {
	
	@AfterClass
	public static void afterClass() {
		HibernateUtil.shutdown();
		
	}
	
	@BeforeClass
	public static void beforeClass() {
		Logger.setLogLevel(LogLevel.OFF);
		String url = "jdbc:postgresql://quentin.cs.uni-saarland.de/reposuiteTest?useUnicode=true&characterEncoding=UTF-8";
		
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", url);
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.connection.autocommit", "false");
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
		properties.put("hibernate.connection.username", "miner");
		properties.put("hibernate.connection.password", "miner");
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		
		HibernateUtil.createSessionFactory(properties);
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSaveRCSFile() {
		HibernateUtil hibernateUtil;
		try {
			hibernateUtil = HibernateUtil.getInstance(new RepoSuiteToolchain(null) {
				
				@Override
				public void setup() {
				}
				
				@Override
				public void shutdown() {
				}
			});
			
			RCSFileManager fileManager = new RCSFileManager();
			Person person = new Person("kim", null, null);
			RCSTransaction rcsTransaction = RCSTransaction.createTransaction("0", "", new DateTime(), person, "");
			rcsTransaction.setBranch(RCSBranch.MASTER);
			RCSFile file = fileManager.createFile("test.java", rcsTransaction);
			file.assignTransaction(rcsTransaction, "formerTest.java");
			RCSRevision revision = new RCSRevision(rcsTransaction, file, ChangeType.Added);
			hibernateUtil.beginTransaction();
			hibernateUtil.saveOrUpdate(rcsTransaction);
			hibernateUtil.commitTransaction();
			
			@SuppressWarnings ("unchecked")
			List<RCSFile> fileList = hibernateUtil.createCriteria(RCSFile.class).list();
			assertEquals(1, fileList.size());
			assertEquals(file, fileList.get(0));
			
			@SuppressWarnings ("unchecked")
			List<Person> personList = hibernateUtil.createCriteria(Person.class).list();
			assertFalse(personList.isEmpty());
			assertTrue(personList.contains(person));
			
			@SuppressWarnings ("unchecked")
			List<RCSFile> revisionList = hibernateUtil.createCriteria(RCSRevision.class).list();
			assertEquals(1, revisionList.size());
			assertEquals(revision, revisionList.get(0));
			
			@SuppressWarnings ("unchecked")
			List<RCSFile> transactionList = hibernateUtil.createCriteria(RCSTransaction.class).list();
			assertFalse(transactionList.isEmpty());
			assertTrue(transactionList.contains(rcsTransaction));
		} catch (UninitializedDatabaseException e) {
			fail();
		}
		
	}
}
