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

package org.mozkito.database.types.character;

import org.mozkito.database.types.CharacterType;

/**
 * The Class DBTypeVarChar.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TypeVarChar extends CharacterType {
	
	/** The length. */
	private final long length;
	
	/**
	 * Instantiates a new dB type var char.
	 * 
	 * @param length
	 *            the length
	 */
	public TypeVarChar(final long length) {
		this.length = length;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.types.Type#size()
	 */
	@Override
	public Long size() {
		return this.length;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toSQL() {
		return "VARCHAR(" + this.length + ")";
	}
}
