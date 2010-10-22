/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.reposuite.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;
import de.unisaarland.cs.st.reposuite.utils.CMDExecutor;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;

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
	public void testCheckout() {
		URL url = SubversionRepositoryTest.class.getResource(System.getProperty("file.separator") + "repotest.svn");
		File tmpDirectory = FileUtils.createRandomDir("repotest_svn", "");
		try {
			List<String> list = new LinkedList<String>();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
			CMDExecutor.execute("svnadmin load " + tmpDirectory.getAbsolutePath(), FileUtils.tmpDir, list);
			this.repository.setup(tmpDirectory.toURI());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidProtocolType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRepositoryURI e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedProtocolType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
