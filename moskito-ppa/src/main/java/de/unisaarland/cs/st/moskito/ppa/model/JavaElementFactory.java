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
package de.unisaarland.cs.st.moskito.ppa.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

public class JavaElementFactory {
	
	/** The class definitions by name. */
	private final Map<String, JavaClassDefinition>  classDefs   = new HashMap<String, JavaClassDefinition>();
	
	/** The method definitions by name. */
	private final Map<String, JavaMethodDefinition> methodDefs  = new HashMap<String, JavaMethodDefinition>();
	
	/** The method calls by name. */
	private final Map<String, JavaMethodCall>       methodCalls = new HashMap<String, JavaMethodCall>();
	
	public JavaElementFactory() {
		
	}
	
	public JavaElementFactory(final PersistenceUtil persistenceUtil) {
		Criteria<JavaClassDefinition> criteria = persistenceUtil.createCriteria(JavaClassDefinition.class);
		List<JavaClassDefinition> defs = persistenceUtil.load(criteria);
		for (JavaClassDefinition def : defs) {
			classDefs.put(def.getFullQualifiedName(), def);
		}
		Criteria<JavaMethodDefinition> criteria2 = persistenceUtil.createCriteria(JavaMethodDefinition.class);
		List<JavaMethodDefinition> mDefs = persistenceUtil.load(criteria2);
		for (JavaMethodDefinition def : mDefs) {
			methodDefs.put(def.getFullQualifiedName(), def);
		}
		Criteria<JavaMethodCall> criteria3 = persistenceUtil.createCriteria(JavaMethodCall.class);
		List<JavaMethodCall> calls = persistenceUtil.load(criteria3);
		for (JavaMethodCall call : calls) {
			methodCalls.put(call.getFullQualifiedName(), call);
		}
	}
	
	/**
	 * Gets the class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @param packageName
	 *            the package name
	 * @return the class definition
	 */
	public JavaClassDefinition getAnonymousClassDefinition(@NotNull final JavaClassDefinition parent,
	                                                       @NotNull final String fullQualifiedName) {
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(parent, fullQualifiedName);
			classDefs.put(fullQualifiedName, def);
		} else {
			def = classDefs.get(fullQualifiedName);
		}
		return def;
	}
	
	/**
	 * Gets the class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @param packageName
	 *            the package name
	 * @return the class definition
	 */
	public JavaClassDefinition getClassDefinition(@NotNull final String fullQualifiedName,
	                                              @NotNull final String file) {
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(fullQualifiedName);
			classDefs.put(fullQualifiedName, def);
		} else {
			def = classDefs.get(fullQualifiedName);
		}
		return def;
	}
	
	/**
	 * Gets the method call.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @return the method call
	 */
	public JavaMethodCall getMethodCall(@NotNull final String objectName,
	                                    @NotNull final String methodName,
	                                    @NotNull final List<String> signature,
	                                    @NotNull final JavaElement parent) {
		
		boolean parentPass = ((parent instanceof JavaClassDefinition) || (parent instanceof JavaMethodDefinition));
		Condition.check(parentPass,
		                "The parent of a JavaMethodCall has to be a JavaMethodDefinition or a JavaClassDefinition");
		if (!parentPass) {
			if (Logger.logError()) {
				Logger.error("The parent of a JavaMethodCall has to be a JavaMethodDefinition or a JavaClassDefinition");
			}
		}
		
		String cacheName = JavaMethodCall.composeFullQualifiedName(objectName, methodName, signature);
		JavaMethodCall call = null;
		if (!methodCalls.containsKey(cacheName)) {
			call = new JavaMethodCall(objectName, methodName, signature);
			methodCalls.put(cacheName, call);
		} else {
			call = methodCalls.get(cacheName);
		}
		return call;
	}
	
	/**
	 * Gets the method definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @return the method definition
	 */
	public JavaMethodDefinition getMethodDefinition(@NotNull final String objectName,
	                                                @NotNull final String methodName,
	                                                @NotNull final List<String> signature) {
		
		JavaMethodDefinition def = null;
		String cacheName = JavaMethodCall.composeFullQualifiedName(objectName, methodName, signature);
		if (!methodDefs.containsKey(cacheName)) {
			
			def = new JavaMethodDefinition(objectName, methodName, signature);
			methodDefs.put(cacheName, def);
		} else {
			def = methodDefs.get(cacheName);
		}
		return def;
	}
}
