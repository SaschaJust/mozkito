/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.mappings.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.filters.Filter;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.persistence.Annotated;

/**
 * The Class Mapping.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Mapping implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long          serialVersionUID = -2154624198669450359L;
	
	/** The composite. */
	private Composite                  composite;
	
	/** The filters. */
	private final Map<String, Boolean> filters          = new HashMap<>();
	
	/**
	 * Instantiates a new mapping.
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
		Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.composite = composite;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.composite, "Field '%s' in '%s'.", "this.composite", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Adds the filter.
	 * 
	 * @param filter
	 *            the filter
	 */
	@Transient
	public Mapping addFilter(final Filter filter,
	                         final boolean value) {
		// PRECONDITIONS
		
		try {
			getFilters().put(filter.getHandle(), value);
			return this;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.model.IMapping#getClass1()
	 */
	@Transient
	public String getClass1() {
		// PRECONDITIONS
		
		try {
			return getComposite().getClass1();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.model.IMapping#getClass2()
	 */
	@Transient
	public String getClass2() {
		// PRECONDITIONS
		
		try {
			return getComposite().getClass2();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the composite.
	 * 
	 * @return the composite
	 */
	public final Composite getComposite() {
		// PRECONDITIONS
		
		try {
			return this.composite;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.composite, "Field '%s' in '%s'.", "composite", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @return the filters
	 */
	public final Map<String, Boolean> getFilters() {
		// PRECONDITIONS
		
		try {
			return this.filters;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.model.IMapping#getFrom()
	 */
	@Transient
	public MappableEntity getFrom() {
		// PRECONDITIONS
		
		try {
			return getComposite().getFrom();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Transient
	public final String getHandle() {
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.model.IMapping#getTo()
	 */
	@Transient
	public MappableEntity getTo() {
		// PRECONDITIONS
		
		try {
			return getComposite().getTo();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param composite
	 *            the composite to set
	 */
	public final void setComposite(final Composite composite) {
		// PRECONDITIONS
		Condition.notNull(composite, "Argument '%s' in '%s'.", "composite", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.composite = composite;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.composite, composite,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
}
