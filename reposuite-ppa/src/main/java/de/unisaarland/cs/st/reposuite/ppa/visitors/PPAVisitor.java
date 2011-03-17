package de.unisaarland.cs.st.reposuite.ppa.visitors;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;

/**
 * The Interface PPAVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface PPAVisitor {
	
	/**
	 * End visit.
	 * 
	 * @param ppaVisitor
	 *            the ppa visitor
	 * @param cu
	 *            the cu
	 * @param node
	 *            the node
	 * @param classContext
	 *            the class context
	 * @param methodContext
	 *            (might be null)
	 * @param elementCache
	 *            the element cache
	 */
	void endVisit(@NotNull PPATypeVisitor ppaVisitor,
	              @NotNull CompilationUnit cu,
	              @NotNull ASTNode node,
	              @NotNull JavaElementLocation<JavaClassDefinition> classContext,
	              JavaElementLocation<JavaMethodDefinition> methodContext,
	              @NotNull JavaElementCache elementCache);
	
	/**
	 * Post visit.
	 * 
	 * @param ppaVisitor
	 *            the ppa visitor
	 * @param cu
	 *            the cu
	 * @param node
	 *            the node
	 * @param classContext
	 *            (might be null)
	 * @param methodContext
	 *            (might be null)
	 * @param currentLine
	 *            the current line
	 * @param elementCache
	 *            the element cache
	 */
	void postVisit(@NotNull PPATypeVisitor ppaVisitor,
	               @NotNull CompilationUnit cu,
	               @NotNull ASTNode node,
	               JavaElementLocation<JavaClassDefinition> classContext,
	               JavaElementLocation<JavaMethodDefinition> methodContext,
	               @NotNegative int currentLine,
	               @NotNull JavaElementCache elementCache);
	
	/**
	 * Pre visit.
	 * 
	 * @param ppaVisitor
	 *            the ppa visitor
	 * @param cu
	 *            the cu
	 * @param node
	 *            the node
	 * @param classContext
	 *            (might be null)
	 * @param methodContext
	 *            (might be null)
	 * @param currentLine
	 *            the current line
	 * @param endLine
	 *            the end line
	 * @param elementCache
	 *            the element cache
	 */
	void preVisit(@NotNull PPATypeVisitor ppaVisitor,
	              @NotNull CompilationUnit cu,
	              @NotNull ASTNode node,
	              JavaElementLocation<JavaClassDefinition> classContext,
	              JavaElementLocation<JavaMethodDefinition> methodContext,
	              @NotNegative int currentLine,
	              @NotNegative int endLine,
	              @NotNull JavaElementCache elementCache);
	
}
