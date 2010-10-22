/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.reposuite.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SubversionRepositoryTest {
	
	private SubversionRepository repository;
	
	@Before
	public void setup() {
		this.repository = new SubversionRepository();
		try {
			this.repository.setup(new URI(
			        "file:///Users/just/Documents/University/msa_ss10/project/svn_repo_2010-05-14"));
		} catch (MalformedURLException e) {
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
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void testCheckout() {
		File checkoutPath = this.repository.checkoutPath("/", "17822");
		System.err.println(checkoutPath.getAbsolutePath());
	}
	
}
