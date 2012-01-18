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
package de.unisaarland.cs.st.moskito.mapping.filters;

import java.util.Set;

import de.unisaarland.cs.st.moskito.mapping.model.Mapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportFieldFilter extends MappingFilter {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#filter(de
	 * .unisaarland.cs.st.reposuite.mapping.model.PersistentMapping,
	 * java.util.Set)
	 */
	@Override
	public Set<? extends MappingFilter> filter(final Mapping mapping,
	                                           final Set<? extends MappingFilter> triggeringFilters) {
		// TODO Auto-generated method stub
		return triggeringFilters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Requires certain field values on reports to be mapped";
	}
	
}
