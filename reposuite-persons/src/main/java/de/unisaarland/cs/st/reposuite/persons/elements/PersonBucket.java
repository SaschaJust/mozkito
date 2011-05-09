/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons.elements;

import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.persons.processing.PersonManager;
import net.ownhero.dev.kisa.Logger;
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
	
	List<Tuple<Person, List<PersonContainer>>> persons = new LinkedList<Tuple<Person, List<PersonContainer>>>();
	
	/**
	 * 
	 */
	public PersonBucket() {
		
	}
	
	/**
	 * @param person
	 * @param container
	 */
	public PersonBucket(final Person person, final PersonContainer container) {
		LinkedList<PersonContainer> list = new LinkedList<PersonContainer>();
		list.add(container);
		this.persons.add(new Tuple<Person, List<PersonContainer>>(person, list));
	}
	
	/**
	 * @param manager
	 */
	public void consolidate(final PersonManager manager) {
		// Consolidate this bucket.
		// We actually should never have empty buckets
		if (!this.persons.isEmpty()) {
			if (Logger.logDebug()) {
				Logger.debug("Consolidating " + this.hashCode());
			}
			
			// Since we checked the entries to be not empty, we can safely
			// remove the first entry and take it as the main one.
			Tuple<Person, List<PersonContainer>> mainEntry = this.persons.remove(0);
			
			for (Tuple<Person, List<PersonContainer>> entry : this.persons) {
				// Check if we already processed/deleted the person in this
				// entry.
				if (!manager.isProcessed(entry.getFirst().getGeneratedId())) {
					if (Logger.logDebug()) {
						Logger.debug("Deleting " + entry.getFirst() + " and merging data into " + mainEntry.getFirst());
					}
					
					// Merge the data of person in the entry into the person in
					// the mainEntry.
					Person.merge(mainEntry.getFirst(), entry.getFirst());
					
					// Step through all containers in the current entry
					for (PersonContainer container : entry.getSecond()) {
						// Replace the person in the container by the person in
						// the mainEntry
						container.replace(entry.getFirst(), mainEntry.getFirst());
						
						// Shift responsibility of the PersonContainer:container
						// to the mainEntry
						mainEntry.getSecond().add(container);
					}
					
					// delete the person in the current entry
					manager.delete(entry.getFirst());
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Skipping " + entry.getFirst()
						        + ". We shouldn't see a person twice on unmerged data.");
					}
				}
			}
			
			this.persons.clear();
			this.persons.add(mainEntry);
			
			if (Logger.logDebug()) {
				Logger.debug("Keeping " + mainEntry);
			}
		}
	}
	
	/**
	 * @param person
	 * @return
	 */
	private Tuple<Person, List<PersonContainer>> find(final Person person) {
		// step through all entries in the person list
		for (Tuple<Person, List<PersonContainer>> p : this.persons) {
			// if one person equals the person under suspect (exact same data)
			if (p.getFirst().equals(person)) {
				// return the entry
				return p;
			}
		}
		
		// return null if we didn't find a person equal to the one under suspect
		return null;
	}
	
	/**
	 * @return
	 */
	public List<String> getEmails() {
		List<String> list = new LinkedList<String>();
		for (Tuple<Person, List<PersonContainer>> key : this.persons) {
			
			list.addAll(key.getFirst().getEmailAddresses());
		}
		return list;
	}
	
	/**
	 * @return
	 */
	public List<String> getFullnames() {
		List<String> list = new LinkedList<String>();
		for (Tuple<Person, List<PersonContainer>> key : this.persons) {
			
			list.addAll(key.getFirst().getFullnames());
		}
		return list;
	}
	
	/**
	 * @return
	 */
	public List<String> getUsernames() {
		List<String> list = new LinkedList<String>();
		for (Tuple<Person, List<PersonContainer>> key : this.persons) {
			list.addAll(key.getFirst().getUsernames());
		}
		return list;
	}
	
	/**
	 * @param email
	 * @return
	 */
	public boolean hasEmail(final String email) {
		for (Tuple<Person, List<PersonContainer>> key : this.persons) {
			if (key.getFirst().getEmailAddresses().contains(email)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param fullname
	 * @return
	 */
	public boolean hasFullname(final String fullname) {
		for (Tuple<Person, List<PersonContainer>> key : this.persons) {
			if (key.getFirst().getFullnames().contains(fullname)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param username
	 * @return
	 */
	public boolean hasUsername(final String username) {
		for (Tuple<Person, List<PersonContainer>> key : this.persons) {
			if (key.getFirst().getUsernames().contains(username)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param person
	 * @param container
	 * @param manager
	 */
	public void insert(final Person person,
	                   final PersonContainer container,
	                   final PersonManager manager) {
		// look-up the person in the present mappings (a person with the exact
		// same data)
		Tuple<Person, List<PersonContainer>> bucketEntry = find(person);
		
		// if we found a matching entry
		if (bucketEntry != null) {
			if (bucketEntry.getFirst().getGeneratedId() != person.getGeneratedId()) {
				if (Logger.logDebug()) {
					Logger.debug("Replacing " + person + " by " + bucketEntry.getFirst());
				}
				
				// replace the person in the container under subject by the
				// person
				// from this bucket
				container.replace(person, bucketEntry.getFirst());
				
				// delete the current person from the database
				manager.delete(person);
			} else {
				if (Logger.logDebug()) {
					Logger.debug("Ignoring replace for identical " + person + ".");
				}
			}
			
			// add responsibility about the PersonContainer:container to the
			// person from this bucket
			bucketEntry.getSecond().add(container);
		} else {
			if (Logger.logDebug()) {
				Logger.debug("Adding person " + person);
			}
			
			// add a new entry for this person since there hasn't been a person
			// with the exact same data yet
			LinkedList<PersonContainer> list = new LinkedList<PersonContainer>();
			list.add(container);
			this.persons.add(new Tuple<Person, List<PersonContainer>>(person, list));
		}
	}
	
	/**
	 * @param tuples
	 * @param manager
	 */
	private void insertAll(final List<Tuple<Person, List<PersonContainer>>> tuples,
	                       final PersonManager manager) {
		for (Tuple<Person, List<PersonContainer>> key : tuples) {
			for (PersonContainer container : key.getSecond()) {
				insert(key.getFirst(), container, manager);
			}
		}
	}
}
