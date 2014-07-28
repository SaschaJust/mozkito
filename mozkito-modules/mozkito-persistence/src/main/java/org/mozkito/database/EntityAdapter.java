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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;
import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class Loader.
 * 
 * @param <T>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class EntityAdapter<T extends Entity> {
	
	/** The loader pool. */
	private QueryPool queryPool;
	private Layout<T> layout;
	
	/**
	 * Instantiates a new loader.
	 * 
	 * @param queryPool
	 *            the loader pool
	 */
	@SuppressWarnings ("unchecked")
	protected EntityAdapter(final QueryPool queryPool) {
		PRECONDITIONS: {
			if (queryPool == null) {
				throw new NullPointerException("The database query pool has to be initiated at this point.");
			}
		}
		
		try {
			// body
			this.queryPool = queryPool;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.queryPool, "Field '%s' in '%s'.", "this.loaderPool", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		try {
			final Method method = getClass().getMethod("loadById", new Class<?>[] { Object.class });
			SANITY: {
				assert method != null;
			}
			
			final Class<T> clazz = (Class<T>) method.getReturnType();
			final List<Layout<?>> layouts = new LinkedList<>();
			
			for (final Field field : clazz.getFields()) {
				if (Layout.class.isAssignableFrom(field.getType()) && ((field.getModifiers() & Modifier.STATIC) != 0)) {
					// found
					layouts.add((Layout<?>) field.get(null));
				}
			}
			
			if (layouts.isEmpty()) {
				throw new UnrecoverableError("Could not find layout in class: " + clazz.getSimpleName());
			} else if (layouts.size() > 1) {
				throw new UnrecoverableError("Found multiple layouts in class: " + clazz.getSimpleName());
			} else {
				this.layout = (Layout<T>) layouts.iterator().next();
			}
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * Active transaction.
	 * 
	 * @return true, if successful
	 */
	public boolean activeTransaction() {
		return this.queryPool.getConnector().activeTransaction();
	}
	
	/**
	 * Begin transaction.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void beginTransaction() throws DatabaseException {
		this.queryPool.getConnector().beginTransaction();
	}
	
	/**
	 * Commit.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void commit() throws DatabaseException {
		this.queryPool.getConnector().commit();
	}
	
	/**
	 * Gets the connector.
	 * 
	 * @return the connector
	 */
	protected Connector getConnector() {
		return this.queryPool.getConnector();
	}
	
	/**
	 * Gets the id.
	 * 
	 * @param idSet
	 *            the id set
	 * @return the id
	 * @throws DatabaseException
	 */
	public abstract Object getId(ResultSet idSet) throws DatabaseException;
	
	/**
	 * Gets the layout.
	 * 
	 * @return the layout
	 */
	public Layout<T> getLayout() {
		return this.layout;
	}
	
	/**
	 * Gets the main table.
	 * 
	 * @return the main table
	 */
	public Table getMainTable() {
		return getLayout().getMainTable();
	}
	
	/**
	 * Gets the query pool.
	 * 
	 * @return the query pool
	 */
	protected QueryPool getQueryPool() {
		return this.queryPool;
	}
	
	/**
	 * Load all.
	 * 
	 * @return the list
	 * @throws DatabaseException
	 *             the database exception
	 */
	public Iterator<T> load() throws DatabaseException {
		return load(this.layout.getMainTable());
	}
	
	/**
	 * Load.
	 * 
	 * @param table
	 *            the table
	 * @return the iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	public Iterator<T> load(final Table table) throws DatabaseException {
		PRECONDITIONS: {
			if (table == null) {
				throw new NullPointerException();
			}
		}
		
		SANITY: {
			assert getConnector() != null;
		}
		
		final ResultSet idSet = getConnector().executeQuery("SELECT " + table.primaryKey() + " FROM " + table + ";");
		
		SANITY: {
			assert idSet != null;
		}
		return loadForIds(idSet);
	}
	
	/**
	 * Load.
	 * 
	 * @param table
	 *            the table
	 * @param criteria
	 *            the criteria
	 * @return the iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	public Iterator<T> load(final Table table,
	                        final Criteria<T> criteria) throws DatabaseException {
		PRECONDITIONS: {
			if (table == null) {
				throw new NullPointerException();
			}
		}
		
		SANITY: {
			assert getConnector() != null;
		}
		
		final CriteriaImpl<T> crit = (CriteriaImpl<T>) criteria;
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT ");
		builder.append(table.primaryKey());
		builder.append(" FROM ");
		builder.append(table);
		
		final StringBuilder eqConditions = new StringBuilder();
		
		for (final Tuple<Column, ?> tuple : crit.eqList) {
			if (eqConditions.length() == 0) {
				eqConditions.append(" WHERE ");
			} else {
				eqConditions.append(" AND ");
			}
			eqConditions.append(tuple.getFirst());
			eqConditions.append(" = ?");
		}
		
		final StringBuilder inConditions = new StringBuilder();
		
		for (final Tuple<Column, Collection<?>> tuple : crit.inList) {
			if ((inConditions.length() == 0) && (eqConditions.length() == 0)) {
				eqConditions.append(" WHERE ");
			} else {
				eqConditions.append(" AND ");
			}
			inConditions.append(tuple.getFirst().name());
			inConditions.append(" in (");
			final StringBuilder qBuilder = new StringBuilder();
			for (@SuppressWarnings ("unused")
			final Object c : tuple.getSecond()) {
				if (qBuilder.length() != 0) {
					qBuilder.append(", ");
				}
				qBuilder.append('?');
			}
			inConditions.append(qBuilder);
			inConditions.append(")");
		}
		
		builder.append(eqConditions);
		builder.append(inConditions);
		
		try {
			final PreparedStatement statement = getConnector().prepare(builder.toString());
			int i = 0;
			for (final Tuple<Column, ?> tuple : crit.eqList) {
				++i;
				tuple.getFirst().type().set(statement, i, tuple.getSecond());
			}
			for (final Tuple<Column, Collection<?>> tuple : crit.inList) {
				for (final Object c : tuple.getSecond()) {
					++i;
					tuple.getFirst().type().set(statement, i, c);
				}
			}
			
			final ResultSet idSet = statement.executeQuery();
			
			SANITY: {
				assert idSet != null;
			}
			return loadForIds(idSet);
		} catch (final SQLException e) {
			throw new DatabaseException();
		}
	}
	
	/**
	 * Load by id.
	 * 
	 * @param id
	 *            the id
	 * @return the t
	 * @throws DatabaseException
	 *             the database exception
	 */
	public abstract T loadById(Object id) throws DatabaseException;
	
	/**
	 * Load for ids.
	 * 
	 * @param idSet
	 *            the id set
	 * @return the iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	protected Iterator<T> loadForIds(final ResultSet idSet) throws DatabaseException {
		final EntityCache cache = getQueryPool().getEntityCache();
		
		return new ResultIterator<T>() {
			
			private final ResultSet ids       = idSet;
			private T               next;
			private boolean         initiated = false;
			
			/**
			 * {@inheritDoc}
			 * 
			 * @see org.mozkito.database.ResultIterator#hasNext()
			 */
			@SuppressWarnings ("unchecked")
			@Override
			public boolean hasNext() {
				if (!this.initiated) {
					try {
						if (this.ids.next()) {
							final Object id = getId(this.ids);
							if (cache.contains(EntityAdapter.this.layout.provides(), id)) {
								this.next = ((T) cache.fetch(EntityAdapter.this.layout.provides(), id));
							} else {
								this.next = loadById(id);
								cache.register(this.next);
							}
						}
						this.initiated = true;
					} catch (SQLException | DatabaseException e) {
						throw new RuntimeException(e);
					}
				}
				return this.next != null;
			}
			
			/**
			 * {@inheritDoc}
			 * 
			 * @see org.mozkito.database.ResultIterator#next()
			 */
			@SuppressWarnings ("unchecked")
			@Override
			public T next() {
				
				try {
					final T current = this.next;
					
					if (this.ids.next()) {
						final Object id = getId(this.ids);
						if (cache.contains(EntityAdapter.this.layout.provides(), id)) {
							this.next = ((T) cache.fetch(EntityAdapter.this.layout.provides(), id));
						} else {
							this.next = loadById(id);
							cache.register(this.next);
						}
					} else {
						this.next = null;
					}
					
					if (!this.initiated) {
						this.initiated = true;
						return next();
					}
					
					return current;
				} catch (SQLException | DatabaseException e) {
					throw new RuntimeException(e);
				}
			}
			
			/**
			 * {@inheritDoc}
			 * 
			 * @see org.mozkito.database.ResultIterator#remove()
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
	}
	
	/**
	 * Rollback.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void rollback() throws DatabaseException {
		this.queryPool.getConnector().rollback();
	}
	
	/**
	 * Save.
	 * 
	 * @param entity
	 *            the entity
	 * @throws DatabaseException
	 *             the database exception
	 */
	public abstract void saveOrUpdate(final T entity) throws DatabaseException;
	
	/**
	 * Verify scheme.
	 * 
	 * @return true, if successful
	 */
	public boolean verifyScheme() {
		throw new RuntimeException("Method 'verifyScheme' has not yet been implemented."); //$NON-NLS-1$
	}
}
