package de.unisaarland.cs.st.reposuite.ppa.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

public class PPAMethodCallVisitor implements PPAVisitor {
	
	private final Map<String, Collection<JavaMethodCall>> methodCallsByFile = new HashMap<String, Collection<JavaMethodCall>>();
	
	@Override
	public void endVisit(@NotNull final PPATypeVisitor ppaVisitor, @NotNull final CompilationUnit cu,
			@NotNull final ASTNode node, @NotNull final Tuple<JavaClassDefinition, Integer> classContext,
			final Tuple<JavaMethodDefinition, Integer> methodContext, @NotNull final JavaElementCache elementCache) {
	}
	
	public Map<String, Collection<JavaMethodCall>> getMethodCallsByFile() {
		return this.methodCallsByFile;
	}
	
	@Override
	public void postVisit(@NotNull final PPATypeVisitor ppaVisitor, @NotNull final CompilationUnit cu,
			@NotNull final ASTNode node, final Tuple<JavaClassDefinition, Integer> classContext,
			final Tuple<JavaMethodDefinition, Integer> methodContext, @NonNegative final int currentLine,
			@NotNull final JavaElementCache elementCache) {
		
		IBinding binding = null;
		
		String methodName = null;
		if (node instanceof MethodInvocation) {
			binding = ((MethodInvocation) node).resolveMethodBinding();
			MethodInvocation mi = (MethodInvocation) node;
			methodName = mi.getName().toString();
		} else if (node instanceof ClassInstanceCreation) {
			binding = ((ClassInstanceCreation) node).resolveConstructorBinding();
		} else if (node instanceof SuperConstructorInvocation) {
			binding = ((SuperConstructorInvocation) node).resolveConstructorBinding();
		} else {
			return;
		}
		
		if (binding == null) {
			StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation");
			ss.append("\n\t");
			if (classContext != null) {
				ss.append("in class ");
				ss.append(classContext.getFirst().getFullQualifiedName());
				ss.append("\n\t");
			}
			if (methodContext != null) {
				ss.append("in method ");
				ss.append(methodContext.getFirst().getFullQualifiedName());
				ss.append("\n\t");
			}
			ss.append("on line ");
			ss.append(currentLine);
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			return;
		}
		IMethodBinding mBinding = (IMethodBinding) binding;
		String calledObject = mBinding.getDeclaringClass().getQualifiedName();
		
		if (calledObject.equals("UNKNOWN")) {
			StringBuilder ss = new StringBuilder();
			ss.append("Could not resolve method binding for MethodInvocation in revision ");
			if (classContext != null) {
				ss.append(" in class `");
				ss.append(classContext.getFirst().getFullQualifiedName());
				ss.append("\n\t");
			}
			if (methodContext != null) {
				ss.append(" in method `");
				ss.append(methodContext.getFirst().getFullQualifiedName());
				ss.append("\n\t");
			}
			ss.append("` on line ");
			ss.append(currentLine);
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
		
		JavaElementDefinition parent = classContext.getFirst();
		if (methodContext != null) {
			parent = methodContext.getFirst();
		}
		
		if (methodName == null) {
			String[] tmp = calledObject.split("\\.");
			methodName = tmp[tmp.length - 1];
		}
		
		String filename = ppaVisitor.getRelativeFilePath();
		
		JavaMethodCall javaMethodCall = elementCache.getMethodCall(calledObject + "." + methodName, arguments,
		        filename, parent, currentLine, currentLine, node.getStartPosition());
		
		if (!this.methodCallsByFile.containsKey(filename)) {
			this.methodCallsByFile.put(filename, new LinkedList<JavaMethodCall>());
		}
		this.methodCallsByFile.get(filename).add(javaMethodCall);
		
	}
	
	@Override
	public void preVisit(@NotNull final PPATypeVisitor ppaVisitor, @NotNull final CompilationUnit cu,
			@NotNull final ASTNode node, final Tuple<JavaClassDefinition, Integer> classContext,
			final Tuple<JavaMethodDefinition, Integer> methodContext, @NonNegative final int currentLine,
			@NonNegative final int endLine, @NotNull final JavaElementCache elementCache) {
	}
	
}
