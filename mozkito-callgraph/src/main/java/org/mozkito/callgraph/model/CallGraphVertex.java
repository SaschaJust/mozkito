/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.mozkito.callgraph.model;

import java.io.Serializable;

/**
 * The Class MinerCallGraphVertex.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public abstract class CallGraphVertex implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4937665383201832091L;
	
	/** The id. */
	private final String      id;
	
	/** The filename. */
	private final String      filename;
	
	/** The parent. */
	protected ClassVertex     parent;
	
	/**
	 * Instantiates a new miner call graph vertex.
	 *
	 * @param id the id
	 * @param filename the filename
	 */
	public CallGraphVertex(final String id, final String filename) {
		this.id = id;
		this.filename = filename;
	}
	
	/* (non-Javadoc)
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
		CallGraphVertex other = (CallGraphVertex) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the filename.
	 *
	 * @return the filename
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	protected String getId() {
		return this.id;
	}
	
	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public ClassVertex getParent() {
		return this.parent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null)
		                                       ? 0
		                                       : this.id.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CallGraphVertex [id=" + this.id + "]";
	}
}
