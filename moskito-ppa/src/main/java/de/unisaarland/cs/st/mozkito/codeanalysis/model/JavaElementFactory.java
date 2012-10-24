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
package de.unisaarland.cs.st.mozkito.codeanalysis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.mozkito.persistence.Criteria;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;

public class JavaElementFactory {
	
	/** The class definitions by name. */
	private final Map<String, JavaTypeDefinition>   classDefs   = new HashMap<String, JavaTypeDefinition>();
	
	/** The method definitions by name. */
	private final Map<String, JavaMethodDefinition> methodDefs  = new HashMap<String, JavaMethodDefinition>();
	
	/** The method calls by name. */
	private final Map<String, JavaMethodCall>       methodCalls = new HashMap<String, JavaMethodCall>();
	
	public JavaElementFactory() {
		
	}
	
	public JavaElementFactory(final PersistenceUtil persistenceUtil) {
		final Criteria<JavaTypeDefinition> criteria = persistenceUtil.createCriteria(JavaTypeDefinition.class);
		final List<JavaTypeDefinition> defs = persistenceUtil.load(criteria);
		for (final JavaTypeDefinition def : defs) {
			this.classDefs.put(def.getFullQualifiedName(), def);
		}
		final Criteria<JavaMethodDefinition> criteria2 = persistenceUtil.createCriteria(JavaMethodDefinition.class);
		final List<JavaMethodDefinition> mDefs = persistenceUtil.load(criteria2);
		for (final JavaMethodDefinition def : mDefs) {
			this.methodDefs.put(def.getFullQualifiedName(), def);
		}
		final Criteria<JavaMethodCall> criteria3 = persistenceUtil.createCriteria(JavaMethodCall.class);
		final List<JavaMethodCall> calls = persistenceUtil.load(criteria3);
		for (final JavaMethodCall call : calls) {
			this.methodCalls.put(call.getFullQualifiedName(), call);
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
	public JavaTypeDefinition getAnonymousClassDefinition(@NotNull final JavaTypeDefinition parent,
	                                                      @NotNull final String fullQualifiedName) {
		
		JavaTypeDefinition def = null;
		if (!this.classDefs.containsKey(fullQualifiedName)) {
			def = new JavaTypeDefinition(parent, fullQualifiedName);
			this.classDefs.put(fullQualifiedName, def);
		} else {
			def = this.classDefs.get(fullQualifiedName);
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
	public JavaTypeDefinition getClassDefinition(@NotNull final String fullQualifiedName,
	                                             @NotNull final String file) {
		
		JavaTypeDefinition def = null;
		if (!this.classDefs.containsKey(fullQualifiedName)) {
			def = new JavaTypeDefinition(fullQualifiedName);
			this.classDefs.put(fullQualifiedName, def);
		} else {
			def = this.classDefs.get(fullQualifiedName);
		}
		return def;
	}
	
	/**
	 * @param fullQualifiedName
	 * @param fullQualifiedName2
	 * @return
	 */
	public JavaTypeDefinition getInterfaceDefinition(final String fullQualifiedName,
	                                                 final String fullQualifiedName2) {
		JavaTypeDefinition def = null;
		if (!this.classDefs.containsKey(fullQualifiedName)) {
			def = new JavaTypeDefinition(fullQualifiedName, true);
			this.classDefs.put(fullQualifiedName, def);
		} else {
			def = this.classDefs.get(fullQualifiedName);
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
		
		final boolean parentPass = ((parent instanceof JavaTypeDefinition) || (parent instanceof JavaMethodDefinition));
		Condition.check(parentPass,
		                "The parent of a JavaMethodCall has to be a JavaMethodDefinition or a JavaClassDefinition");
		if (!parentPass) {
			if (Logger.logError()) {
				Logger.error("The parent of a JavaMethodCall has to be a JavaMethodDefinition or a JavaClassDefinition");
			}
		}
		
		final String cacheName = JavaMethodCall.composeFullQualifiedName(objectName, methodName, signature);
		JavaMethodCall call = null;
		if (!this.methodCalls.containsKey(cacheName)) {
			call = new JavaMethodCall(objectName, methodName, signature);
			this.methodCalls.put(cacheName, call);
		} else {
			call = this.methodCalls.get(cacheName);
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
	 * @param override
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
	                                                @NotNull final List<String> signature,
	                                                final boolean override) {
		
		JavaMethodDefinition def = null;
		final String cacheName = JavaMethodCall.composeFullQualifiedName(objectName, methodName, signature);
		if (!this.methodDefs.containsKey(cacheName)) {
			
			def = new JavaMethodDefinition(objectName, methodName, signature, override);
			this.methodDefs.put(cacheName, def);
		} else {
			def = this.methodDefs.get(cacheName);
		}
		return def;
	}
}
