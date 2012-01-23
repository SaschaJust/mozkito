package de.unisaarland.cs.st.moskito.rcs.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kisa.Logger;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;

public class RCSBranch_NetTest {
	
	@Test
	public void testLoadPersistedMasterBranch() {
		
		AndamaSettings settings = new AndamaSettings();
		DatabaseArguments dbArgs = new DatabaseArguments(settings, true, "rcs");
		
		System.setProperty("database.name", "rcs_branch_test");
		System.setProperty("database.host", "grid1.st.cs.uni-saarland.de");
		System.setProperty("database.user", "miner");
		System.setProperty("database.password", "miner");
		settings.parseArguments();
		
		if (!dbArgs.getValue()) {
			fail();
		}
		
		PersistenceUtil persistenceUtil = null;
		try {
			persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail();
		}
		
		//unzip the database dump
		URL zipURL = this.getClass().getResource(FileUtils.fileSeparator + "reposuite_genealogies_test.psql.zip");
		if (zipURL == null) {
			fail();
		}
		File zipFile = null;
		try {
			zipFile = new File(zipURL.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		
		File baseDir = FileUtils.createRandomDir("RCSBranchTest", "", FileShutdownAction.DELETE);
		
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		
		//load the database dump into the test database
		URL url = null;
		try {
			url = new URL("file://" + baseDir + FileUtils.fileSeparator + "reposuite_genealogies_test.psql");
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
			fail();
		}
		File urlFile = null;
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
		
		RCSBranch masterBranch = RCSBranch.getMasterBranch();
		try {
			RCSTransaction transaction = PersistenceManager.getUtil().loadById(
					"a64df287a21f8a7b0690d13c1561171cbf48a0e1", RCSTransaction.class);
			assertEquals(transaction.getBranch(), masterBranch);
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
