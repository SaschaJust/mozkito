package de.unisaarland.cs.st.reposuite.clustering;

import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;

public class MinCollapseVisitor<T> implements MultilevelClusteringCollapseVisitor<T> {
	
	@Override
	public double getScore(final Cluster<T> newCluster,
	                       final Cluster<T> otherCluster,
	                       final Map<T, Map<T, Double>> originalScoreMatrix) {
		
		double min = Double.MAX_VALUE;
		for (T t1 : newCluster.getAllElements()) {
			for (T t2 : otherCluster.getAllElements()) {
				double d = Double.MAX_VALUE;
				if ((originalScoreMatrix.containsKey(t1)) && (originalScoreMatrix.get(t1).containsKey(t2))) {
					d = originalScoreMatrix.get(t1).get(t2);
				} else if ((originalScoreMatrix.containsKey(t2)) && (originalScoreMatrix.get(t2).containsKey(t1))) {
					d = originalScoreMatrix.get(t2).get(t1);
				} else {
					throw new UnrecoverableError("The orginal score matrix must contain all basic elements!");
				}
				if (d < min) {
					min = d;
				}
			}
		}
		return min;
	}
	
}
