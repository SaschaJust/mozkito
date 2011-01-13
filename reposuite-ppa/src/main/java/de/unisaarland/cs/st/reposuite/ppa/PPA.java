package de.unisaarland.cs.st.reposuite.ppa;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAVisitorWrapper;
import de.unisaarland.cs.st.reposuite.ppa.utils.RepoSuitePPAUtil;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import org.eclipse.jdt.core.dom.ASTParser;

/**
 * The Class PPA.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPA extends Thread {
	
	public static Set<JavaClassDefinition> getJavaClassDefinitions(final File sourceFile){
		//TODO implement
		return null;
	}
	
	public static Set<JavaElementDefinition> getJavaElementDefinitions(final File sourceFile){
		//TODO implement
		return null;
	}
	
	public static void parseCU(final CompilationUnit cu, final Set<ASTVisitor> visitors) {
		PPAVisitorWrapper visitor = new PPAVisitorWrapper(visitors);
		cu.accept(visitor);
	}
	
	public static void parseCU(final File sourceFile, final Set<ASTVisitor> visitors) throws IOException {
		CompilationUnit cu = RepoSuitePPAUtil.getCU(sourceFile);
		parseCU(cu, visitors);
	}
	
}
