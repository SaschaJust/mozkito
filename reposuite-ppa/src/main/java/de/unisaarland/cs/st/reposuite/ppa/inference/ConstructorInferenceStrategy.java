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
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PPABindingsUtil;
import org.eclipse.jdt.core.dom.PPAEngine;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndex;
import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;

public class ConstructorInferenceStrategy extends AbstractInferenceStrategy {
	
	public ConstructorInferenceStrategy(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super(indexer, ppaEngine);
	}
	
	@Override
	public List<PPAIndex> getSecondaryIndexes(final ASTNode node) {
		List<PPAIndex> indexes = super.getSecondaryIndexes(node);
		ClassInstanceCreation cic = (ClassInstanceCreation) node;
		// TODO (and to propagate to other parts of PPA
		// use of constructors for internal classes...
		//		ASTNode container = PPAASTUtil.getContainer(mi);
		//		if (indexer.isIndexable(container)) {
		//			indexes.add(indexer.getMainIndex(container));
		//		}
		
		for (Object arg : cic.arguments()) {
			ASTNode argNode = (ASTNode) arg;
			if (indexer.isIndexable(argNode)) {
				indexes.add(indexer.getMainIndex(argNode));
			}
		}
		
		return indexes;
	}
	
	@Override
	public boolean hasDeclaration(final ASTNode node) {
		ClassInstanceCreation cic = (ClassInstanceCreation) node;
		return !ppaEngine.getAmbiguousNodes().contains(cic);
	}
	
	@Override
	public void inferTypes(final ASTNode node) {
	}
	
	@Override
	public boolean isSafe(final ASTNode node) {
		boolean isSafe = hasDeclaration(node);
		
		if (!isSafe) {
			ClassInstanceCreation cic = (ClassInstanceCreation) node;
			IMethodBinding mBinding = cic.resolveConstructorBinding();
			if (mBinding == null) {
				System.out.println("Hello!");
				cic.resolveConstructorBinding();
			}
			ITypeBinding container = mBinding.getDeclaringClass();
			ITypeBinding returnType = mBinding.getReturnType();
			isSafe = (PPABindingsUtil.getSafetyValue(container) == PPABindingsUtil.FULL_TYPE)
			&& (PPABindingsUtil.getSafetyValue(returnType) > PPABindingsUtil.UNKNOWN_TYPE);
		}
		
		return isSafe;
	}
	
	@Override
	public void makeSafe(final ASTNode node, final TypeFact typeFact) {
	}
	
	@Override
	public void makeSafeSecondary(final ASTNode node, final TypeFact typeFact) {
		ClassInstanceCreation cic = (ClassInstanceCreation) node;
		PPABindingsUtil.fixConstructor(cic, ppaEngine.getRegistry(),
				getResolver(node), indexer, ppaEngine, !ppaEngine.isInMethodBindingPass(),
				ppaEngine.isInMethodBindingPass());
	}
	
}
