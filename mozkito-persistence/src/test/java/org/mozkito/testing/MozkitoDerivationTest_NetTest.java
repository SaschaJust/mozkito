/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.testing;

import net.ownhero.dev.kisa.Logger;

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
		if (Logger.logDebug()) {
			Logger.debug("tearDownAfterClass()"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Instantiates a new mozkito derivation test_ net test.
	 */
	public MozkitoDerivationTest_NetTest() {
		if (Logger.logDebug()) {
			Logger.debug("setUpBeforClass()"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		if (Logger.logDebug()) {
			Logger.debug("setUp()"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Tear down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		if (Logger.logDebug()) {
			Logger.debug("tearDown()"); //$NON-NLS-1$
		}
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
