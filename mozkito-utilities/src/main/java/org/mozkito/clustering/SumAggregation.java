/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.mozkito.clustering;

import java.util.List;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * The Class SumAggregation.
 * 
 * @param <T>
 *            the generic type
 */
public class SumAggregation<T> extends ScoreAggregation<T> {
	
	/**
	 * Instantiates a new sum aggregation.
	 */
	public SumAggregation() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.ScoreAggregation#aggregate(java.util.List)
	 */
	@Override
	public double aggregate(final List<Double> values) {
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		for (final Double v : values) {
			if (v != MultilevelClustering.IGNORE_SCORE) {
				stats.addValue(v);
			}
		}
		return stats.getSum();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.ScoreAggregation#getInfo()
	 */
	@Override
	public String getInfo() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Type: " + SumAggregation.class.getSimpleName());
		sb.append(FileUtils.lineSeparator);
		return sb.toString();
	}
	
}
