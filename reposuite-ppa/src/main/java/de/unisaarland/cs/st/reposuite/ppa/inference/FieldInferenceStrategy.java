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
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PPABindingsUtil;
import org.eclipse.jdt.core.dom.PPADefaultBindingResolver;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.core.dom.SimpleName;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndex;
import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAASTUtil;

public class FieldInferenceStrategy extends AbstractInferenceStrategy {
	
	public FieldInferenceStrategy(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super(indexer, ppaEngine);
	}
	
	@Override
	public PPAIndex getMainIndex(final ASTNode node) {
		SimpleName sName = (SimpleName) node;
		IVariableBinding vBinding = (IVariableBinding) sName.resolveBinding();
		
		return new PPAIndex(vBinding);
	}
	
	@Override
	public List<PPAIndex> getSecondaryIndexes(final ASTNode node) {
		List<PPAIndex> indexes = super.getSecondaryIndexes(node);
		ASTNode fieldContainer = PPAASTUtil.getFieldContainer(node, true, false);
		
		if (indexer.isIndexable(fieldContainer)) {
			PPAIndex tempIndex = indexer.getMainIndex(fieldContainer);
			indexes.add(tempIndex);
		}
		
		return indexes;
	}
	
	@Override
	public boolean hasDeclaration(final ASTNode node) {
		return !ppaEngine.getAmbiguousNodes().contains(node);
	}
	
	@Override
	public void inferTypes(final ASTNode node) {
	}
	
	
	@Override
	public boolean isSafe(final ASTNode node) {
		boolean isSafe = hasDeclaration(node);
		
		if (!isSafe) {
			SimpleName sName = (SimpleName) node;
			IVariableBinding varBinding = (IVariableBinding) sName.resolveBinding();
			ITypeBinding container = varBinding.getDeclaringClass();
			isSafe = (container == null) || (!PPABindingsUtil.isMissingType(container) && !PPABindingsUtil.isUnknownType(container));
		}
		
		return isSafe;
	}
	
	
	@Override
    public void makeSafe(final ASTNode node, final TypeFact typeFact) {
		SimpleName sName = (SimpleName) node;
		PPATypeRegistry typeRegistry = ppaEngine.getRegistry();
		PPADefaultBindingResolver resolver = getResolver(sName);
		
		// The field's type changed.
		if (typeFact.getIndex().equals(getMainIndex(node))) {
			IVariableBinding varBinding = (IVariableBinding) sName.resolveBinding();
			ITypeBinding newType = typeFact.getNewType();
			IVariableBinding newBinding = typeRegistry.getFieldBindingWithType(
					varBinding.getName(), varBinding.getDeclaringClass(), newType, resolver);
			
			resolver.fixFieldBinding(sName, newBinding);
		}
		// The field's container changed.
		//		else {
		//
		//			//
		//
		//		}
	}
	
		
	@Override
	public void makeSafeSecondary(final ASTNode node, final TypeFact typeFact) {
		SimpleName sName = (SimpleName) node;
		PPATypeRegistry typeRegistry = ppaEngine.getRegistry();
		PPADefaultBindingResolver resolver = getResolver(sName);
		ASTNode newContainerNode = PPAASTUtil.getFieldContainer(node, true, false);
		ITypeBinding newContainer = PPABindingsUtil.getTypeBinding(newContainerNode);
		
		if (newContainer == null) {
			return;
		}
		
		IVariableBinding varBinding = (IVariableBinding) sName.resolveBinding();
		IVariableBinding newFieldBinding = null;
		
		if (PPABindingsUtil.isMissingType(newContainer)) {
			newFieldBinding = typeRegistry.getFieldBinding(varBinding.getName(), newContainer,
					varBinding.getType(), resolver);
		} else {
			// Maybe we can find the field declaration
			newFieldBinding = PPABindingsUtil.findFieldHierarchy(newContainer, sName.getFullyQualifiedName());
			if (newFieldBinding == null) {
				// We did not find the field in the container, try to find a suitable container (missing type)
				ITypeBinding tempContainer = PPABindingsUtil.getFirstMissingSuperType(newContainer);
				if (tempContainer != null) {
					newContainer = tempContainer;
					newFieldBinding = typeRegistry.getFieldBinding(varBinding.getName(), newContainer,
							varBinding.getType(), resolver);
				} else {
					newFieldBinding = typeRegistry.getFieldBinding(varBinding.getName(), newContainer,
							varBinding.getType(), resolver);
				}
			} else {
				// In case we found the field in a super type of the container.
				newContainer = newFieldBinding.getDeclaringClass();
			}
		}
		
		// Check field type
		ITypeBinding newFieldType = newFieldBinding.getType();
		ITypeBinding oldFieldType = varBinding.getType();
		if (!newFieldType.isEqualTo(oldFieldType)) {
			if (PPABindingsUtil.isSafer(newFieldType, oldFieldType)) {
				resolver.fixFieldBinding(sName, newFieldBinding);
				TypeFact tFact = new TypeFact(indexer.getMainIndex(sName), oldFieldType,
						TypeFact.UNKNOWN, newFieldType, TypeFact.EQUALS,
						TypeFact.FIELD_STRATEGY);
				ppaEngine.reportTypeFact(tFact);
				
			} else if (!PPABindingsUtil.isMissingType(newContainer)) {
				resolver.fixFieldBinding(sName, newFieldBinding);
				// This is the case where we found the field declaration and the field is "less" safe than the previous type.
				// XXX The oldType = Unknown is to ensure that the new type will be pushed.
				TypeFact tFact = new TypeFact(indexer.getMainIndex(sName), typeRegistry
						.getUnknownBinding(resolver), TypeFact.UNKNOWN, newFieldType,
						TypeFact.EQUALS, TypeFact.FIELD_STRATEGY);
				ppaEngine.reportTypeFact(tFact);
			} else {
				// This is an odd case: we found a ppa generated field, but the type we got before was safer.
				newFieldBinding = typeRegistry.getFieldBindingWithType(varBinding.getName(),
						newContainer, varBinding.getType(), resolver);
				resolver.fixFieldBinding(sName, newFieldBinding);
			}
		} else {
			resolver.fixFieldBinding(sName, newFieldBinding);
		}
		
	}
	
}
