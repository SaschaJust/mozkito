/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
