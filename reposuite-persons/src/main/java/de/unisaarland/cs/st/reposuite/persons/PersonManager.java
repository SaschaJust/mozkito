/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
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
			if (!this.fullnameMap.containsKey(fullname)) {
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
		
		if (!this.persons.remove(collider)) {
			if (Logger.logError()) {
				Logger.error("Could not remove collider from person list: " + collider);
				Logger.error("Relevant active person list: ");
				for (Person person : getPersons()) {
					if (!CollectionUtils.intersection(collider.getEmailAddresses(), person.getEmailAddresses())
					                    .isEmpty()
					        || !CollectionUtils.intersection(collider.getFullnames(), person.getFullnames()).isEmpty()
					        || !CollectionUtils.intersection(collider.getUsernames(), person.getUsernames()).isEmpty()) {
						Logger.error(person.toString());
					}
				}
			}
			throw new UnrecoverableError("Could not remove collider from person list: " + collider);
		}
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
	public void rehash() {
		setPersons(new HashSet<Person>(getPersons()));
	}
	
	/**
	* @param persons
	*/
	public synchronized void setPersons(final Collection<Person> persons) {
		this.persons = new HashSet<Person>(persons);
	}
	
}
