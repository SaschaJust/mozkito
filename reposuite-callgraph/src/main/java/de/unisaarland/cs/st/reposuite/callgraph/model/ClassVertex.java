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
package de.unisaarland.cs.st.reposuite.callgraph.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * The Class ClassVertex.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ClassVertex extends CallGraphVertex implements Serializable {
	
	/**
	 * 
	 */
	private static final long       serialVersionUID = 8442688730560671445L;
	private final Set<MethodVertex> children = new HashSet<MethodVertex>();
	/**
	 * Instantiates a new class vertex.
	 * 
	 * @param id
	 *            the id
	 */
	protected ClassVertex(final String id, final String filename) {
		super(id, filename);
	}
	
	public void addChild(final MethodVertex child) {
		this.children.add(child);
	}
	
	public Set<MethodVertex> getChildren() {
		return this.children;
	}
	
	public String getFullQualifiedName() {
		return this.getId();
	}
	
	@Override
	public String toString() {
		return "ClassVertex [getFullQualifiedName()=" + getFullQualifiedName() + ", getFilename()=" + getFilename()
		+ ", getId()=" + getId() + "]";
	}
}
