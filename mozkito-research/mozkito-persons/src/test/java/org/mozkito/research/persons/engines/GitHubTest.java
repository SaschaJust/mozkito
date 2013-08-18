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

package org.mozkito.research.persons.engines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.research.persons.test.Description;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class GitHubTest {
	
	private static Engine        engine;
	private static PersonFactory factory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// we initialize engine here to have our cache not cleared for every test execution which would pretty quickly
		// cause our fetch limit to be reached and in addition lead to tests being really slow.
		engine = new GitHubEngine();
		factory = new PersonFactory();
	}
	
	/**
	 * Matches.
	 * 
	 * @param p1
	 *            the p1
	 * @param p2
	 *            the p2
	 */
	private final void matches(final Person p1,
	                           final Person p2) {
		assertNotEquals(p1, p2);
		
		final double confidence = engine.confidence(p1, p2);
		assertEquals(1.0d, confidence, 0.00000000001d);
	}
	
	/**
	 * Test1.
	 */
	@Test
	@Description ("(GitHub login = username) vs (User email is email address of GitHub user)")
	public final void test1() {
		final Person p1 = factory.get("sjust83", null, null);
		final Person p2 = factory.get(null, null, "sascha.just@mozkito.org");
		matches(p1, p2);
	}
	
	/**
	 * Test2.
	 */
	@Test
	@Description ("(GitHub login = username) vs (User email address that is NOT on github but prefix search leads to a user with an email address that has the same host as the given one)")
	public final void test2() {
		final Person p1 = factory.get("sjust83", null, null);
		final Person p2 = factory.get(null, null, "sascha@mozkito.org");
		matches(p1, p2);
	}
	
	/**
	 * Test3.
	 */
	@Test
	@Description ("(GitHub login = username) vs (User email address that is NOT on github but prefix search leads to a user with a gravatar ID that leads to the same image as the gravatar for the given email address)")
	public final void test3() {
		final Person p1 = factory.get("sjust83", null, null);
		final Person p2 = factory.get(null, null, "sascha.just@st.cs.uni-saarland.de");
		matches(p1, p2);
	}
}
