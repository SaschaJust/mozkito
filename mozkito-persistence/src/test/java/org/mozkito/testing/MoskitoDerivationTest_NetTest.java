/**
 * 
 */
package org.mozkito.testing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mozkito.persistence.OpenJPAUtil;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@DatabaseSettings (unit = "versions")
public class MoskitoDerivationTest_NetTest extends MozkitoTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("setUpBeforClass()");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("tearDownAfterClass()");
	}
	
	@Before
	public void setUp() throws Exception {
		System.out.println("setUp()");
	}
	
	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown()");
	}
	
	@Test
	@Ignore
	@DatabaseSettings (unit = "codeanalysis", util = OpenJPAUtil.class)
	public void testIgnore() {
		// ignore
	}
	
	@Test
	// @DatabaseSettings (unit = "versions", database = "moskito_xstream_may2011",
	// options = ConnectOptions.VALIDATE)
	public void testNoFail() {
		// fail();
	}
	
	@Test
	public void testPass() {
		// fail();
	}
}
