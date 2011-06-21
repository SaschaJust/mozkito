/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
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
