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
package org.mozkito.persons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.junit.Test;

import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;
import org.mozkito.persons.processing.PersonManager;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@DatabaseSettings (unit = "versions")
public class Merge_NetTest extends DatabaseTest {
	
	/**
	 * Test for {@link Person}, {@link PersonContainer}, {@link PersonManager}
	 * 
	 * @throws ArgumentRegistrationException
	 * @throws SettingsParseError
	 */
	@SuppressWarnings ("deprecation")
	@Test
	public void testMergePerson() throws SettingsParseError, ArgumentRegistrationException {
		
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		Criteria<Person> criteria = persistenceUtil.createCriteria(Person.class);
		List<Person> list = persistenceUtil.load(criteria);
		list.size();
		
		final Person[] persons = new Person[] { new Person("just", null, null),
		        new Person(null, null, "sascha.just@mozkito.org"), new Person(null, "Sascha Just", null),
		        new Person("just", "Sascha Just", null), new Person(null, "Sascha Just", "sascha.just@mozkito.org"),
		        new Person("just", null, "sascha.just@mozkito.org") };
		
		RCSTransaction rcsTransaction = null;
		
		persistenceUtil.beginTransaction();
		
		int i = 0;
		for (final Person person : persons) {
			rcsTransaction = RCSTransaction.createTransaction("" + ++i, "test", new DateTime(), person, "");
			persistenceUtil.saveOrUpdate(rcsTransaction);
		}
		
		persistenceUtil.commitTransaction();
		
		Persons personsMerger;
		try {
			personsMerger = new Persons(persistenceUtil);
			personsMerger.run();
		} catch (final ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
		
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
		assertEquals("sascha.just@mozkito.org", person.getEmailAddresses().iterator().next());
		assertEquals(1, person.getFullnames().size());
		assertEquals("Sascha Just", person.getFullnames().iterator().next());
	}
	
}
