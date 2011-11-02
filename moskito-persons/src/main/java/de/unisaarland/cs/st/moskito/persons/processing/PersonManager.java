/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persons.processing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persons.elements.PersonBucket;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonManager {
	
	private final Map<String, List<PersonBucket>> emailMap    = new HashMap<String, List<PersonBucket>>();
	private final Map<String, List<PersonBucket>> usernameMap = new HashMap<String, List<PersonBucket>>();
	private final Map<String, List<PersonBucket>> fullnameMap = new HashMap<String, List<PersonBucket>>();
	private final PersistenceUtil                 util;
	private final Set<Long>                       deleted     = new TreeSet<Long>();
	
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
	
	/**
	 * 
	 */
	public void consolidate() {
		// make sure we don't consolidate a bucket more than once, thus using a
		// set
		HashSet<PersonBucket> allBuckets = new HashSet<PersonBucket>();
		
		for (List<PersonBucket> buckets : this.emailMap.values()) {
			allBuckets.addAll(buckets);
		}
		
		for (List<PersonBucket> buckets : this.usernameMap.values()) {
			allBuckets.addAll(buckets);
		}
		
		for (List<PersonBucket> buckets : this.fullnameMap.values()) {
			allBuckets.addAll(buckets);
		}
		
		if (Logger.logInfo()) {
			Logger.info("Consolidating " + allBuckets.size() + " buckets.");
			Logger.info("Delete cache size: " + this.deleted.size());
		}
		
		for (PersonBucket bucket : allBuckets) {
			bucket.consolidate(this);
		}
	}
	
	/**
	 * @param person
	 */
	public void delete(final Person person) {
		// delete person if it hasn't been deleted yet
		if (!this.deleted.contains(person.getGeneratedId())) {
			if (Logger.logDebug()) {
				Logger.debug("Deleting " + person);
			}
			
			// delete the Person:person from the database
			// and cache its id
			this.deleted.add(person.getGeneratedId());
			getUtil().delete(person);
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Attempting to delete already deleted " + person);
			}
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
	                            final Collection<PersonBucket> list) {
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
				
				this.emailMap.put(key, buckets);
			}
			
			for (String key : this.usernameMap.keySet()) {
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
				this.usernameMap.put(key, buckets);
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
				this.fullnameMap.put(key, buckets);
			}
		}
	}
	
}
