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

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class ColumnConstraint implements SQLElement {
	
	private Column  myColumn;
	
	private boolean immutable = false;
	
	/**
	 * @return the myColumn
	 */
	public Column getMyColumn() {
		PRECONDITIONS: {
			if (this.myColumn == null) {
				throw new IllegalStateException("The constraint hasn't been associated with any column yet.");
			}
		}
		return this.myColumn;
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
		}
	}
	
	/**
	 * Sets the my column.
	 * 
	 * @param column
	 *            the new my column
	 */
	public void setMyColumn(final Column column) {
		PRECONDITIONS: {
			if (column == null) {
				throw new NullPointerException();
			}
			if (this.myColumn != null) {
				throw new IllegalStateException();
			}
			if (this.immutable) {
				throw new IllegalStateException("Constraint is immutable.");
			}
			
			final ColumnConstraint[] constraints = column.getConstraints();
			assert constraints != null;
			for (final ColumnConstraint constraint : constraints) {
				if ((constraint != this) && getClass().isAssignableFrom(constraint.getClass())) {
					throw new IllegalStateException("Column " + column + " in table " + column.table()
					        + " already has a contraint of type " + getClass().getSimpleName());
				}
			}
		}
		
		this.myColumn = column;
		
		POSTCONDITIONS: {
			assert this.myColumn != null;
		}
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
	 * @see org.mozkito.database.SQLElement#toSQL()
	 */
	@Override
	public String toSQL() {
		return name();
	}
}
