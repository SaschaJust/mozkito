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

package org.mozkito.mappings.mappable.model;

import java.util.Set;

import javax.persistence.Transient;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.mappings.mappable.FieldKey;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class MappableStructuredReport extends MappableEntity {
	
	EnhancedReport            report;
	
	/**
     * 
     */
	private static final long serialVersionUID = -4080743755314740530L;
	
	/**
	 * Instantiates a new mappable structured report.
	 * 
	 * @param report
	 *            the report
	 */
	public MappableStructuredReport(final EnhancedReport report) {
		this.report = report;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#get(org.mozkito.mappings.mappable.FieldKey)
	 */
	@Override
	@Transient
	public Object get(final FieldKey key) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'get' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#get(org.mozkito.mappings.mappable.FieldKey, int)
	 */
	@Override
	@Transient
	public Object get(final FieldKey key,
	                  final int index) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'get' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#getBaseType()
	 */
	@Override
	@Transient
	public Class<?> getBaseType() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getBaseType' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#getId()
	 */
	@Override
	@Transient
	public String getId() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getId' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	public final EnhancedReport getReport() {
		// PRECONDITIONS
		
		try {
			return this.report;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.report, "Field '%s' in '%s'.", "report", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#getText()
	 */
	@Override
	@Transient
	public String getText() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getText' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#supported()
	 */
	@Override
	@Transient
	public Set<FieldKey> supported() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'supported' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
}
