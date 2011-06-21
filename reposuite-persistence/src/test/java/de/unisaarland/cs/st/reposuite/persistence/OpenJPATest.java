/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;

public class OpenJPATest {
	
	@AfterClass
	public static void afterClass() {
	}
	
	@BeforeClass
	public static void beforeClass() {
		
	}
	
	@Before
	public void setUp() throws Exception {
		OpenJPAUtil.createTestSessionFactory("persistence");
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			OpenJPAUtil.getInstance().shutdown();
		} catch (UninitializedDatabaseException e) {
			
		}
	}
	
	@Test
	public void testPerson() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			Person person = new Person("username", "fullname", "em@i.l");
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(person);
			persistenceUtil.commitTransaction();
			
			List<Person> list = persistenceUtil.load(persistenceUtil.createCriteria(Person.class));
			
			assertFalse(list.isEmpty());
			assertEquals(1, list.size());
			assertEquals(person, list.get(0));
		} catch (UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPersonContainer() {
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = OpenJPAUtil.getInstance();
			PersonContainer personContainer = new PersonContainer();
			Person person1 = new Person("username1", "full name1", "em1@i.l");
			Person person2 = new Person("username2", "full name2", "em21@i.l");
			Person person3 = new Person("username3", "full name3", "em3@i.l");
			
			personContainer.add("role1", person1);
			personContainer.add("role2", person2);
			personContainer.add("role3", person3);
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(personContainer);
			persistenceUtil.commitTransaction();
			
			List<PersonContainer> list = persistenceUtil.load(persistenceUtil.createCriteria(PersonContainer.class));
			
			assertFalse(list.isEmpty());
			assertEquals(1, list.size());
			assertEquals(personContainer, list.get(0));
			assertFalse(list.get(0).isEmpty());
			assertEquals(3, list.get(0).size());
			assertEquals(person1, list.get(0).get("role1"));
			assertEquals(person2, list.get(0).get("role2"));
			assertEquals(person3, list.get(0).get("role3"));
		} catch (UninitializedDatabaseException e) {
			fail(e.getMessage());
		}
	}
	
}
