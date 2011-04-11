package de.unisaarland.cs.st.reposuite.changecouplings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.changecouplings.model.ChangeCouplingRule;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.LogLevel;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class ChangeCouplingRuleFactoryTest {
	
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
		properties.put("openjpa.persistence-unit", "core");
		
		OpenJPAUtil.createSessionFactory(properties);
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
			PersistenceUtil persistenceUtil = OpenJPAUtil.getInstance();
			persistenceUtil.beginTransaction();
			
			// ###transaction 1
			
			RCSTransaction rcsTransaction = RCSTransaction.createTransaction("0", "", new DateTime(), person, "");
			RCSFile fileA = fileManager.createFile("A.java", rcsTransaction);
			fileA.assignTransaction(rcsTransaction, "A.java");
			new RCSRevision(rcsTransaction, fileA, ChangeType.Added);
			
			RCSFile fileB = fileManager.createFile("B.java", rcsTransaction);
			fileB.assignTransaction(rcsTransaction, "B.java");
			new RCSRevision(rcsTransaction, fileB, ChangeType.Added);
			
			RCSFile fileC = fileManager.createFile("C.java", rcsTransaction);
			fileC.assignTransaction(rcsTransaction, "C.java");
			new RCSRevision(rcsTransaction, fileC, ChangeType.Added);
			
			persistenceUtil.saveOrUpdate(rcsTransaction);
			
			// ### transaction 2
			
			RCSTransaction rcsTransaction2 = RCSTransaction.createTransaction("1", "", new DateTime(), person, "");
			new RCSRevision(rcsTransaction2, fileA, ChangeType.Modified);
			new RCSRevision(rcsTransaction2, fileB, ChangeType.Added);
			RCSFile fileD = fileManager.createFile("D.java", rcsTransaction);
			fileC.assignTransaction(rcsTransaction2, "D.java");
			new RCSRevision(rcsTransaction2, fileD, ChangeType.Added);
			persistenceUtil.saveOrUpdate(rcsTransaction2);
			
			// ### transaction 3
			
			RCSTransaction rcsTransaction3 = RCSTransaction.createTransaction("2", "", new DateTime(), person, "");
			new RCSRevision(rcsTransaction3, fileA, ChangeType.Modified);
			
			fileC.assignTransaction(rcsTransaction3, "C.java");
			new RCSRevision(rcsTransaction3, fileC, ChangeType.Modified);
			new RCSRevision(rcsTransaction3, fileB, ChangeType.Added);
			persistenceUtil.saveOrUpdate(rcsTransaction3);
			
			// ### transaction 4
			
			RCSTransaction rcsTransaction4 = RCSTransaction.createTransaction("3", "", new DateTime(), person, "");
			new RCSRevision(rcsTransaction4, fileA, ChangeType.Modified);
			new RCSRevision(rcsTransaction4, fileC, ChangeType.Modified);
			new RCSRevision(rcsTransaction4, fileB, ChangeType.Modified);
			persistenceUtil.saveOrUpdate(rcsTransaction4);
			
			persistenceUtil.commitTransaction();
			
			List<ChangeCouplingRule> changeCouplingRules = ChangeCouplingRuleFactory.getChangeCouplingRules(rcsTransaction3,
			                                                                                                1, 0,
			                                                                                                persistenceUtil);
			assertEquals(8, changeCouplingRules.size());
			ChangeCouplingRule rule = changeCouplingRules.get(0);
			assertEquals(1, rule.getPremise().length);
			assertEquals(2, rule.getPremise()[0].intValue());
			assertEquals(1, rule.getImplication().intValue());
			assertEquals(2, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(1);
			assertEquals(1, rule.getPremise().length);
			assertEquals(1, rule.getPremise()[0].intValue());
			assertEquals(2, rule.getImplication().intValue());
			assertEquals(2, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(2);
			assertEquals(2, rule.getPremise().length);
			assertEquals(2, rule.getPremise()[0].intValue());
			assertEquals(3, rule.getPremise()[1].intValue());
			assertEquals(1, rule.getImplication().intValue());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(3);
			assertEquals(1, rule.getPremise().length);
			assertEquals(3, rule.getPremise()[0].intValue());
			assertEquals(1, rule.getImplication().intValue());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(4);
			assertEquals(1, rule.getPremise().length);
			assertEquals(3, rule.getPremise()[0].intValue());
			assertEquals(2, rule.getImplication().intValue());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(1, rule.getConfidence().doubleValue(), 0);
			
			rule = changeCouplingRules.get(5);
			assertEquals(2, rule.getPremise().length);
			assertEquals(1, rule.getPremise()[0].intValue());
			assertEquals(2, rule.getPremise()[1].intValue());
			assertEquals(3, rule.getImplication().intValue());
			assertEquals(1, rule.getSupport().intValue());
			assertEquals(.5, rule.getConfidence().doubleValue(), 0);
			
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
}
