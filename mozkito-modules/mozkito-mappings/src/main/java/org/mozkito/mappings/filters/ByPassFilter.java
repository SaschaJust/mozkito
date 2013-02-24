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

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.requirements.ByPass;
import org.mozkito.mappings.requirements.Expression;

/**
 * The Class ByPassFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ByPassFilter extends Filter {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("ByPassFilter.description"); //$NON-NLS-1$
	/** The Constant TAG. */
	public static final String TAG         = "byPass";                                      //$NON-NLS-1$
	                                                                                         
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.filters.Filter#filter(org.mozkito.mappings.model.Mapping)
	 */
	@Override
	public Mapping filter(final Mapping mapping) {
		// PRECONDITIONS
		
		try {
			return mapping.addFilter(this, false);
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
		return ByPassFilter.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.filters.MappingFilter#supported()
	 */
	@Override
	public Expression supported() {
		// PRECONDITIONS
		
		try {
			return new ByPass();
		} finally {
			// POSTCONDITIONS
		}
	}
}
