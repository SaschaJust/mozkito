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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class TypeDateTime extends Type {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws SQLException
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
		
		Timestamp timestamp = null;
		if (Date.class.isAssignableFrom(value.getClass())) {
			timestamp = new Timestamp(((Date) value).getTime());
		} else if (java.sql.Date.class.isAssignableFrom(value.getClass())) {
			timestamp = new Timestamp(((java.sql.Date) value).getTime());
		} else if (DateTime.class.isAssignableFrom(value.getClass())) {
			timestamp = new Timestamp(((DateTime) value).getMillis());
		} else if (String.class.isAssignableFrom(value.getClass())) {
			timestamp = Timestamp.valueOf((String) value);
		} else {
			throw new IllegalArgumentException(value.toString());
		}
		
		statement.setTimestamp(argument, timestamp);
		
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
		
		return type instanceof TypeDateTime;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#toSQL()
	 */
	@Override
	public String toSQL() {
		return "TIMESTAMP WITH TIME ZONE";
	}
	
}
