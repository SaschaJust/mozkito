package de.unisaarland.cs.st.moskito.rcs.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kisa.Logger;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class RCSBranch_NetTest extends MoskitoTest {
	
	@Test
	@DatabaseSettings (unit = "rcs")
	public void testLoadPersistedMasterBranch() {
		System.err.println("KABUMM");
		// unzip the database dump
		final URL zipURL = this.getClass().getResource(FileUtils.fileSeparator + "reposuite_genealogies_test.psql.zip");
		if (zipURL == null) {
			fail();
		}
		File zipFile = null;
		try {
			zipFile = new File(zipURL.toURI());
		} catch (final URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		
		final File baseDir = FileUtils.createRandomDir("RCSBranchTest", "", FileShutdownAction.DELETE);
		
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		
		// load the database dump into the test database
		URL url = null;
		try {
			url = new URL("file://" + baseDir + FileUtils.fileSeparator + "reposuite_genealogies_test.psql");
		} catch (final MalformedURLException e2) {
			e2.printStackTrace();
			fail();
		}
		File urlFile = null;
		try {
			urlFile = new File(url.toURI());
		} catch (final URISyntaxException e1) {
			e1.printStackTrace();
			fail();
		}
		
		String psqlString = null;
		try {
			psqlString = FileUtils.readFileToString(urlFile);
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		getPersistenceUtil().executeNativeQuery(psqlString);
		
		final RCSBranch masterBranch = BranchFactory.getMasterBranch(getPersistenceUtil());
		final RCSTransaction transaction = getPersistenceUtil().loadById("a64df287a21f8a7b0690d13c1561171cbf48a0e1",
		                                                                 RCSTransaction.class);
		assertEquals(transaction.getBranch(), masterBranch);
	}
}
