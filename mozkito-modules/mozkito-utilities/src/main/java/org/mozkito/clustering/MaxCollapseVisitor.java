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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

/**
 * The Class MaxCollapseVisitor.
 * 
 * @param <T>
 *            the generic type
 */
public class MaxCollapseVisitor<T> implements MultilevelClusteringCollapseVisitor<T> {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringCollapseVisitor#getScore(org.mozkito.clustering.Cluster,
	 * org.mozkito.clustering.Cluster, java.util.Map)
	 */
	@Override
	public double getScore(final Cluster<T> newCluster,
	                       final Cluster<T> otherCluster,
	                       final Map<T, Map<T, Double>> originalScoreMatrix) {
		
		double max = 0;
		for (final T t1 : newCluster.getAllElements()) {
			for (final T t2 : otherCluster.getAllElements()) {
				double d = 0;
				if ((originalScoreMatrix.containsKey(t1)) && (originalScoreMatrix.get(t1).containsKey(t2))) {
					d = originalScoreMatrix.get(t1).get(t2);
				} else if ((originalScoreMatrix.containsKey(t2)) && (originalScoreMatrix.get(t2).containsKey(t1))) {
					d = originalScoreMatrix.get(t2).get(t1);
				} else {
					throw new UnrecoverableError("The orginal score matrix must contain all basic elements!");
				}
				if (d > max) {
					max = d;
				}
			}
		}
		return max;
	}
	
}
