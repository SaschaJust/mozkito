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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozkito.database.SQLElement;
import org.mozkito.database.constraints.ColumnConstraint;
import org.mozkito.database.constraints.TableConstraint;
import org.mozkito.database.constraints.table.ForeignKey;
import org.mozkito.database.constraints.table.PrimaryKey;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.index.Index;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class Table.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Table implements SQLElement {
	
	/**
	 * Names.
	 * 
	 * @param tables
	 *            the tables
	 * @return the string
	 */
	public static String names(final Collection<Table> tables) {
		PRECONDITIONS: {
			if (tables == null) {
				throw new NullPointerException();
			}
		}
		
		final StringBuilder builder = new StringBuilder();
		
		for (final Table table : tables) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(table.name());
		}
		
		return builder.toString();
	}
	
	/** The name. */
	private final String              name;
	
	/** The columns. */
	private final Column[]            columns;
	
	/** The column map. */
	private final Map<String, Column> columnMap        = new HashMap<>();
	
	/** The constraints. */
	private TableConstraint[]         tableConstraints = new TableConstraint[0];
	
	/** The primary key. */
	private PrimaryKey                primaryKey;
	
	/** The primary key columns. */
	private final Column[]            primaryKeyColumns;
	
	/** The indexes. */
	private Index[]                   indexes          = new Index[0];
	
	/** The immutable. */
	private boolean                   immutable        = false;
	
	/**
	 * Instantiates a new table.
	 * 
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 * @throws DatabaseException
	 *             the database exception
	 */
	public Table(final String name, final Column... columns) throws DatabaseException {
		PRECONDITIONS: {
			if (name == null) {
				throw new NullPointerException();
			}
			if (columns == null) {
				throw new NullPointerException();
			}
			if (columns.length < 1) {
				throw new IllegalArgumentException();
			}
		}
		
		this.name = name;
		this.columns = columns;
		
		final List<Column> pkColumns = new ArrayList<>(1);
		
		for (final Column column : columns) {
			if (this.columnMap.containsKey(column.name())) {
				throw new DatabaseException("duplicate column name");
			}
			
			this.columnMap.put(column.name(), column);
			column.setTable(this);
			for (final ColumnConstraint constraint : column.getConstraints()) {
				if (constraint instanceof org.mozkito.database.constraints.column.PrimaryKey) {
					pkColumns.add(column);
				}
			}
			
			if (pkColumns.size() > 1) {
				throw new DatabaseException(
				                            "Compound primary keys have to be added with the primary key table constraint. Found: "
				                                    + Column.getNames(pkColumns.toArray(new Column[0])));
			}
		}
		
		this.primaryKeyColumns = pkColumns.toArray(new Column[0]);
		
		POSTCONDITIONS: {
			assert name != null;
			assert columns != null;
			assert columns.length > 0;
			assert this.columnMap != null;
			assert this.columnMap.size() == columns.length;
		}
	}
	
	/**
	 * Column.
	 * 
	 * @param i
	 *            the i
	 * @return the column
	 */
	public Column column(final int i) {
		PRECONDITIONS: {
			assert this.columns != null;
			if (i < 0) {
				throw new IllegalArgumentException();
			}
			if (i >= this.columns.length) {
				throw new IllegalArgumentException();
			}
		}
		
		return this.columns[i];
	}
	
	/**
	 * Column.
	 * 
	 * @param name
	 *            the name
	 * @return the column
	 */
	public Column column(final String name) {
		PRECONDITIONS: {
			if (name == null) {
				throw new NullPointerException();
			}
			assert this.columnMap != null;
			if (!this.columnMap.containsKey(name)) {
				throw new IllegalArgumentException(String.format("Column '%s' is not defined in table '%s'.", name,
				                                                 name()));
			}
		}
		
		return this.columnMap.get(name);
	}
	
	/**
	 * Gets the column names.
	 * 
	 * @return the column names
	 */
	public String columnNames() {
		SANITY: {
			assert this.columns != null;
			assert this.columns.length > 0;
		}
		
		return Column.getNames(this.columns);
	}
	
	/**
	 * Depends on.
	 * 
	 * @return the sets the
	 */
	public Set<Table> dependsOn() {
		final Set<Table> tables = new HashSet<>();
		
		for (final TableConstraint constraint : this.tableConstraints) {
			if (constraint instanceof ForeignKey) {
				tables.add(((ForeignKey) constraint).targetTable());
			}
		}
		
		for (final Column column : this.columns) {
			for (final ColumnConstraint constraint : column.getConstraints()) {
				if (constraint instanceof org.mozkito.database.constraints.column.ForeignKey) {
					tables.add(((org.mozkito.database.constraints.column.ForeignKey) constraint).targetTable());
				}
			}
		}
		
		return tables;
	}
	
	/**
	 * Indexes.
	 * 
	 * @return the index[]
	 */
	public Index[] indexes() {
		SANITY: {
			assert this.indexes != null;
		}
		
		return this.indexes;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#makeImmutable()
	 */
	public void makeImmutable() {
		if (!this.immutable) {
			this.immutable = true;
			for (final Column column : this.columns) {
				column.makeImmutable();
			}
			for (final TableConstraint constraint : this.tableConstraints) {
				constraint.makeImmutable();
			}
			for (final Index index : this.indexes) {
				index.makeImmutable();
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
	 * Primary key.
	 * 
	 * @return the primary key
	 */
	public PrimaryKey primaryKey() {
		return this.primaryKey;
	}
	
	/**
	 * Primary key columns.
	 * 
	 * @return the column[]
	 */
	public Column[] primaryKeyColumns() {
		return this.primaryKeyColumns;
	}
	
	/**
	 * Sets the constraints.
	 * 
	 * @param constraints
	 *            the new constraints
	 */
	public void setConstraints(final TableConstraint... constraints) {
		PRECONDITIONS: {
			if (constraints != null) {
				for (final TableConstraint tableConstraint : constraints) {
					if (!equals(tableConstraint.table())) {
						throw new IllegalArgumentException(
						                                   String.format("Trying to add a constraint to table '%s' which was defined for columns '%s' in table '%s'. The constraint that caused the error: %s",
						                                                 name(), tableConstraint.getColumnNames(),
						                                                 tableConstraint.table().name(),
						                                                 tableConstraint));
					}
				}
			}
			if (this.immutable) {
				throw new IllegalStateException("Table is immutable.");
			}
		}
		
		if (constraints == null) {
			this.tableConstraints = new TableConstraint[0];
		} else {
			boolean foundPKey = false;
			for (final TableConstraint tableConstraint : constraints) {
				if (tableConstraint instanceof PrimaryKey) {
					if (foundPKey) {
						this.tableConstraints = new TableConstraint[0];
						throw new IllegalArgumentException();
					} else {
						foundPKey = true;
						this.primaryKey = (PrimaryKey) tableConstraint;
					}
				}
			}
			this.tableConstraints = constraints;
		}
	}
	
	/**
	 * Sets the indexes.
	 * 
	 * @param indexes
	 *            the new indexes
	 */
	public void setIndexes(final Index... indexes) {
		PRECONDITIONS: {
			if (indexes != null) {
				for (final Index index : indexes) {
					if (!equals(index.table())) {
						throw new IllegalArgumentException();
					}
				}
			}
			if (this.immutable) {
				throw new IllegalStateException("Table is immutable.");
			}
		}
		
		if (indexes == null) {
			this.indexes = new Index[0];
		} else {
			
			this.indexes = indexes;
		}
	}
	
	/**
	 * Sets the primary key.
	 * 
	 * @param primaryKey
	 *            the new primary key
	 */
	public void setPrimaryKey(final PrimaryKey primaryKey) {
		PRECONDITIONS: {
			if (primaryKey == null) {
				throw new NullPointerException();
			}
			if (this.immutable) {
				throw new IllegalStateException("Table is immutable.");
			}
		}
		this.primaryKey = primaryKey;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.SQLElement#toMinimalSQL()
	 */
	@Override
	public String toMinimalSQL() {
		final StringBuilder builder = new StringBuilder();
		final StringBuilder columnBuilder = new StringBuilder();
		
		builder.append("TABLE ").append(name()).append(" (").append(FileUtils.lineSeparator);
		
		SANITY: {
			assert this.columns != null;
		}
		
		for (final Column column : this.columns) {
			if (columnBuilder.length() != 0) {
				columnBuilder.append(",").append(FileUtils.lineSeparator);
			}
			columnBuilder.append(column.toMinimalSQL());
		}
		
		builder.append(columnBuilder.toString());
		
		builder.append(FileUtils.lineSeparator).append(')');
		
		return builder.toString();
	}
	
	/**
	 * To sql.
	 * 
	 * @return the string
	 */
	@Override
	public String toSQL() {
		final StringBuilder builder = new StringBuilder();
		final StringBuilder columnBuilder = new StringBuilder();
		final StringBuilder constraintBuilder = new StringBuilder();
		
		builder.append("TABLE ").append(name()).append(" (").append(FileUtils.lineSeparator);
		
		SANITY: {
			assert this.columns != null;
		}
		
		for (final Column column : this.columns) {
			if (columnBuilder.length() != 0) {
				columnBuilder.append(",").append(FileUtils.lineSeparator);
			}
			columnBuilder.append(column.toSQL());
		}
		
		builder.append(columnBuilder.toString());
		
		SANITY: {
			assert this.tableConstraints != null;
		}
		
		if (this.tableConstraints.length > 0) {
			builder.append(",").append(FileUtils.lineSeparator);
			for (final TableConstraint tableConstraint : this.tableConstraints) {
				if (constraintBuilder.length() != 0) {
					constraintBuilder.append(",").append(FileUtils.lineSeparator);
				}
				constraintBuilder.append(tableConstraint.toSQL());
			}
		}
		
		builder.append(constraintBuilder.toString());
		builder.append(FileUtils.lineSeparator).append(')');
		
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
}
