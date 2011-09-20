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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;

public class Any extends Expression {
	
	private final Set<Expression> expressions = new HashSet<Expression>();
	
	/**
	 * @param expressions
	 */
	public Any(final Collection<Expression> expressions) {
		this.expressions.addAll(this.expressions);
	}
	
	/**
	 * @param expressions
	 */
	public Any(final Expression... expressions) {
		CollectionUtils.addAll(this.expressions, expressions);
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
		for (Expression expression : this.expressions) {
			if (expression.check(target1, target2, oneEquals)) {
				return true;
			}
		}
		return false;
	}
}
