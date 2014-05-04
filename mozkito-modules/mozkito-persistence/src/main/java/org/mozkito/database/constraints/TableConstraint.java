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

package org.mozkito.database.constraints;

import org.mozkito.database.SQLElement;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;

/**
 * The Class DBConstraints.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class TableConstraint implements SQLElement {
	
	/**
	 * Generate name.
	 * 
	 * @param c
	 *            the c
	 * @param columns
	 *            the columns
	 * @return the string
	 */
	private static String generateName(final Class<? extends TableConstraint> c,
	                                   final Column... columns) {
		PRECONDITIONS: {
			if (c == null) {
				throw new NullPointerException();
			}
			if (columns == null) {
				throw new NullPointerException();
			}
		}
		
		final StringBuilder builder = new StringBuilder();
		builder.append(c.getSimpleName().toLowerCase());
		for (final Column column : columns) {
			builder.append('_').append(column.name().toLowerCase());
		}
		
		POSTCONDITIONS: {
			assert builder.length() > 0;
		}
		return builder.toString();
		
	}
	
	/** The columns. */
	private final Column[] columns;
	
	/** The table. */
	private Table          table;
	
	/** The name. */
	private String         name;
	
	private boolean        immutable = false;
	
	/**
	 * Instantiates a new constraint.
	 * 
	 * @param columns
	 *            the columns
	 */
	public TableConstraint(final Column... columns) {
		this(null, columns);
	}
	
	/**
	 * Instantiates a new dB constraints.
	 * 
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 */
	public TableConstraint(final String name, final Column... columns) {
		PRECONDITIONS: {
			
			if (columns == null) {
				throw new NullPointerException();
			}
			
			if (columns.length == 0) {
				throw new ArrayIndexOutOfBoundsException("Constraints have to refer to at least one column.");
			}
			
			for (final Column column : columns) {
				if (this.table == null) {
					this.table = column.table();
				} else {
					if (!this.table.equals(column.table())) {
						throw new IllegalArgumentException();
					}
				}
			}
		}
		
		try {
			// body
			this.columns = columns;
			this.name = name != null
			                        ? name
			                        : generateName(getClass(), columns);
		} finally {
			POSTCONDITIONS: {
				assert this.table != null;
			}
		}
	}
	
	/**
	 * Gets the column names.
	 * 
	 * @return the column names
	 */
	public String getColumnNames() {
		return Column.getNames(getColumns());
	}
	
	/**
	 * Gets the columns.
	 * 
	 * @return the columns
	 */
	public final Column[] getColumns() {
		return this.columns;
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
			for (final Column column : this.columns) {
				column.makeImmutable();
			}
			this.table.makeImmutable();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#name()
	 */
	public String name() {
		return this.name;
	}
	
	/**
	 * Table.
	 * 
	 * @return the table
	 */
	public Table table() {
		SANITY: {
			assert this.table != null;
		}
		
		return this.table;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toMinimalSQL()
	 */
	@Override
	public String toMinimalSQL() {
		return "";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return name();
	}
}
