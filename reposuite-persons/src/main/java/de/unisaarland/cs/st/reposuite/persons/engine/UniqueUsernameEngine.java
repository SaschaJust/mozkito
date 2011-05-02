/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.persons.elements.PersonBucket;
import de.unisaarland.cs.st.reposuite.persons.processing.PersonManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class UniqueUsernameEngine extends MergingEngine {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persons.engine.MergingEngine#collides(
	 * de.unisaarland.cs.st.reposuite.persistence.model.Person,
	 * de.unisaarland.cs.st.reposuite.persons.processing.PersonManager,
	 * java.util.Map)
	 */
	@Override
	public List<PersonBucket> collides(final Person person,
	                                   final PersonContainer container,
	                                   final PersonManager manager,
	                                   final Map<Class<? extends MergingEngine>, Boolean> features) {
		List<PersonBucket> buckets = manager.getBuckets(person);
		List<PersonBucket> list = new LinkedList<PersonBucket>();
		
		for (PersonBucket bucket : buckets) {
			for (String username : person.getUsernames()) {
				if (bucket.hasUsername(username)) {
					list.add(bucket);
				}
			}
		}
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persons.engine.MergingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Finds collision on unique usernames";
	}
	
}
