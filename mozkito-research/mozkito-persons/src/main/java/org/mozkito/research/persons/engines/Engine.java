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

package org.mozkito.research.persons.engines;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import org.mozkito.persons.model.Person;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class Engine {
	
	/**
	 * Confidence.
	 * 
	 * @param p1
	 *            the first person
	 * @param p2
	 *            the second person
	 * @return the confidence value representing identity of the persons
	 */
	public abstract Double confidence(Person p1,
	                                  Person p2);
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return getClass().getSimpleName().toUpperCase().replace("ENGINE", "");
	}
	
	/**
	 * Gets the prefixes.
	 * 
	 * @param emailAddresses
	 *            the email addresses
	 * @return the prefixes
	 */
	@SuppressWarnings ("unchecked")
	public Collection<String> getPrefixes(final Set<String> emailAddresses) {
		return CollectionUtils.collect(emailAddresses, new Transformer() {
			
			/**
			 * {@inheritDoc}
			 * 
			 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
			 */
			@Override
			public String transform(final Object input) {
				PRECONDITIONS: {
					// none
					assert input != null;
				}
				
				try {
					final String emailAddress = (String) input;
					
					final String[] split = emailAddress.split("@");
					
					SANITY: {
						assert split.length > 1;
					}
					
					return split[0].toLowerCase();
				} finally {
					POSTCONDITIONS: {
						// none
					}
				}
			}
		});
	}
	
	/**
	 * To lower case.
	 * 
	 * @param strings
	 *            the strings
	 * @return the collection
	 */
	@SuppressWarnings ("unchecked")
	public Collection<String> toLowerCase(final Collection<String> strings) {
		return CollectionUtils.collect(strings, new Transformer() {
			
			@Override
			public String transform(final Object input) {
				PRECONDITIONS: {
					// none
				}
				
				try {
					return ((String) input).toLowerCase();
				} finally {
					POSTCONDITIONS: {
						// none
					}
				}
			}
		});
	}
	
}
