/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonManager {
	
	private final Set<Person> persons = new HashSet<Person>();
	
	public Person getPerson(Person person) {
		if (person == null) {
			person = new Person("<<server>>", null, null);
		}
		
		if (this.persons.contains(person)) {
			final Person searchTarget = person;
			person = (Person) CollectionUtils.find(this.persons, new Predicate() {
				
				@Override
				public boolean evaluate(final Object object) {
					return object.equals(searchTarget);
				}
			});
			if (Logger.logTrace()) {
				Logger.trace("Serving known " + Person.getHandle() + ": " + person);
			}
		} else {
			this.persons.add(person);
			if (Logger.logTrace()) {
				Logger.trace("Adding new " + Person.getHandle() + ": " + person);
			}
			
		}
		assert (person != null);
		return person;
	}
}
