/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.persistence;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * The Class Criteria.
 * 
 * @param <T>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Criteria<T> {
	
	/** The root. */
	private Root<T>          root;
	
	/** The builder. */
	private CriteriaBuilder  builder;
	
	/** The query. */
	private CriteriaQuery<T> query;
	
	/**
	 * Instantiates a new criteria.
	 * 
	 * @param root
	 *            the root
	 * @param builder
	 *            the builder
	 * @param query
	 *            the query
	 */
	public Criteria(final Root<T> root, final CriteriaBuilder builder, final CriteriaQuery<T> query) {
		this.root = root;
		this.builder = builder;
		this.query = query;
	}
	
	/**
	 * Eq.
	 * 
	 * @param column
	 *            the column
	 * @param value
	 *            the value
	 * @return the criteria
	 */
	public Criteria<T> eq(final String column,
	                      final Object value) {
		this.query.where(this.builder.equal(this.root.get(column), value));
		return this;
	}
	
	/**
	 * Gets the builder.
	 * 
	 * @return the builder
	 */
	public CriteriaBuilder getBuilder() {
		return this.builder;
	}
	
	/**
	 * Gets the query.
	 * 
	 * @return the query
	 */
	public CriteriaQuery<T> getQuery() {
		return this.query;
	}
	
	/**
	 * Gets the root.
	 * 
	 * @return the root
	 */
	public Root<T> getRoot() {
		return this.root;
	}
	
	/**
	 * In.
	 * 
	 * @param column
	 *            the column
	 * @param values
	 *            the values
	 * @return the criteria
	 */
	public Criteria<T> in(final String column,
	                      final Collection<?> values) {
		this.query.where(this.root.get(column).in(values));
		return this;
	}
	
	/**
	 * Oder by asc.
	 * 
	 * @param column
	 *            the column
	 * @return the criteria
	 */
	public Criteria<T> oderByAsc(final String column) {
		this.query.orderBy(this.builder.asc(this.root.get(column)));
		return this;
	}
	
	/**
	 * Oder by desc.
	 * 
	 * @param column
	 *            the column
	 * @return the criteria
	 */
	public Criteria<T> oderByDesc(final String column) {
		this.query.orderBy(this.builder.desc(this.root.get(column)));
		return this;
	}
	
	/**
	 * Sets the builder.
	 * 
	 * @param builder
	 *            the builder to set
	 */
	public void setBuilder(final CriteriaBuilder builder) {
		this.builder = builder;
	}
	
	/**
	 * Sets the query.
	 * 
	 * @param query
	 *            the query to set
	 */
	public void setQuery(final CriteriaQuery<T> query) {
		this.query = query;
	}
	
	/**
	 * Sets the root.
	 * 
	 * @param root
	 *            the root to set
	 */
	public void setRoot(final Root<T> root) {
		this.root = root;
	}
	
}
