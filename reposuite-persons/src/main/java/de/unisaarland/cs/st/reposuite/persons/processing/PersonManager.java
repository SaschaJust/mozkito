/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons.processing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persons.elements.PersonBucket;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonManager {
	
	private final Map<String, List<PersonBucket>> emailMap    = new HashMap<String, List<PersonBucket>>();
	private final Map<String, List<PersonBucket>> usernameMap = new HashMap<String, List<PersonBucket>>();
	private final Map<String, List<PersonBucket>> fullnameMap = new HashMap<String, List<PersonBucket>>();
	private final PersistenceUtil                 util;
	private final TreeSet<Long>                   deleted     = new TreeSet<Long>();
	
	public PersonManager(final PersistenceUtil util) {
		this.util = util;
	}
	
	/**
	 * 
	 */
	public void beginTransaction() {
		getUtil().beginTransaction();
	}
	
	/**
	 * 
	 */
	public void commitTransaction() {
		getUtil().commitTransaction();
	}
	
	public void consolidate() {
		for (List<PersonBucket> buckets : this.emailMap.values()) {
			for (PersonBucket bucket : buckets) {
				bucket.consolidate(this);
			}
		}
		
		for (List<PersonBucket> buckets : this.usernameMap.values()) {
			for (PersonBucket bucket : buckets) {
				bucket.consolidate(this);
			}
		}
		
		for (List<PersonBucket> buckets : this.fullnameMap.values()) {
			for (PersonBucket bucket : buckets) {
				bucket.consolidate(this);
			}
		}
	}
	
	/**
	 * @param person
	 */
	public void delete(final Person person) {
		if (!this.deleted.contains(person.getGeneratedId())) {
			if (Logger.logDebug()) {
				Logger.debug("Deleting " + person);
			}
			getUtil().delete(person);
			this.deleted.add(person.getGeneratedId());
		}
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
	private PersistenceUtil getUtil() {
		return this.util;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public boolean isProcessed(final long id) {
		return this.deleted.contains(id);
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
					List<PersonBucket> buckets = new LinkedList<PersonBucket>();
					
					for (PersonBucket b1 : this.emailMap.get(key)) {
						boolean found = false;
						for (PersonBucket b2 : list) {
							if (b1 == b2) {
								found = true;
								break;
							}
						}
						
						if (!found) {
							buckets.add(b1);
						} else {
							found = false;
						}
					}
				}
			}
			
			for (String key : this.usernameMap.keySet()) {
				if (this.usernameMap.containsKey(key)) {
					List<PersonBucket> buckets = new LinkedList<PersonBucket>();
					
					for (PersonBucket b1 : this.usernameMap.get(key)) {
						boolean found = false;
						for (PersonBucket b2 : list) {
							if (b1 == b2) {
								found = true;
								break;
							}
						}
						
						if (!found) {
							buckets.add(b1);
						} else {
							found = false;
						}
					}
				}
			}
			
			for (String key : this.fullnameMap.keySet()) {
				List<PersonBucket> buckets = new LinkedList<PersonBucket>();
				
				for (PersonBucket b1 : this.fullnameMap.get(key)) {
					boolean found = false;
					for (PersonBucket b2 : list) {
						if (b1 == b2) {
							found = true;
							break;
						}
					}
					
					if (!found) {
						buckets.add(b1);
					} else {
						found = false;
					}
				}
			}
		}
	}
	
}
