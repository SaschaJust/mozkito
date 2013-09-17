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
package org.mozkito.mappings.selectors;

import java.util.List;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.register.Node;
import org.mozkito.persistence.PersistenceUtil;

/**
 * Selectors analyze a {@link org.mozkito.persistence.Entity} and find possible candidates that can be mapped to the
 * entity, due to some relation.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class Selector extends Node {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("MappingSelector.description"); //$NON-NLS-1$
	                                                                                            
	/** The Constant TAG. */
	public static final String TAG         = "selectors";                                      //$NON-NLS-1$
	                                                                                            
	/**
	 * Parses the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param entity
	 *            the element under subject
	 * @param targetType
	 *            the target entity type of the candidate
	 * @param util
	 *            the util
	 * @return a list of {@link Candidate}s that might be mapped to the given entity
	 */
	public abstract <T extends org.mozkito.persistence.Entity> List<T> parse(org.mozkito.persistence.Entity entity,
	                                                                         Class<T> targetType,
	                                                                         PersistenceUtil util);
	
	/**
	 * Supports.
	 * 
	 * @param from
	 *            the 'from' entity
	 * @param to
	 *            the 'to' entity
	 * @return true if the selector supports this combination of entities
	 */
	public abstract boolean supports(Class<?> from,
	                                 Class<?> to);
}
