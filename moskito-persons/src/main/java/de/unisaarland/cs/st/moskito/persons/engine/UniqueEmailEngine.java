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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persons.engine;

import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.moskito.persons.elements.PersonBucket;
import de.unisaarland.cs.st.moskito.persons.processing.PersonManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UniqueEmailEngine extends MergingEngine {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persons.engine.MergingEngine#collides(de
	 * .unisaarland.cs.st.moskito.persistence.model.Person,
	 * de.unisaarland.cs.st.moskito.persistence.model.PersonContainer,
	 * de.unisaarland.cs.st.moskito.persons.processing.PersonManager)
	 */
	@Override
	public List<PersonBucket> collides(final Person person,
	                                   final PersonContainer container,
	                                   final PersonManager manager) {
		List<PersonBucket> buckets = manager.getBuckets(person);
		List<PersonBucket> list = new LinkedList<PersonBucket>();
		
		for (PersonBucket bucket : buckets) {
			for (String email : person.getEmailAddresses()) {
				if (bucket.hasEmail(email)) {
					list.add(bucket);
				}
			}
		}
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persons.engine.MergingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Finds collision on unique email addresses";
	}
	
}
