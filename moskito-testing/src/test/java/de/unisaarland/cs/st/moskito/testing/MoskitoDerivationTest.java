/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

/**
 * @author just
 * 
 */
public class MoskitoDerivationTest extends MoskitoTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.err.println("setUpBeforClass()");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.err.println("tearDownAfterClass()");
	}
	
	@Before
	public void setUp() throws Exception {
		System.err.println("setUp()");
	}
	
	@After
	public void tearDown() throws Exception {
		System.err.println("tearDown()");
	}
	
	@Test
	@DatabaseSettings (unit = "rcs", database = "moskito_xstream_may2011")
	public void testFail() {
		fail();
	}
	
	@Test
	@Ignore
	@DatabaseSettings (unit = "ppa", util = OpenJPAUtil.class)
	public void testIgnore() {
		
	}
	
	@Test
	@DatabaseSettings (unit = "rcs")
	public void testPass() {
		// fail();
	}
}
