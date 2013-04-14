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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.joda.time.DateTime;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persons.model.Person;

/**
 * A factory for creating Person objects.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PersonFactory {
	
	/**
	 * The Class PersonEntry.
	 * 
	 * @author Sascha Just <sascha.just@mozkito.org>
	 */
	private static final class PersonEntry implements Comparable<PersonEntry> {
		
		/** The latest used. */
		public int    latestUsed;
		/** The person. */
		public Person person;
		
		/**
		 * Instantiates a new person entry.
		 * 
		 * @param person
		 *            the person
		 * @param used
		 *            the used
		 */
		public PersonEntry(final Person person, final int used) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
				this.latestUsed = used;
				this.person = person;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final PersonEntry o) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return Integer.compare(o.latestUsed, this.latestUsed);
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
	}
	
	/** The username to user. */
	private final Map<String, Person>        usernameToUser    = new HashMap<>();
	
	/** The fullname to user. */
	private final Map<String, Person>        fullnameToUser    = new HashMap<>();
	
	/** The email to user. */
	private final Map<String, Person>        emailToUser       = new HashMap<>();
	
	/** The unknown. */
	private final Person                     UNKNOWN;
	
	/** The persistence util. */
	private PersistenceUtil                  persistenceUtil   = null;
	
	/** The threshold. */
	private long                             threshold         = 200l;
	
	/** The max TTL (in seconds). */
	private final int                        maxTTL            = 600;
	
	/** The load. */
	private long                             load              = 0;
	
	/** The created. */
	private final DateTime                   created           = new DateTime();
	
	/** The least recently used. */
	private final PriorityQueue<PersonEntry> leastRecentlyUsed = new PriorityQueue<>();
	
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
			
			this.UNKNOWN = create("<unknown>", null, null);
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.usernameToUser,
				                  "Field '%s' in '%s'.", "usernameToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				Condition.notNull(this.fullnameToUser,
				                  "Field '%s' in '%s'.", "fullnameToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				Condition.notNull(this.emailToUser,
				                  "Field '%s' in '%s'.", "emailToUser", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Instantiates a new person factory.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param threshold
	 *            the threshold
	 */
	public PersonFactory(@NotNull final PersistenceUtil persistenceUtil, final long threshold) {
		this();
		
		try {
			this.persistenceUtil = persistenceUtil;
			this.threshold = threshold;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.persistenceUtil,
				                  "Field '%s' in '%s'.", "persistenceUtil", PersonFactory.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
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
		@SuppressWarnings ("deprecation")
		final Person person = new Person(username, fullname, email);
		final int use = (int) new DateTime().minus(this.created.getMillis()).getMillis() / 1000;
		this.leastRecentlyUsed.add(new PersonEntry(person, use));
		++this.load;
		if ((this.load > this.threshold) && (this.persistenceUtil != null)) {
			prune();
		}
		return person;
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
			
			final Person existingPerson = this.usernameToUser.get(username);
			if ((fullname != null) && (!existingPerson.getFullnames().contains(fullname))) {
				existingPerson.addFullname(fullname);
			}
			
			if ((email != null) && (!existingPerson.getEmailAddresses().contains(email))) {
				existingPerson.addEmail(email);
			}
			
			return existingPerson;
		} else if (fullname != null) {
			if (!this.fullnameToUser.containsKey(fullname)) {
				this.fullnameToUser.put(fullname, create(username, fullname, email));
			}
			
			final Person existingPerson = this.fullnameToUser.get(fullname);
			
			if ((email != null) && (!existingPerson.getEmailAddresses().contains(email))) {
				existingPerson.addEmail(email);
			}
			
			return existingPerson;
		} else if (email != null) {
			if (!this.emailToUser.containsKey(email)) {
				this.emailToUser.put(email, person);
			}
			
			return this.emailToUser.get(email);
		} else {
			return getUnknown();
		}
	}
	
	/**
	 * Gets the load.
	 * 
	 * @return the load
	 */
	public final long getLoad() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.load;
		} finally {
			POSTCONDITIONS: {
				CompareCondition.notNegative(this.load, "Field '%s' in '%s'.", "load", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the max ttl.
	 * 
	 * @return the maxTTL
	 */
	public final int getMaxTTL() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.maxTTL;
		} finally {
			POSTCONDITIONS: {
				CompareCondition.positive(this.maxTTL, "Field '%s' in '%s'.", "maxTTL", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the threshold.
	 * 
	 * @return the threshold
	 */
	public final long getThreshold() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.threshold;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.threshold, "Field '%s' in '%s'.", "threshold", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
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
	
	/**
	 * Prune.
	 */
	private void prune() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final List<PersonEntry> list = new LinkedList<>();
			assert !this.leastRecentlyUsed.isEmpty();
			assert this.persistenceUtil != null;
			final int now = (int) new DateTime().minus(this.created.getMillis()).getMillis() / 1000;
			
			do {
				final PersonEntry entry = this.leastRecentlyUsed.poll();
				list.add(entry);
			} while (!this.leastRecentlyUsed.isEmpty()
			        && ((now - this.leastRecentlyUsed.iterator().next().latestUsed) > this.maxTTL));
			
			this.persistenceUtil.beginTransaction();
			
			for (final PersonEntry entry : list) {
				this.persistenceUtil.save(entry.person);
				if (!entry.person.getUsernames().isEmpty()) {
					this.usernameToUser.remove(entry.person.getUsernames().iterator().next());
				} else if (!entry.person.getFullnames().isEmpty()) {
					this.fullnameToUser.remove(entry.person.getFullnames().iterator().next());
				} else {
					assert !entry.person.getEmailAddresses().isEmpty();
					this.emailToUser.remove(entry.person.getEmailAddresses().iterator().next());
				}
				--this.load;
			}
			
			this.persistenceUtil.commitTransaction();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the persistence util.
	 * 
	 * @param persistenceUtil
	 *            the persistenceUtil to set
	 */
	public final void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		PRECONDITIONS: {
			Condition.notNull(persistenceUtil, "Argument '%s' in '%s'.", "persistenceUtil", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		try {
			this.persistenceUtil = persistenceUtil;
		} finally {
			POSTCONDITIONS: {
				CompareCondition.equals(this.persistenceUtil, persistenceUtil,
				                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Sets the threshold.
	 * 
	 * @param threshold
	 *            the threshold to set
	 */
	public final void setThreshold(final long threshold) {
		PRECONDITIONS: {
			Condition.notNull(threshold, "Argument '%s' in '%s'.", "threshold", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		try {
			this.threshold = threshold;
		} finally {
			POSTCONDITIONS: {
				CompareCondition.equals(this.threshold, threshold,
				                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
			}
		}
	}
}
