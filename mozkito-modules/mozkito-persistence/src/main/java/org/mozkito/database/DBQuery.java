/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.database;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.persistence.Criteria;

/**
 * The Class Loader.
 * 
 * @param <T>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class DBQuery<T extends DBEntity> {
	
	/** The loader pool. */
	private DBQueryPool dBQueryPool;
	
	/**
	 * Instantiates a new loader.
	 * 
	 * @param dBQueryPool
	 *            the loader pool
	 */
	public DBQuery(final DBQueryPool dBQueryPool) {
		PRECONDITIONS: {
			if (dBQueryPool == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			// body
			this.dBQueryPool = dBQueryPool;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.dBQueryPool,
				                  "Field '%s' in '%s'.", "this.loaderPool", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Load all.
	 * 
	 * @return the list
	 */
	public abstract List<T> load();
	
	/**
	 * Load.
	 * 
	 * @param criteria
	 *            the criteria
	 * @return the list
	 */
	public abstract List<T> load(Criteria<T> criteria);
	
	/**
	 * Load by id.
	 * 
	 * @param id
	 *            the id
	 * @return the t
	 */
	public abstract T loadById(String id);
	
	/**
	 * Save.
	 * 
	 * @param entity
	 *            the entity
	 * @return true, if successful
	 */
	public DBUpdateStatus saveOrUpdate(final T entity) {
		return saveOrUpdate(entity, new DBSaveStack());
	}
	
	private DBUpdateStatus saveOrUpdate(final T entity,
	                                    final DBSaveStack dBSaveStack) {
		PRECONDITIONS: {
			if (entity == null) {
				throw new NullPointerException();
			}
			if (dBSaveStack == null) {
				throw new NullPointerException();
			}
		}
		
		dBSaveStack.add(entity);
		
		Method idGetter = null;
		final Method[] declaredMethods = entity.getClass().getDeclaredMethods();
		for (final Method method : declaredMethods) {
			if (method.isAnnotationPresent(javax.persistence.Id.class)) {
				idGetter = method;
				break;
			}
		}
		if (idGetter == null) {
			// FIXME could not find id getter
			throw new NullPointerException();
		}
		
		String insertQuery = String.format("INSERT INTO %s", entity.getDBTableName());
		String idColumnName = idGetter.getName().toLowerCase().replaceFirst("get", "");
		if (!idGetter.isAnnotationPresent(javax.persistence.GeneratedValue.class)) {
			try {
	            insertQuery += String.format(" (%s) VALUES (%s)",idColumnName, idGetter.invoke(entity));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	            // TODO Auto-generated catch block
            }
		}
		
		// create DB entry with no values
		// if ID getter annotated with @GeneratedId, do not set Id
		// else if getter returns null throw exception
		// else set Id while creating entry
		
		// select to get ID back from DB
		
		// set Id back to object
		
		// push object onto saveStack
		
		// for all getters:
		// if name minus prefix "get" and toLowercase equals a fieldname (toLowercase) and has no @transient annotation
		// assume saveOrUpdate
		// if assume saveOrUpdate, call getter and save to temporary proxyobject
		// if not instanceof collection
		// check for object in saveStack
		// if not: loaderPool.getLoader(object) (assert exists) .saveOrUpdate(object, saveStack)
		// else: nothing
		// use object.getId in query for this object
		// else: (instanceof collection)
		// determine target table and foreach object in the collection:
		// if not in saveStack:
		// loaderPool.getLoader(object).saveOrUpdate(object, saveStack)
		// else: // nothing
		// use object.getId in query for join table
		
		// create queries for join tables
		// create query
		// execute
		
		return DBUpdateStatus.SUCCESS;
	}
}
