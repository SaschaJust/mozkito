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

package org.mozkito.database.index;

import org.mozkito.database.SQLElement;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;

/**
 * The Class Index.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Index implements SQLElement {
	
	/**
	 * Generate a name for the index.
	 * 
	 * @param table
	 *            the table the index is defined for
	 * @param columns
	 *            the columns the index is defined for
	 * @return the name of the index
	 * @precondition table not null
	 * @precondition columns not null
	 * @postcondition non-null not-empty string
	 */
	private static String generateName(final Table table,
	                                   final Column... columns) {
		PRECONDITIONS: {
			if (table == null) {
				throw new NullPointerException();
			}
			if (columns == null) {
				throw new NullPointerException();
			}
		}
		
		final StringBuilder builder = new StringBuilder();
		builder.append(table.name().toLowerCase());
		for (final Column column : columns) {
			builder.append('_').append(column.name().toLowerCase());
		}
		builder.append("_idx");
		
		POSTCONDITION: {
			assert builder.length() > 0;
		}
		
		return builder.toString();
	}
	
	/** The name. */
	private final String   name;
	
	/** The table. */
	private final Table    table;
	
	/** The columns. */
	private final Column[] columns;
	
	private boolean        immutable = false;
	
	/**
	 * Instantiates a new index.
	 * 
	 * @param columns
	 *            the columns
	 */
	public Index(final Column... columns) {
		this(null, columns);
	}
	
	/**
	 * Instantiates a new index.
	 * 
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 */
	public Index(final String name, final Column... columns) {
		PRECONDITIONS: {
			if (columns == null) {
				throw new NullPointerException();
			}
			if (columns.length == 0) {
				throw new ArrayIndexOutOfBoundsException();
			}
			
		}
		
		Table localTable = null;
		for (final Column column : columns) {
			if (localTable != null) {
				if (!localTable.equals(column.table())) {
					throw new IllegalArgumentException();
				}
			} else {
				localTable = column.table();
			}
		}
		
		this.table = localTable;
		this.columns = columns;
		
		if (name != null) {
			this.name = name;
		} else {
			this.name = generateName(this.table, columns);
		}
		
	}
	
	/**
	 * Columns.
	 * 
	 * @return the column[]
	 */
	public Column[] columns() {
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
	 * Name.
	 * 
	 * @return the string
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
	public String toSQL() {
		SANITY: {
			assert this.columns != null;
			assert this.name != null;
		}
		
		final StringBuilder builder = new StringBuilder();
		builder.append("INDEX ").append(this.name).append(" ON ").append(this.table.name()).append(" (")
		       .append(Column.getNames(this.columns)).append(')');
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		SANITY: {
			assert this.name != null;
		}
		return this.name;
	}
}
