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
package de.unisaarland.cs.st.moskito.mapping.elements;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

// TODO: Auto-generated Javadoc
/**
 * This class is used by the {@link PersistenceUtil} only and serves as a composed key in the database.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MapId {
	
	/** The from id. */
	private String fromId;
	
	/** The to id. */
	private String toId;
	
	/*
	 * (non-Javadoc)
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
		final MapId other = (MapId) obj;
		if (getFromId() == null) {
			if (other.getFromId() != null) {
				return false;
			}
		} else if (!getFromId().equals(other.getFromId())) {
			return false;
		}
		if (getToId() == null) {
			if (other.getToId() != null) {
				return false;
			}
		} else if (!getToId().equals(other.getToId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the from id.
	 * 
	 * @return the id of the "from" entity
	 */
	public String getFromId() {
		return this.fromId;
	}
	
	/**
	 * Gets the to id.
	 * 
	 * @return the id of the "to" entity
	 */
	public String getToId() {
		return this.toId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getFromId() == null)
		                                                  ? 0
		                                                  : getFromId().hashCode());
		result = (prime * result) + ((getToId() == null)
		                                                ? 0
		                                                : getToId().hashCode());
		return result;
	}
	
	/**
	 * Sets the id of the "from" entity.
	 * 
	 * @param fromId
	 *            the new from id
	 */
	public void setFromId(final String fromId) {
		this.fromId = fromId;
	}
	
	/**
	 * Sets the id of the "to" entity.
	 * 
	 * @param toId
	 *            the new to id
	 */
	public void setToId(final String toId) {
		this.toId = toId;
	}
	
}
