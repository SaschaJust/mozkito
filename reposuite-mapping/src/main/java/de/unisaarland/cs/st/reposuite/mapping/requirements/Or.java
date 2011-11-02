/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.requirements;

import de.unisaarland.cs.st.reposuite.mapping.mappable.model.MappableEntity;

/**
 * The or expression evaluates to true if one or more of the inner expressions
 * evaluate to true. Evaluates to false otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Or extends Expression {
	
	private final Expression expression1;
	
	private final Expression expression2;
	
	/**
	 * @param expression1
	 * @param expression2
	 */
	public Or(final Expression expression1, final Expression expression2) {
		this.expression1 = expression1;
		this.expression2 = expression2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.requirements.Expression#check(
	 * java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.reposuite.mapping.requirements.Index)
	 */
	@Override
	public boolean check(final Class<? extends MappableEntity> target1,
	                     final Class<? extends MappableEntity> target2,
	                     final Index oneEquals) {
		return getExpression1().check(target1, target2, oneEquals)
		        || getExpression2().check(target1, target2, oneEquals);
	}
	
	/**
	 * @return the 'from' expression
	 */
	public Expression getExpression1() {
		return this.expression1;
	}
	
	/**
	 * @return the 'to' expression
	 */
	public Expression getExpression2() {
		return this.expression2;
	}
}
