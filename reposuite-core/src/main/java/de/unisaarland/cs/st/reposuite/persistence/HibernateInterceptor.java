/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	PersonManager                      personManager;
	Map<Person, List<PersonContainer>> remap            = new HashMap<Person, List<PersonContainer>>();
	HibernateUtil                      hibernateUtil    = null;
	
	/**
     * 
     */
	private static final long          serialVersionUID = 3960920011929042813L;
	
	/**
	 * 
	 */
	public HibernateInterceptor(final HibernateUtil hibernateUtil) {
		this.personManager = new PersonManager(hibernateUtil);
		this.hibernateUtil = hibernateUtil;
	}
	
	/**
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings ("unused")
	private Method getGetterForPerson(final String fieldName) {
		Condition.notNull(fieldName);
		Condition.greater(fieldName.length(), 1, "The name of the field has to consist of at least 2 characters.");
		
		try {
			return Person.class.getMethod("get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),
			        new Class<?>[0]);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	/**
	 * @param getterMethod
	 * @param person
	 * @return
	 */
	@SuppressWarnings ("unused")
	private Object getValueFromPerson(final Method getterMethod, final Person person) {
		Condition.notNull(getterMethod);
		Condition.notNull(person);
		
		try {
			return getterMethod.invoke(person, new Object[0]);
		} catch (IllegalArgumentException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (IllegalAccessException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (InvocationTargetException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	};
	
	/**
	 * 
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
	public boolean onSave(final Object entity, final Serializable id, final Object[] state,
	        final String[] propertyNames, final Type[] types) {
		if (entity instanceof PersonContainer) {
			PersonContainer container = (PersonContainer) entity;
			
			if (Logger.logWarn()) {
				Logger.warn("Intercepting save action for " + container);
			}
			
			for (Person person : container.interceptorTargets()) {
				Collection<Person> collisions = this.personManager.collision(person);
				
				// found collision?
				if (!collisions.isEmpty()) {
					if (collisions.size() == 1) {
						Person reference = Person.merge(collisions.iterator().next(), person);
						if (Logger.logWarn()) {
							Logger.warn("Replacing person " + person + " by " + reference + ".");
						}
						container.replace(person, Person.merge(reference, person));
						if (Logger.logWarn()) {
							Logger.warn("from " + person + ".");
						}
					} else {
						if (Logger.logWarn()) {
							Logger.warn("Performing merge on " + person + " due to collisions with "
							        + JavaUtils.collectionToString(collisions));
						}
						// merge
						Person keeper = null;
						int i = 0;
						
						// find the person with the most references
						for (Person collider : collisions) {
							if (this.remap.get(collider).size() > i) {
								keeper = collider;
								i = this.remap.get(collider).size();
							}
						}
						
						if (Logger.logWarn()) {
							Logger.warn("Keeping " + keeper + " due to least references ("
							        + this.remap.get(keeper).size());
						}
						
						collisions.remove(keeper);
						
						if (Logger.logWarn()) {
							Logger.warn("Merging " + keeper + " with " + JavaUtils.collectionToString(collisions));
						}
						Person.merge(keeper, collisions);
						if (Logger.logWarn()) {
							Logger.warn("Merging " + keeper + " with " + person + ".");
						}
						Person.merge(keeper, person);
						
						this.hibernateUtil.beginTransaction();
						for (Person collider : collisions) {
							if (Logger.logWarn()) {
								Logger.warn("Deleting collision " + collider + ".");
							}
							this.personManager.delete(collider);
							this.hibernateUtil.delete(collider);
						}
						if (Logger.logWarn()) {
							Logger.warn("Saving merged person " + keeper + ".");
						}
						this.hibernateUtil.save(keeper);
						
						if (Logger.logWarn()) {
							Logger.warn("Performing replace on known referencing entities of collisions.");
						}
						for (PersonContainer tmpContainer : this.remap.get(keeper)) {
							for (Person tmpPerson : tmpContainer.interceptorTargets()) {
								if (tmpPerson.matches(keeper)) {
									if (Logger.logWarn()) {
										Logger.warn("Replacing " + tmpPerson + " by " + keeper);
									}
									tmpContainer.replace(tmpPerson, keeper);
								}
							}
							if (Logger.logWarn()) {
								Logger.warn("Updating referencing entity.");
							}
							this.hibernateUtil.update(tmpContainer);
						}
						if (Logger.logWarn()) {
							Logger.warn("Committing to database.");
						}
						this.hibernateUtil.commitTransaction();
						
						if (Logger.logWarn()) {
							Logger.warn("Replacing person " + person + " by " + keeper + ".");
						}
						container.replace(person, keeper);
					}
				} else {
					// new Person
					if (Logger.logWarn()) {
						Logger.warn("Adding new person " + person + ".");
					}
					this.personManager.add(person);
				}
				
				if (!this.remap.containsKey(person)) {
					if (Logger.logWarn()) {
						Logger.warn("Creating new mapping for person " + person + ".");
					}
					this.remap.put(person, new LinkedList<PersonContainer>());
				}
				
				if (Logger.logWarn()) {
					Logger.warn("Adding reference on person " + person + " from " + container + " to remap cache.");
				}
				this.remap.get(person).add(container);
			}
		}
		
		return super.onSave(entity, id, state, propertyNames, types);
	}
	
}
