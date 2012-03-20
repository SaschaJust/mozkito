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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.JavaUtils;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;

/**
 * The all expression evaluates to true if and only if all checks on the inner expressions evaluate to true. Evaluates
 * to false otherwise.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class All extends Expression {
	
	private final Set<Expression> expressions = new HashSet<Expression>();
	
	/**
	 * @param expressions
	 *            a collection of inner expressions
	 */
	public All(final Collection<Expression> expressions) {
		this.expressions.addAll(this.expressions);
	}
	
	/**
	 * @param expressions
	 *            a collection of inner expressions
	 */
	public All(final Expression... expressions) {
		CollectionUtils.addAll(this.expressions, expressions);
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
		for (final Expression expression : this.expressions) {
			if (!expression.check(target1, target2, oneEquals)) {
				return false;
			}
		}
		return true;
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
		if (!check(target1, target2, oneEquals)) {
			final List<Expression> list = new LinkedList<Expression>();
			for (final Expression expression : this.expressions) {
				final List<Expression> failureCause = expression.getFailureCause(target1, target2, oneEquals);
				if (failureCause != null) {
					list.addAll(failureCause);
				}
			}
			
			return list;
			
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		return "(∀ [true] : " + JavaUtils.collectionToString(this.expressions) + ")";
	}
	
}
