/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class HibernateInterceptor extends EmptyInterceptor {
	
	PersonManager                             personManager;
	HashMap<Person, HashSet<PersonContainer>> remap               = new HashMap<Person, HashSet<PersonContainer>>();
	HibernateUtil                             hibernateUtil       = null;
	private HibernateInterceptor              previousInterceptor = null;
	
	/**
     * 
     */
	private static final long                 serialVersionUID    = 3960920011929042813L;
	
	/**
	 * @param interceptor
	 * @param hibernateUtil
	 */
	public HibernateInterceptor(final HibernateInterceptor interceptor, final HibernateUtil hibernateUtil) {
		this(hibernateUtil);
		this.previousInterceptor = interceptor;
	}
	
	/**
	 * @param hibernateUtil
	 */
	public HibernateInterceptor(final HibernateUtil hibernateUtil) {
		this.personManager = new PersonManager(hibernateUtil);
		this.hibernateUtil = hibernateUtil;
	}
	
	/**
	 * loads the all known entities from the persistent storage
	 */
	public void loadEntities() {
		this.personManager.loadEntities();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(final Object entity,
	                      final Serializable id,
	                      final Object[] state,
	                      final String[] propertyNames,
	                      final Type[] types) {
		if (entity instanceof PersonContainer) {
			PersonContainer container = (PersonContainer) entity;
			
			if (Logger.logDebug()) {
				Logger.debug("Intercepting save action for " + container);
			}
			
			for (Person person : container.interceptorTargets()) {
				Collection<Person> collisions = this.personManager.collision(person);
				
				// found collision?
				if (!collisions.isEmpty()) {
					if (collisions.size() == 1) {
						Person collider = collisions.iterator().next();
						Person reference = Person.merge(collider, person);
						if (Logger.logDebug()) {
							Logger.debug("Replacing person " + person + " by " + reference + ".");
						}
						
						container.replace(person, reference);
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
							Condition.containsKey(this.remap, collider,
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
							this.hibernateUtil.delete(collider);
						}
						if (Logger.logDebug()) {
							Logger.debug("Saving merged person " + keeper + ".");
						}
						this.hibernateUtil.save(keeper);
						
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
							this.hibernateUtil.update(tmpContainer);
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
		
		if (this.previousInterceptor == null) {
			return super.onSave(entity, id, state, propertyNames, types);
		} else {
			return this.previousInterceptor.onSave(entity, id, state, propertyNames, types);
		}
	}
	
	/**
	 * Refreshes Hashmap due to modifications of the keys...
	 */
	private void rehash() {
		HashMap<Person, HashSet<PersonContainer>> remapNew = new HashMap<Person, HashSet<PersonContainer>>();
		remapNew.putAll(this.remap);
		this.remap = remapNew;
	}
	
}
