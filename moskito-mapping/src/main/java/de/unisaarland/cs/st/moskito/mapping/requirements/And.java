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
package de.unisaarland.cs.st.moskito.mapping.requirements;

import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;

/**
 * The and expression evaluates to true if and only if both inner expressions evaluate to true. Evaluates to false
 * otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class And extends Expression {
	
	/** The expression1. */
	private final Expression expression1;
	
	/** The expression2. */
	private final Expression expression2;
	
	/**
	 * Instantiates a new and.
	 * 
	 * @param expression1
	 *            the expression1
	 * @param expression2
	 *            the expression2
	 */
	public And(final Expression expression1, final Expression expression2) {
		this.expression1 = expression1;
		this.expression2 = expression2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#check( java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public boolean check(final Class<? extends MappableEntity> target1,
	                     final Class<? extends MappableEntity> target2,
	                     final Index oneEquals) {
		return getExpression1().check(target1, target2, oneEquals)
		        && getExpression2().check(target1, target2, oneEquals);
	}
	
	/**
	 * Gets the expression1.
	 * 
	 * @return the first expression
	 */
	public Expression getExpression1() {
		return this.expression1;
	}
	
	/**
	 * Gets the expression2.
	 * 
	 * @return the second expression
	 */
	public Expression getExpression2() {
		return this.expression2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#getFailureCause (java.lang.Class,
	 * java.lang.Class, de.unisaarland.cs.st.moskito.mapping.requirements.Index)
	 */
	@Override
	public List<Expression> getFailureCause(final Class<? extends MappableEntity> target1,
	                                        final Class<? extends MappableEntity> target2,
	                                        final Index oneEquals) {
		List<Expression> failureCause = this.expression1.getFailureCause(target1, target2, oneEquals);
		final boolean check = check(target1, target2, oneEquals);
		if (!check) {
			if (failureCause == null) {
				failureCause = this.expression2.getFailureCause(target1, target2, oneEquals);
				if (failureCause == null) {
					return check
					            ? null
					            : new LinkedList<Expression>() {
						            
						            private static final long serialVersionUID = 1L;
						            
						            {
							            add(And.this);
						            }
					            };
				}
			}
		} else {
			return null;
		}
		
		return failureCause;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.expression1.toString() + " && " + this.expression2.toString() + ")";
	}
}
