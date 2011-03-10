package de.unisaarland.cs.st.reposuite.clustering;


public class BaseCluster<T> extends Cluster<T> {
	
	public BaseCluster(final T t1, final T t2, final double score) {
		super(new VirtualCluster<T>(t1), new VirtualCluster<T>(t2), score);
	}
	
}
