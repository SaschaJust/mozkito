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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.JavaUtils;

import org.apache.commons.collections.CollectionUtils;
import org.mozkito.mappings.mappable.model.MappableEntity;


/**
 * The any expression evaluates to true if any of the inner expressions evaluate to true. Evaluates to false otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class Any extends Expression {
	
	/** The expressions. */
	private final Set<Expression> expressions = new HashSet<Expression>();
	
	/**
	 * Instantiates a new any.
	 * 
	 * @param expressions
	 *            a collection of inner expressions
	 */
	public Any(final Collection<Expression> expressions) {
		this.expressions.addAll(this.expressions);
	}
	
	/**
	 * Instantiates a new any.
	 * 
	 * @param expressions
	 *            sa collection of inner expressions
	 */
	public Any(final Expression... expressions) {
		CollectionUtils.addAll(this.expressions, expressions);
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
		for (final Expression expression : this.expressions) {
			if (expression.check(target1, target2, oneEquals)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the expressions.
	 * 
	 * @return the expressions
	 */
	public final Set<Expression> getExpressions() {
		return this.expressions;
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
		if (!check(target1, target2, oneEquals)) {
			return new LinkedList<Expression>() {
				
				private static final long serialVersionUID = 1L;
				
				{
					addAll(getExpressions());
				}
			};
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "(∃ [true] : " + JavaUtils.collectionToString(this.expressions) + ")";
	}
}
