/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
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
	
	private final HibernateUtil            hibernateUtil;
	
	public PersonManager(final HibernateUtil hibernateUtil) {
		this.hibernateUtil = hibernateUtil;
	}
	
	/**
	 * @param person
	 */
	public void add(final Person person) {
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
	public Collection<Person> collision(final Person person) {
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
	public void delete(final Person collider) {
		for (String key : this.emailMap.keySet()) {
			if (this.emailMap.get(key).equals(collider)) {
				this.emailMap.remove(key);
			}
		}
		
		for (String key : this.usernameMap.keySet()) {
			if (this.usernameMap.get(key).equals(collider)) {
				this.usernameMap.remove(key);
			}
		}
		
		for (String key : this.fullnameMap.keySet()) {
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
	public Collection<Person> getPersons() {
		return this.persons;
	}
	
	/**
	 * 
	 */
	public void loadEntities() {
		if (this.hibernateUtil != null) {
			Criteria criteria = this.hibernateUtil.createCriteria(Person.class);
			@SuppressWarnings ("unchecked") List<Person> results = criteria.list();
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
	public void setPersons(final Collection<Person> persons) {
		this.persons = new HashSet<Person>(persons);
	}
	
}
