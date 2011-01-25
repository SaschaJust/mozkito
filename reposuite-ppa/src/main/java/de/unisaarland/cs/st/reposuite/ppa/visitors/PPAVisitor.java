package de.unisaarland.cs.st.reposuite.ppa.visitors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
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
			@NotNull JavaClassDefinition classContext, JavaMethodDefinition methodContext);
	
	/**
	 * @param cu
	 * @param node
	 * @param classContext
	 *            (might be null)
	 * @param methodContext
	 *            (might be null)
	 */
	void postVisit(@NotNull PPATypeVisitor ppaVisitor, @NotNull CompilationUnit cu, @NotNull ASTNode node,
			JavaClassDefinition classContext, JavaMethodDefinition methodContext, @NonNegative int currentLine);
	
	/**
	 * @param cu
	 * @param node
	 * @param classContext
	 *            (might be null)
	 * @param methodContext
	 *            (might be null)
	 */
	void preVisit(@NotNull PPATypeVisitor ppaVisitor, @NotNull CompilationUnit cu, @NotNull ASTNode node,
			JavaClassDefinition classContext, JavaMethodDefinition methodContext, @NonNegative int currentLine,
			@NonNegative int endLine);
	
}
