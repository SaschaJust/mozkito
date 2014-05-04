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

import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class InnerJoin extends Join {
	
	/**
	 * @param left
	 * @param leftColumns
	 * @param right
	 * @param rightColumns
	 */
	public InnerJoin(final Table left, final Column[] leftColumns, final Table right,
	        final Column[] rightColumns) {
		super(left, leftColumns, right, rightColumns);
	}
	
}
