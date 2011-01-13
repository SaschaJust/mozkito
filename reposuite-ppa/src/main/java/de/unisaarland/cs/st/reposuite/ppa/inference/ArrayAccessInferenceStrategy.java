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

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractInferenceStrategy;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PPABindingsUtil;
import org.eclipse.jdt.core.dom.PPAEngine;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;

public class ArrayAccessInferenceStrategy extends AbstractInferenceStrategy {
	
	public ArrayAccessInferenceStrategy(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super(indexer, ppaEngine);
	}
	
	@Override
	public void inferTypes(final ASTNode node) {
		ArrayAccess aAccess = (ArrayAccess) node;
		// Expression exp1 = aAccess.getArray();
		Expression exp2 = aAccess.getIndex();
		
		if (!indexer.isSafe(exp2)) {
			ITypeBinding oldBinding = exp2.resolveTypeBinding();
			ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("int", exp2);
			TypeFact tFact = new TypeFact(indexer.getMainIndex(exp2), oldBinding, TypeFact.UNKNOWN,
					newBinding, TypeFact.SUBTYPE, TypeFact.ARRAY_STRATEGY);
			ppaEngine.reportTypeFact(tFact);
		}
	}
	
	@Override
	public boolean isSafe(final ASTNode node) {
		ArrayAccess aAccess = (ArrayAccess) node;
		Expression exp1 = aAccess.getArray();
		Expression exp2 = aAccess.getIndex();
		
		return (!indexer.isIndexable(exp1) || indexer.isSafe(exp1))
		&& (!indexer.isIndexable(exp2) || indexer.isSafe(exp2));
	}
	
	@Override
	public void makeSafe(final ASTNode node, final TypeFact typeFact) {
		ArrayAccess aAccess = (ArrayAccess) node;
		Expression exp1 = aAccess.getArray();
		boolean isSafe = !PPABindingsUtil.isUnknownType(exp1.resolveTypeBinding())
		&& (indexer.isSafe(exp1));
		ITypeBinding factType = typeFact != null ? typeFact.getNewType() : null;
		if (!isSafe && (factType != null)) {
			ITypeBinding newBinding = ppaEngine.getRegistry().getArrayTypeBinding(factType, exp1);
			ITypeBinding oldBinding = exp1.resolveTypeBinding();
			TypeFact newTypeFact = new TypeFact(indexer.getMainIndex(exp1), oldBinding,
					TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
			ppaEngine.reportTypeFact(newTypeFact);
		}
	}
	
	@Override
	public void makeSafeSecondary(final ASTNode node, final TypeFact typeFact) {
	}
	
}
