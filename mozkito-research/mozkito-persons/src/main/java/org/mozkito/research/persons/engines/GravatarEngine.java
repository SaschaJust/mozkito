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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.persons.model.Person;
import org.mozkito.research.persons.Gravatar;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class GravatarEngine extends Engine {
	
	/** The existing gravatars. */
	private final Map<String, Gravatar> existingGravatars    = new HashMap<>();
	
	/** The not existing gravatars. */
	private final Set<String>           notExistingGravatars = new HashSet<>();
	
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
			OUTERLOOP: for (final String email1 : p1.getEmailAddresses()) {
				final Gravatar gravatar1 = getGravatar(email1);
				
				if (gravatar1 != null) {
					INNERLOOP: for (final String email2 : p2.getEmailAddresses()) {
						final Gravatar gravatar2 = getGravatar(email2);
						
						if (gravatar2 != null) {
							if (gravatar1.equals(gravatar2)) {
								return 1.0d;
							}
						}
					}
				}
			}
			
			return 0d;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the existingGravatars
	 */
	public final Map<String, Gravatar> getExistingGravatars() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.existingGravatars;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.existingGravatars,
				                  "Field '%s' in '%s'.", "existingGravatars", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the gravatar.
	 * 
	 * @param email
	 *            the email
	 * @return the gravatar
	 */
	private Gravatar getGravatar(final String email) {
		if (this.existingGravatars.containsKey(email)) {
			return this.existingGravatars.get(email);
		} else if (this.notExistingGravatars.contains(email)) {
			return null;
		} else {
			final Gravatar gravatar = Gravatar.get(email);
			if (gravatar != null) {
				this.existingGravatars.put(email, gravatar);
			} else {
				this.notExistingGravatars.add(email);
			}
			
			return gravatar;
		}
		
	}
	
	/**
	 * @return the notExistingGravatars
	 */
	public final Set<String> getNotExistingGravatars() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.notExistingGravatars;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.notExistingGravatars,
				                  "Field '%s' in '%s'.", "notExistingGravatars", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
}
