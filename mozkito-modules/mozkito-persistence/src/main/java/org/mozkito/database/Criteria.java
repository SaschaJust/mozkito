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

package org.mozkito.database;

import java.util.Collection;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 * @param <T>
 */
public interface Criteria<T extends Entity> {
	
	/**
	 * Eq.
	 * 
	 * @param column
	 *            the column
	 * @param value
	 *            the value
	 * @return the criteria
	 */
	public abstract Criteria<T> eq(String column,
	                               Object value);
	
	/**
	 * In.
	 * 
	 * @param column
	 *            the column
	 * @param values
	 *            the values
	 * @return the criteria
	 */
	public abstract Criteria<T> in(String column,
	                               Collection<?> values);
	
	/**
	 * Oder by asc.
	 * 
	 * @param column
	 *            the column
	 * @return the criteria
	 */
	public abstract Criteria<T> oderByAsc(String column);
	
	/**
	 * Oder by desc.
	 * 
	 * @param column
	 *            the column
	 * @return the criteria
	 */
	public abstract Criteria<T> oderByDesc(String column);
	
}
