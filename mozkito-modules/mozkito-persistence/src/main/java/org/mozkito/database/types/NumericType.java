/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.database.types;

import org.apache.commons.lang.math.NumberUtils;

import org.mozkito.database.types.numeric.DecimalType;
import org.mozkito.database.types.numeric.FloatingPointType;
import org.mozkito.database.types.numeric.IntegerType;

/**
 * The Class NumericType.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class NumericType extends Type {
	
	/**
	 * Maximum.
	 * 
	 * @return the number
	 */
	public abstract Number maximum();
	
	/**
	 * Minimum.
	 * 
	 * @return the number
	 */
	public abstract Number minimum();
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#subsumes(org.mozkito.database.types.Type)
	 */
	@Override
	public boolean subsumes(final Type type) {
		PRECONDITIONS: {
			if (type == null) {
				throw new NullPointerException();
			}
		}
		
		if (type instanceof NumericType) {
			if ((this instanceof IntegerType) && (type instanceof IntegerType)) {
				return (NumberUtils.compare(minimum().doubleValue(), ((NumericType) type).minimum().doubleValue()) <= 0)
				        && (NumberUtils.compare(maximum().doubleValue(), ((NumericType) type).maximum().doubleValue()) >= 0);
			} else if ((this instanceof FloatingPointType) && (type instanceof FloatingPointType)) {
				return (NumberUtils.compare(minimum().doubleValue(), ((NumericType) type).minimum().doubleValue()) <= 0)
				        && (NumberUtils.compare(maximum().doubleValue(), ((NumericType) type).maximum().doubleValue()) >= 0);
			} else if ((this instanceof DecimalType) && (type instanceof DecimalType)) {
				return (NumberUtils.compare(minimum().doubleValue(), ((NumericType) type).minimum().doubleValue()) <= 0)
				        && (NumberUtils.compare(maximum().doubleValue(), ((NumericType) type).maximum().doubleValue()) >= 0);
			}
		}
		
		return false;
	}
}
