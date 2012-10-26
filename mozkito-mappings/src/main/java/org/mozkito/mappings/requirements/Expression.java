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

import java.util.List;

import org.mozkito.mappings.mappable.model.MappableEntity;


/**
 * The superclass of all {@link Expression}s that are used to express the support of an engine for specific instances
 * of.
 * 
 * {@link MappableEntity}s.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Expression {
	
	/**
	 * Evaluates the criterion.
	 * 
	 * Returns 1 if the criterion is matched. Returns -1 if the criterion is matched in opposite order, i.e.
	 * check(target2, target1) returns 1. Returns 0 otherwise.
	 * 
	 * @param target1
	 *            the 'from' {@link MappableEntity}
	 * @param target2
	 *            the 'to' {@link MappableEntity}
	 * @return the result of the evaluation
	 */
	public int check(final Class<? extends MappableEntity> target1,
	                 final Class<? extends MappableEntity> target2) {
		if (check(target1, target2, Index.FROM) || check(target1, target2, Index.TO)) {
			return 1;
		} else if (check(target2, target1, Index.FROM) || check(target2, target1, Index.TO)) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Evaluates the criterion in regard to the index {@link Index#ONE} refers to. This has to be implemented by all
	 * extensions of {@link Expression}.
	 * 
	 * @param target1
	 *            the 'from' {@link MappableEntity}
	 * @param target2
	 *            the 'to' {@link MappableEntity}
	 * @param oneEquals
	 *            the index {@link Index#ONE} refers to in the evaluation
	 * @return the result of the evaluation
	 */
	public abstract boolean check(final Class<? extends MappableEntity> target1,
	                              final Class<? extends MappableEntity> target2,
	                              final Index oneEquals);
	
	/**
	 * Gets the failure cause.
	 * 
	 * @param target1
	 *            the target1
	 * @param target2
	 *            the target2
	 * @param oneEquals
	 *            the one equals
	 * @return the failure cause
	 */
	public abstract List<Expression> getFailureCause(final Class<? extends MappableEntity> target1,
	                                                 final Class<? extends MappableEntity> target2,
	                                                 final Index oneEquals);
}
