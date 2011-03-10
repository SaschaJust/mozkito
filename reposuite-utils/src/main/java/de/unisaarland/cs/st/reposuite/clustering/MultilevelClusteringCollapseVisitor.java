package de.unisaarland.cs.st.reposuite.clustering;


public interface MultilevelClusteringCollapseVisitor<T> {
	
	public double getScore(Cluster<T> newCluster, Cluster<T> otherCluster);
	
}
