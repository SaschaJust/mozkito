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

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class NotNull extends TableConstraint {
	
	/**
	 * Instantiates a new not null.
	 * 
	 * @param columns
	 *            the columns
	 */
	public NotNull(final Column... columns) {
		super(columns);
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
		
		final StringBuilder builder = new StringBuilder();
		final StringBuilder notNullBuilder = new StringBuilder();
		
		builder.append("CONSTRAINT ").append(name()).append(" CHECK (");
		for (final Column column : getColumns()) {
			if (notNullBuilder.length() != 0) {
				notNullBuilder.append(" AND ");
			}
			notNullBuilder.append(column.name()).append(" IS NOT NULL");
		}
		
		builder.append(notNullBuilder.toString()).append(')');
		
		return builder.toString();
	}
}
