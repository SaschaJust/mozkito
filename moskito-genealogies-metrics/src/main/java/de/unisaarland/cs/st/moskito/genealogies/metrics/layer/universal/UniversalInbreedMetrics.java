/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalInbreedMetrics.
 *
 * @param <T> the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
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
		Collection<String> result = new LinkedList<String>();
		result.add(numInbreedChildren);
		result.add(numInbreedParents);
		result.add(avgInbreedChildren);
		result.add(avgInbreedParents);
		return result;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal inbreed metrics.
	 *
	 * @param genealogy the genealogy
	 */
	public UniversalInbreedMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	/**
	 * Handle.
	 *
	 * @param node the node
	 * @return the collection
	 */
	@SuppressWarnings ("unchecked")
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		Collection<T> vertexParents = genealogy.getAllDependants(node);
		Collection<T> vertexChildren = genealogy.getAllDependants(node);
		
		DescriptiveStatistics inbreedChildrenStat = new DescriptiveStatistics();
		DescriptiveStatistics inbreedParentsStat = new DescriptiveStatistics();
		
		Collection<T> inbreedChildren = new HashSet<T>();
		Collection<T> inbreedParents = new HashSet<T>();
		
		for (T child : genealogy.getAllDependants(node)) {
			Collection<T> grandChildren = genealogy.getAllDependants(child);
			@SuppressWarnings ("rawtypes")
			Collection intersection = CollectionUtils.intersection(vertexChildren, grandChildren);
			inbreedChildren.addAll(intersection);
			inbreedChildrenStat.addValue(intersection.size());
		}
		
		for (T parent : genealogy.getAllParents(node)) {
			Collection<T> grandParents = genealogy.getAllParents(parent);
			@SuppressWarnings ("rawtypes")
			Collection intersection = CollectionUtils.intersection(vertexParents, grandParents);
			inbreedParents.addAll(intersection);
			inbreedParentsStat.addValue(intersection.size());
		}
		
		String nodeId = genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(numInbreedChildren, nodeId, inbreedChildren.size()));
		result.add(new GenealogyMetricValue(numInbreedParents, nodeId, inbreedParents.size()));
		
		result.add(new GenealogyMetricValue(avgInbreedChildren, nodeId,
		                                    (inbreedChildrenStat.getN() < 1)
		                                                                    ? 0
		                                                                    : inbreedChildrenStat.getMean()));
		result.add(new GenealogyMetricValue(avgInbreedParents, nodeId,
		                                    (inbreedParentsStat.getN() < 1)
		                                                                   ? 0
		                                                                   : inbreedParentsStat.getMean()));
		
		return result;
	}
}
