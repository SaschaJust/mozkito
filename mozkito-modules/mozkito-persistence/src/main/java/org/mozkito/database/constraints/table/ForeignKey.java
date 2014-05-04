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

package org.mozkito.database.constraints.table;

import org.mozkito.database.constraints.TableConstraint;
import org.mozkito.database.exceptions.TypeMismatchException;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;

/**
 * The Class ForeignKey.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ForeignKey extends TableConstraint {
	
	/** The table. */
	private Table    table;
	
	/** The other columns. */
	private Column[] otherColumns;
	
	/**
	 * Instantiates a new foreign key.
	 * 
	 * @param column
	 *            the column
	 * @param table
	 *            the table
	 * @param otherColumn
	 *            the other column
	 * @throws TypeMismatchException
	 */
	public ForeignKey(final Column column, final Table table, final Column otherColumn) throws TypeMismatchException {
		this(new Column[] { column }, table, new Column[] { otherColumn });
	}
	
	/**
	 * Instantiates a new foreign key.
	 * 
	 * @param columns
	 *            the columns
	 * @param table
	 *            the table
	 * @param otherColumns
	 *            the other columns
	 * @throws TypeMismatchException
	 */
	public ForeignKey(final Column[] columns, final Table table, final Column[] otherColumns)
	        throws TypeMismatchException {
		super(columns);
		
		PRECONDITIONS: {
			if (columns == null) {
				throw new NullPointerException();
			}
			if (table == null) {
				throw new NullPointerException();
			}
			if (otherColumns == null) {
				throw new NullPointerException();
			}
			if (columns.length == 0) {
				throw new IllegalArgumentException();
			}
			if (otherColumns.length == 0) {
				throw new IllegalArgumentException();
			}
			if (columns.length != otherColumns.length) {
				throw new IllegalArgumentException();
			}
		}
		
		try {
			// body
			this.table = table;
			this.otherColumns = otherColumns;
			
			for (int i = 0; i < columns.length; ++i) {
				if (!columns[i].type().subsumes(otherColumns[i].type())) {
					throw new TypeMismatchException(
					                                String.format("ForeignKey constraint is incorrect. Column '%s' in table '%s' has type '%s', but column '%s' in foreign table '%s' has type '%s'.",
					                                              columns[i], columns[i].table(), columns[i].type(),
					                                              otherColumns[i], otherColumns[i].table(),
					                                              otherColumns[i].type()));
					
				}
			}
		} finally {
			POSTCONDITIONS: {
				assert this.table != null;
				assert this.otherColumns != null;
				assert getColumns() != null;
				assert getColumns().length == otherColumns.length;
			}
		}
	}
	
	/**
	 * Target table.
	 * 
	 * @return the table
	 */
	public Table targetTable() {
		return this.table;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toSQL()
	 */
	@Override
	public String toSQL() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final StringBuilder builder = new StringBuilder();
			builder.append("CONSTRAINT ").append(name());
			builder.append(" FOREIGN KEY (");
			builder.append(getColumnNames());
			builder.append(") REFERENCES ");
			builder.append(this.table.name());
			builder.append(" (").append(Column.getNames(this.otherColumns)).append(")");
			
			return builder.toString();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
