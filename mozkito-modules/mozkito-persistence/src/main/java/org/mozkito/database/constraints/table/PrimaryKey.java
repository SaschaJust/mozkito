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
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;

/**
 * The Class DBId.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PrimaryKey extends TableConstraint {
	
	/**
	 * Instantiates a new dB id.
	 * 
	 * @param columns
	 *            the columns
	 */
	public PrimaryKey(final Column... columns) {
		super(columns);
	}
	
	/**
	 * Gets the column indexes.
	 * 
	 * @return the column indexes
	 */
	public int[] getColumnIndexes() {
		SANITY: {
			assert getColumns() != null;
		}
		
		Table table = null;
		final int[] columnIndexes = new int[getColumns().length];
		int arrayIndex = 0;
		
		for (final Column column : getColumns()) {
			if (table == null) {
				table = column.table();
			}
			
			SANITY: {
				assert table.equals(column.table());
			}
			
			for (int i = 0; i < table.columnCount(); ++i) {
				assert arrayIndex < columnIndexes.length;
				
				if (column.equals(table.column(i))) {
					columnIndexes[arrayIndex] = i;
					++arrayIndex;
				}
			}
		}
		
		return columnIndexes;
	}
	
	/**
	 * Gets the column name.
	 * 
	 * @param i
	 *            the i
	 * @return the column name
	 */
	public String getColumnName(final int i) {
		PRECONDITIONS: {
			assert getColumns() != null;
			if (i < 0) {
				throw new IllegalArgumentException();
			}
			if (i >= getColumns().length) {
				throw new IllegalArgumentException();
			}
		}
		
		SANITY: {
			assert getColumns() != null;
		}
		
		return getColumns()[i].name();
	}
	
	/**
	 * Gets the column type.
	 * 
	 * @param i
	 *            the i
	 * @return the column type
	 */
	public Type getColumnType(final int i) {
		PRECONDITIONS: {
			assert getColumns() != null;
			if (i < 0) {
				throw new IllegalArgumentException();
			}
			if (i >= getColumns().length) {
				throw new IllegalArgumentException();
			}
		}
		
		try {
			return getColumns()[i].type();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toSQL()
	 */
	@Override
	public String toSQL() {
		SANITY: {
			assert getColumns() != null;
		}
		return "PRIMARY KEY (" + Column.getNames(getColumns()) + ")";
		
	}
	
}
