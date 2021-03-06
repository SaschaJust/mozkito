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
package org.mozkito.codeanalysis.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;

import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaElementLocation;
import org.mozkito.codeanalysis.model.JavaElementLocationSet;
import org.mozkito.codeanalysis.model.JavaMethodCall;

/**
 * The Class PPAMethodCallVisitor generated MethodCalls(Locations) for the given compilation unit. Instances must be
 * passed as visitors to the PPATypeVisitor.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PPAMethodCallVisitor implements PPAVisitor {
	
	/** The method calls by file. */
	private final Map<String, Collection<JavaMethodCall>> methodCallsByFile = new HashMap<String, Collection<JavaMethodCall>>();
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.visitors.PPAVisitor#endVisit(de. unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor,
	 * org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode,
	 * org.mozkito.ppa.model.JavaElementLocation, org.mozkito.ppa.model.JavaElementLocation,
	 * org.mozkito.ppa.model.JavaElementCache)
	 */
	@Override
	public void endVisit(@NotNull final PPATypeVisitor ppaVisitor,
	                     @NotNull final CompilationUnit cu,
	                     @NotNull final ASTNode node,
	                     final JavaElementLocation classContext,
	                     final JavaElementLocation methodContext,
	                     @NotNull final JavaElementLocationSet elementCache) {
		// ignore
	}
	
	/**
	 * Gets the method calls by file.
	 * 
	 * @return the method calls by file
	 */
	public Map<String, Collection<JavaMethodCall>> getMethodCallsByFile() {
		return this.methodCallsByFile;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.visitors.PPAVisitor#postVisit(de. unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor,
	 * org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode,
	 * org.mozkito.ppa.model.JavaElementLocation, org.mozkito.ppa.model.JavaElementLocation, int,
	 * org.mozkito.ppa.model.JavaElementCache)
	 */
	@Override
	public void postVisit(@NotNull final PPATypeVisitor ppaVisitor,
	                      @NotNull final CompilationUnit cu,
	                      @NotNull final ASTNode node,
	                      final JavaElementLocation classContext,
	                      final JavaElementLocation methodContext,
	                      @NotNegative final int currentLine,
	                      @NotNull final JavaElementLocationSet locationSet) {
		
		IBinding binding = null;
		
		int position = node.getStartPosition();
		int thisLine = currentLine;
		
		String methodName = null;
		if (node instanceof MethodInvocation) {
			final MethodInvocation mi = (MethodInvocation) node;
			binding = mi.resolveMethodBinding();
			methodName = mi.getName().toString();
			final int index = node.toString().indexOf(methodName);
			position = position + index + 1;
			thisLine = cu.getLineNumber(position);
		} else if (node instanceof ClassInstanceCreation) {
			binding = ((ClassInstanceCreation) node).resolveConstructorBinding();
			methodName = "<init>";
		} else if (node instanceof SuperConstructorInvocation) {
			binding = ((SuperConstructorInvocation) node).resolveConstructorBinding();
			methodName = "super";
		} else {
			return;
		}
		
		if (binding == null) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation");
			ss.append("\n\t");
			if (classContext != null) {
				ss.append("in class ");
				ss.append(classContext.getElement().getFullQualifiedName());
				ss.append("\n\t");
			}
			if (methodContext != null) {
				ss.append("in method ");
				ss.append(methodContext.getElement().getFullQualifiedName());
				ss.append("\n\t");
			}
			ss.append("on line ");
			ss.append(thisLine);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		final IMethodBinding mBinding = (IMethodBinding) binding;
		
		final String calledObject = mBinding.getDeclaringClass().getQualifiedName();
		
		if ("UNKNOWN".equals(calledObject) || calledObject.isEmpty()) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation in revision ");
			if (classContext != null) {
				ss.append(" in class `");
				ss.append(classContext.getElement().getFullQualifiedName());
				ss.append("\n\t");
			}
			if (methodContext != null) {
				ss.append(" in method `");
				ss.append(methodContext.getElement().getFullQualifiedName());
				ss.append("\n\t");
			}
			ss.append("` on line ");
			ss.append(thisLine);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		
		if (!ppaVisitor.checkFilters(calledObject)) {
			return;
		}
		
		final ITypeBinding[] args = mBinding.getParameterTypes();
		final List<String> arguments = new ArrayList<String>();
		for (final ITypeBinding arg : args) {
			arguments.add(arg.getName());
		}
		
		JavaElement parent = classContext.getElement();
		if (methodContext != null) {
			parent = methodContext.getElement();
		}
		
		final String filename = ppaVisitor.getRelativeFilePath();
		
		final JavaElementLocation javaMethodCall = locationSet.addMethodCall(calledObject, methodName, arguments,
		                                                                     filename, parent, thisLine, thisLine,
		                                                                     position);
		
		if (!this.methodCallsByFile.containsKey(filename)) {
			this.methodCallsByFile.put(filename, new LinkedList<JavaMethodCall>());
		}
		this.methodCallsByFile.get(filename).add((JavaMethodCall) javaMethodCall.getElement());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.visitors.PPAVisitor#preVisit(de. unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor,
	 * org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode,
	 * org.mozkito.ppa.model.JavaElementLocation, org.mozkito.ppa.model.JavaElementLocation, int, int,
	 * org.mozkito.ppa.model.JavaElementCache)
	 */
	@Override
	public void preVisit(@NotNull final PPATypeVisitor ppaVisitor,
	                     @NotNull final CompilationUnit cu,
	                     @NotNull final ASTNode node,
	                     final JavaElementLocation classContext,
	                     final JavaElementLocation methodContext,
	                     @NotNegative final int currentLine,
	                     @NotNegative final int endLine,
	                     @NotNull final JavaElementLocationSet elementCache) {
		// ignore
	}
	
}
