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
package de.unisaarland.cs.st.mozkito.callgraph.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import de.unisaarland.cs.st.mozkito.callgraph.model.CallGraph;
import de.unisaarland.cs.st.mozkito.callgraph.model.ClassVertex;
import de.unisaarland.cs.st.mozkito.callgraph.model.MethodVertex;
import de.unisaarland.cs.st.mozkito.callgraph.model.VertexFactory;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaElementLocation;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaElementLocationSet;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaMethodDefinition;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaTypeDefinition;
import de.unisaarland.cs.st.mozkito.codeanalysis.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.mozkito.codeanalysis.visitors.PPAVisitor;

/**
 * The Class CallGraphPPAVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CallGraphPPAVisitor implements PPAVisitor {
	
	/** The call graph. */
	private final CallGraph              callGraph;
	
	/** The update. */
	private boolean                      update           = false;
	
	/** The resetted vertices. */
	private final Set<ClassVertex>       resettedVertices = new HashSet<ClassVertex>();
	
	/** The changed methods. */
	private final Set<MethodVertex>      changedMethods   = new HashSet<MethodVertex>();
	
	/** The filename. */
	private final String                 filename;
	
	/** The java element cache. */
	private final JavaElementLocationSet javaElementCache;
	
	/**
	 * Instantiates a new call graph codeanalysis visitor.
	 * 
	 * @param callGraph
	 *            the call graph
	 * @param update
	 *            the update
	 * @param fileName
	 *            the file name
	 * @param javaElementCache
	 *            the java element cache
	 */
	public CallGraphPPAVisitor(final CallGraph callGraph, final boolean update, final String fileName,
	        final JavaElementLocationSet javaElementCache) {
		this.callGraph = callGraph;
		this.update = update;
		this.filename = fileName;
		this.javaElementCache = javaElementCache;
	}
	
	/**
	 * Adds the edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	private void addEdge(final MethodVertex from,
	                     final MethodVertex to) {
		if (this.update) {
			final ClassVertex parent = from.getParent();
			if ((parent != null) && (!this.resettedVertices.contains(parent))) {
				this.callGraph.removeRecursive(parent);
				this.resettedVertices.add(parent);
			}
		}
		this.callGraph.addEdge(from, to);
		this.changedMethods.add(from);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.ppa.visitors.PPAVisitor#endVisit(de.unisaarland.cs.st.mozkito.ppa.visitors.
	 * PPATypeVisitor, org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocationSet)
	 */
	@Override
	public void endVisit(final PPATypeVisitor ppaVisitor,
	                     final CompilationUnit cu,
	                     final ASTNode node,
	                     final JavaElementLocation classContext,
	                     final JavaElementLocation methodContext,
	                     final JavaElementLocationSet elementCache) {
		// stub
	}
	
	/**
	 * Gets the changed classes.
	 * 
	 * @return the changed classes
	 */
	public Set<ClassVertex> getChangedClasses() {
		final Set<ClassVertex> changedClasses = new HashSet<ClassVertex>();
		for (final MethodVertex v : this.changedMethods) {
			if (v.getParent() != null) {
				changedClasses.add(v.getParent());
			}
		}
		return changedClasses;
	}
	
	/**
	 * Gets the changed methods.
	 * 
	 * @return the changed methods
	 */
	public Set<MethodVertex> getChangedMethods() {
		return this.changedMethods;
	}
	
	/**
	 * Handle class instance creation.
	 * 
	 * @param line
	 *            the line
	 * @param cic
	 *            the cic
	 * @param classContext
	 *            the class context
	 * @param methodContext
	 *            the method context
	 */
	private void handleClassInstanceCreation(final int line,
	                                         final ClassInstanceCreation cic,
	                                         final JavaElementLocation classContext,
	                                         final JavaElementLocation methodContext) {
		final IMethodBinding mBinding = cic.resolveConstructorBinding();
		if (mBinding == null) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for ConstructorInvocation");
			ss.append("\n\t");
			if ((classContext != null) && (classContext.getElement() != null)) {
				ss.append("in class ");
				ss.append(classContext.getElement().getFullQualifiedName());
				ss.append("\n\t");
			}
			ss.append("on line ");
			ss.append(line);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		
		if (mBinding.getDeclaringClass() == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not fetch declaring class. Ignoring!");
			}
			return;
		}
		String calledObjectName = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObjectName.startsWith("src.")) {
			calledObjectName = calledObjectName.substring(4);
		}
		
		if (calledObjectName.equals("UNKNOWN")) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve called class name for ConstructorInvocation");
			ss.append("\n\t");
			ss.append("in revision ");
			if ((classContext != null) && (classContext.getElement() != null)) {
				ss.append(" in class ");
				ss.append(classContext.getElement().getFullQualifiedName());
				ss.append("\n\t");
			}
			ss.append("on line ");
			ss.append(line);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
		}
		
		final JavaTypeDefinition calledObject = (JavaTypeDefinition) this.javaElementCache.addClassDefinition(calledObjectName,
		                                                                                                      this.filename,
		                                                                                                      0, 0, 0,
		                                                                                                      0)
		                                                                                  .getElement();
		
		final ITypeBinding[] args = mBinding.getParameterTypes();
		final List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		final String methodName = mBinding.getName().toString();
		
		if ((methodContext != null) && (methodContext.getElement() != null)) {
			// add edge in call graph
			final MethodVertex from = VertexFactory.createMethodVertex(methodContext.getElement()
			                                                                        .getFullQualifiedName(),
			                                                           this.filename);
			final MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                       methodName,
			                                                                                                       arguments),
			                                                         this.filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(methodContext.getElement().getFullQualifiedName() + " calls "
				        + to.getFullQualifiedMethodName());
			}
			
		} else {
			final String initMethodName = JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                            "<init>",
			                                                                            new ArrayList<String>());
			final MethodVertex from = VertexFactory.createMethodVertex(initMethodName, this.filename);
			final MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                       methodName,
			                                                                                                       arguments),
			                                                         this.filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		}
	}
	
	/**
	 * Handle method invocation.
	 * 
	 * @param line
	 *            the line
	 * @param mi
	 *            the mi
	 * @param classContextLocation
	 *            the class context location
	 * @param methodContextLocation
	 *            the method context location
	 */
	private void handleMethodInvocation(final int line,
	                                    final MethodInvocation mi,
	                                    final JavaElementLocation classContextLocation,
	                                    final JavaElementLocation methodContextLocation) {
		
		if ((classContextLocation == null) || (classContextLocation.getElement() == null)) {
			if (Logger.logWarn()) {
				Logger.warn("Found empty class context. Ignore!");
			}
			return;
		}
		
		final JavaTypeDefinition classContext = (JavaTypeDefinition) classContextLocation.getElement();
		
		final IBinding binding = mi.resolveMethodBinding();
		
		if (binding == null) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation");
			ss.append("\n\t");
			ss.append("\n\t");
			ss.append("in class ");
			ss.append(classContext.getFullQualifiedName());
			ss.append("\n\t");
			ss.append("on line ");
			ss.append(line);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		final IMethodBinding mBinding = (IMethodBinding) binding;
		String calledObjectName = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObjectName.startsWith("src.")) {
			calledObjectName = calledObjectName.substring(4);
		}
		
		if (calledObjectName.equals("UNKNOWN")) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation ");
			ss.append(" in class `");
			ss.append(classContext.getFullQualifiedName());
			ss.append("` on line ");
			ss.append(line);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		
		final JavaTypeDefinition calledObject = (JavaTypeDefinition) this.javaElementCache.addClassDefinition(calledObjectName,
		                                                                                                      this.filename,
		                                                                                                      0, 0, 0,
		                                                                                                      0)
		                                                                                  .getElement();
		
		final ITypeBinding[] args = mBinding.getParameterTypes();
		final List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		final String methodName = mi.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			final JavaMethodDefinition methodContext = (JavaMethodDefinition) methodContextLocation.getElement();
			
			// add edge in call graph
			final MethodVertex from = VertexFactory.createMethodVertex(methodContext.getFullQualifiedName(),
			                                                           this.filename);
			
			final MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                       methodName,
			                                                                                                       arguments),
			                                                         this.filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		} else {
			
			final String initMethodName = JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                            "<init>",
			                                                                            new ArrayList<String>());
			final MethodVertex from = VertexFactory.createMethodVertex(initMethodName, this.filename);
			final MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                       methodName,
			                                                                                                       arguments),
			                                                         this.filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		}
		
	}
	
	/**
	 * Handle super constructor invocation.
	 * 
	 * @param line
	 *            the line
	 * @param sci
	 *            the sci
	 * @param classContextLocation
	 *            the class context location
	 * @param methodContextLocation
	 *            the method context location
	 */
	@SuppressWarnings ("deprecation")
	private void handleSuperConstructorInvocation(final int line,
	                                              final SuperConstructorInvocation sci,
	                                              final JavaElementLocation classContextLocation,
	                                              final JavaElementLocation methodContextLocation) {
		
		if ((classContextLocation == null) || (classContextLocation.getElement() == null)) {
			if (Logger.logWarn()) {
				Logger.warn("Found empty class context. Ignore!");
			}
			return;
		}
		
		final JavaTypeDefinition classContext = (JavaTypeDefinition) classContextLocation.getElement();
		
		final IMethodBinding mBinding = sci.resolveConstructorBinding();
		if (mBinding == null) {
			if (classContext.getParent() == null) {
				
				final StringBuilder ss = new StringBuilder();
				ss.append("Could not resolve method binding for SuperConstructorInvocation");
				ss.append("\n\t");
				ss.append("in class ");
				ss.append(classContext.getFullQualifiedName());
				ss.append("\n\t");
				ss.append("on line ");
				ss.append(line);
				if (Logger.logError()) {
					Logger.error(ss.toString());
				}
				return;
			}
			final JavaMethodDefinition methodContext = (JavaMethodDefinition) methodContextLocation.getElement();
			
			final MethodVertex from = VertexFactory.createMethodVertex(methodContext.getFullQualifiedName(),
			                                                           this.filename);
			final MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(classContext.getParent()
			                                                                                                                   .getFullQualifiedName(),
			                                                                                                       "<init>",
			                                                                                                       new ArrayList<String>()),
			                                                         this.filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
			return;
		}
		
		String calledObjectName = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObjectName.startsWith("src.")) {
			calledObjectName = calledObjectName.substring(4);
		}
		
		if (calledObjectName.equals("UNKNOWN")) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve called class name for SuperConstructorInvocation");
			ss.append("\n\t");
			ss.append("in revision ");
			ss.append(" in class ");
			ss.append(classContext.getFullQualifiedName());
			ss.append("\n\t");
			ss.append("on line ");
			ss.append(line);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		
		final JavaTypeDefinition calledObject = (JavaTypeDefinition) this.javaElementCache.addClassDefinition(calledObjectName,
		                                                                                                      this.filename,
		                                                                                                      0, 0, 0,
		                                                                                                      0)
		                                                                                  .getElement();
		
		final ITypeBinding[] args = mBinding.getParameterTypes();
		final List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		final String methodName = mBinding.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			// add edge in call graph
			final MethodVertex from = VertexFactory.createMethodVertex(methodContextLocation.getElement()
			                                                                                .getFullQualifiedName(),
			                                                           this.filename);
			final MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                       methodName,
			                                                                                                       arguments),
			                                                         this.filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		} else {
			// add edge in call graph
			if (Logger.logError()) {
				Logger.error("Found method call outside method declaration in class `"
				                     + classContext.getFullQualifiedName() + "` in line " + line,
				             new RuntimeException());
			}
		}
		
	}
	
	/**
	 * Handle super method invocation.
	 * 
	 * @param line
	 *            the line
	 * @param smi
	 *            the smi
	 * @param classContextLocation
	 *            the class context location
	 * @param methodContextLocation
	 *            the method context location
	 */
	private void handleSuperMethodInvocation(final int line,
	                                         final SuperMethodInvocation smi,
	                                         final JavaElementLocation classContextLocation,
	                                         final JavaElementLocation methodContextLocation) {
		
		if ((classContextLocation == null) || (classContextLocation.getElement() == null)) {
			if (Logger.logWarn()) {
				Logger.warn("Found empty class context. Ignore!");
			}
			return;
		}
		
		final JavaTypeDefinition classContext = (JavaTypeDefinition) classContextLocation.getElement();
		final IBinding binding = smi.resolveMethodBinding();
		
		if (binding == null) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation");
			ss.append("\n\t");
			ss.append("\n\t");
			ss.append("in class ");
			ss.append(classContext.getFullQualifiedName());
			ss.append("\n\t");
			ss.append("on line ");
			ss.append(line);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		
		final IMethodBinding mBinding = (IMethodBinding) binding;
		String calledObjectName = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObjectName.startsWith("src.")) {
			calledObjectName = calledObjectName.substring(4);
		}
		
		if (calledObjectName.equals("UNKNOWN")) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation ");
			ss.append(" in class `");
			ss.append(classContext.getFullQualifiedName());
			ss.append("` on line ");
			ss.append(line);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		
		final JavaTypeDefinition calledObject = (JavaTypeDefinition) this.javaElementCache.addClassDefinition(calledObjectName,
		                                                                                                      this.filename,
		                                                                                                      0, 0, 0,
		                                                                                                      0)
		                                                                                  .getElement();
		
		final ITypeBinding[] args = mBinding.getParameterTypes();
		final List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		final String methodName = smi.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			// add edge in call graph
			final MethodVertex from = VertexFactory.createMethodVertex(methodContextLocation.getElement()
			                                                                                .getFullQualifiedName(),
			                                                           this.filename);
			final MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                       methodName,
			                                                                                                       arguments),
			                                                         this.filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		} else {
			// add edge in call graph
			if (Logger.logError()) {
				Logger.error("Found method call outside method declaration in class `%s` in line d",
				             classContext.getFullQualifiedName(), line);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.ppa.visitors.PPAVisitor#postVisit(de.unisaarland.cs.st.mozkito.ppa.visitors.
	 * PPATypeVisitor, org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocation, int,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocationSet)
	 */
	@Override
	public void postVisit(final PPATypeVisitor ppaVisitor,
	                      final CompilationUnit cu,
	                      final ASTNode node,
	                      final JavaElementLocation classContext,
	                      final JavaElementLocation methodContext,
	                      final int currentLine,
	                      final JavaElementLocationSet elementCache) {
		if (classContext == null) {
			return;
		}
		if (node instanceof MethodInvocation) {
			final MethodInvocation mi = (MethodInvocation) node;
			final int line = cu.getLineNumber(mi.getStartPosition());
			handleMethodInvocation(line, mi, classContext, methodContext);
		} else if (node instanceof ClassInstanceCreation) {
			final ClassInstanceCreation cic = (ClassInstanceCreation) node;
			final int line = cu.getLineNumber(cic.getStartPosition());
			handleClassInstanceCreation(line, cic, classContext, methodContext);
		} else if (node instanceof SuperConstructorInvocation) {
			final SuperConstructorInvocation sci = (SuperConstructorInvocation) node;
			final int line = cu.getLineNumber(sci.getStartPosition());
			handleSuperConstructorInvocation(line, sci, classContext, methodContext);
		} else if (node instanceof SuperMethodInvocation) {
			final SuperMethodInvocation smi = (SuperMethodInvocation) node;
			final int line = cu.getLineNumber(smi.getStartPosition());
			handleSuperMethodInvocation(line, smi, classContext, methodContext);
		} else {
			// int line = cu.getLineNumber(node.getStartPosition());
			// Logger.getLogger(PPATypeVisitor.class).fatal(
			// "Found method declaration outside class while parsing line "
			// + line + " while parsing " + ". Skipping ASTNode!");
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.ppa.visitors.PPAVisitor#preVisit(de.unisaarland.cs.st.mozkito.ppa.visitors.
	 * PPATypeVisitor, org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocation,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocation, int, int,
	 * de.unisaarland.cs.st.mozkito.ppa.model.JavaElementLocationSet)
	 */
	@Override
	public void preVisit(final PPATypeVisitor ppaVisitor,
	                     final CompilationUnit cu,
	                     final ASTNode node,
	                     final JavaElementLocation classContext,
	                     final JavaElementLocation methodContext,
	                     final int currentLine,
	                     final int endLine,
	                     final JavaElementLocationSet elementCache) {
		// stub
	}
	
}
