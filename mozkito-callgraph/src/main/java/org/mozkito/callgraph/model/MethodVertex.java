/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mozkito.callgraph.model;

import java.io.Serializable;

/**
 * The Class MethodVertex.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class MethodVertex extends CallGraphVertex implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4013481775627394171L;
	
	/** The method name. */
	private final String      methodName;
	
	/**
	 * Instantiates a new method vertex.
	 * 
	 * @param id
	 *            the id
	 * @param filename
	 *            the filename
	 */
	protected MethodVertex(final String id, final String filename) {
		super(id, filename);
		this.parent = VertexFactory.createClassVertex(this);
		this.parent.addChild(this);
		final int index = id.lastIndexOf(".");
		this.methodName = id.substring(index + 1);
	}
	
	/**
	 * Gets the full qualified method name.
	 * 
	 * @return the full qualified method name
	 */
	public String getFullQualifiedMethodName() {
		return this.parent.getFullQualifiedName() + "." + getMethodName();
	}
	
	/**
	 * Gets the method name.
	 * 
	 * @return the method name
	 */
	public String getMethodName() {
		return this.methodName;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	@Override
	public ClassVertex getParent() {
		return this.parent;
	}
}
