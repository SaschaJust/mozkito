/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.MapCondition;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonsMerger extends RepoSuiteSinkThread<PersonContainer> {
	
	PersonManager                             personManager;
	HashMap<Person, HashSet<PersonContainer>> remap           = new HashMap<Person, HashSet<PersonContainer>>();
	PersistenceUtil                           persistenceUtil = null;
	Set<Person>                               deletes         = new HashSet<Person>();
	
	public PersonsMerger(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, PersonsMerger.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
		this.personManager = new PersonManager();
	}
	
	// private PersonContainer findPerson(final Person person) {
	// for (HashSet<PersonContainer> containerSet : this.remap.values()) {
	// if (containerSet.contains(person)) {
	// return (PersonContainer) CollectionUtils.find(containerSet, new
	// Predicate() {
	//
	// @Override
	// public boolean evaluate(final Object object) {
	// return person.matches((Person) object);
	// }
	// });
	// }
	// }
	// return null;
	// }
	
	/**
	 * Refreshes Hashmap due to modifications of the keys...
	 */
	private void rehash() {
		HashMap<Person, HashSet<PersonContainer>> remapNew = new HashMap<Person, HashSet<PersonContainer>>();
		remapNew.putAll(this.remap);
		this.remap = remapNew;
	}
	
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			PersonContainer container = null;
			
			while (!isShutdown() && ((container = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Processing " + container + ".");
				}
				
				for (Person person : container.interceptorTargets()) {
					
					Collection<Person> collisions = this.personManager.collision(person);
					
					// found collision?
					if (!collisions.isEmpty()) {
						
						if (!this.remap.keySet().contains(person)) {
							this.deletes.add(person);
						}
						
						if (Logger.logDebug()) {
							Logger.debug("Performing merge on " + person + " due to collisions with "
							        + JavaUtils.collectionToString(collisions));
						}
						
						// merge
						Person keeper = null;
						int i = 0;
						
						// find the person with the most references
						List<PersonContainer> updatableTargets = new LinkedList<PersonContainer>();
						rehash();
						for (Person collider : collisions) {
							try {
								MapCondition.containsKey(this.remap, collider,
								                         "Requesting remap for unknown collider. This should not happen.");
							} catch (AssertionError e) {
								if (Logger.logError()) {
									Logger.error("Causing instance: " + person);
									Logger.error("Collider instance: " + collider);
									
									for (Person p : this.remap.keySet()) {
										if (person.matches(p)) {
											Logger.error("Match in remap table: " + p);
										}
									}
									
									for (Person p : this.personManager.getPersons()) {
										if (person.matches(p)) {
											Logger.error("Match in person manager: " + p);
										}
									}
								}
								throw new UnrecoverableError(
								                             "Requesting remap for unknown collider. This should not happen.");
							}
							
							if (this.remap.get(collider).size() > i) {
								keeper = collider;
								i = this.remap.get(collider).size();
							}
							
							updatableTargets.addAll(this.remap.get(collider));
						}
						
						if (Logger.logDebug()) {
							Logger.debug("Keeping " + keeper + " due to most references ("
							        + this.remap.get(keeper).size() + ")");
						}
						
						collisions.remove(keeper);
						
						if (Logger.logDebug()) {
							Logger.debug("Merging " + keeper + " with " + person + ".");
						}
						
						Person.merge(keeper, person);
						
						if (Logger.logDebug()) {
							Logger.debug("Merging " + keeper + " with " + JavaUtils.collectionToString(collisions));
						}
						
						Person.merge(keeper, collisions);
						
						this.personManager.rehash();
						
						// this.persistenceUtil.beginTransaction();
						for (Person collider : collisions) {
							if (Logger.logDebug()) {
								Logger.debug("Deleting collision " + collider + ".");
							}
							this.personManager.delete(collider);
							this.remap.remove(collider);
							// this.persistenceUtil.delete(collider);
						}
						if (Logger.logDebug()) {
							Logger.debug("Saving merged person " + keeper + ".");
						}
						// this.persistenceUtil.save(keeper);
						
						if (Logger.logDebug()) {
							Logger.debug("Replacing person " + person + " by " + keeper + ".");
						}
						container.replace(person, keeper);
						person = keeper;
						
						if (Logger.logDebug()) {
							Logger.debug("Performing replace on known referencing entities of collisions.");
						}
						
						rehash();
						for (PersonContainer tmpContainer : updatableTargets) {
							for (Person tmpPerson : tmpContainer.interceptorTargets()) {
								if (tmpPerson.matches(keeper)) {
									if (Logger.logDebug()) {
										Logger.debug("Replacing " + tmpPerson + " by " + keeper);
									}
									tmpContainer.replace(tmpPerson, keeper);
								}
							}
							if (Logger.logDebug()) {
								Logger.debug("Updating referencing entity.");
							}
							// this.persistenceUtil.update(tmpContainer);
						}
						if (Logger.logDebug()) {
							Logger.debug("Committing to database.");
						}
						
						// this.persistenceUtil.commitTransaction();
						// }
					} else {
						// new Person
						if (Logger.logDebug()) {
							Logger.debug("Adding new person " + person + ".");
						}
						this.personManager.add(person);
					}
					
					rehash();
					if (!this.remap.containsKey(person)) {
						for (Person p : this.remap.keySet()) {
							if (p.hashCode() == person.hashCode()) {
								System.err.println("ERROR: " + p.hashCode());
							}
						}
						if (Logger.logDebug()) {
							Logger.debug("Creating new mapping for person " + person + ".");
						}
						this.remap.put(person, new HashSet<PersonContainer>());
					}
					
					if (Logger.logDebug()) {
						Logger.debug("Adding reference on person " + person + " from " + container + " to remap cache.");
					}
					boolean add = this.remap.get(person).add(container);
					if (!add) {
						if (Logger.logTrace()) {
							Logger.trace(container + " already known: "
							        + JavaUtils.collectionToString(this.remap.get(person)));
						}
					}
					
				}
			}
			
			// save new person objects
			if (Logger.logInfo()) {
				Logger.info("Storing new person objects (" + this.remap.keySet().size() + ").");
			}
			
			this.persistenceUtil.beginTransaction();
			for (Person person : this.remap.keySet()) {
				this.persistenceUtil.save(person);
			}
			this.persistenceUtil.commitTransaction();
			
			if (Logger.logInfo()) {
				Logger.info("Deleting old persons (" + this.deletes.size() + ").");
			}
			int commitIndex = 0;
			this.persistenceUtil.beginTransaction();
			for (Person person : this.deletes) {
				this.persistenceUtil.delete(person);
				if (commitIndex % 1000 == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
			}
			this.persistenceUtil.commitTransaction();
			
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
