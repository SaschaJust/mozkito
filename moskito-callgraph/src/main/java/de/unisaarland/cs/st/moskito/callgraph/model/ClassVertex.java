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
package de.unisaarland.cs.st.moskito.callgraph.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class ClassVertex.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ClassVertex extends CallGraphVertex implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long       serialVersionUID = 8442688730560671445L;
	
	/** The children. */
	private final Set<MethodVertex> children         = new HashSet<MethodVertex>();
	
	/**
	 * Instantiates a new class vertex.
	 *
	 * @param id the id
	 * @param filename the filename
	 */
	protected ClassVertex(final String id, final String filename) {
		super(id, filename);
	}
	
	/**
	 * Adds the child.
	 *
	 * @param child the child
	 */
	public void addChild(final MethodVertex child) {
		this.children.add(child);
	}
	
	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public Set<MethodVertex> getChildren() {
		return this.children;
	}
	
	/**
	 * Gets the full qualified name.
	 *
	 * @return the full qualified name
	 */
	public String getFullQualifiedName() {
		return this.getId();
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.callgraph.model.CallGraphVertex#toString()
	 */
	@Override
	public String toString() {
		return "ClassVertex [getFullQualifiedName()=" + getFullQualifiedName() + ", getFilename()=" + getFilename()
		        + ", getId()=" + getId() + "]";
	}
}
