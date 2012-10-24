/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.codeanalysis.visitors;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.mozkito.codeanalysis.model.JavaElementLocation;
import org.mozkito.codeanalysis.model.JavaElementLocationSet;


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
	 *            the codeanalysis visitor
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
	              @NotNull JavaElementLocation classContext,
	              JavaElementLocation methodContext,
	              @NotNull JavaElementLocationSet elementCache);
	
	/**
	 * Post visit.
	 * 
	 * @param ppaVisitor
	 *            the codeanalysis visitor
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
	               JavaElementLocation classContext,
	               JavaElementLocation methodContext,
	               @NotNegative int currentLine,
	               @NotNull JavaElementLocationSet elementCache);
	
	/**
	 * Pre visit.
	 * 
	 * @param ppaVisitor
	 *            the codeanalysis visitor
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
	              JavaElementLocation classContext,
	              JavaElementLocation methodContext,
	              @NotNegative int currentLine,
	              @NotNegative int endLine,
	              @NotNull JavaElementLocationSet elementCache);
	
}
