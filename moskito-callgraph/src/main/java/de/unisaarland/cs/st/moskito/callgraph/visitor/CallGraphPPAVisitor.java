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
package de.unisaarland.cs.st.moskito.callgraph.visitor;

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

import de.unisaarland.cs.st.moskito.callgraph.model.CallGraph;
import de.unisaarland.cs.st.moskito.callgraph.model.ClassVertex;
import de.unisaarland.cs.st.moskito.callgraph.model.MethodVertex;
import de.unisaarland.cs.st.moskito.callgraph.model.VertexFactory;
import de.unisaarland.cs.st.moskito.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocationSet;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.moskito.ppa.visitors.PPAVisitor;

public class CallGraphPPAVisitor implements PPAVisitor {
	
	private final CallGraph         callGraph;
	private boolean                 update           = false;
	private final Set<ClassVertex>  resettedVertices = new HashSet<ClassVertex>();
	private final Set<MethodVertex> changedMethods   = new HashSet<MethodVertex>();
	private final String            filename;
	private final JavaElementLocationSet  javaElementCache;
	
	public CallGraphPPAVisitor(final CallGraph callGraph, final boolean update, final String fileName,
	                           final JavaElementLocationSet javaElementCache) {
		this.callGraph = callGraph;
		this.update = update;
		filename = fileName;
		this.javaElementCache = javaElementCache;
	}
	
	private void addEdge(final MethodVertex from,
	                     final MethodVertex to) {
		if (update) {
			ClassVertex parent = from.getParent();
			if ((parent != null) && (!resettedVertices.contains(parent))) {
				callGraph.removeRecursive(parent);
				resettedVertices.add(parent);
			}
		}
		callGraph.addEdge(from, to);
		changedMethods.add(from);
	}
	
	@Override
	public void endVisit(final PPATypeVisitor ppaVisitor,
	                     final CompilationUnit cu,
	                     final ASTNode node,
	                     final JavaElementLocation classContext,
	                     final JavaElementLocation methodContext,
	                     final JavaElementLocationSet elementCache) {
		
	}
	
	public Set<ClassVertex> getChangedClasses() {
		Set<ClassVertex> changedClasses = new HashSet<ClassVertex>();
		for (MethodVertex v : changedMethods) {
			if (v.getParent() != null) {
				changedClasses.add(v.getParent());
			}
		}
		return changedClasses;
	}
	
	public Set<MethodVertex> getChangedMethods() {
		return changedMethods;
	}
	
	private void handleClassInstanceCreation(final int line,
	                                         final ClassInstanceCreation cic,
	                                         final JavaElementLocation classContext,
	                                         final JavaElementLocation methodContext) {
		IMethodBinding mBinding = cic.resolveConstructorBinding();
		if (mBinding == null) {
			StringBuilder ss = new StringBuilder();
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
			StringBuilder ss = new StringBuilder();
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
		
		JavaClassDefinition calledObject = (JavaClassDefinition) javaElementCache.addClassDefinition(calledObjectName,
		                                                                                             filename, 0, 0, 0,
		                                                                                             0).getElement();
		
		ITypeBinding[] args = mBinding.getParameterTypes();
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		String methodName = mBinding.getName().toString();
		
		if ((methodContext != null) && (methodContext.getElement() != null)) {
			// add edge in call graph
			MethodVertex from = VertexFactory.createMethodVertex(methodContext.getElement().getFullQualifiedName(),
			                                                     filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(methodContext.getElement().getFullQualifiedName() + " calls "
				             + to.getFullQualifiedMethodName());
			}
			
		} else {
			String initMethodName = JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                      "<init>", new ArrayList<String>());
			MethodVertex from = VertexFactory.createMethodVertex(initMethodName, filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		}
	}
	
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
		
		JavaClassDefinition classContext = (JavaClassDefinition) classContextLocation.getElement();
		
		IBinding binding = mi.resolveMethodBinding();
		
		if (binding == null) {
			StringBuilder ss = new StringBuilder();
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
		IMethodBinding mBinding = (IMethodBinding) binding;
		String calledObjectName = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObjectName.startsWith("src.")) {
			calledObjectName = calledObjectName.substring(4);
		}
		
		if (calledObjectName.equals("UNKNOWN")) {
			StringBuilder ss = new StringBuilder();
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
		
		JavaClassDefinition calledObject = (JavaClassDefinition) javaElementCache.addClassDefinition(calledObjectName,
		                                                                                             filename, 0, 0, 0,
		                                                                                             0).getElement();
		
		ITypeBinding[] args = mBinding.getParameterTypes();
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		String methodName = mi.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			JavaMethodDefinition methodContext = (JavaMethodDefinition) methodContextLocation.getElement();
			
			// add edge in call graph
			MethodVertex from = VertexFactory.createMethodVertex(methodContext.getFullQualifiedName(), filename);
			
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		} else {
			
			String initMethodName = JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                      "<init>", new ArrayList<String>());
			MethodVertex from = VertexFactory.createMethodVertex(initMethodName, filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		}
		
	}
	
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
		
