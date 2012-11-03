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
package org.mozkito.mappings.model;

import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.selectors.Selector;

/**
 * The Interface ICandidate.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface ICandidate {
	
	/**
	 * Adds the selector.
	 * 
	 * @param selector
	 *            the selector
	 * @return true, if successful
	 */
	boolean addSelector(final Selector selector);
	
	/**
	 * Gets the class1.
	 * 
	 * @return the class1
	 */
	String getClass1();
	
	/**
	 * Gets the class2.
	 * 
	 * @return the class2
	 */
	String getClass2();
	
	/**
	 * Gets the from.
	 * 
	 * @return the from entity
	 */
	MappableEntity getFrom();
	
	/**
	 * Gets the to.
	 * 
	 * @return the to entity
	 */
	MappableEntity getTo();
	
}
