package de.unisaarland.cs.st.moskito.clustering;

import java.util.List;

public abstract class ScoreAggregation<T> {
	
	public ScoreAggregation() {

	}
	
	public abstract double aggregate(final List<Double> values);
	
	public abstract String getInfo();
}
