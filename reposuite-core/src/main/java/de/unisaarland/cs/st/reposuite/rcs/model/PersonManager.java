/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonManager {
	
	private Set<Person> persons = new HashSet<Person>();
	
	public Person getPerson(Person person) {
		if (person == null) {
			person = new Person("<unknown>", null, null);
		}
		
		final Person searchTarget = person;
		Person findPerson = (Person) CollectionUtils.find(this.persons, new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				return object.equals(searchTarget);
			}
		});
		
		if (findPerson != null) {
			person = findPerson;
			if (Logger.logTrace()) {
				Logger.trace("Serving known " + Person.getHandle() + ": " + person);
			}
		} else {
			@SuppressWarnings ("unchecked") Collection<Person> candidates = CollectionUtils.select(this.persons,
			        new Predicate() {
				        
				        @Override
				        public boolean evaluate(final Object object) {
					        return ((((Person) object).getUsername() != null) && ((Person) object).getUsername()
					                .equals(searchTarget.getUsername()))
					                || ((((Person) object).getEmail() != null) && ((Person) object).getEmail().equals(
					                        searchTarget.getEmail()))
					                || ((((Person) object).getFullname() != null)
					                        && (((Person) object).getEmail() == null)
					                        && (((Person) object).getUsername() == null) && ((Person) object)
					                        .getFullname().equals(searchTarget.getFullname()));
				        }
			        });
			
			if ((candidates != null) && !candidates.isEmpty()) {
				/*
				 * found multiple targets with - same username or - same email
				 * or - same fullname but username/email not set merge them
				 */
				candidates.add(searchTarget);
				Person moltenCore = Person.merge(candidates);
				if (Logger.logWarn()) {
					Logger.warn("Merged " + candidates.size() + " " + Person.getHandle() + "s ("
					        + JavaUtils.collectionToString(candidates) + "), resulting in: " + moltenCore);
					Logger.warn("Set size before: " + this.persons.size());
				}
				
				this.persons.removeAll(candidates);
				
				if (Logger.logWarn()) {
					Logger.warn("Set size after: " + this.persons.size());
				}
				
				this.persons.add(moltenCore);
				return moltenCore;
			} else {
				
				this.persons.add(person);
				if (Logger.logTrace()) {
					Logger.trace("Adding new " + Person.getHandle() + ": " + person);
				}
			}
		}
		Condition.notNull(person);
		return person;
	}
	
	public void setPersons(final Collection<Person> persons) {
		this.persons = new HashSet<Person>(persons);
	}
}
