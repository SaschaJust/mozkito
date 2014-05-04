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

package org.mozkito.database.model;

import org.mozkito.database.SQLElement;
import org.mozkito.database.constraints.ColumnConstraint;
import org.mozkito.database.types.Type;

/**
 * The Class DBColumn.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Column implements SQLElement {
	
	/**
	 * Gets the names.
	 * 
	 * @param columns
	 *            the columns
	 * @return the names
	 */
	public static String getNames(final Column[] columns) {
		PRECONDITIONS: {
			if (columns == null) {
				throw new NullPointerException();
			}
		}
		
		final StringBuilder columnNames = new StringBuilder();
		
		for (final Column column : columns) {
			if (columnNames.length() > 0) {
				columnNames.append(", ");
			}
			SANITY: {
				assert column.name() != null;
			}
			columnNames.append(column.name());
		}
		
		return columnNames.toString();
	}
	
	/** The immutable. */
	private boolean            immutable = false;
	
	/** The name. */
	private final String       name;
	
	/** The type. */
	private final Type         type;
	
	/** The table. */
	private Table              table;
	
	/** The constraints. */
	private ColumnConstraint[] constraints;
	
	/**
	 * Instantiates a new dB column.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param constraints
	 *            the constraints
	 */
	public Column(final String name, final Type type, final ColumnConstraint... constraints) {
		PRECONDITIONS: {
			if (name == null) {
				throw new NullPointerException();
			}
			if (type == null) {
				throw new NullPointerException();
			}
			if (name.length() == 0) {
				throw new IllegalArgumentException();
			}
		}
		
		try {
			// body
			this.name = name;
			this.type = type;
			this.constraints = constraints;
			
			for (final ColumnConstraint constraint : constraints) {
				constraint.setMyColumn(this);
			}
			
		} finally {
			POSTCONDITIONS: {
				assert name != null;
				assert type != null;
			}
		}
	}
	
	/**
	 * Gets the constraints.
	 * 
	 * @return the constraints
	 */
	public ColumnConstraint[] getConstraints() {
		SANITY: {
			assert this.constraints != null;
		}
		return this.constraints;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#makeImmutable()
	 */
	@Override
	public void makeImmutable() {
		if (!this.immutable) {
			this.immutable = true;
			for (final ColumnConstraint constraint : this.constraints) {
				constraint.makeImmutable();
			}
		}
	}
	
	/**
	 * Name.
	 * 
	 * @return the string
	 */
	public String name() {
		return this.name;
	}
	
	/**
	 * Sets the table.
	 * 
	 * @param table
	 *            the new table
	 */
	protected void setTable(final Table table) {
		PRECONDITIONS: {
			if (table == null) {
				throw new NullPointerException();
			}
			if (this.immutable) {
				throw new IllegalStateException("Column is immutable.");
			}
		}
		
		if (this.table != null) {
			throw new RuntimeException("Table alredy been set.");
		}
		this.table = table;
	}
	
	/**
	 * Table.
	 * 
	 * @return the table
	 */
	public Table table() {
		return this.table;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toMinimalSQL()
	 */
	@Override
	public String toMinimalSQL() {
		SANITY: {
			assert this.name != null;
			assert this.type != null;
		}
		
		final StringBuilder builder = new StringBuilder();
		builder.append(name()).append(' ').append(this.type.toSQL());
		
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toSQL()
	 */
	@Override
	public String toSQL() {
		SANITY: {
			assert this.name != null;
			assert this.type != null;
			assert this.constraints != null;
		}
		
		final StringBuilder builder = new StringBuilder();
		builder.append(name()).append(' ').append(this.type.toSQL());
		
		for (final ColumnConstraint constraint : this.constraints) {
			builder.append(' ').append(constraint.toSQL());
		}
		
		return builder.toString();
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
	
	/**
	 * Type.
	 * 
	 * @return the type
	 */
	public Type type() {
		return this.type;
	}
}
