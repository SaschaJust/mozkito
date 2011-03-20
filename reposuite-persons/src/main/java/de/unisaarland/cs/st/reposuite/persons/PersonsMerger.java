/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.conditions.MapCondition;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonsMerger extends
        RepoSuiteTransformerThread<PersonContainer, HashMap<Person, HashSet<PersonContainer>>> {
	
	PersonManager                             personManager;
	HashMap<Person, HashSet<PersonContainer>> remap         = new HashMap<Person, HashSet<PersonContainer>>();
	HibernateUtil                             hibernateUtil = null;
	List<Person>                              deletes       = new LinkedList<Person>();
	
	public PersonsMerger(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final HibernateUtil hibernateUtil) {
		super(threadGroup, PersonsMerger.class.getSimpleName(), settings);
		this.hibernateUtil = hibernateUtil;
		this.personManager = new PersonManager(hibernateUtil);
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
						
						if (collisions.size() == 1) {
							Person collider = collisions.iterator().next();
							Person reference = Person.merge(collider, person);
							if (Logger.logDebug()) {
								Logger.debug("Replacing person " + person + " by " + reference + ".");
							}
							
							container.replace(person, reference);
							person.clearTransaction();
							person = reference;
						} else {
							if (Logger.logDebug()) {
								Logger.debug("Performing merge on " + person + " due to collisions with "
								        + JavaUtils.collectionToString(collisions));
							}
							// merge
							Person keeper = null;
							int i = 0;
							
							// find the person with the most references
							//
							List<PersonContainer> updatableTargets = new LinkedList<PersonContainer>();
							rehash();
							for (Person collider : collisions) {
								MapCondition.containsKey(this.remap, collider,
								                         "Requesting remap for unknown collider. This should not happen.");
								
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
							
							// this.hibernateUtil.beginTransaction();
							for (Person collider : collisions) {
								if (Logger.logDebug()) {
									Logger.debug("Deleting collision " + collider + ".");
								}
								this.personManager.delete(collider);
								this.remap.remove(collider);
								collider.clearTransaction();
								// this.hibernateUtil.delete(collider);
							}
							if (Logger.logDebug()) {
								Logger.debug("Saving merged person " + keeper + ".");
							}
							// this.hibernateUtil.save(keeper);
							
							if (Logger.logDebug()) {
								Logger.debug("Replacing person " + person + " by " + keeper + ".");
							}
							container.replace(person, keeper);
							person.clearTransaction();
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
								// this.hibernateUtil.update(tmpContainer);
							}
							if (Logger.logDebug()) {
								Logger.debug("Committing to database.");
							}
							
							// this.hibernateUtil.commitTransaction();
						}
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
			
			write(this.remap);
			
			// save new person objects
			if (Logger.logInfo()) {
				Logger.info("Storing new person objects (" + this.remap.keySet().size() + ").");
			}
			this.hibernateUtil.beginTransaction();
			for (Person person : this.remap.keySet()) {
				this.hibernateUtil.save(person);
			}
			this.hibernateUtil.commitTransaction();
			
			if (Logger.logInfo()) {
				Logger.info("Deleting old persons (" + this.deletes.size() + ").");
			}
			int commitIndex = 0;
			this.hibernateUtil.beginTransaction();
			Person person2 = null;
			try {
				for (Person person : this.deletes) {
					person2 = person;
					this.hibernateUtil.delete(person);
					if (commitIndex % 1000 == 0) {
						this.hibernateUtil.commitTransaction();
						this.hibernateUtil.beginTransaction();
					}
				}
				this.hibernateUtil.commitTransaction();
			} catch (Throwable t) {
				
				if (Logger.logError()) {
					Logger.error("Cannot delete: " + person2);
				}
			}
			
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
