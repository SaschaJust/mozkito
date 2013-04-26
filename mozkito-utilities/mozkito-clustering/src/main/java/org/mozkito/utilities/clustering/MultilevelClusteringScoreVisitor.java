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
package org.mozkito.utilities.clustering;

/**
 * The Interface MultilevelPartitioningScoreVisitor.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public interface MultilevelClusteringScoreVisitor<T> {
	
	/**
	 * Close.
	 */
	void close();
	
	/**
	 * Returns the highest possible score value.
	 * 
	 * @return the max possible score
	 */
	double getMaxPossibleScore();
	
	/**
	 * Returns a confidence value between 0 and getMaxPossibleScore(). The higher the confidence, the stronger the voter
	 * indicates that the provided artifacts t1 and t2 should belong to the same cluster.
	 * 
	 * @param t1
	 *            the t1
	 * @param t2
	 *            the t2
	 * @return the new (manipulated) score
	 */
	double getScore(final T t1,
	                final T t2);
}
