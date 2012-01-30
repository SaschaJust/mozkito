/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.moskito.persons.processing.PersonManager;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Merge_NetTest extends MoskitoTest {
	
	/**
	 * Test for {@link Person}, {@link PersonContainer}, {@link PersonManager}
	 */
	@Test
	@DatabaseSettings (unit = "rcs")
	public void testMergePerson() {
		
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		Criteria<Person> criteria = persistenceUtil.createCriteria(Person.class);
		List<Person> list = persistenceUtil.load(criteria);
		list.size();
		
		final Person[] persons = new Person[] { new Person("just", null, null),
		        new Person(null, null, "sascha.just@st.cs.uni-saarland.de"), new Person(null, "Sascha Just", null),
		        new Person("just", "Sascha Just", null),
		        new Person(null, "Sascha Just", "sascha.just@st.cs.uni-saarland.de"),
		        new Person("just", null, "sascha.just@st.cs.uni-saarland.de") };
		
		RCSTransaction rcsTransaction = null;
		
		persistenceUtil.beginTransaction();
		
		int i = 0;
		for (final Person person : persons) {
			rcsTransaction = RCSTransaction.createTransaction("" + ++i, "test", new DateTime(), person, "",
			                                                  branchFactory);
			persistenceUtil.saveOrUpdate(rcsTransaction);
		}
		
		persistenceUtil.commitTransaction();
		
		final Persons personsMerger = new Persons(persistenceUtil);
		personsMerger.run();
		
		criteria = persistenceUtil.createCriteria(Person.class);
		list = persistenceUtil.load(criteria);
		final Person person = (Person) CollectionUtils.find(list, new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				final Person p = (Person) object;
				return (p.getUsernames().size() == 1) && p.getUsernames().iterator().next().equals("just");
			}
		});
		
		assertTrue(!list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(1, person.getUsernames().size());
		assertEquals("just", person.getUsernames().iterator().next());
		assertEquals(1, person.getEmailAddresses().size());
		assertEquals("sascha.just@st.cs.uni-saarland.de", person.getEmailAddresses().iterator().next());
		assertEquals(1, person.getFullnames().size());
		assertEquals("Sascha Just", person.getFullnames().iterator().next());
	}
	
}
