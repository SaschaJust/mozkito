/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.utililities.diff;

import java.util.HashSet;

import difflib.Chunk;

/**
 * The Class DiffUtils.
 */
public class DiffUtils {
	
	/**
	 * Gets the line numbers.
	 * 
	 * @param chunk
	 *            the chunk
	 * @return the line numbers
	 */
	public static HashSet<Integer> getLineNumbers(final Chunk chunk) {
		final HashSet<Integer> result = new HashSet<Integer>();
		final int startPos = chunk.getPosition();
		for (int i = 0; i < chunk.size(); ++i) {
			result.add(startPos + i + 1);
		}
		return result;
	}
	
}
