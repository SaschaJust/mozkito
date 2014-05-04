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
package org.mozkito.persons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.mozkito.database.PersistenceUtil;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class Person_PersistenceTest.
 */
@DatabaseSettings (unit = "persons")
public class Person_PersistenceTest extends DatabaseTest {
	
	private PersonFactory personFactory;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		this.personFactory = new PersonFactory();
	}
	
	/**
	 * Test person.
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void testPerson() throws DatabaseException {
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		final Person person = this.personFactory.get("username", "fullname", "em@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
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
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void testPersonContainer() throws DatabaseException {
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		
		// final PersonContainer personContainer = new PersonContainer();
		final Person person1 = this.personFactory.get("username1", "full name1", "em1@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		final Person person2 = this.personFactory.get("username2", "full name2", "em21@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		final Person person3 = this.personFactory.get("username3", "full name3", "em3@i.l"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		//		personContainer.add("role1", person1); //$NON-NLS-1$
		//		personContainer.add("role2", person2); //$NON-NLS-1$
		//		personContainer.add("role3", person3); //$NON-NLS-1$
		
		persistenceUtil.beginTransaction();
		persistenceUtil.save(person1);
		persistenceUtil.save(person2);
		persistenceUtil.save(person3);
		persistenceUtil.commitTransaction();
		
		final List<Person> list = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
		
		assertFalse(list.isEmpty());
		assertEquals(3, list.size());
		
		assertEquals(person1, list.get(0));
		assertEquals(person2, list.get(1));
		assertEquals(person3, list.get(2));
	}
	
}
