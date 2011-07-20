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
package de.unisaarland.cs.st.reposuite.clustering;

import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;

public class ThresholdRatioCollapseVisitor<T> implements MultilevelClusteringCollapseVisitor<T> {
	
	@Override
	public double getScore(final Cluster<T> newCluster,
			final Cluster<T> otherCluster,
			final Map<T, Map<T, Double>> originalScoreMatrix) {
		
		double sum = 0;
		double size = 0;
		for (T t1 : newCluster.getAllElements()) {
			for (T t2 : otherCluster.getAllElements()) {
				double d = 0;
				if ((originalScoreMatrix.containsKey(t1)) && (originalScoreMatrix.get(t1).containsKey(t2))) {
					d = originalScoreMatrix.get(t1).get(t2);
				} else if ((originalScoreMatrix.containsKey(t2)) && (originalScoreMatrix.get(t2).containsKey(t1))) {
					d = originalScoreMatrix.get(t2).get(t1);
				} else {
					throw new UnrecoverableError("The orginal score matrix must contain all basic elements!");
				}
				if (d > 0.5) {
					sum += 1;
				}
				size += 1;
			}
		}
		return sum / size;
	}
	
}