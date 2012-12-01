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
package org.mozkito.clustering;

import java.util.Map;

/**
 * The Interface MultilevelClusteringCollapseVisitor.
 * 
 * @param <T>
 *            the generic type
 */
public interface MultilevelClusteringCollapseVisitor<T> {
	
	/**
	 * Gets the score.
	 * 
	 * @param newCluster
	 *            the new cluster
	 * @param otherCluster
	 *            the other cluster
	 * @param originalScoreMatrix
	 *            the original score matrix
	 * @return the score
	 */
	double getScore(final Cluster<T> newCluster,
	                final Cluster<T> otherCluster,
	                final Map<T, Map<T, Double>> originalScoreMatrix);
	
}
