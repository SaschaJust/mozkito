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

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.persistence.model.Person;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@DatabaseSettings (unit = "persistence")
public class SuperTest extends DatabaseTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	/**
     * 
     */
	public SuperTest() {
		// PRECONDITIONS
		
		try {
			final Person person = new Person("ich", "du", "er@sie.es");
			getPersistenceUtil().beginTransaction();
			getPersistenceUtil().save(person);
			getPersistenceUtil().commitTransaction();
		} finally {
			// POSTCONDITIONS
		}
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
	
	/**
	 * Test.
	 */
	@Test
	public final void test() {
		final List<Person> list = getPersistenceUtil().load(getPersistenceUtil().createCriteria(Person.class));
		for (final Person person : list) {
			System.err.println(person);
		}
	}
}
