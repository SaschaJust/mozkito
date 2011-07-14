package de.unisaarland.cs.st.reposuite.clustering;

import java.util.LinkedList;
import java.util.List;

public abstract class ScoreAggregation<T> {
	
	private final List<MultilevelClusteringScoreVisitor<T>> scoreVisitors = new LinkedList<MultilevelClusteringScoreVisitor<T>>();
	
	public ScoreAggregation(final List<MultilevelClusteringScoreVisitor<T>> scoreVisitors) {
		this.scoreVisitors.addAll(scoreVisitors);
	}
	
	public abstract double aggregate(final List<Double> values);
	
	public abstract String getInfo();
	
	public List<MultilevelClusteringScoreVisitor<T>> getScoreVisitors() {
		return this.scoreVisitors;
	}
	
}
