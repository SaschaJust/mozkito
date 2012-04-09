/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.filters;

import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.mapping.model.IMapping;
import de.unisaarland.cs.st.moskito.mapping.register.Node;

/**
 * The Class MappingFilter.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class MappingFilter extends Node {
	
	/**
	 * Instantiates a new mapping filter.
	 */
	public MappingFilter() {
		
	}
	
	/**
	 * Filter.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param triggeringFilters
	 *            the triggering filters
	 * @return the set<? extends mapping filter>
	 */
	@NoneNull
	public abstract Set<? extends MappingFilter> filter(final IMapping mapping,
	                                                    Set<? extends MappingFilter> triggeringFilters);
	
}
