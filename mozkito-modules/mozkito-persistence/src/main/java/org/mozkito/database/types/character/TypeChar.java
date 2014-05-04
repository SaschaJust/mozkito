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
 * The Class TypeChar.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TypeChar extends CharacterType {
	
	/** The length. */
	public final long length;
	
	/**
	 * Instantiates a new type char.
	 * 
	 * @param length
	 *            the length
	 */
	public TypeChar(final long length) {
		PRECONDITIONS: {
			if (length <= 0) {
				throw new IllegalArgumentException();
			}
		}
		
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
	
}
