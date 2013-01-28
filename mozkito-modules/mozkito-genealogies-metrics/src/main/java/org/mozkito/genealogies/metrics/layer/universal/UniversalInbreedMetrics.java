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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalInbreedMetrics.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalInbreedMetrics<T> {
	
	/** The num inbreed children. */
	private static String numInbreedChildren = "NumInbreedChildren";
	
	/** The num inbreed parents. */
	private static String numInbreedParents  = "NumInbreedParents";
	
	/** The avg inbreed children. */
	private static String avgInbreedChildren = "AvgInbreedChildren";
	
	/** The avg inbreed parents. */
	private static String avgInbreedParents  = "AvgInbreedParents";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(UniversalInbreedMetrics.numInbreedChildren);
		result.add(UniversalInbreedMetrics.numInbreedParents);
		result.add(UniversalInbreedMetrics.avgInbreedChildren);
		result.add(UniversalInbreedMetrics.avgInbreedParents);
		return result;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal inbreed metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalInbreedMetrics(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @return the collection
	 */
	@SuppressWarnings ("unchecked")
	public Collection<GenealogyMetricValue> handle(final T node) {
		final Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		final Collection<T> vertexParents = this.genealogy.getAllDependants(node);
		final Collection<T> vertexChildren = this.genealogy.getAllDependants(node);
		
		final DescriptiveStatistics inbreedChildrenStat = new DescriptiveStatistics();
		final DescriptiveStatistics inbreedParentsStat = new DescriptiveStatistics();
		
		final Collection<T> inbreedChildren = new HashSet<T>();
		final Collection<T> inbreedParents = new HashSet<T>();
		
		for (final T child : this.genealogy.getAllDependants(node)) {
			final Collection<T> grandChildren = this.genealogy.getAllDependants(child);
			@SuppressWarnings ("rawtypes")
			final Collection intersection = CollectionUtils.intersection(vertexChildren, grandChildren);
			inbreedChildren.addAll(intersection);
			inbreedChildrenStat.addValue(intersection.size());
		}
		
		for (final T parent : this.genealogy.getAllParents(node)) {
			final Collection<T> grandParents = this.genealogy.getAllParents(parent);
			@SuppressWarnings ("rawtypes")
			final Collection intersection = CollectionUtils.intersection(vertexParents, grandParents);
			inbreedParents.addAll(intersection);
			inbreedParentsStat.addValue(intersection.size());
		}
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(UniversalInbreedMetrics.numInbreedChildren, nodeId, inbreedChildren.size()));
		result.add(new GenealogyMetricValue(UniversalInbreedMetrics.numInbreedParents, nodeId, inbreedParents.size()));
		
		result.add(new GenealogyMetricValue(UniversalInbreedMetrics.avgInbreedChildren, nodeId,
		                                    (inbreedChildrenStat.getN() < 1)
		                                                                    ? 0
		                                                                    : inbreedChildrenStat.getMean()));
		result.add(new GenealogyMetricValue(UniversalInbreedMetrics.avgInbreedParents, nodeId,
		                                    (inbreedParentsStat.getN() < 1)
		                                                                   ? 0
		                                                                   : inbreedParentsStat.getMean()));
		
		return result;
	}
}
