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

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.mozkito.codeanalysis.utils.JavaElementLocations;

/**
 * The Class JavaElementDefinitionCache. All instances extending JavaElement must be stored here. Careful! Instances
 * must be created new or fetched within the persister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JavaElementLocationSet {
	
	/** The class definition locations. */
	private final List<JavaElementLocation> classDefLocations   = new LinkedList<JavaElementLocation>();
	
	/** The method definition locations. */
	private final List<JavaElementLocation> methodDefLocations  = new LinkedList<JavaElementLocation>();
	
	/** The method call locations. */
	private final List<JavaElementLocation> methodCallLocations = new LinkedList<JavaElementLocation>();
	
	private final JavaElementFactory        elementFactory;
	
	public JavaElementLocationSet(final JavaElementFactory factory) {
		this.elementFactory = factory;
	}
	
	/**
	 * Adds an anonymous class definition to the set.
	 * 
	 * @param parent
	 *            the parent
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @return the java element location added.
	 */
	public JavaElementLocation addAnonymousClassDefinition(@NotNull final JavaTypeDefinition parent,
	                                                       @NotNull final String fullQualifiedName,
	                                                       @NotNull final String file,
	                                                       @NotNegative final int startLine,
	                                                       @NotNegative final int endLine,
	                                                       @NotNegative final int position,
	                                                       @NotNegative final int bodyStartLine) {
		
		final JavaTypeDefinition definition = this.elementFactory.getAnonymousClassDefinition(parent, fullQualifiedName);
		final JavaElementLocation location = new JavaElementLocation(definition, startLine, endLine, position,
		                                                             bodyStartLine, file);
		this.classDefLocations.add(location);
		return location;
	}
	
	/**
	 * Adds a class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @return the class definition added.
	 */
	public JavaElementLocation addClassDefinition(@NotNull final String fullQualifiedName,
	                                              @NotNull final String file,
	                                              @NotNegative final int startLine,
	                                              @NotNegative final int endLine,
	                                              @NotNegative final int position,
	                                              @NotNegative final int bodyStartLine) {
		final JavaTypeDefinition definition = this.elementFactory.getClassDefinition(fullQualifiedName,
		                                                                             fullQualifiedName);
		final JavaElementLocation location = new JavaElementLocation(definition, startLine, endLine, position,
		                                                             bodyStartLine, file);
		this.classDefLocations.add(location);
		return location;
	}
	
	/**
	 * @param string
	 * @param relativeFilePath
	 * @param startLine
	 * @param endLine
	 * @param startPosition
	 * @param bodyStartLine
	 * @return
	 */
	public JavaElementLocation addInterfaceDefinition(final String fullQualifiedName,
	                                                  final String relativeFilePath,
	                                                  final int startLine,
	                                                  final int endLine,
	                                                  final int startPosition,
	                                                  final int bodyStartLine) {
		final JavaTypeDefinition definition = this.elementFactory.getInterfaceDefinition(fullQualifiedName,
		                                                                                 fullQualifiedName);
		final JavaElementLocation location = new JavaElementLocation(definition, startLine, endLine, startPosition,
		                                                             bodyStartLine, relativeFilePath);
		this.classDefLocations.add(location);
		return location;
	}
	
	/**
	 * Adds a method call.
	 * 
	 * @param objectName
	 *            the object name
	 * @param methodName
	 *            the method name
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
	 * @return the method call added
	 */
	public JavaElementLocation addMethodCall(@NotNull final String objectName,
	                                         @NotNull final String methodName,
	                                         @NotNull final List<String> signature,
	                                         @NotNull final String file,
	                                         @NotNull final JavaElement parent,
	                                         @NotNegative final int startLine,
	                                         @NotNegative final int endLine,
	                                         @NotNegative final int position) {
		final JavaMethodCall call = this.elementFactory.getMethodCall(objectName, methodName, signature, parent);
		final JavaElementLocation location = new JavaElementLocation(call, startLine, endLine, position, -1, file);
		this.methodCallLocations.add(location);
		return location;
	}
	
	/**
	 * Add a method definition.
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
	 * @param override
	 * @return the method definition added
	 */
	public JavaElementLocation addMethodDefinition(@NotNull final String objectName,
	                                               @NotNull final String methodName,
	                                               @NotNull final List<String> signature,
	                                               @NotNull final String file,
	                                               @NotNegative final int startLine,
	                                               @NotNegative final int endLine,
	                                               @NotNegative final int position,
	                                               final int bodyStartLine,
	                                               final boolean override) {
		final JavaMethodDefinition definition = this.elementFactory.getMethodDefinition(objectName, methodName,
		                                                                                signature, override);
		final JavaElementLocation location = new JavaElementLocation(definition, startLine, endLine, position,
		                                                             bodyStartLine, file);
		this.methodDefLocations.add(location);
		return location;
	}
	
	/**
	 * Gets the java element locations.
	 * 
	 * @return the java element locations
	 */
	public JavaElementLocations getJavaElementLocations() {
		final JavaElementLocations result = new JavaElementLocations();
		result.addAllClassDefs(this.classDefLocations);
		result.addAllMethodCalls(this.methodCallLocations);
		result.addAllMethodDefs(this.methodDefLocations);
		return result;
	}
	
}
