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
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PPABindingsUtil;
import org.eclipse.jdt.core.dom.PPAEngine;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndex;
import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;

public class BinaryInferenceStrategy extends AbstractInferenceStrategy {
	
	public BinaryInferenceStrategy(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super(indexer, ppaEngine);
	}
	
	@Override
	public PPAIndex getMainIndex(final ASTNode node) {
		return super.getMainIndex(node);
	}
	
	@Override
	public List<PPAIndex> getSecondaryIndexes(final ASTNode node) {
		List<PPAIndex> list = super.getSecondaryIndexes(node);
		InfixExpression infix = (InfixExpression) node;
		Expression left = infix.getLeftOperand();
		Expression right = infix.getRightOperand();
		if (indexer.isIndexable(left)) {
			list.add(indexer.getMainIndex(left));
		}
		
		if (indexer.isIndexable(right)) {
			list.add(indexer.getMainIndex(right));
		}
		
		return list;
	}
	
	@Override
	public void inferTypes(final ASTNode node) {
		processOperators(node, null);
	}
	
	private boolean isBoolean(final Expression exp, final boolean isSafe) {
		boolean isBoolean = false;
		if (isSafe) {
			ITypeBinding binding = exp.resolveTypeBinding();
			if ((binding != null) && binding.getQualifiedName().equals("boolean")) {
				isBoolean = true;
			}
		}
		return isBoolean;
	}
	
	private boolean isLeftIndex(final ASTNode node, final TypeFact fact) {
		boolean isLeft = false;
		InfixExpression infix = (InfixExpression) node;
		Expression left = infix.getLeftOperand();
		
		if (indexer.isIndexable(left)) {
			isLeft = indexer.getMainIndex(left).equals(fact.getIndex());
		}
		
		return isLeft;
	}
	
	@Override
	public boolean isSafe(final ASTNode node) {
		InfixExpression infix = (InfixExpression) node;
		boolean leftSafe = true;
		boolean rightSafe = true;
		
		Expression left = infix.getLeftOperand();
		Expression right = infix.getRightOperand();
		
		leftSafe = indexer.isSafe(left);
		
		rightSafe = indexer.isSafe(right);
		
		return leftSafe && rightSafe;
	}
	
	private boolean isSecondary(final ASTNode node, final TypeFact fact) {
		return getSecondaryIndexes(node).contains(fact.getIndex());
	}
	
	@Override
	public void makeSafe(final ASTNode node, final TypeFact typeFact) {
		processOperators(node, typeFact);
	}
	
	@Override
	public void makeSafeSecondary(final ASTNode node, final TypeFact typeFact) {
		processOperators(node, typeFact);
	}
	
