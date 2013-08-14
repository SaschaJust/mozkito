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

import org.apache.commons.collections.CollectionUtils;

import org.mozkito.persons.model.Person;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class EqualUsernameEngine extends Engine {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.research.persons.engines.Engine#confidence(org.mozkito.persons.model.Person,
	 *      org.mozkito.persons.model.Person)
	 */
	@Override
	public Double confidence(final Person p1,
	                         final Person p2) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return CollectionUtils.containsAny(toLowerCase(p1.getUsernames()), toLowerCase(p2.getUsernames()))
			                                                                                                  ? 1.0d
			                                                                                                  : 0.0d;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
