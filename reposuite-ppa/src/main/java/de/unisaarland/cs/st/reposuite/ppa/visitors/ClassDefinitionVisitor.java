package de.unisaarland.cs.st.reposuite.ppa.visitors;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;

public class ClassDefinitionVisitor extends ASTVisitor {
	
	private final Set<JavaClassDefinition> classDefs = new HashSet<JavaClassDefinition>();
	private File                           sourceFile;
	private String                         packageName;
	private Stack<JavaClassDefinition>     stack;
	
	
	@Override
	public void preVisit(final ASTNode node) {
		
	}

}
