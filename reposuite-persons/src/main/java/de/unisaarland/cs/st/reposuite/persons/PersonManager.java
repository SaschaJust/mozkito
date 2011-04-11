/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonManager {
	
	private Set<Person>                    persons     = new HashSet<Person>();
	private final Map<String, Person>      emailMap    = new HashMap<String, Person>();
	private final Map<String, Person>      usernameMap = new HashMap<String, Person>();
	private final Map<String, Set<Person>> fullnameMap = new HashMap<String, Set<Person>>();
	
	private final PersistenceUtil          persistenceUtil;
	
	public PersonManager(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
	
	/**
	 * @param person
	 */
	public synchronized void add(final Person person) {
		getPersons().add(person);
		
		for (String email : person.getEmailAddresses()) {
			this.emailMap.put(email, person);
		}
		
		for (String username : person.getUsernames()) {
			this.usernameMap.put(username, person);
		}
		
		for (String fullname : person.getFullnames()) {
			if (this.fullnameMap.get(fullname) == null) {
				this.fullnameMap.put(fullname, new HashSet<Person>());
			}
			this.fullnameMap.get(fullname).add(person);
		}
	}
	
	/**
	 * @param person
	 * @return
	 */
	public synchronized Collection<Person> collision(final Person person) {
		LinkedList<Person> colliders = new LinkedList<Person>();
		
		for (Person reference : getPersons()) {
			if (reference.matches(person)) {
				colliders.add(reference);
			}
		}
		return colliders;
	}
	
	/**
	 * @param collider
	 */
	public synchronized void delete(final Person collider) {
		String[] keys = this.emailMap.keySet().toArray(new String[0]);
		for (String key : keys) {
			if (this.emailMap.get(key).equals(collider)) {
				this.emailMap.remove(key);
			}
		}
		
		keys = this.usernameMap.keySet().toArray(new String[0]);
		for (String key : keys) {
			if (this.usernameMap.get(key).equals(collider)) {
				this.usernameMap.remove(key);
			}
		}
		
		keys = this.fullnameMap.keySet().toArray(new String[0]);
		for (String key : keys) {
			if (this.fullnameMap.get(key).remove(collider)) {
				if (this.fullnameMap.get(key).isEmpty()) {
					this.fullnameMap.remove(key);
				}
			}
		}
		this.persons.remove(collider);
	}
	
	/**
	 * @return
	 */
	public synchronized Collection<Person> getPersons() {
		return this.persons;
	}
	
	/**
	 * 
	 */
	public synchronized void loadEntities() {
		if (this.persistenceUtil != null) {
			Criteria<Person> criteria = this.persistenceUtil.createCriteria(Person.class);
			List<Person> results = this.persistenceUtil.load(criteria);
			if ((results != null) && (results.size() > 0)) {
				setPersons(results);
				if (Logger.logInfo()) {
					Logger.info("Loaded " + results.size() + " persons from persitence storage.");
				}
			}
		}
	}
	
	/**
	 * @param persons
	 */
	public synchronized void setPersons(final Collection<Person> persons) {
		this.persons = new HashSet<Person>(persons);
	}
	
}