		JavaClassDefinition classContext = (JavaClassDefinition) classContextLocation.getElement();
		
		IMethodBinding mBinding = sci.resolveConstructorBinding();
		if (mBinding == null) {
			if (classContext.getParent() == null) {
				
				StringBuilder ss = new StringBuilder();
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
			} else {
				
				JavaMethodDefinition methodContext = (JavaMethodDefinition) methodContextLocation.getElement();
				
				MethodVertex from = VertexFactory.createMethodVertex(methodContext.getFullQualifiedName(), filename);
				MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(classContext.getParent()
				                                                                                                 .getFullQualifiedName(),
				                                                                                                 "<init>",
				                                                                                                 new ArrayList<String>()),
				                                                                                                 filename);
				addEdge(from, to);
				if (Logger.logDebug()) {
					Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
				}
				return;
			}
		}
		
		String calledObjectName = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObjectName.startsWith("src.")) {
			calledObjectName = calledObjectName.substring(4);
		}
		
		if (calledObjectName.equals("UNKNOWN")) {
			StringBuilder ss = new StringBuilder();
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
		
		JavaClassDefinition calledObject = (JavaClassDefinition) javaElementCache.addClassDefinition(calledObjectName,
		                                                                                             filename, 0, 0, 0,
		                                                                                             0).getElement();
		
		ITypeBinding[] args = mBinding.getParameterTypes();
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		String methodName = mBinding.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			// add edge in call graph
			MethodVertex from = VertexFactory.createMethodVertex(methodContextLocation.getElement()
			                                                     .getFullQualifiedName(), filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
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
		
		JavaClassDefinition classContext = (JavaClassDefinition) classContextLocation.getElement();
		IBinding binding = smi.resolveMethodBinding();
		
		if (binding == null) {
			StringBuilder ss = new StringBuilder();
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
		
		IMethodBinding mBinding = (IMethodBinding) binding;
		String calledObjectName = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObjectName.startsWith("src.")) {
			calledObjectName = calledObjectName.substring(4);
		}
		
		if (calledObjectName.equals("UNKNOWN")) {
			StringBuilder ss = new StringBuilder();
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
		
		JavaClassDefinition calledObject = (JavaClassDefinition) javaElementCache.addClassDefinition(calledObjectName,
		                                                                                             filename, 0, 0, 0,
		                                                                                             0).getElement();
		
		ITypeBinding[] args = mBinding.getParameterTypes();
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		String methodName = smi.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			// add edge in call graph
			MethodVertex from = VertexFactory.createMethodVertex(methodContextLocation.getElement()
			                                                     .getFullQualifiedName(), filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject.getFullQualifiedName(),
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
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
			MethodInvocation mi = (MethodInvocation) node;
			int line = cu.getLineNumber(mi.getStartPosition());
			handleMethodInvocation(line, mi, classContext, methodContext);
		} else if (node instanceof ClassInstanceCreation) {
			ClassInstanceCreation cic = (ClassInstanceCreation) node;
			int line = cu.getLineNumber(cic.getStartPosition());
			handleClassInstanceCreation(line, cic, classContext, methodContext);
		} else if (node instanceof SuperConstructorInvocation) {
			SuperConstructorInvocation sci = (SuperConstructorInvocation) node;
			int line = cu.getLineNumber(sci.getStartPosition());
			handleSuperConstructorInvocation(line, sci, classContext, methodContext);
		} else if (node instanceof SuperMethodInvocation) {
			SuperMethodInvocation smi = (SuperMethodInvocation) node;
			int line = cu.getLineNumber(smi.getStartPosition());
			handleSuperMethodInvocation(line, smi, classContext, methodContext);
		} else {
			// int line = cu.getLineNumber(node.getStartPosition());
			// Logger.getLogger(PPATypeVisitor.class).fatal(
			// "Found method declaration outside class while parsing line "
			// + line + " while parsing " + ". Skipping ASTNode!");
		}
		
	}
	
	@Override
	public void preVisit(final PPATypeVisitor ppaVisitor,
	                     final CompilationUnit cu,
	                     final ASTNode node,
	                     final JavaElementLocation classContext,
	                     final JavaElementLocation methodContext,
	                     final int currentLine,
	                     final int endLine,
	                     final JavaElementLocationSet elementCache) {
	}
	
}
