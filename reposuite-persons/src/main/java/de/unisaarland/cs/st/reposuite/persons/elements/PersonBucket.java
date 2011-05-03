/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons.elements;

import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.persons.processing.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonBucket {
	
	public static PersonBucket merge(final PersonBucket from,
	                                 final PersonBucket to,
	                                 final PersonManager manager) {
		to.insertAll(from.persons, manager);
		return to;
	}
	
	List<Tuple<Person, PersonContainer>> persons = new LinkedList<Tuple<Person, PersonContainer>>();
	
	public PersonBucket() {
		
	}
	
	public PersonBucket(final Person person, final PersonContainer container) {
		this.persons.add(new Tuple<Person, PersonContainer>(person, container));
	}
	
	private Tuple<Person, PersonContainer> find(final Person person) {
		for (Tuple<Person, PersonContainer> p : this.persons) {
			if (p.getFirst().equals(person)) {
				return p;
			}
		}
		return null;
	}
	
	public List<String> getEmails() {
		List<String> list = new LinkedList<String>();
		for (Tuple<Person, PersonContainer> key : this.persons) {
			
			list.addAll(key.getFirst().getEmailAddresses());
		}
		return list;
	}
	
	public List<String> getFullnames() {
		List<String> list = new LinkedList<String>();
		for (Tuple<Person, PersonContainer> key : this.persons) {
			
			list.addAll(key.getFirst().getFullnames());
		}
		return list;
	}
	
	public List<String> getUsernames() {
		List<String> list = new LinkedList<String>();
		for (Tuple<Person, PersonContainer> key : this.persons) {
			list.addAll(key.getFirst().getUsernames());
		}
		return list;
	}
	
	public boolean hasEmail(final String email) {
		for (Tuple<Person, PersonContainer> key : this.persons) {
			if (key.getFirst().getEmailAddresses().contains(email)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasFullname(final String fullname) {
		for (Tuple<Person, PersonContainer> key : this.persons) {
			if (key.getFirst().getFullnames().contains(fullname)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasUsername(final String username) {
		for (Tuple<Person, PersonContainer> key : this.persons) {
			if (key.getFirst().getUsernames().contains(username)) {
				return true;
			}
		}
		return false;
	}
	
	public void insert(final Person person,
	                   final PersonContainer container,
	                   final PersonManager manager) {
		Tuple<Person, PersonContainer> p = find(person);
		if (p != null) {
			
			if (Logger.logDebug()) {
				Logger.debug("Replacing person: " + person);
			}
			container.replace(person, p.getFirst());
			manager.delete(person);
		} else {
			this.persons.add(new Tuple<Person, PersonContainer>(person, container));
		}
	}
	
	private void insertAll(final List<Tuple<Person, PersonContainer>> tuples,
	                       final PersonManager manager) {
		for (Tuple<Person, PersonContainer> key : tuples) {
			insert(key.getFirst(), key.getSecond(), manager);
		}
	}
}
