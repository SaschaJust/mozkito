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

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

/**
 * The Class TestChangeSet.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TestChangeSet extends DBEntity {
	
	/** The author. */
	private TestPerson   author;
	
	/** The timestamp. */
	private DateTime     timestamp;
	
	/** The message. */
	private String       message;
	
	/** The id. */
	private String       id;
	
	/** The parents. */
	private List<String> parents;
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	@ManyToOne
	public TestPerson getAuthor() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.author;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	public String getId() {
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
	 * Gets the message.
	 * 
	 * @return the message
	 */
	@Basic
	public String getMessage() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.message;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the parents.
	 * 
	 * @return the parents
	 */
	@ElementCollection
	public List<String> getParents() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.parents;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	public DateTime getTimestamp() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.timestamp;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(final TestPerson author) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.author = author;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final String id) {
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
	 * Sets the message.
	 * 
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.message = message;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the parents.
	 * 
	 * @param parents
	 *            the parents to set
	 */
	public void setParents(final List<String> parents) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.parents = parents;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.timestamp = timestamp;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
