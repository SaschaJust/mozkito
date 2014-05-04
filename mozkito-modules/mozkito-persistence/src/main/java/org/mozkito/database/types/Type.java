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

import org.mozkito.database.SQLElement;
import org.mozkito.database.types.character.TypeText;
import org.mozkito.database.types.character.TypeVarChar;
import org.mozkito.database.types.numeric.integer.TypeBigInt;
import org.mozkito.database.types.numeric.integer.TypeSerial;
import org.mozkito.database.types.numeric.integer.TypeSmallInt;

/**
 * The Class DBType.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Type implements SQLElement {
	
	/** The Constant TYPE_LONG. */
	private static final Type TYPE_LONG   = new TypeBigInt();
	
	/** The Constant TYPE_SERIAL. */
	private static final Type TYPE_SERIAL = new TypeSerial();
	
	/** The Constant TYPE_SHORT. */
	private static final Type TYPE_SHORT  = new TypeSmallInt();
	
	/** The Constant TYPE_TEXT. */
	private static final Type TYPE_TEXT   = new TypeText();
	
	/**
	 * Gets the boolean.
	 * 
	 * @return the boolean
	 */
	public static Type getBoolean() {
		return new TypeBoolean();
	}
	
	/**
	 * Gets the date time.
	 * 
	 * @return the date time
	 */
	public static Type getDateTime() {
		return new TypeDateTime();
	}
	
	/**
	 * Gets the long.
	 * 
	 * @return the long
	 */
	public static final Type getLong() {
		return TYPE_LONG;
	};
	
	/**
	 * Gets the serial.
	 * 
	 * @return the serial
	 */
	public static Type getSerial() {
		return TYPE_SERIAL;
	}
	
	/**
	 * Gets the short.
	 * 
	 * @return the short
	 */
	public static final Type getShort() {
		return TYPE_SHORT;
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public static Type getText() {
		return TYPE_TEXT;
	}
	
	/**
	 * Gets the var char.
	 * 
	 * @param enumType
	 *            the enum type
	 * @return the var char
	 */
	@SuppressWarnings ("rawtypes")
	public static Type getVarChar(final Class<?> enumType) {
		PRECONDITIONS: {
			if (enumType == null) {
				throw new NullPointerException();
			}
			if (!enumType.isEnum()) {
				throw new IllegalArgumentException();
			}
		}
		
		int maxLength = 0;
		final Enum[] enumEntries = (Enum[]) enumType.getEnumConstants();
		
		SANITY: {
			assert enumEntries != null;
		}
		
		if (enumEntries.length < 1) {
			throw new RuntimeException("Received enum class without a single entry. This makes no sense.");
		}
		
		for (final Enum e : enumEntries) {
			SANITY: {
				assert e != null;
				assert e.name() != null;
			}
			
			if (e.name().length() > maxLength) {
				maxLength = e.name().length();
			}
		}
		
		POSTCONDITIONS: {
			assert maxLength > 0;
		}
		
		return getVarChar(maxLength);
	}
	
	/**
	 * Gets the var char.
	 * 
	 * @param value
	 *            the value
	 * @return the var char
	 */
	public static final Type getVarChar(final int value) {
		return new TypeVarChar(value);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#makeImmutable()
	 */
	@Override
	public void makeImmutable() {
		// type cannot be changed after creation anyways.
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#name()
	 */
	public String name() {
		SANITY: {
			assert getClass().getSimpleName().length() > 4;
			assert getClass().getSimpleName().startsWith("Type");
		}
		
		return getClass().getSimpleName().substring(4).toUpperCase();
	}
	
	/**
	 * Sets the.
	 * 
	 * @param statement
	 *            the statement
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 * @return the prepared statement
	 * @throws SQLException
	 *             the SQL exception
	 */
	public abstract PreparedStatement set(PreparedStatement statement,
	                                      int argument,
	                                      Object value) throws SQLException;
	
	/**
	 * Length.
	 * 
	 * @return the long
	 */
	public abstract Long size();
	
	/**
	 * Subsumes.
	 * 
	 * @param type
	 *            the type
	 * @return true, if successful
	 */
	public abstract boolean subsumes(Type type);
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toMinimalSQL()
	 */
	@Override
	public String toMinimalSQL() {
		return toSQL();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toSQL()
	 */
	@Override
	public String toSQL() {
		return name();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name();
	}
	
}
