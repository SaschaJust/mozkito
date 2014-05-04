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

package org.mozkito.database.joins;

import java.util.Arrays;

import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class Join {
	
	private Table    right;
	private Table    left;
	private Column[] leftColumns;
	private Column[] rightColumns;
	
	/**
	 * Instantiates a new join.
	 * 
	 * @param left
	 *            the left
	 * @param leftColumns
	 *            the left columns
	 * @param right
	 *            the right
	 * @param rightColumns
	 *            the right columns
	 */
	public Join(final Table left, final Column[] leftColumns, final Table right, final Column[] rightColumns) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.left = left;
			this.leftColumns = leftColumns;
			this.right = right;
			this.rightColumns = rightColumns;
		} finally {
			POSTCONDITIONS: {
				assert this.left != null;
				assert this.right != null;
				assert this.leftColumns != null;
				assert this.rightColumns != null;
				assert this.leftColumns.length == this.rightColumns.length;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DBJoin [right=");
		builder.append(this.right);
		builder.append(", left=");
		builder.append(this.left);
		builder.append(", leftColumns=");
		builder.append(Arrays.toString(this.leftColumns));
		builder.append(", rightColumns=");
		builder.append(Arrays.toString(this.rightColumns));
		builder.append("]");
		return builder.toString();
	}
	
}
