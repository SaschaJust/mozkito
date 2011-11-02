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
package de.unisaarland.cs.st.moskito.ppa.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;

/**
 * The Class JavaElementLocations.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JavaElementLocations {
	
	/** The method defs. */
	Map<String, TreeSet<JavaElementLocation>> methodDefs  = new HashMap<String, TreeSet<JavaElementLocation>>();
	
	/** The class defs. */
	Map<String, TreeSet<JavaElementLocation>> classDefs   = new HashMap<String, TreeSet<JavaElementLocation>>();
	
	/** The method calls. */
	Map<String, TreeSet<JavaElementLocation>> methodCalls = new HashMap<String, TreeSet<JavaElementLocation>>();
	
	/**
	 * Instantiates a new java element locations.
	 */
	public JavaElementLocations() {
		
	}
	
	/**
	 * Adds the all class defs.
	 * 
	 * @param classDef
	 *            the class def
	 * @return true, if successful
	 */
	public boolean addAllClassDefs(final Collection<JavaElementLocation> classDef) {
		boolean result = true;
		for (JavaElementLocation e : classDef) {
			if (!classDefs.containsKey(e.getFilePath())) {
				classDefs.put(e.getFilePath(), new TreeSet<JavaElementLocation>());
			}
			result &= classDefs.get(e.getFilePath()).add(e);
		}
		return result;
	}
	
	/**
	 * Adds the all method calls.
	 * 
	 * @param methodCall
	 *            the method call
	 * @return true, if successful
	 */
	public boolean addAllMethodCalls(final Collection<JavaElementLocation> methodCall) {
		boolean result = true;
		for (JavaElementLocation e : methodCall) {
			if (!methodCalls.containsKey(e.getFilePath())) {
				methodCalls.put(e.getFilePath(), new TreeSet<JavaElementLocation>());
			}
			result &= methodCalls.get(e.getFilePath()).add(e);
		}
		return result;
	}
	
	/**
	 * Adds the all method defs.
	 * 
	 * @param methodDef
	 *            the method def
	 * @return true, if successful
	 */
	public boolean addAllMethodDefs(final Collection<JavaElementLocation> methodDef) {
		boolean result = true;
		for (JavaElementLocation e : methodDef) {
			if (!methodDefs.containsKey(e.getFilePath())) {
				methodDefs.put(e.getFilePath(), new TreeSet<JavaElementLocation>());
			}
			result &= methodDefs.get(e.getFilePath()).add(e);
		}
		return result;
	}
	
	/**
	 * Adds the class def.
	 * 
	 * @param classDef
	 *            the class def
	 * @return true, if successful
	 */
	public boolean addClassDef(final JavaElementLocation classDef) {
		if (!classDefs.containsKey(classDef.getFilePath())) {
			classDefs.put(classDef.getFilePath(), new TreeSet<JavaElementLocation>());
		}
		return classDefs.get(classDef.getFilePath()).add(classDef);
	}
	
	/**
	 * Adds the method call.
	 * 
	 * @param methodCall
	 *            the method call
	 * @return true, if successful
	 */
	public boolean addMethodCall(final JavaElementLocation methodCall) {
		if (!methodCalls.containsKey(methodCall.getFilePath())) {
			methodCalls.put(methodCall.getFilePath(), new TreeSet<JavaElementLocation>());
		}
		return methodCalls.get(methodCall.getFilePath()).add(methodCall);
	}
	
	/**
	 * Adds the method def.
	 * 
	 * @param methodDef
	 *            the method def
	 * @return true, if successful
	 */
	public boolean addMethodDef(final JavaElementLocation methodDef) {
		if (!methodDefs.containsKey(methodDef.getFilePath())) {
			methodDefs.put(methodDef.getFilePath(), new TreeSet<JavaElementLocation>());
		}
		return methodDefs.get(methodDef.getFilePath()).add(methodDef);
	}
	
	/**
	 * Contains file path.
	 * 
	 * @param filePath
	 *            the file path
	 * @return true, if successful
	 */
	public boolean containsFilePath(final String filePath) {
		if (classDefs.containsKey(filePath)) {
			return true;
		} else if (methodDefs.containsKey(filePath)) {
			return true;
		} else if (methodCalls.containsKey(filePath)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the class defs.
	 * 
	 * @return the class defs
	 */
	public Collection<JavaElementLocation> getClassDefs() {
		Set<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		for (TreeSet<JavaElementLocation> e : classDefs.values()) {
			result.addAll(e);
		}
		return result;
	}
	
	/**
	 * Gets the class defs.
	 * 
	 * @param filePath
	 *            the file path
	 * @return the class defs
	 */
	public TreeSet<JavaElementLocation> getClassDefs(final String filePath) {
		TreeSet<JavaElementLocation> result = classDefs.get(filePath);
		if (result == null) {
			result = new TreeSet<JavaElementLocation>();
		}
		return result;
	}
	
	/**
	 * Gets the class defs by file.
	 * 
	 * @return the class defs by file
	 */
	public Map<String, TreeSet<JavaElementLocation>> getClassDefsByFile() {
		return classDefs;
	}
	
	/**
	 * Gets the defs.
	 * 
	 * @return the defs
	 */
	public Collection<JavaElementLocation> getDefs() {
		Collection<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		for (String filePath : methodDefs.keySet()) {
			result.addAll(methodDefs.get(filePath));
		}
		for (String filePath : classDefs.keySet()) {
			result.addAll(classDefs.get(filePath));
		}
		return result;
	}
	
	/**
	 * Gets the defs.
	 * 
	 * @param filePath
	 *            the file path
	 * @return the defs
	 */
	public Collection<JavaElementLocation> getDefs(final String filePath) {
		Collection<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		if (methodDefs.containsKey(filePath)) {
			result.addAll(methodDefs.get(filePath));
		}
		if (classDefs.containsKey(filePath)) {
			result.addAll(classDefs.get(filePath));
		}
		return result;
	}
	
	/**
	 * Gets the defs by file.
	 * 
	 * @return the defs by file
	 */
	public Map<String, Collection<JavaElementLocation>> getDefsByFile() {
		Map<String, Collection<JavaElementLocation>> map = new HashMap<String, Collection<JavaElementLocation>>();
		for (String filePath : methodDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(methodDefs.get(filePath));
		}
		for (String filePath : classDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(classDefs.get(filePath));
		}
		return map;
	}
	
	/**
	 * Gets the elements.
	 * 
	 * @return the elements
	 */
	public Collection<JavaElementLocation> getElements() {
		Collection<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		for (String filePath : methodDefs.keySet()) {
			result.addAll(methodDefs.get(filePath));
		}
		for (String filePath : classDefs.keySet()) {
			result.addAll(classDefs.get(filePath));
		}
		for (String filePath : methodCalls.keySet()) {
			result.addAll(methodCalls.get(filePath));
		}
		return result;
	}
	
	/**
	 * Gets the elements.
	 * 
	 * @param filePath
	 *            the file path
	 * @return the elements
	 */
	public Collection<JavaElementLocation> getElements(final String filePath) {
		Collection<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		if (methodDefs.containsKey(filePath)) {
			result.addAll(methodDefs.get(filePath));
		}
		if (classDefs.containsKey(filePath)) {
			result.addAll(classDefs.get(filePath));
		}
		if (methodCalls.containsKey(filePath)) {
			result.addAll(methodCalls.get(filePath));
		}
		return result;
	}
	
	/**
	 * Gets the elements by file.
	 * 
	 * @return the elements by file
	 */
	public Map<String, Collection<JavaElementLocation>> getElementsByFile() {
		Map<String, Collection<JavaElementLocation>> map = new HashMap<String, Collection<JavaElementLocation>>();
		for (String filePath : methodDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(methodDefs.get(filePath));
		}
		for (String filePath : classDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(classDefs.get(filePath));
		}
		for (String filePath : methodCalls.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(methodCalls.get(filePath));
		}
		return map;
	}
	
	/**
	 * Gets the method calls.
	 * 
	 * @return the method calls
	 */
	public Collection<JavaElementLocation> getMethodCalls() {
		Set<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		for (TreeSet<JavaElementLocation> e : methodCalls.values()) {
			result.addAll(e);
		}
		return result;
	}
	
	/**
	 * Gets the method calls.
	 * 
	 * @param filePath
	 *            the file path
	 * @return the method calls
	 */
	public TreeSet<JavaElementLocation> getMethodCalls(final String filePath) {
		TreeSet<JavaElementLocation> result = methodCalls.get(filePath);
		if (result == null) {
			result = new TreeSet<JavaElementLocation>();
		}
		return result;
	}
	
	/**
	 * Gets the method calls by file.
	 * 
	 * @return the method calls by file
	 */
	public Map<String, TreeSet<JavaElementLocation>> getMethodCallsByFile() {
		return methodCalls;
	}
	
	/**
	 * Gets the method defs.
	 * 
	 * @return the method defs
	 */
	public Collection<JavaElementLocation> getMethodDefs() {
		Set<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		for (TreeSet<JavaElementLocation> e : methodDefs.values()) {
			result.addAll(e);
		}
		return result;
	}
	
	/**
	 * Gets the method defs.
	 * 
	 * @param filePath
	 *            the file path
	 * @return the method defs
	 */
	public TreeSet<JavaElementLocation> getMethodDefs(final String filePath) {
		TreeSet<JavaElementLocation> result = methodDefs.get(filePath);
		if (result == null) {
			result = new TreeSet<JavaElementLocation>();
		}
		return result;
	}
	
	/**
	 * Gets the method defs by file.
	 * 
	 * @return the method defs by file
	 */
	public Map<String, TreeSet<JavaElementLocation>> getMethodDefsByFile() {
		return methodDefs;
	}
	
}
