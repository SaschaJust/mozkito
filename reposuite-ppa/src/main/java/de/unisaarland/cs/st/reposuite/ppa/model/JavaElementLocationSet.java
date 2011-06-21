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
package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.ppa.utils.JavaElementLocations;

/**
 * The Class JavaElementDefinitionCache. All instances extending JavaElement
 * must be stored here. Careful! Instances must be created new or fetched within
 * the persister.
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
	public JavaElementLocation addAnonymousClassDefinition(@NotNull final JavaClassDefinition parent,
	                                                       @NotNull final String fullQualifiedName,
	                                                       @NotNull final String file,
	                                                       @NotNegative final int startLine,
	                                                       @NotNegative final int endLine,
	                                                       @NotNegative final int position,
	                                                       @NotNegative final int bodyStartLine) {
		
		JavaClassDefinition definition = JavaElementFactory.getAnonymousClassDefinition(parent, fullQualifiedName);
		JavaElementLocation location = new JavaElementLocation(definition, startLine, endLine, position, bodyStartLine,
		                                                       file);
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
		JavaClassDefinition definition = JavaElementFactory.getClassDefinition(fullQualifiedName, fullQualifiedName);
		JavaElementLocation location = new JavaElementLocation(definition, startLine, endLine, position, bodyStartLine,
		                                                       file);
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
		JavaMethodCall call = JavaElementFactory.getMethodCall(objectName, methodName, signature, parent);
		JavaElementLocation location = new JavaElementLocation(call, startLine, endLine, position, -1, file);
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
	 * @return the method definition added
	 */
	public JavaElementLocation addMethodDefinition(@NotNull final String objectName,
	                                               @NotNull final String methodName,
	                                               @NotNull final List<String> signature,
	                                               @NotNull final String file,
	                                               @NotNegative final int startLine,
	                                               @NotNegative final int endLine,
	                                               @NotNegative final int position,
	                                               final int bodyStartLine) {
		JavaMethodDefinition definition = JavaElementFactory.getMethodDefinition(objectName, methodName, signature);
		JavaElementLocation location = new JavaElementLocation(definition, startLine, endLine, position, bodyStartLine,
		                                                       file);
		this.methodDefLocations.add(location);
		return location;
	}
	
	/**
	 * Gets the java element locations.
	 * 
	 * @return the java element locations
	 */
	public JavaElementLocations getJavaElementLocations() {
		JavaElementLocations result = new JavaElementLocations();
		result.addAllClassDefs(this.classDefLocations);
		result.addAllMethodCalls(this.methodCallLocations);
		result.addAllMethodDefs(this.methodDefLocations);
		return result;
	}
	
}
