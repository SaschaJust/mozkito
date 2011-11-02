/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.clustering;

/**
 * The Interface MultilevelPartitioningScoreVisitor.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface MultilevelClusteringScoreVisitor<T> {
	
	/**
	 * Returns the highest possible score value
	 * 
	 * @return
	 */
	public double getMaxPossibleScore();
	
	/**
	 * Returns a confidence value between 0 and getMaxPossibleScore(). The
	 * higher the confidence, the stronger the voter indicates that the provided
	 * artifacts t1 and t2 should belong to the same cluster.
	 * 
	 * @param ts
	 *            the ts
	 * @param oldScore
	 *            the old score
	 * @return the new (manipulated) score
	 */
	public double getScore(final T t1,
	                       final T t2);
}
