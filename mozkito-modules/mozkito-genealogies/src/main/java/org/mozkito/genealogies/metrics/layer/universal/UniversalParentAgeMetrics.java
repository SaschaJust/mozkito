/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import org.mozkito.genealogies.core.ChangeGenealogy;
import org.mozkito.genealogies.metrics.DayTimeDiff;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalParentAgeMetrics.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalParentAgeMetrics<T> {
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/** The day comparator. */
	private final DayTimeDiff<T>     dayComparator;
	
	/** The avg parent age. */
	private static String            avgParentAge = "avgParentAge";
	
	/** The min parent age. */
	private static String            minParentAge = "minParentAge";
	
	/** The max parent age. */
	private static String            maxParentAge = "maxParentAge";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static final Collection<String> getMetricNames() {
		final Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(UniversalParentAgeMetrics.avgParentAge);
		metricNames.add(UniversalParentAgeMetrics.minParentAge);
		metricNames.add(UniversalParentAgeMetrics.maxParentAge);
		return metricNames;
	}
	
	/**
	 * Instantiates a new universal parent age metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 * @param dayComparator
	 *            the day comparator
	 */
	public UniversalParentAgeMetrics(final ChangeGenealogy<T> genealogy, final DayTimeDiff<T> dayComparator) {
		this.genealogy = genealogy;
		this.dayComparator = dayComparator;
	}
	
	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @return the collection
	 */
	public final Collection<GenealogyMetricValue> handle(final T node) {
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(3);
		
		final String nodeId = this.genealogy.getNodeId(node);
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (final T parent : this.genealogy.getAllParents(node)) {
			stats.addValue(this.dayComparator.daysDiff(node, parent));
		}
		
		metricValues.add(new GenealogyMetricValue(UniversalParentAgeMetrics.avgParentAge, nodeId,
		                                          (stats.getN() < 1)
		                                                            ? 0
		                                                            : stats.getMean()));
		metricValues.add(new GenealogyMetricValue(UniversalParentAgeMetrics.maxParentAge, nodeId,
		                                          (stats.getN() < 1)
		                                                            ? 0
		                                                            : stats.getMax()));
		metricValues.add(new GenealogyMetricValue(UniversalParentAgeMetrics.minParentAge, nodeId,
		                                          (stats.getN() < 1)
		                                                            ? 0
		                                                            : stats.getMin()));
		
		return metricValues;
	}
	
}
