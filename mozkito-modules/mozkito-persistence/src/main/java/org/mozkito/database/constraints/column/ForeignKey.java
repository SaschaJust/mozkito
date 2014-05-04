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

package org.mozkito.database.constraints.column;

import org.mozkito.database.constraints.ColumnConstraint;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;

/**
 * The Class ForeignKey.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ForeignKey extends ColumnConstraint {
	
	/** The table. */
	public final Table table;
	
	/** The target column. */
	private Column     targetColumn;
	
	/**
	 * Instantiates a new foreign key.
	 * 
	 * @param table
	 *            the table
	 */
	public ForeignKey(final Table table) {
		PRECONDITIONS: {
			if (table == null) {
				throw new NullPointerException();
			}
		}
		
		this.table = table;
		this.targetColumn = null;
		
		POSTCONDITIONS: {
			assert this.table != null;
		}
	}
	
	/**
	 * Instantiates a new foreign key.
	 * 
	 * @param table
	 *            the table
	 * @param column
	 *            the column
	 */
	public ForeignKey(final Table table, final Column column) {
		PRECONDITIONS: {
			if (table == null) {
				throw new NullPointerException();
			}
			
			if (column == null) {
				throw new NullPointerException();
			}
		}
		
		this.table = table;
		this.targetColumn = column;
		
		POSTCONDITIONS: {
			assert this.table != null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#name()
	 */
	@Override
	public String name() {
		return "FOREIGN KEY";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.constraints.ColumnConstraint#setMyColumn(org.mozkito.database.model.Column)
	 */
	@Override
	public void setMyColumn(final Column column) {
		super.setMyColumn(column);
		
		if (this.targetColumn == null) {
			final Column _targetColumn = this.table.column(column.name());
			if (_targetColumn == null) {
				throw new IllegalStateException("Target table " + this.table + " does not have a column with name "
				        + column.name());
			}
			this.targetColumn = _targetColumn;
		}
		
		if (!column.type().subsumes(this.targetColumn.type())) {
			throw new IllegalStateException("Referenced column " + this.targetColumn + " in table " + this.table
			        + " has type " + this.targetColumn.type() + " which is not be subsumed by this column "
			        + column.name() + " in table " + this.table + " having type " + column.type());
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
	 * @see org.mozkito.database.constraints.ColumnConstraint#toSQL()
	 */
	@Override
	public String toSQL() {
		return "REFERENCES "
		        + this.table
		        + ((this.targetColumn == null) || this.targetColumn.name().equals(getMyColumn().name())
		                                                                                               ? ""
		                                                                                               : "("
		                                                                                                       + this.targetColumn.name()
		                                                                                                       + ")");
	}
	
}
