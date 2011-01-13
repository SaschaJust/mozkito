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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PostfixExpression;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndex;
import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;

public class PostfixInferenceStrategy extends AbstractInferenceStrategy {
	
	public PostfixInferenceStrategy(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super(indexer, ppaEngine);
	}
	
	@Override
	public List<PPAIndex> getSecondaryIndexes(final ASTNode node) {
		List<PPAIndex> list = super.getSecondaryIndexes(node);
		PostfixExpression prefix = (PostfixExpression) node;
		Expression exp = prefix.getOperand();
		if (indexer.isIndexable(exp)) {
			list.add(indexer.getMainIndex(exp));
		}
		return list;
	}
	
	@Override
	public void inferTypes(final ASTNode node) {
		processOperators((PostfixExpression) node, null);
	}
	
	@Override
	public boolean isSafe(final ASTNode node) {
		PostfixExpression prefix = (PostfixExpression) node;
		Expression exp = prefix.getOperand();
		return !indexer.isIndexable(exp) || indexer.isSafe(exp);
	}
	
	@Override
	public void makeSafe(final ASTNode node, final TypeFact typeFact) {
		processOperators((PostfixExpression) node, typeFact);
	}
	
	@Override
	public void makeSafeSecondary(final ASTNode node, final TypeFact typeFact) {
		processOperators((PostfixExpression) node, typeFact);
	}
	
	private void processOperators(final PostfixExpression node, final TypeFact newFact) {
		ITypeBinding newType = null;
		Expression exp = node.getOperand();
		boolean isPrimary = (newFact != null)
		&& indexer.getMainIndex(node).equals(newFact.getIndex());
		boolean isSafe = indexer.isSafe(exp) || ((newFact != null) && !isPrimary);
		
		if (isPrimary && !isSafe) {
			ITypeBinding newBinding = newFact.getNewType();
			ITypeBinding oldBinding = exp.resolveTypeBinding();
			TypeFact typeFact = new TypeFact(indexer.getMainIndex(exp), oldBinding,
					TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.UNARY_STRATEGY);
			ppaEngine.reportTypeFact(typeFact);
			newType = newBinding;
		} else if (!isSafe) {
			ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("int", exp);
			ITypeBinding oldBinding = exp.resolveTypeBinding();
			TypeFact typeFact = new TypeFact(indexer.getMainIndex(exp), oldBinding,
					TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.UNARY_STRATEGY);
			ppaEngine.reportTypeFact(typeFact);
			newType = newBinding;
		}
		
		if ((newType != null) && !isPrimary) {
			ITypeBinding oldBinding = node.resolveTypeBinding();
			TypeFact typeFact = new TypeFact(indexer.getMainIndex(node), oldBinding,
					TypeFact.UNKNOWN, newType, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
			ppaEngine.reportTypeFact(typeFact);
			// report new type for this expression.
			// fix this expression.
		} else if (isPrimary) {
			if (newType != null) {
				ppaEngine.getRegistry().fixUnary(node, newType);
			} else if (newFact != null) {
				ppaEngine.getRegistry().fixUnary(node, newFact.getNewType());
			}
		}
	}
	
}
