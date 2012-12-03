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

import java.util.HashMap;

/**
 * A factory for creating Vertex objects.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class VertexFactory {
	
	/** The class vertices. */
	protected static HashMap<String, ClassVertex>  classVertices  = new HashMap<String, ClassVertex>();
	
	/** The method vertices. */
	protected static HashMap<String, MethodVertex> methodVertices = new HashMap<String, MethodVertex>();
	
	/**
	 * Clear.
	 */
	public static void clear() {
		VertexFactory.classVertices.clear();
		VertexFactory.methodVertices.clear();
	}
	
	/**
	 * Creates the class vertex based on the method vertex ID.
	 * 
	 * @param v
	 *            the v
	 * @return the class vertex
	 */
	public static ClassVertex createClassVertex(final MethodVertex v) {
		final String fullname = v.getId();
		
		final String parentName = fullname.substring(0, fullname.lastIndexOf("."));
		if (VertexFactory.classVertices.containsKey(parentName)) {
			return VertexFactory.classVertices.get(parentName);
		}
		final ClassVertex cv = new ClassVertex(parentName, v.getFilename());
		VertexFactory.classVertices.put(parentName, cv);
		return cv;
		
	}
	
	/**
	 * Creates a method vertex.
	 * 
	 * @param methodName
	 *            the method name
	 * @param filename
	 *            the filename
	 * @return the method vertex
	 */
	public static MethodVertex createMethodVertex(final String methodName,
	                                              final String filename) {
		if (VertexFactory.methodVertices.containsKey(methodName)) {
			return VertexFactory.methodVertices.get(methodName);
		}
		final MethodVertex methodVertex = new MethodVertex(methodName, filename);
		VertexFactory.methodVertices.put(methodName, methodVertex);
		return methodVertex;
	}
	
	/**
	 * Gets the registered class vertex.
	 * 
	 * @param className
	 *            the class name
	 * @return the registered class vertex
	 */
	public static ClassVertex getRegisteredClassVertex(final String className) {
		return VertexFactory.classVertices.get(className);
	}
	
	/**
	 * Gets the registered method vertex.
	 * 
	 * @param methodName
	 *            the method name
	 * @return the registered method vertex
	 */
	public static MethodVertex getRegisteredMethodVertex(final String methodName) {
		return VertexFactory.methodVertices.get(methodName);
	}
	
}
