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
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementRelation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 * The Class PPAMethodCallVisitor generated MethodCalls(Locations) for the given
 * compilation unit. Instances must be passed as visitors to the PPATypeVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAMethodCallVisitor implements PPAVisitor {
	
	/** The method calls by file. */
	private final Map<String, Collection<JavaMethodCall>> methodCallsByFile = new HashMap<String, Collection<JavaMethodCall>>();
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.visitors.PPAVisitor#endVisit(de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor, org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache)
	 */
	@Override
	public void endVisit(@NotNull final PPATypeVisitor ppaVisitor, @NotNull final CompilationUnit cu,
	                     @NotNull final ASTNode node, final JavaElementLocation<JavaClassDefinition> classContext,
	                     final JavaElementLocation<JavaMethodDefinition> methodContext, @NotNull final JavaElementCache elementCache) {
	}
	
	/**
	 * Gets the method calls by file.
	 * 
	 * @return the method calls by file
	 */
	public Map<String, Collection<JavaMethodCall>> getMethodCallsByFile() {
		return this.methodCallsByFile;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.visitors.PPAVisitor#postVisit(de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor, org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, int, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache)
	 */
	@Override
	public void postVisit(@NotNull final PPATypeVisitor ppaVisitor, @NotNull final CompilationUnit cu,
	                      @NotNull final ASTNode node, final JavaElementLocation<JavaClassDefinition> classContext,
	                      final JavaElementLocation<JavaMethodDefinition> methodContext, @NonNegative final int currentLine,
	                      @NotNull final JavaElementCache elementCache) {
		
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
		
		JavaElementDefinition parent = classContext.getElement();
		if (methodContext != null) {
			parent = methodContext.getElement();
		}
		
		String filename = ppaVisitor.getRelativeFilePath();
		
		JavaElementLocation<JavaMethodCall> javaMethodCall = elementCache.getMethodCall(
		                                                                                calledObject + "." + methodName, arguments,
		                                                                                filename, parent, thisLine, thisLine, position);
		
		if (methodContext != null) {
			JavaElementRelation relation = javaMethodCall.getElement().addParent(methodContext.getElement());
			javaMethodCall.setParentRelation(relation);
		} else if (classContext != null) {
			JavaElementRelation relation = javaMethodCall.getElement().addParent(classContext.getElement());
			javaMethodCall.setParentRelation(relation);
		}
		
		if (!this.methodCallsByFile.containsKey(filename)) {
			this.methodCallsByFile.put(filename, new LinkedList<JavaMethodCall>());
		}
		this.methodCallsByFile.get(filename).add(javaMethodCall.getElement());
		
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ppa.visitors.PPAVisitor#preVisit(de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor, org.eclipse.jdt.core.dom.CompilationUnit, org.eclipse.jdt.core.dom.ASTNode, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation, int, int, de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache)
	 */
	@Override
	public void preVisit(@NotNull final PPATypeVisitor ppaVisitor, @NotNull final CompilationUnit cu,
	                     @NotNull final ASTNode node, final JavaElementLocation<JavaClassDefinition> classContext,
	                     final JavaElementLocation<JavaMethodDefinition> methodContext, @NonNegative final int currentLine,
	                     @NonNegative final int endLine, @NotNull final JavaElementCache elementCache) {
	}
	
}
