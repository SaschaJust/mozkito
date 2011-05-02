/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons.processing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persons.elements.PersonBucket;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonManager {
	
	private final Map<String, List<PersonBucket>> emailMap    = new HashMap<String, List<PersonBucket>>();
	private final Map<String, List<PersonBucket>> usernameMap = new HashMap<String, List<PersonBucket>>();
	private final Map<String, List<PersonBucket>> fullnameMap = new HashMap<String, List<PersonBucket>>();
	private final PersistenceUtil                 util;
	
	public PersonManager(final PersistenceUtil util) {
		this.util = util;
	}
	
	/**
	 * @param person
	 * @return
	 */
	public List<PersonBucket> getBuckets(final Person person) {
		List<PersonBucket> list = new LinkedList<PersonBucket>();
		
		for (String email : person.getEmailAddresses()) {
			if (this.emailMap.containsKey(email)) {
				list.addAll(this.emailMap.get(email));
			}
		}
		
		for (String username : person.getUsernames()) {
			if (this.usernameMap.containsKey(username)) {
				list.addAll(this.usernameMap.get(username));
			}
		}
		
		for (String fullname : person.getFullnames()) {
			if (this.fullnameMap.containsKey(fullname)) {
				list.addAll(this.fullnameMap.get(fullname));
			}
		}
		
		return list;
	}
	
	/**
	 * @return the util
	 */
	public PersistenceUtil getUtil() {
		return this.util;
	}
	
	/**
	 * @param bucket
	 * @param list
	 */
	public void updateAndRemove(final PersonBucket bucket,
	                            final List<PersonBucket> list) {
		// update
		for (String username : bucket.getUsernames()) {
			if (!this.usernameMap.containsKey(username)) {
				this.usernameMap.put(username, new LinkedList<PersonBucket>());
			}
			List<PersonBucket> buckets = this.usernameMap.get(username);
			buckets.add(bucket);
			this.usernameMap.put(username, buckets);
		}
		
		for (String email : bucket.getEmails()) {
			if (!this.emailMap.containsKey(email)) {
				this.emailMap.put(email, new LinkedList<PersonBucket>());
			}
			List<PersonBucket> buckets = this.emailMap.get(email);
			buckets.add(bucket);
			this.emailMap.put(email, buckets);
		}
		
		for (String fullname : bucket.getFullnames()) {
			if (!this.fullnameMap.containsKey(fullname)) {
				this.fullnameMap.put(fullname, new LinkedList<PersonBucket>());
			}
			List<PersonBucket> buckets = this.fullnameMap.get(fullname);
			buckets.add(bucket);
			this.fullnameMap.put(fullname, buckets);
		}
		
		// remove
		if (!list.isEmpty()) {
			for (String key : this.emailMap.keySet()) {
				if (this.emailMap.containsKey(key)) {
					this.emailMap.get(key).removeAll(list);
				}
			}
			
			for (String key : this.usernameMap.keySet()) {
				if (this.usernameMap.containsKey(key)) {
					this.usernameMap.get(key).removeAll(list);
				}
			}
			
			for (String key : this.fullnameMap.keySet()) {
				if (this.fullnameMap.containsKey(key)) {
					this.fullnameMap.get(key).removeAll(list);
				}
			}
		}
	}
	
}
