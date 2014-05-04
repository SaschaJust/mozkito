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

import org.apache.commons.lang.StringUtils;

import org.mozkito.database.constraints.TableConstraint;
import org.mozkito.database.model.Column;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Check extends TableConstraint {
	
	private final String condition;
	
	/**
	 * Instantiates a new check.
	 * 
	 * @param condition
	 *            the condition
	 * @param columns
	 *            the columns
	 */
	public Check(final String condition, final Column... columns) {
		super(columns);
		PRECONDITIONS: {
			if (condition == null) {
				throw new NullPointerException();
			}
		}
		
		SANITY: {
			assert columns != null;
			assert columns.length > 0;
		}
		
		for (final Column column : columns) {
			if (!StringUtils.containsIgnoreCase(condition, column.name())) {
				throw new IllegalArgumentException();
			}
		}
		
		this.condition = condition;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toSQL() {
		SANITY: {
			assert this.condition != null;
		}
		return "CONSTRAINT " + name() + " CHECK (" + this.condition + ")";
	}
	
}
