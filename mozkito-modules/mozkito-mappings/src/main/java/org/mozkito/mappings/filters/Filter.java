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

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.register.Node;
import org.mozkito.mappings.requirements.Expression;

/**
 * The Class MappingFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Filter extends Node {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("Filter.description"); //$NON-NLS-1$
	/** The Constant TAG. */
	public static final String TAG         = "filters";                               //$NON-NLS-1$
	                                                                                   
	/**
	 * Instantiates a new mapping filter.
	 */
	public Filter() {
		
	}
	
	/**
	 * Filter.
	 * 
	 * @param mapping
	 *            the mapping
	 * @return the mapping
	 */
	@NoneNull
	public abstract Mapping filter(final Mapping mapping);
	
	/**
	 * Supported.
	 * 
	 * @return the expression
	 */
	public abstract Expression supported();
	
}