	private void processOperators(final ASTNode node, final TypeFact newFact) {
		InfixExpression infix = (InfixExpression) node;
		InfixExpression.Operator operator = infix.getOperator();
		
		//		if (node.toString().equals("falseTb == ShortBinding")) {
		//			System.out.println();
		//		}
		
		Expression left = infix.getLeftOperand();
		Expression right = infix.getRightOperand();
		boolean isSecondaryIndex = (newFact != null) && isSecondary(node, newFact);
		boolean isPrimaryIndex = (newFact != null) && !isSecondaryIndex;
		
		boolean leftSafe = !PPABindingsUtil.isUnknownType(left.resolveTypeBinding())
		&& (indexer.isSafe(left) || (isSecondaryIndex && isLeftIndex(node, newFact)));
		boolean rightSafe = !PPABindingsUtil.isUnknownType(right.resolveTypeBinding())
		&& (indexer.isSafe(right) || (isSecondaryIndex && !isLeftIndex(node, newFact)));
		boolean leftBoolean = isBoolean(left, leftSafe);
		boolean rightBoolean = isBoolean(right, rightSafe);
		ITypeBinding newType = null;
		ITypeBinding factType = newFact != null ? newFact.getNewType() : null;
		
		boolean isNewType = true;
		
		// && and ||
		if ((operator == InfixExpression.Operator.CONDITIONAL_AND)
				|| (operator == InfixExpression.Operator.CONDITIONAL_OR)) {
			if (!leftSafe) {
				ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("boolean",
						left);
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
			}
			
			if (!rightSafe) {
				ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("boolean",
						right);
				ITypeBinding oldBinding = right.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
			}
		}
		
		// & and |
		else if ((operator == InfixExpression.Operator.AND)
				|| (operator == InfixExpression.Operator.CONDITIONAL_OR)
				|| (operator == InfixExpression.Operator.XOR)) {
			if (leftBoolean && !rightSafe) {
				ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("boolean",
						right);
				ITypeBinding oldBinding = right.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			} else if (rightBoolean && !leftSafe) {
				ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("boolean",
						left);
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			} else if (leftSafe && !rightSafe) {
				ITypeBinding newBinding = left.resolveTypeBinding();
				ITypeBinding oldBinding = right.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			} else if (rightSafe && !leftSafe) {
				ITypeBinding newBinding = right.resolveTypeBinding();
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			} else if (!rightSafe && !leftSafe && isPrimaryIndex) {
				ITypeBinding newBinding = factType;
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newBinding = factType;
				oldBinding = right.resolveTypeBinding();
				typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding, TypeFact.UNKNOWN,
						newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			}
		}
		// << >> >>>
		else if ((operator == InfixExpression.Operator.LEFT_SHIFT)
				|| (operator == InfixExpression.Operator.RIGHT_SHIFT_SIGNED)
				|| (operator == InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED)) {
			if (!leftSafe) {
				ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("int", left);
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
			}
			
			if (!rightSafe) {
				ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("int", right);
				ITypeBinding oldBinding = right.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			}
		}
		// - / * % < <= > >=
		else if ((operator == InfixExpression.Operator.MINUS)
				|| (operator == InfixExpression.Operator.DIVIDE)
				|| (operator == InfixExpression.Operator.REMAINDER)
				|| (operator == InfixExpression.Operator.TIMES)
				|| (operator == InfixExpression.Operator.LESS)
				|| (operator == InfixExpression.Operator.LESS_EQUALS)
				|| (operator == InfixExpression.Operator.GREATER)
				|| (operator == InfixExpression.Operator.GREATER_EQUALS)) {
			if (leftSafe && !rightSafe) {
				ITypeBinding newBinding = left.resolveTypeBinding();
				ITypeBinding oldBinding = right.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			} else if (rightSafe && !leftSafe) {
				ITypeBinding newBinding = right.resolveTypeBinding();
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			} else if (!rightSafe && !leftSafe && !isPrimaryIndex) {
				ITypeBinding newBinding = ppaEngine.getRegistry().getPrimitiveBinding("int", left);
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				
				newBinding = ppaEngine.getRegistry().getPrimitiveBinding("int", right);
				oldBinding = right.resolveTypeBinding();
				typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding, TypeFact.UNKNOWN,
						newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			} else if (!rightSafe && !leftSafe && isPrimaryIndex) {
				ITypeBinding newBinding = factType;
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				
				newBinding = factType;
				oldBinding = right.resolveTypeBinding();
				typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding, TypeFact.UNKNOWN,
						newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				newType = newBinding;
			}
		}
		// == !=
		else if ((operator == InfixExpression.Operator.EQUALS)
				|| (operator == InfixExpression.Operator.NOT_EQUALS)) {
			if (leftSafe && !rightSafe) {
				ITypeBinding newBinding = left.resolveTypeBinding();
				ITypeBinding oldBinding = right.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
			} else if (rightSafe && !leftSafe) {
				ITypeBinding newBinding = right.resolveTypeBinding();
				ITypeBinding oldBinding = left.resolveTypeBinding();
				TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
						TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
				isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
			}
		}
		// +
		else if (operator == InfixExpression.Operator.PLUS) {
			if (isPrimaryIndex && !factType.isPrimitive()) {
				ITypeBinding newBinding = factType;
				if (!leftSafe) {
					
					ITypeBinding oldBinding = left.resolveTypeBinding();
					TypeFact typeFact = new TypeFact(indexer.getMainIndex(left), oldBinding,
							TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE,
							TypeFact.BINARY_STRATEGY);
					isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				}
				if (!rightSafe) {
					newBinding = factType;
					ITypeBinding oldBinding = right.resolveTypeBinding();
					TypeFact typeFact = new TypeFact(indexer.getMainIndex(right), oldBinding,
							TypeFact.UNKNOWN, newBinding, TypeFact.SUBTYPE,
							TypeFact.BINARY_STRATEGY);
					isNewType = isNewType && ppaEngine.reportTypeFact(typeFact);
				}
				newType = newBinding;
			}
		}
		
		if ((newType != null) && !isPrimaryIndex && isNewType) {
			ITypeBinding oldBinding = infix.resolveTypeBinding();
			TypeFact typeFact = new TypeFact(indexer.getMainIndex(node), oldBinding,
					TypeFact.UNKNOWN, newType, TypeFact.SUBTYPE, TypeFact.BINARY_STRATEGY);
			ppaEngine.reportTypeFact(typeFact);
			// report new type for this expression.
			// fix this expression.
		} else if (isPrimaryIndex) {
			if ((newType != null) && isNewType) {
				ppaEngine.getRegistry().fixBinary(node, newType);
			} else if (factType != null) {
				ppaEngine.getRegistry().fixBinary(node, factType);
			}
		}
	}
	
}
