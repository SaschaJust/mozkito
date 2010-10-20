package de.unisaarland.cs.st.reposuite.ppa;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public interface PPAVisitor {
	
	void endVisit(CompilationUnit cu, ASTNode node, ClassContext peek);
	
	void postVisit(CompilationUnit cu, ASTNode node, ClassContext context);
	
	void preVisit(CompilationUnit cu, ASTNode node, ClassContext context);
	
}
