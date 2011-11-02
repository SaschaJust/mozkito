package de.unisaarland.cs.st.moskito.clustering;

import java.util.List;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;



public class SumAggregation<T> extends ScoreAggregation<T> {
	
	
	public SumAggregation() {
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
		return stats.getSum();
	}
	
	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type: " + SumAggregation.class.getSimpleName());
		sb.append(FileUtils.lineSeparator);
		return sb.toString();
	}
	
}
