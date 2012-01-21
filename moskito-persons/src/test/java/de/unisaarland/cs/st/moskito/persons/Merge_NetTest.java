/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persons;

import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.moskito.persons.processing.PersonManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Merge_NetTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OpenJPAUtil.createTestSessionFactory("rcs");
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		OpenJPAUtil.getInstance().shutdown();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// set JavaVM arguments
		Properties sysProperties = System.getProperties();
		sysProperties.setProperty("database.user", "miner");
		sysProperties.setProperty("database.password", "miner");
		sysProperties.setProperty("database.name", "reposuiteTestPersons");
		sysProperties.setProperty("database.host", "quentin.cs.uni-saarland.de");
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test for {@link Person}, {@link PersonContainer}, {@link PersonManager}
	 */
	@Test
	public void testMergePerson() {
		
		// PersistenceUtil persistenceUtil;
		// try {
		// persistenceUtil = PersistenceUtil.getInstance();
		// Criteria criteria = persistenceUtil.createCriteria(Person.class);
		// int personCount = criteria.list().size();
		//
		// Person[] persons = new Person[] { new Person("just", null, null),
		// new Person(null, null, "sascha.just@st.cs.uni-saarland.de"), new
		// Person(null, "Sascha Just", null),
		// new Person("just", "Sascha Just", null),
		// new Person(null, "Sascha Just", "sascha.just@st.cs.uni-saarland.de")
		// };
		//
		// RCSTransaction rcsTransaction = null;
		//
		// persistenceUtil.beginTransaction();
		//
		// int i = 0;
		// for (Person person : persons) {
		// rcsTransaction = RCSTransaction.createTransaction("" + ++i, "test",
		// new DateTime(), person, "");
		// persistenceUtil.saveOrUpdate(rcsTransaction);
		// }
		//
		// persistenceUtil.commitTransaction();
		//
		// Persons personsMerger = new Persons();
		// personsMerger.run();
		//
		// criteria = persistenceUtil.createCriteria(Person.class);
		// @SuppressWarnings ("unchecked")
		// List<Person> list = criteria.list();
		// Person person = (Person) CollectionUtils.find(list, new Predicate() {
		//
		// @Override
		// public boolean evaluate(final Object object) {
		// Person p = (Person) object;
		// return (p.getUsernames().size() == 1) &&
		// p.getUsernames().iterator().next().equals("just");
		// }
		// });
		//
		// assertTrue(!list.isEmpty());
		// assertEquals(personCount + 1, list.size());
		// assertEquals(1, person.getUsernames().size());
		// assertEquals("just", person.getUsernames().iterator().next());
		// assertEquals(1, person.getEmailAddresses().size());
		// assertEquals("sascha.just@st.cs.uni-saarland.de",
		// person.getEmailAddresses().iterator().next());
		// assertEquals(1, person.getFullnames().size());
		// assertEquals("Sascha Just", person.getFullnames().iterator().next());
		// assertEquals(persons.length, person.getTransactions().size());
		// for (Person p : persons) {
		// if (p != person) {
		// assertEquals(0, p.getTransactions().size());
		// }
		// }
		// } catch (UninitializedDatabaseException e) {
		// fail();
		// }
	}
	
	@Test
	public void testMergePersonSingleContainer() {
	}
}
