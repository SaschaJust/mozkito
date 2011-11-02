package de.unisaarland.cs.st.moskito.untangling.aggregation;

import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.clustering.ScoreAggregation;


public class VarSumAggregation<T> extends ScoreAggregation<T> {
	
	
	public VarSumAggregation() {
		super();
	}
	
	@Override
	public double aggregate(final List<Double> values) {
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (Double v : values) {
			if (v != MultilevelClustering.IGNORE_SCORE) {
				stats.addValue(v);
			}
		}
		
		double sum = stats.getSum();
		double avg = stats.getMean();
		double avgDiff = 0d;
		for (double value : stats.getValues()) {
			avgDiff += Math.abs(value - avg);
		}
		avgDiff /= stats.getN();
		return sum - avgDiff;
		
	}
	
	@Override
	public String getInfo() {
		return "";
	}
	
}
