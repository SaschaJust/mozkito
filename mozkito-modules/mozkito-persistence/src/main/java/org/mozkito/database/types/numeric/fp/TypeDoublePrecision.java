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

package org.mozkito.database.types.numeric.fp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mozkito.database.types.NumericType;

/**
 * The Class IntegerType.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TypeDoublePrecision extends NumericType {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.NumericType#maximum()
	 */
	@Override
	public Number maximum() {
		return Double.MAX_VALUE;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.NumericType#minimum()
	 */
	@Override
	public Number minimum() {
		return -Double.MAX_VALUE;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#name()
	 */
	@Override
	public String name() {
		return "DOUBLE PRECISION";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#set(java.sql.PreparedStatement, int, java.lang.Object)
	 */
	@Override
	public PreparedStatement set(final PreparedStatement statement,
	                             final int argument,
	                             final Object value) throws SQLException {
		PRECONDITIONS: {
			if (statement == null) {
				throw new NullPointerException();
			}
			
			if (argument <= 0) {
				throw new IllegalArgumentException();
			}
		}
		
		Double theDouble = null;
		if (Double.class.isAssignableFrom(value.getClass())) {
			theDouble = ((Double) value);
		} else if (String.class.isAssignableFrom(value.getClass())) {
			theDouble = Double.parseDouble((String) value);
		} else {
			throw new IllegalArgumentException(value.toString());
		}
		
		statement.setDouble(argument, theDouble);
		
		return statement;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#size()
	 */
	@Override
	public Long size() {
		return 8l;
	}
}
