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
package de.unisaarland.cs.st.moskito.callgraph.model;

import java.io.Serializable;

/**
 * The Class MinerCallGraphVertex.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public abstract class CallGraphVertex implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4937665383201832091L;
	private final String id;
	private final String  filename;
	protected ClassVertex parent;
	
	/**
	 * Instantiates a new miner call graph vertex.
	 * 
	 * @param id
	 *            the id
	 */
	public CallGraphVertex(final String id, final String filename) {
		this.id = id;
		this.filename = filename;
	}
	
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
		CallGraphVertex other = (CallGraphVertex) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	protected String getId() {
		return id;
	}
	
	public ClassVertex getParent() {
		return parent;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	
	@Override
	public String toString() {
		return "CallGraphVertex [id=" + id + "]";
	}
}