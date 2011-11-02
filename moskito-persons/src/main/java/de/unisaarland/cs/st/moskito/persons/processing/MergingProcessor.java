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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.moskito.persons.elements.PersonBucket;
import de.unisaarland.cs.st.moskito.persons.engine.MergingEngine;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MergingProcessor {
	
	private PersonManager                    manager;
	private final Map<String, MergingEngine> engines = new HashMap<String, MergingEngine>();
	
	/**
	 * @param engine
	 */
	public void addEngine(final MergingEngine engine) {
		this.engines.put(engine.getClass().getCanonicalName(), engine);
	}
	
	/**
	 * The method is called after processing all {@link Person}s to consolidate 
	 * the {@link Person}s in the created {@link PersonBucket}s.
	 */
	public void consolidate() {
		// delegate consolidation to the PersonManager:manager
		this.manager.consolidate();
	}
	
	/**
	 * Performs the actual merging algorithm using all active {@link MergingEngine}s.
	 * 
	 * @param container 
	 *        the {@link PersonContainer} unter subject
	 */
	public void process(final PersonContainer container) {
		// process current PersonContainer:container
		
		// step through every Person:person in the PersonContainer:container
		// and apply the algorithm
		for (Person person : container.getPersons()) {
			if (Logger.logDebug()) {
				Logger.debug("Performing merging algorithm on " + person);
			}
			
			// provide a container to add target buckets returned by merging
			// engines
			Set<PersonBucket> targetBuckets = new HashSet<PersonBucket>();
			
			// the main target bucket
			PersonBucket mainTargetBucket = null;
			
			// get all target buckets from all MergingEngine:engines and add
			// them to the PersonBucket container
			for (MergingEngine engine : this.engines.values()) {
				targetBuckets.addAll(engine.collides(person, container, this.manager));
			}
			
			// if the engines found valid buckets for the current person
			if (!targetBuckets.isEmpty()) {
				if (Logger.logDebug()) {
					Logger.debug("Found active bucket(s) (" + targetBuckets.size() + ") for: " + person);
				}
				
				// we checked that the container isn't empty
				// so we can remove one element without further checks
				mainTargetBucket = targetBuckets.iterator().next();
				targetBuckets.remove(mainTargetBucket);
				
				// insert the current person in the first bucket
				// this will cause a replace&delete of the person
				// under suspect if the bucket already contains
				// a person with the exact same data
				mainTargetBucket.insert(person, container, this.manager);
				
				// if there are further target buckets merge them
				// with the first one
				for (PersonBucket bucket : targetBuckets) {
					PersonBucket.merge(bucket, mainTargetBucket, this.manager);
				}
			} else {
				// there aren't any matching buckets yet
				if (Logger.logDebug()) {
					Logger.debug("No active buckets for: " + person + ". Creating new one.");
				}
				
				// create a new bucket for the person
				mainTargetBucket = new PersonBucket(person, container);
			}
			
			// put it in the PersonManager:manager
			// that will update internal responsibility maps (e.g.
			// Map<username, List<PersonBucket>)
			this.manager.updateAndRemove(mainTargetBucket, targetBuckets);
		}
	}
	
	/**
	 * Provide the {@link PersistenceUtil} for database manipulation.
	 * 
	 * @param util the {@link PersistenceUtil} to be used
	 */
	public void providePersistenceUtil(final PersistenceUtil util) {
		// Store the PersistenceUtil:util in the manager.
		// There shall be no other place where operations
		// on the database are requested.
		this.manager = new PersonManager(util);
	}
}
