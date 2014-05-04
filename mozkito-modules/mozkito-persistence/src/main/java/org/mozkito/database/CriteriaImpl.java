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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;
import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class DatabaseCriteria.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * @param <T>
 *            the generic type
 */
public class CriteriaImpl<T extends Entity> implements Criteria<T> {
	
	/**
	 * The Enum ORDER.
	 */
	public static enum Order {
		/** The ascending. */
		ASCENDING,
		/** The descending. */
		DESCENDING;
		
	}
	
	/** The layout. */
	private final Layout<T>                  layout;
	
	/** The eq list. */
	final List<Tuple<Column, ?>>             eqList = new LinkedList<>();
	
	/** The in list. */
	final List<Tuple<Column, Collection<?>>> inList = new LinkedList<>();
	
	/** The order. */
	final List<Tuple<Order, Column>>         order  = null;
	
	/** The query target type. */
	private Class<T>                         queryTargetType;
	
	/**
	 * Instantiates a new database criteria.
	 * 
	 * @param entityAdapter
	 *            the entity adapter
	 */
	@SuppressWarnings ("unchecked")
	public CriteriaImpl(final EntityAdapter<T> entityAdapter) {
		PRECONDITIONS: {
			if (entityAdapter == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			final Method method = getClass().getMethod("queries", new Class<?>[0]);
			final Class<?> type = method.getReturnType();
			this.queryTargetType = (Class<T>) type;
		} catch (NoSuchMethodException | SecurityException | ClassCastException e) {
			throw new UnrecoverableError(e);
		}
		
		this.layout = entityAdapter.getLayout();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Criteria#eq(java.lang.String, java.lang.Object)
	 */
	@Override
	public Criteria<T> eq(final String column,
	                      final Object value) {
		PRECONDITIONS: {
			if (column == null) {
				throw new NullPointerException();
			}
			if (value == null) {
				throw new NullPointerException();
			}
		}
		
		final Table table = this.layout.getMainTable();
		
		SANITY: {
			assert table != null;
		}
		
		final Column theColumn = table.column(column);
		
		SANITY: {
			assert column != null;
		}
		
		this.eqList.add(new Tuple<>(theColumn, value));
		
		return this;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Criteria#in(java.lang.String, java.util.Collection)
	 */
	@Override
	public Criteria<T> in(final String column,
	                      final Collection<?> values) {
		PRECONDITIONS: {
			if (column == null) {
				throw new NullPointerException();
			}
			if (values == null) {
				throw new NullPointerException();
			}
			if (values.isEmpty()) {
				throw new ArrayIndexOutOfBoundsException();
			}
		}
		
		final Table table = this.layout.getMainTable();
		
		SANITY: {
			assert table != null;
		}
		
		final Column theColumn = table.column(column);
		
		SANITY: {
			assert column != null;
		}
		
		this.inList.add(new Tuple<Column, Collection<?>>(theColumn, values));
		
		return this;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Criteria#oderByAsc(java.lang.String)
	 */
	@Override
	public Criteria<T> oderByAsc(final String column) {
		PRECONDITIONS: {
			if (column == null) {
				throw new NullPointerException();
			}
		}
		
		final Table table = this.layout.getMainTable();
		
		SANITY: {
			assert table != null;
		}
		
		final Column theColumn = table.column(column);
		
		SANITY: {
			assert column != null;
		}
		
		this.order.add(new Tuple<>(Order.ASCENDING, theColumn));
		
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Criteria#oderByDesc(java.lang.String)
	 */
	@Override
	public Criteria<T> oderByDesc(final String column) {
		PRECONDITIONS: {
			if (column == null) {
				throw new NullPointerException();
			}
		}
		
		final Table table = this.layout.getMainTable();
		
		SANITY: {
			assert table != null;
		}
		
		final Column theColumn = table.column(column);
		
		SANITY: {
			assert column != null;
		}
		
		this.order.add(new Tuple<>(Order.DESCENDING, theColumn));
		
		return this;
	}
	
	/**
	 * Queries.
	 * 
	 * @return the class
	 */
	public Class<T> queries() {
		return this.queryTargetType;
	}
	
}
