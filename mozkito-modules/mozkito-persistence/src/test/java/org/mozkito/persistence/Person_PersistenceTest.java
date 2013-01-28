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
package org.mozkito.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class Person_PersistenceTest.
 */
@DatabaseSettings (unit = "persistence")
public class Person_PersistenceTest extends DatabaseTest {
	
	/**
	 * Test person.
	 */
	@Test
	public void testPerson() {
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		final Person person = new Person("username", "fullname", "em@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		persistenceUtil.beginTransaction();
		persistenceUtil.save(person);
		persistenceUtil.commitTransaction();
		
		final List<Person> list = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
		
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(person, list.get(0));
	}
	
	/**
	 * Test per son container.
	 */
	@Test
	public void testPersonContainer() {
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		
		final PersonContainer personContainer = new PersonContainer();
		final Person person1 = new Person("username1", "full name1", "em1@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		final Person person2 = new Person("username2", "full name2", "em21@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		final Person person3 = new Person("username3", "full name3", "em3@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		personContainer.add("role1", person1); //$NON-NLS-1$
		personContainer.add("role2", person2); //$NON-NLS-1$
		personContainer.add("role3", person3); //$NON-NLS-1$
		
		persistenceUtil.beginTransaction();
		persistenceUtil.save(personContainer);
		persistenceUtil.commitTransaction();
		
		final List<PersonContainer> list = persistenceUtil.load(persistenceUtil.createCriteria(PersonContainer.class));
		
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(personContainer, list.get(0));
		assertFalse(list.get(0).isEmpty());
		assertEquals(3, list.get(0).size());
		assertEquals(person1, list.get(0).get("role1")); //$NON-NLS-1$
		assertEquals(person2, list.get(0).get("role2")); //$NON-NLS-1$
		assertEquals(person3, list.get(0).get("role3")); //$NON-NLS-1$
	}
	
}
