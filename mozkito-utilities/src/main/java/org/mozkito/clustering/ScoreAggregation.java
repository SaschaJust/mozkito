/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package org.mozkito.clustering;

import java.util.List;

/**
 * The Class ScoreAggregation.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class ScoreAggregation<T> {
	
	/**
	 * Instantiates a new score aggregation.
	 */
	public ScoreAggregation() {
		
	}
	
	/**
	 * Aggregate.
	 * 
	 * @param values
	 *            the values
	 * @return the double
	 */
	public abstract double aggregate(final List<Double> values);
	
	/**
	 * Gets the info.
	 * 
	 * @return the info
	 */
	public abstract String getInfo();
}
