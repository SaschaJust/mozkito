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
package de.unisaarland.cs.st.moskito.mapping.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class FilteredMapping implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4228229393714955483L;
	private Mapping           mapping;
	private List<String>      filters          = new LinkedList<String>();
	
	/**
	 * used by persistence provider only
	 */
	public FilteredMapping() {
	}
	
	/**
	 * @param score
	 */
	public FilteredMapping(final Mapping mapping, final Collection<? extends MappingFilter> triggeringFilters) {
		for (final MappingFilter filter : triggeringFilters) {
			getFilters().add(filter.getClass().getCanonicalName());
		}
	}
	
	/**
	 * @return the filters
	 */
	@ElementCollection
	public List<String> getFilters() {
		return this.filters;
	}
	
	/**
	 * @return the mapping
	 */
	@Id
	@OneToOne (cascade = {}, fetch = FetchType.EAGER, optional = false)
	public Mapping getMapping() {
		return this.mapping;
	}
	
	/**
	 * @param filters
	 *            the filters to set
	 */
	public void setFilters(final List<String> filters) {
		this.filters = filters;
	}
	
	/**
	 * @param mapping
	 *            the mapping to set
	 */
	public void setMapping(final Mapping mapping) {
		this.mapping = mapping;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FilteredMapping [mapping=");
		builder.append(getMapping());
		builder.append(", filters=");
		builder.append(getFilters());
		builder.append("]");
		return builder.toString();
	}
	
}
