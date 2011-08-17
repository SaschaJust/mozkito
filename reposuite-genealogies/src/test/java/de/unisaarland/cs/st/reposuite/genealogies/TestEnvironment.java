package de.unisaarland.cs.st.reposuite.genealogies;

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
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.junit.Ignore;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryFactory;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

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
	
	private static PersistenceUtil persistenceUtil = null;
	
	public static Map<RCSTransaction, Set<JavaChangeOperation>> transactionMap  = new HashMap<RCSTransaction, Set<JavaChangeOperation>>();
	
	public static PersistenceUtil getPersistenceUtil() {
		return persistenceUtil;
	}
	
	public static void setup() {
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
		
		Repository repository = null;
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
			Set<JavaChangeOperation> operations = new HashSet<JavaChangeOperation>();
			for (RCSRevision revision : transaction.getRevisions()) {
				Criteria<JavaChangeOperation> operationCriteria = persistenceUtil
						.createCriteria(JavaChangeOperation.class);
				operationCriteria.eq("revision", revision);
				operations.addAll(persistenceUtil.load(operationCriteria));
			}
			transactionMap.put(transaction, operations);
		}
		
		if (transactionMap.size() != 10) {
			System.err.println("The imported database dump must contain exactly 10 transaction entries.");
			fail();
		}
		
		//done everything is set.
		persistenceUtil.commitTransaction();
		
	}
	
}
