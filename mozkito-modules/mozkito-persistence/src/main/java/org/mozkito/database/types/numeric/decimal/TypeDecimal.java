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

package org.mozkito.database.types.numeric.decimal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mozkito.database.types.numeric.DecimalType;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class TypeDecimal extends DecimalType {
	
	private static long decimalsToBytes(final short decimal) {
		return (long) (Math.floor(Math.log(Math.pow(10, decimal) - 1) / Math.log(256)) + 1);
	}
	
	private final BigDecimal maxValue;
	
	private final BigDecimal minValue;
	
	private final long       size;
	
	private final short      beforeDecimal;
	
	private final short      afterDecimal;
	
	/**
	 * Instantiates a new type decimal.
	 * 
	 * @param beforeDecimal
	 *            the before decimal
	 * @param afterDecimal
	 *            the after decimal
	 */
	public TypeDecimal(final short beforeDecimal, final short afterDecimal) {
		this.beforeDecimal = beforeDecimal;
		this.afterDecimal = afterDecimal;
		this.maxValue = new BigDecimal(10).pow(beforeDecimal)
		                                  .subtract(new BigDecimal(1).divide(new BigDecimal(10).pow(afterDecimal)));
		this.minValue = new BigDecimal(0).subtract(this.maxValue);
		
		// neglecting the 1 sign bit
		this.size = decimalsToBytes(beforeDecimal) + decimalsToBytes(afterDecimal);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.NumericType#maximum()
	 */
	@Override
	public Number maximum() {
		return this.maxValue;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.NumericType#minimum()
	 */
	@Override
	public Number minimum() {
		return this.minValue;
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
		
		BigDecimal theDecimal = null;
		if (BigDecimal.class.isAssignableFrom(value.getClass())) {
			theDecimal = ((BigDecimal) value);
		} else if (Double.class.isAssignableFrom(value.getClass())) {
			theDecimal = BigDecimal.valueOf((Double) value);
		} else if (String.class.isAssignableFrom(value.getClass())) {
			theDecimal = BigDecimal.valueOf(Double.parseDouble((String) value));
		} else {
			throw new IllegalArgumentException(value.toString());
		}
		
		statement.setBigDecimal(argument, theDecimal);
		
		return statement;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#size()
	 */
	@Override
	public Long size() {
		return this.size;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#toSQL()
	 */
	@Override
	public String toSQL() {
		return name() + "(" + this.beforeDecimal + ", " + this.afterDecimal + ")";
	}
	
}
