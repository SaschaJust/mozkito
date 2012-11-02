/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just - mozkito.org
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
package org.mozkito.mappings.selectors;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class ClassifiedReportSelector extends Selector {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Override
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
	 * @see org.mozkito.mappings.selectors.Selector#parse(org.mozkito.mappings.mappable.model.MappableEntity,
	 * java.lang.Class, org.mozkito.persistence.PersistenceUtil)
	 */
	@Override
	public <T extends MappableEntity> List<T> parse(final MappableEntity entity,
	                                                final Class<T> targetType,
	                                                final PersistenceUtil util) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.Selector#supports(java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}
