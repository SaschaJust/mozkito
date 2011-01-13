/*******************************************************************************
 * PPA - Partial Program Analysis for Java
 * Copyright (C) 2008 Barthelemy Dagenais
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.txt>
 *******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.inference;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractInferenceStrategy;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocationUtil;
import org.eclipse.jdt.core.dom.PPABindingsUtil;
import org.eclipse.jdt.core.dom.PPAEngine;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndex;
import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;

public class MethodInferenceStrategy extends AbstractInferenceStrategy {
	
	
	public MethodInferenceStrategy(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super(indexer, ppaEngine);
	}
	
	@Override
	public List<PPAIndex> getSecondaryIndexes(final ASTNode node) {
		List<PPAIndex> indexes = super.getSecondaryIndexes(node);
		ASTNode container = MethodInvocationUtil.getContainer(node);
		if ((container != null) && indexer.isIndexable(container)) {
			indexes.add(indexer.getMainIndex(container));
		}
		
		for (Object arg : MethodInvocationUtil.getArguments(node)) {
			ASTNode argNode = (ASTNode) arg;
			if (indexer.isIndexable(argNode)) {
				indexes.add(indexer.getMainIndex(argNode));
			}
		}
		
		return indexes;
	}
	
	@Override
	public boolean hasDeclaration(final ASTNode node) {
		return !ppaEngine.getAmbiguousNodes().contains(MethodInvocationUtil.getName(node));
	}
	
	@Override
	public void inferTypes(final ASTNode node) {
	}
	
	@Override
	public boolean isSafe(final ASTNode node) {
		boolean isSafe = hasDeclaration(node);
		
		if (!isSafe) {
			IMethodBinding mBinding = MethodInvocationUtil.getMethodBinding(node);
			ITypeBinding container = mBinding.getDeclaringClass();
			ITypeBinding returnType = mBinding.getReturnType();
			isSafe = (PPABindingsUtil.getSafetyValue(container) == PPABindingsUtil.FULL_TYPE) && (PPABindingsUtil.getSafetyValue(returnType) > PPABindingsUtil.UNKNOWN_TYPE);
		}
		
		return isSafe;
	}
	
	@Override
	public void makeSafe(final ASTNode node, final TypeFact typeFact) {
		ITypeBinding returnType = typeFact.getNewType();
		PPABindingsUtil.fixMethod(MethodInvocationUtil.getName(node), returnType, ppaEngine.getRegistry(), getResolver(node),
				indexer, ppaEngine, !ppaEngine.isInMethodBindingPass(), ppaEngine.isInMethodBindingPass());
	}
	
	@Override
	public void makeSafeSecondary(final ASTNode node, final TypeFact typeFact) {
		ITypeBinding returnType = MethodInvocationUtil.getMethodBinding(node).getReturnType();
		if (PPABindingsUtil.isUnknownType(returnType)) {
			PPABindingsUtil.fixMethod(MethodInvocationUtil.getName(node), null, ppaEngine.getRegistry(), getResolver(node), indexer,
					ppaEngine, !ppaEngine.isInMethodBindingPass(), ppaEngine.isInMethodBindingPass());
		} else {
			PPABindingsUtil.fixMethod(MethodInvocationUtil.getName(node), returnType, ppaEngine.getRegistry(), getResolver(node),
					indexer, ppaEngine, !ppaEngine.isInMethodBindingPass(), ppaEngine.isInMethodBindingPass());
		}
	}
	
}
