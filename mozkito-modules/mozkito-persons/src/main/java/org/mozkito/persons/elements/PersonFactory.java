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

package org.mozkito.persons.elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persons.model.Person;

/**
 * A factory for creating Person objects.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PersonFactory {
	
	/** The username to user. */
	private final Map<String, Person> usernameToUser = new HashMap<>();
	
	/** The fullname to user. */
	private final Map<String, Person> fullnameToUser = new HashMap<>();
	
	/** The email to user. */
	private final Map<String, Person> emailToUser    = new HashMap<>();
	
	/** The constructor. */
	private Constructor<Person>       constructor;
	
	/** The unknown. */
	private final Person              UNKNOWN;
	
	/**
	 * Instantiates a new person manager.
	 */
	public PersonFactory() {
		PRECONDITIONS: {
			Condition.notNull(this.usernameToUser,
			                  "Field '%s' in '%s'.", "usernameToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.fullnameToUser,
			                  "Field '%s' in '%s'.", "fullnameToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.emailToUser,
			                  "Field '%s' in '%s'.", "emailToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		try {
			// body
			try {
				this.constructor = Person.class.getConstructor(String.class, String.class, String.class);
			} catch (NoSuchMethodException | SecurityException e) {
				if (Logger.logError()) {
					Logger.error("%s constructor got changed. Please fix the %s class.",
					             Person.class.getCanonicalName(), PersonFactory.class.getCanonicalName());
				}
			}
			
			this.UNKNOWN = create("<unknown>", null, null);
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.usernameToUser,
				                  "Field '%s' in '%s'.", "usernameToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				Condition.notNull(this.fullnameToUser,
				                  "Field '%s' in '%s'.", "fullnameToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				Condition.notNull(this.emailToUser,
				                  "Field '%s' in '%s'.", "emailToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				Condition.notNull(this.constructor,
				                  "Field '%s' in '%s'.", "constructor", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Creates the.
	 * 
	 * @param username
	 *            the username
	 * @param fullname
	 *            the fullname
	 * @param email
	 *            the email
	 * @return the person
	 */
	private Person create(final String username,
	                      final String fullname,
	                      final String email) {
		try {
			return this.constructor.newInstance(username, fullname, email);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (Logger.logError()) {
				Logger.error(e, "%s constructor got changed. Please fix the %s class. Creation process failed.",
				             Person.class.getCanonicalName(), PersonFactory.class.getCanonicalName());
			}
			return null;
		}
	}
	
	/**
	 * Gets the.
	 * 
	 * @param username
	 *            the username
	 * @param fullname
	 *            the fullname
	 * @param email
	 *            the email
	 * @return the person
	 */
	public Person get(final String username,
	                  final String fullname,
	                  final String email) {
		final Person person = create(username, fullname, email);
		
		if (username != null) {
			if (!this.usernameToUser.containsKey(username)) {
				this.usernameToUser.put(username, create(username, fullname, email));
			}
			
			return this.usernameToUser.get(username);
		} else if (fullname != null) {
			if (!this.fullnameToUser.containsKey(fullname)) {
				this.fullnameToUser.put(fullname, create(username, fullname, email));
			}
			
			return this.fullnameToUser.get(fullname);
		} else if (email != null) {
			if (!this.emailToUser.containsKey(email)) {
				this.emailToUser.put(email, person);
			}
			
			return this.emailToUser.get(email);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the unknown.
	 * 
	 * @return the unknown
	 */
	public Person getUnknown() {
		return this.UNKNOWN;
	}
}
