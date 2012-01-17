package de.unisaarland.cs.st.moskito.genealogies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

import org.junit.Ignore;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.utils.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.RepositoryFactory;
import de.unisaarland.cs.st.moskito.rcs.RepositoryType;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * This class provides a test environment abstraction that allows all genealogy
 * tests to operate on well defined git repository. The repository gets parsed
 * and stored in the DB on demand. All tests depending on the TestEnvironment
 * will require network and VPN access and will be slow.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Ignore
public class TestEnvironment {
	
	public static enum TestEnvironmentOperation {
		T1F1, T1F2, T2F3, T3F1D, T3F1A, T3F2, T4F3D, T4F3A, T4F4, T5F4, T6F2, T7F2, T8F2, T9F1, T10F3, T10F4, T3F2M;
	}
	
	private static PersistenceUtil                                   persistenceUtil         = null;
	
	public static Map<RCSTransaction, Set<JavaChangeOperation>>      transactionMap          = new HashMap<RCSTransaction, Set<JavaChangeOperation>>();
	
	public static Map<Integer, RCSTransaction>                       environmentTransactions = new HashMap<Integer, RCSTransaction>();
	
	public static Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations   = new HashMap<TestEnvironmentOperation, JavaChangeOperation>();
	
	private static Repository                                        repository;
	
	protected static CoreChangeGenealogy                             changeGenealogy;
	
	protected static File                                            tmpGraphDBFile;
	
	public static PersistenceUtil getPersistenceUtil() {
		return persistenceUtil;
	}
	
	public static Repository getRepository() {
		return TestEnvironment.repository;
	}
	
