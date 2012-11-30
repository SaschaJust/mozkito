/**
 * 
 */
package org.mozkito.testing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class MozkitoDerivationTest_NetTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@DatabaseSettings (unit = "versions")
public class MozkitoDerivationTest_NetTest extends DatabaseTest {
	
	/**
	 * Tear down after class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("tearDownAfterClass()"); //$NON-NLS-1$
	}
	
	/**
	 * Instantiates a new mozkito derivation test_ net test.
	 */
	public MozkitoDerivationTest_NetTest() {
		System.out.println("setUpBeforClass()"); //$NON-NLS-1$
	}
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println("setUp()"); //$NON-NLS-1$
	}
	
	/**
	 * Tear down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown()"); //$NON-NLS-1$
	}
	
	/**
	 * Test ignore.
	 */
	@Test
	@Ignore
	public void testIgnore() {
		// ignore
	}
	
	/**
	 * Test no fail.
	 */
	@Test
	// @DatabaseSettings (unit = "versions", database = "moskito_xstream_may2011",
	// options = ConnectOptions.VALIDATE)
	public void testNoFail() {
		// fail();
	}
	
	/**
	 * Test pass.
	 */
	@Test
	public void testPass() {
		// fail();
	}
}
