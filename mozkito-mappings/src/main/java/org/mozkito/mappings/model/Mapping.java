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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.mozkito.mappings.filters.Filter;
import org.mozkito.persistence.Annotated;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class Mapping.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Mapping implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2154624198669450359L;
	
	/** The filters. */
	private final Set<Filter> filters          = new HashSet<>();
	
	/** The composite. */
	private IComposite        composite;
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @param composite
	 *            the composite
	 * @param triggeringFilters
	 *            the triggering filters
	 */
	public Mapping(@NotNull final IComposite composite, @NotNull final Set<Filter> triggeringFilters) {
		// PRECONDITIONS
		Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.composite = composite;
			this.filters.clear();
			this.filters.addAll(triggeringFilters);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.composite, "Field '%s' in '%s'.", "this.composite", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			CollectionCondition.notEmpty(this.filters, "Field '%s' in '%s'.", "filters", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @return the composite
	 */
	public final IComposite getComposite() {
		// PRECONDITIONS
		
		try {
			return this.composite;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.composite, "Field '%s' in '%s'.", "composite", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the filters
	 */
	public final Set<Filter> getFilters() {
		// PRECONDITIONS
		
		try {
			return this.filters;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.filters, "Field '%s' in '%s'.", "filters", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
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
}
