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

/**
 * This class is used by the {@link PersistenceUtil} only and serves as a composed key in the database.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MapId {
	
	private String fromId;
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
		MapId other = (MapId) obj;
		if (this.getFromId() == null) {
			if (other.getFromId() != null) {
				return false;
			}
		} else if (!this.getFromId().equals(other.getFromId())) {
			return false;
		}
		if (this.getToId() == null) {
			if (other.getToId() != null) {
				return false;
			}
		} else if (!this.getToId().equals(other.getToId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the id of the "from" entity
	 */
	public String getFromId() {
		return this.fromId;
	}
	
	/**
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
		result = (prime * result) + ((this.getFromId() == null)
		                                                       ? 0
		                                                       : this.getFromId().hashCode());
		result = (prime * result) + ((this.getToId() == null)
		                                                     ? 0
		                                                     : this.getToId().hashCode());
		return result;
	}
	
	/**
	 * Sets the id of the "from" entity
	 * 
	 * @param fromId
	 */
	public void setFromId(final String fromId) {
		this.fromId = fromId;
	}
	
	/**
	 * Sets the id of the "to" entity
	 * 
	 * @param toId
	 */
	public void setToId(final String toId) {
		this.toId = toId;
	}
	
}
