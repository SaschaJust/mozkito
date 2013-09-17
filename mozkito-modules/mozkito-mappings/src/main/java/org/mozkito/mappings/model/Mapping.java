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
package org.mozkito.mappings.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.elements.RelationType;
import org.mozkito.mappings.filters.Filter;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class Mapping.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class Mapping implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long    serialVersionUID = -2154624198669450359L;
	
	/** The composite. */
	private Composite            composite;
	
	/** The filters. */
	private Map<String, Boolean> filters          = new HashMap<>();
	
	/** The relation types. */
	private Set<RelationType>    relationTypes    = new HashSet<>();
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @deprecated default constructor should only be called by the active {@link PersistenceUtil}
	 */
	@Deprecated
	public Mapping() {
		// for OpenJPA only.
	}
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @param composite
	 *            the composite
	 */
	public Mapping(@NotNull final Composite composite) {
		// PRECONDITIONS
		Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.composite = composite;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.composite, "Field '%s' in '%s'.", "this.composite", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Adds the filter.
	 * 
	 * @param filter
	 *            the filter
	 * @param value
	 *            the value
	 * @return the mapping
	 */
	@Transient
	public Mapping addFilter(final Filter filter,
	                         final boolean value) {
		// PRECONDITIONS
		
		try {
			getFilters().put(filter.getClassName(), value);
			return this;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Mapping other = (Mapping) obj;
		if (getComposite() == null) {
			if (other.getComposite() != null) {
				return false;
			}
		} else if (!getComposite().equals(other.getComposite())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Override
	@Transient
	public final String getClassName() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/**
	 * Gets the composite.
	 * 
	 * @return the composite
	 */
	@Id
	@ManyToOne (fetch = FetchType.EAGER, cascade = {})
	public Composite getComposite() {
		return this.composite;
	}
	
	/**
	 * Gets the filters.
	 * 
	 * @return the filters
	 */
	@ElementCollection
	public Map<String, Boolean> getFilters() {
		return this.filters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.model.IMapping#getFrom()
	 */
	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	@Transient
	public org.mozkito.persistence.Entity getFrom() {
		// PRECONDITIONS
		
		try {
			return getComposite().getFrom();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @return the relationTypes
	 */
	@ElementCollection (fetch = FetchType.EAGER)
	public final Set<RelationType> getRelationTypes() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.relationTypes;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.relationTypes,
				                  "Field '%s' in '%s'.", "relationTypes", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	@Transient
	public org.mozkito.persistence.Entity getTo() {
		// PRECONDITIONS
		
		try {
			return getComposite().getTo();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getComposite() == null)
		                                                     ? 0
		                                                     : getComposite().hashCode());
		return result;
	}
	
	/**
	 * Sets the composite.
	 * 
	 * @param composite
	 *            the composite to set
	 */
	public void setComposite(final Composite composite) {
		
		this.composite = composite;
		
	}
	
	/**
	 * Sets the filters.
	 * 
	 * @param filters
	 *            the filters to set
	 */
	public void setFilters(final Map<String, Boolean> filters) {
		
		this.filters = filters;
		
	}
	
	/**
	 * @param relationTypes
	 *            the relationTypes to set
	 */
	public final void setRelationTypes(final Set<RelationType> relationTypes) {
		PRECONDITIONS: {
			// none, we even allow null here, since this is a JPA setter.
		}
		
		try {
			this.relationTypes = relationTypes;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getClassName());
		builder.append(" [composite="); //$NON-NLS-1$
		builder.append(getComposite());
		builder.append(", filters="); //$NON-NLS-1$
		builder.append(JavaUtils.mapToString(getFilters()));
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
}
