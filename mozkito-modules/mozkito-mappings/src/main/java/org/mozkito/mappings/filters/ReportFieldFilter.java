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
package org.mozkito.mappings.filters;

import org.mozkito.issues.elements.Type;
import org.mozkito.issues.model.EnhancedReport;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;

/**
 * The Class ReportFieldFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportFieldFilter extends Filter {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("ReportFieldFilter.optionSetDescription"); //$NON-NLS-1$
	                                                                                                       
	/**
	 * Gets the default type.
	 * 
	 * @return the default type
	 */
	public static final Type getDefaultType() {
		return Type.BUG;
	}
	
	/** The type. */
	private final Type type;
	
	/**
	 * Instantiates a new report field filter.
	 * 
	 * @param type
	 *            the type
	 */
	public ReportFieldFilter(final Type type) {
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.filters.Filter#filter(org.mozkito.mappings.model.Mapping)
	 */
	@Override
	public Mapping filter(final Mapping mapping) {
		// PRECONDITIONS
		
		try {
			if (!mapping.getComposite().getTo().get(FieldKey.TYPE).equals(this.type)) {
				mapping.addFilter(this, true);
			} else {
				mapping.addFilter(this, false);
			}
			
			return mapping;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.filters.MappingFilter#getDescription ()
	 */
	@Override
	public String getDescription() {
		return ReportFieldFilter.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.filters.MappingFilter#supported()
	 */
	@Override
	public Expression supported() {
		return new Atom(Index.FROM, EnhancedReport.class);
	}
	
}
