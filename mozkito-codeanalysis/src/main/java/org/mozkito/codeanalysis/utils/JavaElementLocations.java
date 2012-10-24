/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.codeanalysis.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mozkito.codeanalysis.model.JavaElementLocation;


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
			if (!this.classDefs.containsKey(e.getFilePath())) {
				this.classDefs.put(e.getFilePath(), new TreeSet<JavaElementLocation>());
			}
			result &= this.classDefs.get(e.getFilePath()).add(e);
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
			if (!this.methodCalls.containsKey(e.getFilePath())) {
				this.methodCalls.put(e.getFilePath(), new TreeSet<JavaElementLocation>());
			}
			result &= this.methodCalls.get(e.getFilePath()).add(e);
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
			if (!this.methodDefs.containsKey(e.getFilePath())) {
				this.methodDefs.put(e.getFilePath(), new TreeSet<JavaElementLocation>());
			}
			result &= this.methodDefs.get(e.getFilePath()).add(e);
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
		if (!this.classDefs.containsKey(classDef.getFilePath())) {
			this.classDefs.put(classDef.getFilePath(), new TreeSet<JavaElementLocation>());
		}
		return this.classDefs.get(classDef.getFilePath()).add(classDef);
	}
	
	/**
	 * Adds the method call.
	 * 
	 * @param methodCall
	 *            the method call
	 * @return true, if successful
	 */
	public boolean addMethodCall(final JavaElementLocation methodCall) {
		if (!this.methodCalls.containsKey(methodCall.getFilePath())) {
			this.methodCalls.put(methodCall.getFilePath(), new TreeSet<JavaElementLocation>());
		}
		return this.methodCalls.get(methodCall.getFilePath()).add(methodCall);
	}
	
	/**
	 * Adds the method def.
	 * 
	 * @param methodDef
	 *            the method def
	 * @return true, if successful
	 */
	public boolean addMethodDef(final JavaElementLocation methodDef) {
		if (!this.methodDefs.containsKey(methodDef.getFilePath())) {
			this.methodDefs.put(methodDef.getFilePath(), new TreeSet<JavaElementLocation>());
		}
		return this.methodDefs.get(methodDef.getFilePath()).add(methodDef);
	}
	
	/**
	 * Contains file path.
	 * 
	 * @param filePath
	 *            the file path
	 * @return true, if successful
	 */
	public boolean containsFilePath(final String filePath) {
		if (this.classDefs.containsKey(filePath)) {
			return true;
		} else if (this.methodDefs.containsKey(filePath)) {
			return true;
		} else if (this.methodCalls.containsKey(filePath)) {
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
		for (TreeSet<JavaElementLocation> e : this.classDefs.values()) {
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
		TreeSet<JavaElementLocation> result = this.classDefs.get(filePath);
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
		return this.classDefs;
	}
	
	/**
	 * Gets the defs.
	 * 
	 * @return the defs
	 */
	public Collection<JavaElementLocation> getDefs() {
		Collection<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		for (String filePath : this.methodDefs.keySet()) {
			result.addAll(this.methodDefs.get(filePath));
		}
		for (String filePath : this.classDefs.keySet()) {
			result.addAll(this.classDefs.get(filePath));
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
		if (this.methodDefs.containsKey(filePath)) {
			result.addAll(this.methodDefs.get(filePath));
		}
		if (this.classDefs.containsKey(filePath)) {
			result.addAll(this.classDefs.get(filePath));
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
		for (String filePath : this.methodDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(this.methodDefs.get(filePath));
		}
		for (String filePath : this.classDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(this.classDefs.get(filePath));
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
		for (String filePath : this.methodDefs.keySet()) {
			result.addAll(this.methodDefs.get(filePath));
		}
		for (String filePath : this.classDefs.keySet()) {
			result.addAll(this.classDefs.get(filePath));
		}
		for (String filePath : this.methodCalls.keySet()) {
			result.addAll(this.methodCalls.get(filePath));
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
		Collection<JavaElementLocation> result = new LinkedList<JavaElementLocation>();
		if (this.methodDefs.containsKey(filePath)) {
			result.addAll(this.methodDefs.get(filePath));
		}
		if (this.classDefs.containsKey(filePath)) {
			result.addAll(this.classDefs.get(filePath));
		}
		if (this.methodCalls.containsKey(filePath)) {
			result.addAll(this.methodCalls.get(filePath));
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
		for (String filePath : this.methodDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(this.methodDefs.get(filePath));
		}
		for (String filePath : this.classDefs.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(this.classDefs.get(filePath));
		}
		for (String filePath : this.methodCalls.keySet()) {
			if (!map.containsKey(filePath)) {
				map.put(filePath, new HashSet<JavaElementLocation>());
			}
			map.get(filePath).addAll(this.methodCalls.get(filePath));
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
		for (TreeSet<JavaElementLocation> e : this.methodCalls.values()) {
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
		TreeSet<JavaElementLocation> result = this.methodCalls.get(filePath);
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
		return this.methodCalls;
	}
	
	/**
	 * Gets the method defs.
	 * 
	 * @return the method defs
	 */
	public Collection<JavaElementLocation> getMethodDefs() {
		Set<JavaElementLocation> result = new HashSet<JavaElementLocation>();
		for (TreeSet<JavaElementLocation> e : this.methodDefs.values()) {
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
		TreeSet<JavaElementLocation> result = this.methodDefs.get(filePath);
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
		return this.methodDefs;
	}
	
}
