package org.eclipse.jdt.core.dom;

import java.util.List;

import de.unisaarland.cs.st.reposuite.ppa.utils.PPAASTUtil;

public class MethodInvocationUtil {
	@SuppressWarnings("unchecked")
	public static List getArguments(final ASTNode node) {
		List arguments = null;
		if (node instanceof MethodInvocation) {
			arguments = ((MethodInvocation)node).arguments();
		} else if (node instanceof SuperMethodInvocation) {
			arguments = ((SuperMethodInvocation)node).arguments();
		}
		return arguments;
	}
	
	public static ASTNode getContainer(final ASTNode node) {
		ASTNode container = null;
		if (node instanceof MethodInvocation) {
			container = PPAASTUtil.getContainer((MethodInvocation)node);
		} else if (node instanceof SuperMethodInvocation) {
			container = PPAASTUtil.getSpecificParentType(node, ASTNode.TYPE_DECLARATION);
			if (container != null) {
				TypeDeclaration td = (TypeDeclaration) container;
				container = td.getSuperclassType();
			}
		}
		return container;
	}
	
	public static IMethodBinding getMethodBinding(final ASTNode node) {
		IMethodBinding methodBinding = null;
		if (node instanceof MethodInvocation) {
			methodBinding = ((MethodInvocation)node).resolveMethodBinding();
		} else if (node instanceof SuperMethodInvocation) {
			methodBinding = ((SuperMethodInvocation)node).resolveMethodBinding();
		}
		return methodBinding;
	}
	
	public static SimpleName getName(final ASTNode node) {
		SimpleName sName = null;
		if (node instanceof MethodInvocation) {
			sName = ((MethodInvocation)node).getName();
		} else if (node instanceof SuperMethodInvocation) {
			sName = ((SuperMethodInvocation)node).getName();
		}
		return sName;
	}
}
