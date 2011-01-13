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
package de.unisaarland.cs.st.reposuite.ppa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.SimpleName;

import de.unisaarland.cs.st.reposuite.ppa.inference.ArrayAccessInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.AssignInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.BinaryInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.ConditionInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.ConstructorInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.FieldInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.MethodInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.PostfixInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.PrefixInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.QNameInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.ReturnInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.TypeInferenceStrategy;
import de.unisaarland.cs.st.reposuite.ppa.inference.VariableDeclarationInferenceStrategy;

public class PPAIndexer {
	
	public final static int FIELD_TYPE = 10001;
	
	public final static int LOCAL_TYPE = 10002;
	
	public final static int METHOD_TYPE = 10003;
	
	public final static int TYPE_TYPE = 10004;
	
	private final PPAEngine ppaEngine;
	
	private final Map<Integer, TypeInferenceStrategy> strategies = new HashMap<Integer, TypeInferenceStrategy>();
	
	public PPAIndexer(final PPAEngine ppaEngine) {
		this.ppaEngine = ppaEngine;
		initStrategies();
	}
	
	public List<PPAIndex> getAllIndexes(final ASTNode node) {
		List<PPAIndex> indexes = new ArrayList<PPAIndex>();
		indexes.add(getMainIndex(node));
		indexes.addAll(getSecondaryIndexes(node));
		return indexes;
	}
	
	public PPAIndex getMainIndex(final ASTNode node) {
		PPAIndex index = null;
		
		int type = getNodeType(node);
		
		if (strategies.containsKey(type)) {
			index = strategies.get(type).getMainIndex(node);
		}
		
		assert index != null;
		
		return index;
	}
	
	public int getNodeType(final ASTNode node) {
		int type = node.getNodeType();
		
		if (type == ASTNode.SIMPLE_NAME) {
			SimpleName sName = (SimpleName) node;
			IBinding binding = sName.resolveBinding();
			
			if (binding != null) {
				if (binding instanceof IVariableBinding) {
					IVariableBinding varBinding = (IVariableBinding) binding;
					if (varBinding.isField()) {
						type = FIELD_TYPE;
					} else {
						type = LOCAL_TYPE;
					}
				} else if (binding instanceof IMethodBinding) {
					type = METHOD_TYPE;
				} else if (binding instanceof ITypeBinding) {
					type = TYPE_TYPE;
				}
			}
		}
		
		return type;
	}
	
	public List<PPAIndex> getSecondaryIndexes(final ASTNode node) {
		List<PPAIndex> indexes = null;
		
		int type = getNodeType(node);
		
		if (strategies.containsKey(type)) {
			indexes = strategies.get(type).getSecondaryIndexes(node);
		}
		
		assert indexes != null;
		
		return indexes;
	}
	
	public boolean hasDeclaration(final ASTNode node) {
		boolean isSafe = true;
		
		if (node != null) {
			int type = getNodeType(node);
			
			if (strategies.containsKey(type)) {
				isSafe = strategies.get(type).hasDeclaration(node);
			}
		}
		return isSafe;
	}
	
	public void inferTypes(final ASTNode node) {
		int type = getNodeType(node);
		
		if (strategies.containsKey(type)) {
			strategies.get(type).inferTypes(node);
		} else {
			assert false;
		}
	}
	
	private void initStrategies() {
		strategies.put(FIELD_TYPE, new FieldInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.ASSIGNMENT, new AssignInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.QUALIFIED_NAME, new QNameInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.RETURN_STATEMENT, new ReturnInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.VARIABLE_DECLARATION_FRAGMENT,
				new VariableDeclarationInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.METHOD_INVOCATION, new MethodInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.SUPER_METHOD_INVOCATION, new MethodInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.FOR_STATEMENT, new ConditionInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.WHILE_STATEMENT, new ConditionInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.DO_STATEMENT, new ConditionInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.IF_STATEMENT, new ConditionInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.INFIX_EXPRESSION, new BinaryInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.ARRAY_ACCESS, new ArrayAccessInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.PREFIX_EXPRESSION, new PrefixInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.POSTFIX_EXPRESSION, new PostfixInferenceStrategy(this, ppaEngine));
		strategies.put(ASTNode.CLASS_INSTANCE_CREATION, new ConstructorInferenceStrategy(this,
				ppaEngine));
	}
	
	public boolean isIndexable(final ASTNode node) {
		int type = getNodeType(node);
		
		return strategies.containsKey(type);
	}
	
	public boolean isSafe(final ASTNode node) {
		boolean isSafe = true;
		
		if (node != null) {
			int type = getNodeType(node);
			
			if (strategies.containsKey(type)) {
				isSafe = strategies.get(type).isSafe(node);
			}
		}
		return isSafe;
	}
	
	public void makeSafe(final ASTNode node, final TypeFact typeFact) {
		int type = getNodeType(node);
		
		if (strategies.containsKey(type)) {
			TypeInferenceStrategy strategy = strategies.get(type);
			if (!strategy.isSafe(node)) {
				strategies.get(type).makeSafe(node, typeFact);
			}
		} else {
			assert false;
		}
	}
	
	public void makeSafeSecondary(final ASTNode node, final TypeFact typeFact) {
		int type = getNodeType(node);
		
		if (strategies.containsKey(type)) {
			strategies.get(type).makeSafeSecondary(node, typeFact);
		} else {
			assert false;
		}
	}
	
}
