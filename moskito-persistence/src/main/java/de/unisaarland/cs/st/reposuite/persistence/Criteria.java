/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Criteria<T> {
	
	private Root<T>          root;
	private CriteriaBuilder  builder;
	private CriteriaQuery<T> query;
	
	/**
	 * @param root
	 * @param builder
	 * @param query
	 */
	public Criteria(final Root<T> root, final CriteriaBuilder builder, final CriteriaQuery<T> query) {
		this.root = root;
		this.builder = builder;
		this.query = query;
	}
	
	/**
	 * @param column
	 * @param value
	 * @return
	 */
	public Criteria<T> eq(final String column,
	                      final Object value) {
		this.query.where(this.builder.equal(this.root.get(column), value));
		return this;
	}
	
	/**
	 * @return the builder
	 */
	public CriteriaBuilder getBuilder() {
		return this.builder;
	}
	
	/**
	 * @return the query
	 */
	public CriteriaQuery<T> getQuery() {
		return this.query;
	}
	
	/**
	 * @return the root
	 */
	public Root<T> getRoot() {
		return this.root;
	}
	
	public Criteria<T> in(final String column,
	                      final Collection<?> values) {
		this.query.where(this.root.get(column).in(values));
		return this;
	}
	
	/**
	 * @param builder
	 *            the builder to set
	 */
	public void setBuilder(final CriteriaBuilder builder) {
		this.builder = builder;
	}
	
	/**
	 * @param query
	 *            the query to set
	 */
	public void setQuery(final CriteriaQuery<T> query) {
		this.query = query;
	}
	
	/**
	 * @param root
	 *            the root to set
	 */
	public void setRoot(final Root<T> root) {
		this.root = root;
	}
	
}
