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
package org.mozkito.mappings.requirements;

import java.util.LinkedList;
import java.util.List;

import org.mozkito.mappings.mappable.model.MappableEntity;


/**
 * The not expression evaluates to true if the innerexpression evaluates to false. Evaluates to true otherwise.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Not extends Expression {
	
	/** The expression. */
	private final Expression expression;
	
	/**
	 * Instantiates a new not.
	 * 
	 * @param expression
	 *            the inner expression used in the evaluation
	 */
	public Not(final Expression expression) {
		this.expression = expression;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.requirements.Expression#check( java.lang.Class, java.lang.Class,
	 * org.mozkito.mapping.requirements.Index)
	 */
	@Override
	public boolean check(final Class<? extends MappableEntity> target1,
	                     final Class<? extends MappableEntity> target2,
	                     final Index oneEquals) {
		return !this.expression.check(target1, target2, oneEquals);
	}
	
	/**
	 * Gets the expression.
	 * 
	 * @return the inner expression
	 */
	public Expression getExpression() {
		return this.expression;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.requirements.Expression#getFailureCause (java.lang.Class,
	 * java.lang.Class, org.mozkito.mapping.requirements.Index)
	 */
	@Override
	public List<Expression> getFailureCause(final Class<? extends MappableEntity> target1,
	                                        final Class<? extends MappableEntity> target2,
	                                        final Index oneEquals) {
		final List<Expression> failureCause = this.expression.getFailureCause(target1, target2, oneEquals);
		if (failureCause == null) {
			return check(target1, target2, oneEquals)
			                                         ? null
			                                         : new LinkedList<Expression>() {
				                                         
				                                         private static final long serialVersionUID = 1L;
				                                         
				                                         {
					                                         add(Not.this);
				                                         }
			                                         };
		} else {
			return check(target1, target2, oneEquals)
			                                         ? null
			                                         : failureCause;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "!" + this.expression.toString();
	}
}
