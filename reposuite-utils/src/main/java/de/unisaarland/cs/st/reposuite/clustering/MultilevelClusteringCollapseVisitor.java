package de.unisaarland.cs.st.reposuite.clustering;

import java.util.Map;

public interface MultilevelClusteringCollapseVisitor<T> {
	
	public double getScore(final Cluster<T> newCluster,
	                       final Cluster<T> otherCluster,
	                       final Map<T, Map<T, Double>> originalScoreMatrix);
	
}
