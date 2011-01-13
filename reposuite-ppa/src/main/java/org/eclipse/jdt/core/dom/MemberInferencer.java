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
package org.eclipse.jdt.core.dom;

import java.util.Set;

import org.eclipse.jdt.internal.compiler.lookup.PPATypeBindingOptions;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAASTUtil;

public class MemberInferencer {
	
	private final PPAEngine engine;
	private final PPAIndexer indexer;
	private final PPATypeRegistry typeRegistry;
	private final PPADefaultBindingResolver resolver;
	private final Set<ASTNode> ambNodes;
	
	public MemberInferencer(final PPAIndexer indexer,
			final PPADefaultBindingResolver resolver, final PPAEngine engine) {
		this.indexer = indexer;
		this.engine = engine;
		this.typeRegistry = engine.getRegistry();
		this.resolver = resolver;
		this.ambNodes = engine.getAmbiguousNodes();
	}
	
	private ITypeBinding getProbableContainerType(final ASTNode container) {
		ITypeBinding binding = PPABindingsUtil
		.getFirstFieldContainerMissingSuperType(container);
		
		if (PPABindingsUtil.isUnknownType(binding)) {
			// This is a workaround if the container is part of a qualified name.
			// The container may be obtained by looking at the declaring class of the field being fixed.
			// This will always return UNKNOWNP.UNKNOWN, which is obviously not good!!!
			if (container instanceof Name) {
				Name name = (Name) container;
				IBinding iBinding = name.resolveBinding();
				if (iBinding instanceof IVariableBinding) {
					IVariableBinding varBinding = (IVariableBinding)iBinding;
					binding = varBinding.getType();
				}
				
			}
		}
		
		return binding;
	}
	
	private boolean isField(final IBinding binding) {
		boolean isField = false;
		
		if ((binding != null) && (binding instanceof IVariableBinding)) {
			isField = ((IVariableBinding) binding).isField();
		}
		
		return isField;
	}
	
	private boolean isMethod(final IBinding binding) {
		return (binding != null) && (binding instanceof IMethodBinding);
	}
	
	private boolean isUnknownField(final IBinding binding) {
		boolean unknownField = false;
		if ((binding != null) && (binding instanceof IVariableBinding)) {
			IVariableBinding varBinding = (IVariableBinding) binding;
			unknownField = varBinding.isField()
			&& varBinding.getDeclaringClass().getQualifiedName()
			.equals(PPATypeRegistry.UNKNOWN_CLASS_FQN);
		}
		
		return unknownField;
	}
	
	private boolean isUnknownMethod(final IBinding binding) {
		boolean unknownMethod = false;
		
		if (isMethod(binding)) {
			IMethodBinding mBinding = (IMethodBinding) binding;
			ITypeBinding tBinding = mBinding.getReturnType();
			unknownMethod = PPABindingsUtil.isUnknownType(tBinding);
		}
		
		return unknownMethod;
	}
	
	private void processConstructors() {
		for (ASTNode node : this.ambNodes) {
			if (node instanceof ClassInstanceCreation) {
				ClassInstanceCreation cic = (ClassInstanceCreation) node;
				PPABindingsUtil.fixConstructor(cic, this.typeRegistry, this.resolver,
						this.indexer, this.engine, true, false);
			}
		}
	}
	
	private void processFields() {
		for (ASTNode node : this.ambNodes) {
			if (node instanceof SimpleName) {
				SimpleName sName = (SimpleName) node;
				IBinding binding = sName.resolveBinding();
				if (isUnknownField(binding)) {
					tryToFixField(sName, (IVariableBinding) binding);
				}
			}
		}
	}
	
	public void processMembers() {
		processFields();
		processMethods();
		processConstructors();
	}
	
	private void processMethods() {
		for (ASTNode node : this.ambNodes) {
			if (node instanceof SimpleName) {
				SimpleName sName = (SimpleName) node;
				IBinding binding = sName.resolveBinding();
				if (isUnknownMethod(binding)) {
					PPABindingsUtil.fixMethod(sName, this.typeRegistry, this.resolver,
							this.indexer, this.engine);
				}
			}
		}
	}
	
	private void tryToFixField(final SimpleName name, final IVariableBinding binding) {
		ASTNode container = PPAASTUtil.getFieldContainer(name, true, false);
		ITypeBinding probableContainerType = getProbableContainerType(container);
		
		if ((probableContainerType != null)
				&& !probableContainerType.getQualifiedName().equals(
						PPATypeRegistry.UNKNOWN_CLASS_FQN)) {
			IVariableBinding newFieldBinding = this.typeRegistry.getFieldBinding(
					binding.getName(), probableContainerType,
					binding.getType(), this.resolver);
			this.resolver.fixFieldBinding(name, newFieldBinding);
		} else if ((probableContainerType == null) && (container instanceof Name)) {
			Name containerName = (Name) container;
			if (!isField(containerName.resolveBinding())) {
				String fqn = containerName.getFullyQualifiedName();
				ITypeBinding containerType = this.typeRegistry.getTypeBinding(
						(CompilationUnit) PPAASTUtil.getSpecificParentType(
								container, ASTNode.COMPILATION_UNIT), fqn,
								this.resolver, false, PPATypeBindingOptions.parseOptions(containerName));
				IVariableBinding newFieldBinding = this.typeRegistry
				.getFieldBinding(binding.getName(), containerType,
						binding.getType(), this.resolver);
				this.resolver.fixFieldBinding(name, newFieldBinding);
			} else {
				// We just uncovered a wrong container!
				// TODO Find a new suitable type
				// TODO Report this new type
			}
		}
	}
	
}
