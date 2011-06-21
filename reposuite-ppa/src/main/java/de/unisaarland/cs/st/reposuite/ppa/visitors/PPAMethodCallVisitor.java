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
package de.unisaarland.cs.st.reposuite.ppa.visitors;

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

import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocationSet;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;

/**
 * The Class PPAMethodCallVisitor generated MethodCalls(Locations) for the given
 * compilation unit. Instances must be passed as visitors to the PPATypeVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAMethodCallVisitor implements PPAVisitor {
	
	/** The method calls by file. */
	private final Map<String, Collection<JavaMethodCall>> methodCallsByFile = new HashMap<String, Collection<JavaMethodCall>>();
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.visitors.PPAVisitor#endVisit(de.
	 * unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor,
	 * org.eclipse.jdt.core.dom.CompilationUnit,
	 * org.eclipse.jdt.core.dom.ASTNode,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache)
	 */
	@Override
	public void endVisit(@NotNull final PPATypeVisitor ppaVisitor,
	                     @NotNull final CompilationUnit cu,
	                     @NotNull final ASTNode node,
	                     final JavaElementLocation classContext,
	                     final JavaElementLocation methodContext,
	                     @NotNull final JavaElementLocationSet elementCache) {
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
	 * @see de.unisaarland.cs.st.reposuite.ppa.visitors.PPAVisitor#postVisit(de.
	 * unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor,
	 * org.eclipse.jdt.core.dom.CompilationUnit,
	 * org.eclipse.jdt.core.dom.ASTNode,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, int,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache)
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
			MethodInvocation mi = (MethodInvocation) node;
			binding = mi.resolveMethodBinding();
			methodName = mi.getName().toString();
			int index = node.toString().indexOf(methodName);
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
			StringBuilder ss = new StringBuilder();
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
		IMethodBinding mBinding = (IMethodBinding) binding;
		
		String calledObject = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObject.equals("UNKNOWN") || calledObject.equals("")) {
			StringBuilder ss = new StringBuilder();
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
		
		ITypeBinding[] args = mBinding.getParameterTypes();
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		
		JavaElement parent = classContext.getElement();
		if (methodContext != null) {
			parent = methodContext.getElement();
		}
		
		String filename = ppaVisitor.getRelativeFilePath();
		
		JavaElementLocation javaMethodCall = locationSet.addMethodCall(calledObject, methodName, arguments, filename,
		                                                               parent, thisLine, thisLine, position);
		
		if (!this.methodCallsByFile.containsKey(filename)) {
			this.methodCallsByFile.put(filename, new LinkedList<JavaMethodCall>());
		}
		this.methodCallsByFile.get(filename).add((JavaMethodCall) javaMethodCall.getElement());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.visitors.PPAVisitor#preVisit(de.
	 * unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor,
	 * org.eclipse.jdt.core.dom.CompilationUnit,
	 * org.eclipse.jdt.core.dom.ASTNode,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, int, int,
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache)
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
	}
	
}
