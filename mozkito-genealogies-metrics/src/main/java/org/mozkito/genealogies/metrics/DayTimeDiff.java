/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.mozkito.genealogies.metrics;

/**
 * The Interface DayTimeDiff.
 *
 * @param <T> the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public interface DayTimeDiff<T> {
	
	/**
	 * Days diff.
	 *
	 * @param t1 the t1
	 * @param t2 the t2
	 * @return the int
	 */
	public int daysDiff(T t1,
	                    T t2);
	
}
