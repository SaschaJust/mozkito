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

/**
 * The Class CharacterType.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class CharacterType extends Type {
	
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
		
		String string = null;
		if (String.class.isAssignableFrom(value.getClass())) {
			string = ((String) value);
		} else {
			throw new IllegalArgumentException(value.toString());
		}
		
		statement.setString(argument, string);
		
		return statement;
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
		
		if (type instanceof CharacterType) {
			if (size() == null) {
				return true;
			}
			
			final CharacterType cType = (CharacterType) type;
			return (cType.size() != null) && (size().compareTo(cType.size()) >= 0);
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#toSQL()
	 */
	@Override
	public String toSQL() {
		return getClass().getSimpleName().toUpperCase().substring(4) + (size() != null
		                                                                              ? "(" + size() + ")"
		                                                                              : "");
	}
}
