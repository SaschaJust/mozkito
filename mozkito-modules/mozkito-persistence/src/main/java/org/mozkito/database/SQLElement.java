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

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public interface SQLElement {
	
	/** The Constant DIALECT. */
	static final String DIALECT = "POSTGRESQL";
	
	/**
	 * Make immutable.
	 */
	void makeImmutable();
	
	/**
	 * Name.
	 * 
	 * @return the string
	 */
	String name();
	
	/**
	 * This function returns the SQL representation of the SQLElement without any constraints or additional information.
	 * e.g. a table would only be represented by the columns and corresponding types—no primary keys, foreign keys, any
	 * sort of constraint, etc...
	 * 
	 * 
	 * @return the string
	 */
	String toMinimalSQL();
	
	/**
	 * To sql.
	 * 
	 * @return the string
	 */
	String toSQL();
}
