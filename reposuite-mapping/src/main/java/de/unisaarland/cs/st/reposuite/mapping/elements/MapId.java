/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.elements;

public class MapId {
	
	private String fromId;
	private String toId;
	
	/**
	 * @return
	 */
	public String getFromId() {
		return fromId;
	}
	
	/**
	 * @param fromId
	 */
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	
	/**
	 * @return
	 */
	public String getToId() {
		return toId;
	}
	
	/**
	 * @param toId
	 */
	public void setToId(String toId) {
		this.toId = toId;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromId == null) ? 0 : fromId.hashCode());
		result = prime * result + ((toId == null) ? 0 : toId.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MapId other = (MapId) obj;
		if (fromId == null) {
			if (other.fromId != null) return false;
		} else if (!fromId.equals(other.fromId)) return false;
		if (toId == null) {
			if (other.toId != null) return false;
		} else if (!toId.equals(other.toId)) return false;
		return true;
	}
	
}
