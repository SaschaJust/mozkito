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
package de.unisaarland.cs.st.moskito.persons.engine;

import java.util.List;

import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.moskito.persons.elements.PersonBucket;
import de.unisaarland.cs.st.moskito.persons.processing.PersonManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MergingEngine implements net.ownhero.dev.hiari.settings.registerable.ArgumentProvider {
	
	public MergingEngine() {
		
	}
	
	/**
	 * @param person
	 * @param container
	 * @param manager
	 * @param features
	 * @return
	 */
	public abstract List<PersonBucket> collides(Person person,
	                                            PersonContainer container,
	                                            PersonManager manager);
	
	/**
	 * @return
	 */
	public abstract String getDescription();
	
}
