package de.unisaarland.cs.st.reposuite.changecouplings;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.changecouplings.model.ChangeCouplingRule;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;


public class ChangeCouplingRuleFactoryTest {
	
	@AfterClass
	public static void afterClass() {
		HibernateUtil.shutdown();
		
	}
	
	@BeforeClass
	public static void beforeClass() {
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
	public void testChangeCouplings() {
		RCSFileManager fileManager = new RCSFileManager();
		Person person = new Person("kim", "", "");
		
		try {
			HibernateUtil hibernateUtil = HibernateUtil.getInstance();
			hibernateUtil.beginTransaction();
			
			RCSTransaction rcsTransaction = new RCSTransaction("0", "", new DateTime(), person, null);
			RCSFile fileA = fileManager.createFile("A.java", rcsTransaction);
			fileA.assignTransaction(rcsTransaction, "A.java");
			RCSRevision revision = new RCSRevision(rcsTransaction, fileA, ChangeType.Added, null);
			
			RCSFile fileB = fileManager.createFile("B.java", rcsTransaction);
			fileB.assignTransaction(rcsTransaction, "B.java");
			RCSRevision revision2 = new RCSRevision(rcsTransaction, fileB, ChangeType.Added, null);
			hibernateUtil.saveOrUpdate(rcsTransaction);
			
			RCSTransaction rcsTransaction2 = new RCSTransaction("1", "", new DateTime(), person, rcsTransaction);
			fileA.assignTransaction(rcsTransaction2, "A.java");
			RCSRevision revision3 = new RCSRevision(rcsTransaction2, fileA, ChangeType.Modified, rcsTransaction);
			
			RCSFile fileC = fileManager.createFile("C.java", rcsTransaction2);
			fileC.assignTransaction(rcsTransaction2, "C.java");
			RCSRevision revision4 = new RCSRevision(rcsTransaction2, fileC, ChangeType.Added, null);
			hibernateUtil.saveOrUpdate(rcsTransaction2);
			
			RCSTransaction rcsTransaction3 = new RCSTransaction("2", "", new DateTime(), person, rcsTransaction2);
			fileA.assignTransaction(rcsTransaction3, "A.java");
			RCSRevision revision5 = new RCSRevision(rcsTransaction3, fileA, ChangeType.Modified, rcsTransaction2);
			
			fileC.assignTransaction(rcsTransaction3, "C.java");
			RCSRevision revision6 = new RCSRevision(rcsTransaction3, fileC, ChangeType.Modified, rcsTransaction2);
			
			RCSFile fileD = fileManager.createFile("D.java", rcsTransaction3);
			fileD.assignTransaction(rcsTransaction3, "D.java");
			RCSRevision revision7 = new RCSRevision(rcsTransaction3, fileD, ChangeType.Added, null);
			hibernateUtil.saveOrUpdate(rcsTransaction3);
			
			RCSTransaction rcsTransaction4 = new RCSTransaction("3", "", new DateTime(), person, rcsTransaction3);
			fileA.assignTransaction(rcsTransaction4, "A.java");
			RCSRevision revision8 = new RCSRevision(rcsTransaction4, fileA, ChangeType.Modified, rcsTransaction3);
			
			fileC.assignTransaction(rcsTransaction4, "C.java");
			RCSRevision revision9 = new RCSRevision(rcsTransaction4, fileC, ChangeType.Modified, rcsTransaction3);
			
			fileD.assignTransaction(rcsTransaction4, "D.java");
			RCSRevision revision10 = new RCSRevision(rcsTransaction4, fileD, ChangeType.Modified, rcsTransaction3);
			hibernateUtil.saveOrUpdate(rcsTransaction4);
			
			hibernateUtil.commitTransaction();
			
			Collection<ChangeCouplingRule> changeCouplingRules = ChangeCouplingRuleFactory.getChangeCouplingRules(
					rcsTransaction3, 1, 0);
			assertEquals(1, changeCouplingRules.size());
			
			hibernateUtil.shutdown();
			
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
