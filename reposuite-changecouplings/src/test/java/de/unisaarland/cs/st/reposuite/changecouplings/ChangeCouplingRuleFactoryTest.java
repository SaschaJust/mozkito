package de.unisaarland.cs.st.reposuite.changecouplings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.LogLevel;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class ChangeCouplingRuleFactoryTest {
	
	private static PersistenceUtil persistenceUtil = null;
	
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
		properties.put("openjpa.persistence-unit", "rcs");
		OpenJPAUtil.createSessionFactory(properties);
		
		try {
			
			persistenceUtil = OpenJPAUtil.getInstance();
			URL sqlURL = ChangeCouplingRuleFactoryTest.class.getResource(FileUtils.fileSeparator
			        + "change_file_couplings.psql");
			
			File sqlFile = new File(sqlURL.toURI());
			String query = FileUtils.readFileToString(sqlFile);
			// query =
			// "CREATE OR REPLACE FUNCTION reposuite_changecouplings(tid character varying(40), tablename varchar) RETURNS integer AS $$\nreturn1\n$$ LANGUAGE plpython2u;";
			persistenceUtil.executeNativeQuery(query);
		} catch (IOException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not set or update change coupling functions. Trying to continue ... ", e);
			}
		} catch (UninitializedDatabaseException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not set or update change coupling functions. Trying to continue ... ", e);
			}
		} catch (URISyntaxException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not set or update change coupling functions. Trying to continue ... ", e);
			}
		} finally {
			if (persistenceUtil != null) {
				persistenceUtil.commitTransaction();
			}
		}
		
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testChangeCouplings() {
		persistenceUtil.beginTransaction();
		
		RCSFileManager fileManager = new RCSFileManager();
		Person person = new Person("kim", "", "");
		
		// ###transaction 1
		
		DateTime now = new DateTime();
		RCSTransaction rcsTransaction = RCSTransaction.createTransaction("0", "", now, person, "");
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
		
		RCSTransaction rcsTransaction2 = RCSTransaction.createTransaction("1", "", now.plus(10000), person, "");
		new RCSRevision(rcsTransaction2, fileA, ChangeType.Modified);
		new RCSRevision(rcsTransaction2, fileB, ChangeType.Added);
		RCSFile fileD = fileManager.createFile("D.java", rcsTransaction);
		fileC.assignTransaction(rcsTransaction2, "D.java");
		new RCSRevision(rcsTransaction2, fileD, ChangeType.Added);
		persistenceUtil.saveOrUpdate(rcsTransaction2);
		
		// ### transaction 3
		
		RCSTransaction rcsTransaction3 = RCSTransaction.createTransaction("2", "", now.plus(20000), person, "");
		new RCSRevision(rcsTransaction3, fileA, ChangeType.Modified);
		
		fileC.assignTransaction(rcsTransaction3, "C.java");
		new RCSRevision(rcsTransaction3, fileC, ChangeType.Modified);
		new RCSRevision(rcsTransaction3, fileB, ChangeType.Added);
		persistenceUtil.saveOrUpdate(rcsTransaction3);
		
		// ### transaction 4
		
		RCSTransaction rcsTransaction4 = RCSTransaction.createTransaction("3", "", now.plus(30000), person, "");
		new RCSRevision(rcsTransaction4, fileA, ChangeType.Modified);
		new RCSRevision(rcsTransaction4, fileC, ChangeType.Modified);
		new RCSRevision(rcsTransaction4, fileB, ChangeType.Modified);
		persistenceUtil.saveOrUpdate(rcsTransaction4);
		
		persistenceUtil.commitTransaction();
		
		List<FileChangeCoupling> changeCouplingRules = ChangeCouplingRuleFactory.getFileChangeCouplings(rcsTransaction3,
		                                                                                                1, 0,
		                                                                                                persistenceUtil);
		assertEquals(8, changeCouplingRules.size());
		FileChangeCoupling rule = changeCouplingRules.get(0);
		assertEquals(1, rule.getPremise().size());
		assertTrue(rule.getPremise().contains(fileB));
		assertEquals(fileA, rule.getImplication());
		assertEquals(2, rule.getSupport().intValue());
		assertEquals(1, rule.getConfidence().doubleValue(), 0);
		
		rule = changeCouplingRules.get(1);
		assertEquals(1, rule.getPremise().size());
		assertTrue(rule.getPremise().contains(fileA));
		assertEquals(fileB, rule.getImplication());
		assertEquals(2, rule.getSupport().intValue());
		assertEquals(1, rule.getConfidence().doubleValue(), 0);
		
		rule = changeCouplingRules.get(2);
		assertEquals(2, rule.getPremise().size());
		assertTrue(rule.getPremise().contains(fileB));
		assertTrue(rule.getPremise().contains(fileC));
		assertEquals(fileA, rule.getImplication());
		assertEquals(1, rule.getSupport().intValue());
		assertEquals(1, rule.getConfidence().doubleValue(), 0);
		
		rule = changeCouplingRules.get(3);
		assertEquals(2, rule.getPremise().size());
		assertTrue(rule.getPremise().contains(fileA));
		assertTrue(rule.getPremise().contains(fileC));
		assertEquals(fileB, rule.getImplication());
		assertEquals(1, rule.getSupport().intValue());
		assertEquals(1, rule.getConfidence().doubleValue(), 0);
		
		rule = changeCouplingRules.get(4);
		assertEquals(1, rule.getPremise().size());
		assertTrue(rule.getPremise().contains(fileC));
		assertEquals(fileA, rule.getImplication());
		assertEquals(1, rule.getSupport().intValue());
		assertEquals(1, rule.getConfidence().doubleValue(), 0);
		
		rule = changeCouplingRules.get(5);
		assertEquals(1, rule.getPremise().size());
		assertTrue(rule.getPremise().contains(fileC));
		assertEquals(fileB, rule.getImplication());
		assertEquals(1, rule.getSupport().intValue());
		assertEquals(1, rule.getConfidence().doubleValue(), 0);
		
		rule = changeCouplingRules.get(6);
		assertEquals(1, rule.getPremise().size());
		assertEquals(fileC, rule.getImplication());
		assertEquals(1, rule.getSupport().intValue());
		assertEquals(.5, rule.getConfidence().doubleValue(), 0);
		
		rule = changeCouplingRules.get(7);
		assertEquals(1, rule.getPremise().size());
		assertEquals(fileC, rule.getImplication());
		assertEquals(1, rule.getSupport().intValue());
		assertEquals(.5, rule.getConfidence().doubleValue(), 0);
		
	}
	
}
