/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.clustering;

import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;

public class MaxCollapseVisitor<T> implements MultilevelClusteringCollapseVisitor<T> {
	
	@Override
	public double getScore(final Cluster<T> newCluster,
	                       final Cluster<T> otherCluster,
	                       final Map<T, Map<T, Double>> originalScoreMatrix) {
		
		double max = 0;
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
				if (d > max) {
					max = d;
				}
			}
		}
		return max;
	}
	
}
