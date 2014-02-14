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

package org.mozkito.database;

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class TestPerson extends DBEntity {
	
	private Set<String> emails;
	private Set<String> usernames;
	private Set<String> fullnames;
	private Integer     id;
	
	/**
	 * @return the emails
	 */
	public Set<String> getEmails() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.emails;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the fullnames
	 */
	public Set<String> getFullnames() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.fullnames;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the usernames
	 */
	public Set<String> getUsernames() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.usernames;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param emails
	 *            the emails to set
	 */
	public void setEmails(final Set<String> emails) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.emails = emails;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param fullnames
	 *            the fullnames to set
	 */
	public void setFullnames(final Set<String> fullnames) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.fullnames = fullnames;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final Integer id) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.id = id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param usernames
	 *            the usernames to set
	 */
	public void setUsernames(final Set<String> usernames) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.usernames = usernames;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
