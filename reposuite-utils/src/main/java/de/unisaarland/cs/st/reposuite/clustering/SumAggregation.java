package de.unisaarland.cs.st.reposuite.clustering;

import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;



public class SumAggregation<T> extends ScoreAggregation<T> {
	
	
	public SumAggregation() {
		super();
	}
	
	@Override
	public double aggregate(final List<Double> values) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (Double v : values) {
			stats.addValue(v);
		}
		return stats.getSum();
	}
	
	@Override
	public String getInfo() {
		return "";
	}
	
}
