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
package org.mozkito.mappings.elements;

import org.mozkito.mappings.model.Candidate;

/**
 * The Class CandidateId.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CandidateId {
	
	/** The from. */
	int            id;
	
	/** The to. */
	Candidate.Type relationType;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CandidateId other = (CandidateId) obj;
		if (this.id != other.id) {
			return false;
		}
		if (this.relationType != other.relationType) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
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
	 * Gets the relation type.
	 * 
	 * @return the relationType
	 */
	public Candidate.Type getRelationType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.relationType;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.id;
		result = (prime * result) + ((this.relationType == null)
		                                                        ? 0
		                                                        : this.relationType.hashCode());
		return result;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
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
	 * Sets the relation type.
	 * 
	 * @param relationType
	 *            the relationType to set
	 */
	public void setRelationType(final Candidate.Type relationType) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.relationType = relationType;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
