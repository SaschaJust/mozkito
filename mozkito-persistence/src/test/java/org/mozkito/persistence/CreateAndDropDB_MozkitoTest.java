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

package org.mozkito.persistence;

import net.ownhero.dev.kisa.Logger;

import org.junit.AfterClass;
import org.junit.Test;

import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class CreateAndDropDB_MozkitoTest.
 */
@DatabaseSettings (database = "just_test", unit = "persistence")
public class CreateAndDropDB_MozkitoTest extends DatabaseTest {
	
	/**
	 * Sets the up before class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	
	/**
	 * Tear down after class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (Logger.logInfo()) {
			Logger.info("after class"); //$NON-NLS-1$
		}
	}
	
	/**
     * 
     */
	public CreateAndDropDB_MozkitoTest() {
		// PRECONDITIONS
		
		try {
			if (Logger.logInfo()) {
				Logger.info("before class"); //$NON-NLS-1$
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Test.
	 */
	@Test
	public void test() {
		// fail("Not yet implemented");
	}
	
}
