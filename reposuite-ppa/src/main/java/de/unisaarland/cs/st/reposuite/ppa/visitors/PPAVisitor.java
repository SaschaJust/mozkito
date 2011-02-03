package de.unisaarland.cs.st.reposuite.ppa.visitors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 * @author kim
 * 
 */
public interface PPAVisitor {
	
	/**
	 * @param cu
	 * @param node
	 * @param classContext
	 * @param methodContext
	 *            (might be null)
	 */
	void endVisit(@NotNull PPATypeVisitor ppaVisitor, @NotNull CompilationUnit cu, @NotNull ASTNode node,
			@NotNull Tuple<JavaClassDefinition, Integer> classContext,
	        Tuple<JavaMethodDefinition, Integer> methodContext, @NotNull JavaElementCache elementCache);
	
	/**
	 * @param cu
	 * @param node
	 * @param classContext
	 *            (might be null)
	 * @param methodContext
	 *            (might be null)
	 */
	void postVisit(@NotNull PPATypeVisitor ppaVisitor, @NotNull CompilationUnit cu, @NotNull ASTNode node,
			Tuple<JavaClassDefinition, Integer> classContext, Tuple<JavaMethodDefinition, Integer> methodContext,
	        @NonNegative int currentLine, @NotNull JavaElementCache elementCache);
	
	/**
	 * @param cu
	 * @param node
	 * @param classContext
	 *            (might be null)
	 * @param methodContext
	 *            (might be null)
	 */
	void preVisit(@NotNull PPATypeVisitor ppaVisitor, @NotNull CompilationUnit cu, @NotNull ASTNode node,
			Tuple<JavaClassDefinition, Integer> classContext, Tuple<JavaMethodDefinition, Integer> methodContext,
	        @NonNegative int currentLine, @NonNegative int endLine, @NotNull JavaElementCache elementCache);
	
}
