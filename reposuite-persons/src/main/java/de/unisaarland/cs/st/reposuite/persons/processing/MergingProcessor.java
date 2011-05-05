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
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.persons.elements.PersonBucket;
import de.unisaarland.cs.st.reposuite.persons.engine.MergingEngine;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
	 * 
	 */
	public void consolidate() {
		this.manager.consolidate();
	}
	
	/**
	 * @param container
	 */
	public void process(final PersonContainer container) {
		for (Person person : container.getPersons()) {
			HashMap<Class<? extends MergingEngine>, Boolean> features = new HashMap<Class<? extends MergingEngine>, Boolean>();
			boolean collision = false;
			for (MergingEngine engine : this.engines.values()) {
				List<PersonBucket> list = engine.collides(person, container, this.manager, features);
				if (!list.isEmpty()) {
					
					if (Logger.logDebug()) {
						Logger.debug("Collision for person: " + person);
					}
					PersonBucket first = list.iterator().next();
					list.remove(first);
					first.insert(person, container, this.manager);
					
					for (PersonBucket bucket : list) {
						PersonBucket.merge(bucket, first, this.manager);
					}
					
					collision = true;
					this.manager.updateAndRemove(first, list);
					break;
				}
			}
			if (!collision) {
				if (Logger.logDebug()) {
					Logger.debug("No collision for person: " + person);
				}
				this.manager.updateAndRemove(new PersonBucket(person, container), new LinkedList<PersonBucket>());
			}
			collision = false;
		}
	}
	
	/**
	 * @param util
	 */
	public void providePersistenceUtil(final PersistenceUtil util) {
		this.manager = new PersonManager(util);
	}
}
