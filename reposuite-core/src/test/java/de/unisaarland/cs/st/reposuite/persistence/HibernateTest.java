package de.unisaarland.cs.st.reposuite.persistence;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

public class HibernateTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSaveRCSFile() {
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
		HibernateUtil hibernateUtil;
		try {
			hibernateUtil = HibernateUtil.getInstance();
			
			RCSFileManager fileManager = new RCSFileManager();
			Person person = new Person("kim", "", "");
			RCSTransaction rcsTransaction = new RCSTransaction("0", "", new DateTime(), person, null);
			RCSFile file = fileManager.createFile("test.java", rcsTransaction);
			file.assignTransaction(rcsTransaction, "formerTest.java");
			RCSRevision revision = new RCSRevision(rcsTransaction, file, ChangeType.Added, null);
			hibernateUtil.beginTransaction();
			hibernateUtil.saveOrUpdate(rcsTransaction);
			hibernateUtil.commitTransaction();
			
			@SuppressWarnings ("unchecked") List<RCSFile> fileList = hibernateUtil.createCriteria(RCSFile.class).list();
			assertEquals(1, fileList.size());
			assertEquals(file, fileList.get(0));
			
			@SuppressWarnings ("unchecked") List<Person> personList = hibernateUtil.createCriteria(Person.class).list();
			assertEquals(1, personList.size());
			assertEquals(person, personList.get(0));
			
			@SuppressWarnings ("unchecked") List<RCSFile> revisionList = hibernateUtil
			        .createCriteria(RCSRevision.class).list();
			assertEquals(1, revisionList.size());
			assertEquals(revision, revisionList.get(0));
			
			@SuppressWarnings ("unchecked") List<RCSFile> transactionList = hibernateUtil.createCriteria(
			        RCSTransaction.class).list();
			assertEquals(1, transactionList.size());
			assertEquals(rcsTransaction, transactionList.get(0));
		} catch (UninitializedDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HibernateUtil.shutdown();
	}
}