	public static void setup() {
		//TODO replace by setup using compressed genealogy directory.
		//TODO this setup should be a net test
		transactionMap.clear();
		environmentOperations.clear();
		environmentTransactions.clear();
		
		// UNZIP git repo
		URL zipURL = TestEnvironment.class.getResource(FileUtils.fileSeparator + "genealogies_test.git.zip");
		if (zipURL == null) {
			fail();
		}
		
		File baseDir = null;
		try {
			baseDir = new File((new URL(zipURL.toString().substring(0,
					zipURL.toString().lastIndexOf(FileUtils.fileSeparator)))).toURI());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			fail();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		
		File zipFile = null;
		try {
			zipFile = new File(zipURL.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		// UNZIP END
		
		repository = null;
		try {
			repository = RepositoryFactory.getRepositoryHandler(RepositoryType.GIT).newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			fail();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			fail();
		} catch (UnregisteredRepositoryTypeException e1) {
			e1.printStackTrace();
			fail();
		}
		
		URL url = TestEnvironment.class.getResource(FileUtils.fileSeparator + "genealogies_test.git");
		File urlFile = null;
		try {
			urlFile = new File(url.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		
		try {
			repository.setup(urlFile.toURI(), null, null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		System.setProperty("database.name", "reposuite_genealogies_test");
		OpenJPAUtil.createTestSessionFactory("ppa");
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail();
		}
		
		//unzip the database dump
		zipURL = TestEnvironment.class.getResource(FileUtils.fileSeparator + "reposuite_genealogies_test.psql.zip");
		if (zipURL == null) {
			fail();
		}
		try {
			zipFile = new File(zipURL.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		
		//load the database dump into the test database
		url = TestEnvironment.class.getResource(FileUtils.fileSeparator + "reposuite_genealogies_test.psql");
		try {
			urlFile = new File(url.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		
		String psqlString = null;
		try {
			psqlString = FileUtils.readFileToString(urlFile);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
		persistenceUtil.executeNativeQuery(psqlString);
		
		persistenceUtil.beginTransaction();
		
		//read all transactions and JavaChangeOperations
		Criteria<RCSTransaction> transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class);
		List<RCSTransaction> transactionList = persistenceUtil.load(transactionCriteria);
		for (RCSTransaction transaction : transactionList) {
			
			if (transaction.getId().equals("a64df287a21f8a7b0690d13c1561171cbf48a0e1")) {
				environmentTransactions.put(1, transaction);
			} else if (transaction.getId().equals("a10344533c2b442235aa3bf3dc87dd0ac37cb0af")) {
				environmentTransactions.put(2, transaction);
			} else if (transaction.getId().equals("f281d550d264f53c7e5fd8c7390627c2aaaf2b8a")) {
				environmentTransactions.put(3, transaction);
			} else if (transaction.getId().equals("b38a68d16490c120920fe2281c40317fae960f86")) {
				environmentTransactions.put(4, transaction);
			} else if (transaction.getId().equals("47e6e4206b716af283f583e4d1963a32bef38a92")) {
				environmentTransactions.put(5, transaction);
			} else if (transaction.getId().equals("2005a1a45c9d28a03166d2f61df82552e9b9d502")) {
				environmentTransactions.put(6, transaction);
			} else if (transaction.getId().equals("f3cb1d5a03f6ecda2ce67e2f716f8b0c2d2842f0")) {
				environmentTransactions.put(7, transaction);
			} else if (transaction.getId().equals("0c078d2b779e24fe341028ee132f9613e58763c2")) {
				environmentTransactions.put(8, transaction);
			} else if (transaction.getId().equals("3039e34e53c1bfecfac2e21544b041c890bac8b4")) {
				environmentTransactions.put(9, transaction);
			} else if (transaction.getId().equals("5658606e2f80c30d0b835ed4216e9f8e0cc996fb")) {
				environmentTransactions.put(10, transaction);
			} else {
				fail("Got unexpected RCSTransaction from database: " + transaction.getId());
			}
			
			Set<JavaChangeOperation> operations = new HashSet<JavaChangeOperation>();
			for (RCSRevision revision : transaction.getRevisions()) {
				Criteria<JavaChangeOperation> operationCriteria = persistenceUtil
						.createCriteria(JavaChangeOperation.class);
				operationCriteria.eq("revision", revision);
				List<JavaChangeOperation> changeOps = persistenceUtil.load(operationCriteria);
				operations.addAll(changeOps);
				for (JavaChangeOperation op : changeOps) {
					switch ((int) op.getId()) {
						case 252:
							environmentOperations.put(TestEnvironmentOperation.T1F2, op);
							break;
						case 253:
							environmentOperations.put(TestEnvironmentOperation.T1F1, op);
							break;
						case 255:
							environmentOperations.put(TestEnvironmentOperation.T2F3, op);
							break;
						case 260:
							environmentOperations.put(TestEnvironmentOperation.T3F1D, op);
							break;
						case 259:
							environmentOperations.put(TestEnvironmentOperation.T3F1A, op);
							break;
						case 261:
							environmentOperations.put(TestEnvironmentOperation.T3F2M, op);
							break;
						case 263:
							environmentOperations.put(TestEnvironmentOperation.T3F2, op);
							break;
						case 269:
							environmentOperations.put(TestEnvironmentOperation.T4F3D, op);
							break;
						case 268:
							environmentOperations.put(TestEnvironmentOperation.T4F3A, op);
							break;
						case 264:
							environmentOperations.put(TestEnvironmentOperation.T4F4, op);
							break;
						case 271:
							environmentOperations.put(TestEnvironmentOperation.T5F4, op);
							break;
						case 276:
							environmentOperations.put(TestEnvironmentOperation.T6F2, op);
							break;
						case 279:
							environmentOperations.put(TestEnvironmentOperation.T7F2, op);
							break;
						case 280:
							environmentOperations.put(TestEnvironmentOperation.T8F2, op);
							break;
						case 284:
							environmentOperations.put(TestEnvironmentOperation.T9F1, op);
							break;
						case 290:
							environmentOperations.put(TestEnvironmentOperation.T10F3, op);
							break;
						case 285:
							environmentOperations.put(TestEnvironmentOperation.T10F4, op);
							break;
						default:
							break;
					}
				}
			}
			transactionMap.put(transaction, operations);
		}
		
		if (transactionMap.size() != 10) {
			System.err.println("The imported database dump must contain exactly 10 transaction entries.");
			fail();
		}
		
		//done everything is set.
		persistenceUtil.commitTransaction();
		
		tmpGraphDBFile = FileUtils.createRandomDir("reposuite", "change_genealogy_test", FileShutdownAction.KEEP);
		System.out.println(tmpGraphDBFile.getAbsolutePath());
		changeGenealogy = ChangeGenealogyUtils.readFromDB(tmpGraphDBFile, getPersistenceUtil());
		
		assertTrue(changeGenealogy != null);
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (JavaChangeOperation operation : transactionEntry.getValue()) {
				changeGenealogy.addVertex(operation);
			}
		}
		
		assertEquals(41, changeGenealogy.vertexSize());
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (JavaChangeOperation op : transactionEntry.getValue()) {
				assertTrue(changeGenealogy.hasVertex(op));
				assertFalse(changeGenealogy.addVertex(op));
			}
		}
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
				environmentOperations.get(TestEnvironmentOperation.T1F1),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T2F3),
				environmentOperations.get(TestEnvironmentOperation.T1F2), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
				environmentOperations.get(TestEnvironmentOperation.T1F1),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2M),
				environmentOperations.get(TestEnvironmentOperation.T1F2), GenealogyEdgeType.DefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3D),
				environmentOperations.get(TestEnvironmentOperation.T2F3), GenealogyEdgeType.DeletedCallOnCall));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3A),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F4),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T5F4),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T6F2),
				environmentOperations.get(TestEnvironmentOperation.T3F2M),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T7F2),
				environmentOperations.get(TestEnvironmentOperation.T6F2),
				GenealogyEdgeType.DefinitionOnDeletedDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T8F2),
				environmentOperations.get(TestEnvironmentOperation.T7F2), GenealogyEdgeType.DefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T9F1),
				environmentOperations.get(TestEnvironmentOperation.T3F1A),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
				environmentOperations.get(TestEnvironmentOperation.T9F1),
				GenealogyEdgeType.DeletedCallOnDeletedDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
				environmentOperations.get(TestEnvironmentOperation.T9F1),
				GenealogyEdgeType.DeletedCallOnDeletedDefinition));
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
				environmentOperations.get(TestEnvironmentOperation.T4F3A), GenealogyEdgeType.DeletedCallOnCall));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
				environmentOperations.get(TestEnvironmentOperation.T5F4), GenealogyEdgeType.DeletedCallOnCall));
		
		Logger.setLogLevel(LogLevel.INFO);
		
	}
	
}
