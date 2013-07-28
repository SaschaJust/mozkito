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

package org.mozkito.genealogies.core;

/**
 * The Enum GenealogyEdgeType.
 */
public enum GenealogyEdgeType {
	
	/** The Definition on definition. */
	DefinitionOnDefinition,
	
	/** The Definition on deleted definition. */
	DefinitionOnDeletedDefinition,
	
	/** The Call on definition. */
	CallOnDefinition,
	
	/** The Deleted definition on definition. */
	DeletedDefinitionOnDefinition,
	
	/** The Deleted call on call. */
	DeletedCallOnCall,
	
	/** The Deleted call on deleted definition. */
	DeletedCallOnDeletedDefinition,
	
	/** The unknown. */
	UNKNOWN,
	
	/** The Modified definition on definition. */
	ModifiedDefinitionOnDefinition;
	
	/**
	 * As string array.
	 * 
	 * @param types
	 *            the types
	 * @return the string[]
	 */
	public static String[] asStringArray(final GenealogyEdgeType... types) {
		final String[] result = new String[types.length];
		for (int i = 0; i < types.length; ++i) {
			result[i] = types[i].name();
		}
		return result;
	}
}
