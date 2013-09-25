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

package org.mozkito.infozilla.elements;

/**
 * The Class FilterResult.
 * 
 * @param <T>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class FilterResult<T> extends Triple<Integer, Integer, T> {
	
	/**
	 * Instantiates a new filter result.
	 * 
	 * @param startPosition
	 *            the start position
	 * @param endPosition
	 *            the end position
	 * @param data
	 *            the data
	 */
	public FilterResult(final Integer startPosition, final Integer endPosition, final T data) {
		super(startPosition, endPosition, data);
	}
	
}
