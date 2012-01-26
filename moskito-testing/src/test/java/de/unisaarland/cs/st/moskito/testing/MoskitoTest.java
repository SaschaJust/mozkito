/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;

import de.unisaarland.cs.st.moskito.testing_impl.MoskitoSuite;

/**
 * @author just
 * 
 */
@RunWith (MoskitoSuite.class)
public abstract class MoskitoTest {
	
	//
	// static {
	// MoskitoTestingAgent.initialize();
	// }
	
	/**
	 * @throws java.lang.Exception
	 */
	public static void setUpBeforeClass() throws Exception {
		System.err.println("WORKS!");
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	public MoskitoTest() {
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
}
