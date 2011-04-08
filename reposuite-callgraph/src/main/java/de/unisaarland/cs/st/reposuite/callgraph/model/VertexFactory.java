package de.unisaarland.cs.st.reposuite.callgraph.model;

import java.util.HashMap;

/**
 * A factory for creating Vertex objects.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class VertexFactory {
	
	protected static HashMap<String, ClassVertex>  classVertices  = new HashMap<String, ClassVertex>();
	protected static HashMap<String, MethodVertex> methodVertices = new HashMap<String, MethodVertex>();
	
	/**
	 * Clear.
	 */
	public static void clear(){
		classVertices.clear();
		methodVertices.clear();
	}
	
	/**
	 * Creates the class vertex based on the method vertex ID.
	 * 
	 * @param v
	 *            the v
	 * @return the class vertex
	 */
	public static ClassVertex createClassVertex(final MethodVertex v) {
		String fullname = v.getId();
		String[] nameParts = fullname.split("\\.");
		StringBuilder ss = new StringBuilder();
		ss.append(nameParts[0]);
		for (int i = 1; i < nameParts.length - 1; ++i) {
			ss.append(".");
			ss.append(nameParts[i]);
		}
		String parentName = ss.toString();
		if (classVertices.containsKey(parentName)) {
			return classVertices.get(parentName);
		}
		ClassVertex cv = new ClassVertex(parentName, v.getFilename());
		classVertices.put(parentName, cv);
		return cv;
		
	}
	
	/**
	 * Creates a method vertex.
	 * 
	 * @param methodName
	 *            the method name
	 * @return the method vertex
	 */
	public static MethodVertex createMethodVertex(final String methodName, final String filename) {
		if (methodVertices.containsKey(methodName)) {
			return methodVertices.get(methodName);
		}
		MethodVertex methodVertex = new MethodVertex(methodName, filename);
		methodVertices.put(methodName, methodVertex);
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
		return classVertices.get(className);
	}
	
	/**
	 * Gets the registered method vertex.
	 * 
	 * @param methodName
	 *            the method name
	 * @return the registered method vertex
	 */
	public static MethodVertex getRegisteredMethodVertex(final String methodName) {
		return methodVertices.get(methodName);
	}
	
}
