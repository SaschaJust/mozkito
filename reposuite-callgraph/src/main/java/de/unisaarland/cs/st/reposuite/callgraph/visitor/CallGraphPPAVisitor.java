package de.unisaarland.cs.st.reposuite.callgraph.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import de.unisaarland.cs.st.reposuite.callgraph.model.CallGraph;
import de.unisaarland.cs.st.reposuite.callgraph.model.ClassVertex;
import de.unisaarland.cs.st.reposuite.callgraph.model.MethodVertex;
import de.unisaarland.cs.st.reposuite.callgraph.model.VertexFactory;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPAVisitor;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class CallGraphPPAVisitor implements PPAVisitor {
	
	private final CallGraph    callGraph;
	private boolean                 update           = false;
	private final Set<ClassVertex>  resettedVertices = new HashSet<ClassVertex>();
	private final Set<MethodVertex> changedMethods   = new HashSet<MethodVertex>();
	private final String            filename;
	private final JavaElementCache  javaElementCache;
	
	public CallGraphPPAVisitor(final CallGraph callGraph, final boolean update,
	                           final String fileName, final JavaElementCache javaElementCache) {
		this.callGraph = callGraph;
		this.update = update;
		this.filename = fileName;
		this.javaElementCache = javaElementCache;
	}
	
	private void addEdge(final MethodVertex from,
	                     final MethodVertex to) {
		if (this.update) {
			ClassVertex parent = from.getParent();
			if ((parent != null) && (!this.resettedVertices.contains(parent))) {
				this.callGraph.removeRecursive(parent);
				this.resettedVertices.add(parent);
			}
		}
		this.callGraph.addEdge(from, to);
		this.changedMethods.add(from);
	}
	
	@Override
	public void endVisit(final PPATypeVisitor ppaVisitor,
	                     final CompilationUnit cu,
	                     final ASTNode node,
	                     final JavaElementLocation<JavaClassDefinition> classContext,
	                     final JavaElementLocation<JavaMethodDefinition> methodContext,
	                     final JavaElementCache elementCache) {
		
	}
	
	public Set<ClassVertex> getChangedClasses() {
		Set<ClassVertex> changedClasses = new HashSet<ClassVertex>();
		for (MethodVertex v : this.changedMethods) {
			if (v.getParent() != null) {
				changedClasses.add(v.getParent());
			}
		}
		return changedClasses;
	}
	
	public Set<MethodVertex> getChangedMethods() {
		return this.changedMethods;
	}
	
	private void handleClassInstanceCreation(final int line,
	                                         final ClassInstanceCreation cic,
	                                         final JavaElementLocation<JavaClassDefinition> classContext,
	                                         final JavaElementLocation<JavaMethodDefinition> methodContext) {
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
		
		JavaClassDefinition calledObject = javaElementCache.getClassDefinition(calledObjectName, filename, 0, 0, 0, 0)
		.getElement();
		
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
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject,
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(methodContext.getElement().getFullQualifiedName() + " calls "
				             + to.getFullQualifiedMethodName());
			}
			
		} else {
			String initMethodName = JavaMethodDefinition.composeFullQualifiedName(calledObject, "<init>",
			                                                                      new ArrayList<String>());
			MethodVertex from = VertexFactory.createMethodVertex(initMethodName, filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject,
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
	                                    final JavaElementLocation<JavaClassDefinition> classContextLocation,
	                                    final JavaElementLocation<JavaMethodDefinition> methodContextLocation) {
		
		if ((classContextLocation == null) || (classContextLocation.getElement() == null)) {
			if (Logger.logWarn()) {
				Logger.warn("Found empty class context. Ignore!");
			}
			return;
		}
		
		JavaClassDefinition classContext = classContextLocation.getElement();
		
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
		
		JavaClassDefinition calledObject = javaElementCache.getClassDefinition(calledObjectName, filename, 0, 0, 0, 0)
		.getElement();
		
		ITypeBinding[] args = mBinding.getParameterTypes();
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		String methodName = mi.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			JavaMethodDefinition methodContext = methodContextLocation.getElement();
			
			// add edge in call graph
			MethodVertex from = VertexFactory.createMethodVertex(methodContext.getFullQualifiedName(), filename);
			
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject,
			                                                                                                 methodName,
			                                                                                                 arguments),
			                                                                                                 filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		} else {
			
			String initMethodName = JavaMethodDefinition.composeFullQualifiedName(calledObject, "<init>",
			                                                                      new ArrayList<String>());
			MethodVertex from = VertexFactory.createMethodVertex(initMethodName, filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject,
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
	                                              final JavaElementLocation<JavaClassDefinition> classContextLocation,
	                                              final JavaElementLocation<JavaMethodDefinition> methodContextLocation) {
		
		if ((classContextLocation == null) || (classContextLocation.getElement() == null)) {
			if (Logger.logWarn()) {
				Logger.warn("Found empty class context. Ignore!");
			}
			return;
		}
		
		JavaClassDefinition classContext = classContextLocation.getElement();
		
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
				
				JavaMethodDefinition methodContext = methodContextLocation.getElement();
				
				MethodVertex from = VertexFactory.createMethodVertex(methodContext.getFullQualifiedName(), filename);
				MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(classContext.getParent(),
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
		
		JavaClassDefinition calledObject = javaElementCache.getClassDefinition(calledObjectName, filename, 0, 0, 0, 0)
		.getElement();
		
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
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject,
			                                                                                                 methodName,
			                                                                                                 arguments), filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		} else {
			// add edge in call graph
			if (Logger.logError()) {
				Logger.error("Found method call outside method declaration in class `"
				             + classContext.getFullQualifiedName()
				             + "` in line " + line, new RuntimeException());
			}
		}
		
	}
	
	private void handleSuperMethodInvocation(final int line,
	                                         final SuperMethodInvocation smi,
	                                         final JavaElementLocation<JavaClassDefinition> classContextLocation,
	                                         final JavaElementLocation<JavaMethodDefinition> methodContextLocation) {
		
		if ((classContextLocation == null) || (classContextLocation.getElement() == null)) {
			if (Logger.logWarn()) {
				Logger.warn("Found empty class context. Ignore!");
			}
			return;
		}
		
		JavaClassDefinition classContext = classContextLocation.getElement();
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
		
		JavaClassDefinition calledObject = javaElementCache.getClassDefinition(calledObjectName, filename, 0, 0, 0, 0)
		.getElement();
		
		ITypeBinding[] args = mBinding.getParameterTypes();
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			arguments.add(args[i].getName());
		}
		String methodName = smi.getName().toString();
		
		if ((methodContextLocation != null) && (methodContextLocation.getElement() != null)) {
			// add edge in call graph
			MethodVertex from = VertexFactory.createMethodVertex(methodContextLocation.getElement()
			                                                     .getFullQualifiedName(),
			                                                     filename);
			MethodVertex to = VertexFactory.createMethodVertex(JavaMethodDefinition.composeFullQualifiedName(calledObject,
			                                                                                                 methodName,
			                                                                                                 arguments), filename);
			addEdge(from, to);
			if (Logger.logDebug()) {
				Logger.debug(from.getFullQualifiedMethodName() + " calls " + to.getFullQualifiedMethodName());
			}
		} else {
			// add edge in call graph
			if (Logger.logError()) {
				Logger.error("Found method call outside method declaration in class `"
				             + classContext.getFullQualifiedName()
				             + "` in line " + line, new RuntimeException());
			}
		}
	}
	
	@Override
	public void postVisit(final PPATypeVisitor ppaVisitor,
	                      final CompilationUnit cu,
	                      final ASTNode node,
	                      final JavaElementLocation<JavaClassDefinition> classContext,
	                      final JavaElementLocation<JavaMethodDefinition> methodContext,
	                      final int currentLine,
	                      final JavaElementCache elementCache) {
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
	                     final JavaElementLocation<JavaClassDefinition> classContext,
	                     final JavaElementLocation<JavaMethodDefinition> methodContext,
	                     final int currentLine,
	                     final int endLine,
	                     final JavaElementCache elementCache) {
	}
	
}
