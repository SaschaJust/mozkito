package de.unisaarland.cs.st.reposuite.untangling.aggregation;

import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.clustering.ScoreAggregation;


public class VarSumAggregation<T> extends ScoreAggregation<T> {
	
	
	public VarSumAggregation(final List<MultilevelClusteringScoreVisitor<T>> scoreVisitors) {
		super(scoreVisitors);
	}
	
	@Override
	public double aggregate(final List<Double> values) {
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (Double v : values) {
			stats.addValue(v);
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
