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
/**
 * 
 */
package org.mozkito.persons.engine;

import java.util.List;

import net.ownhero.dev.ioda.JavaUtils;

import org.mozkito.persons.elements.PersonBucket;
import org.mozkito.persons.model.Person;
import org.mozkito.persons.model.PersonContainer;
import org.mozkito.persons.processing.PersonManager;

/**
 * The Class MergingEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class MergingEngine {
	
	/**
	 * The Class Options.
	 */
	
	/**
	 * Instantiates a new merging engine.
	 */
	public MergingEngine() {
		
	}
	
	/**
	 * Collides.
	 * 
	 * @param person
	 *            the person
	 * @param container
	 *            the container
	 * @param manager
	 *            the manager
	 * @return the list
	 */
	public abstract List<PersonBucket> collides(Person person,
	                                            PersonContainer container,
	                                            PersonManager manager);
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(GravatarEngine.class);
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public abstract String getDescription();
	
}
