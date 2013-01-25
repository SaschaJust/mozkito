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
package org.mozkito.persistence;

/**
 * The Enum ConnectOptions.
 */
public enum ConnectOptions {
	
	/**
	 * This should be the default option when you connect to an existing database. validates the database schema after
	 * the connect. Adds/alternates schema (if possible) according to the most recent model.
	 */
	VALIDATE_OR_CREATE_SCHEMA,
	
	/** drops the whole database (if exists) and creates a new one. Then behaves the same as VALIDATE_OR_CREATE_SCHEMA. */
	DROP_AND_CREATE_DATABASE,
	
	/** behaves the same as VALIDATE_OR_CREATE_SCHEMA but drops already existing table entries. */
	DROP_CONTENTS;
}
