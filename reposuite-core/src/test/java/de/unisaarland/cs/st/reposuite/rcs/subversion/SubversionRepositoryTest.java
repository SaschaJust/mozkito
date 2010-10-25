/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.CommandExecutor;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SubversionRepositoryTest {
	
	private SubversionRepository repository;
	
	public void setup() {
		this.repository = new SubversionRepository();
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void testAnnotate() {
		
	}
	
	@Test
	public void testCheckout() {
		URL url = SubversionRepositoryTest.class.getResource(System.getProperty("file.separator") + "repotest.svn");
		File tmpDirectory = FileUtils.createRandomDir("repotest_svn", "");
		try {
			Integer returnValue = 0;
			FileUtils.forceDeleteOnExit(tmpDirectory);
			if (RepoSuiteSettings.logDebug()) {
				Logger.debug("Creating SVN repository at: " + tmpDirectory.getAbsolutePath());
			}
			Tuple<Integer, List<String>> execute = CommandExecutor.execute("/opt/local/bin/svnadmin", new String[] {
			        "create", tmpDirectory.getAbsolutePath() }, tmpDirectory, null, null);
			returnValue += execute.getFirst();
			execute = CommandExecutor.execute("/opt/local/bin/svnadmin",
			        new String[] { "load", tmpDirectory.getAbsolutePath() }, tmpDirectory, url.openStream(), null);
			returnValue += execute.getFirst();
			assertTrue(returnValue == 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCheckoutDir() {
		
	}
	
	@Test
	public void testCheckoutFile() {
		
	}
	
	@Test
	public void testGetChangedPaths() {
		
	}
	
	@Test
	public void testGetFirstRevisionID() {
		
	}
	
	@Test
	public void testGetLastRevisionID() {
		
	}
	
	@Test
	public void testLog() {
		
	}
	
	@Test
	public void testRenameEdit() {
		
	}
}
