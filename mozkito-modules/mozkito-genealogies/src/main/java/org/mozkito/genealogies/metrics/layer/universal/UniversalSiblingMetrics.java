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
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import org.mozkito.genealogies.core.ChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalSiblingMetrics.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalSiblingMetrics<T> {
	
	/** The num sibling children. */
	private static String numSiblingChildren = "NumSiblingChildren";
	
	/** The avg sibling children. */
	private static String avgSiblingChildren = "AvgSiblingChildren";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(UniversalSiblingMetrics.numSiblingChildren);
		result.add(UniversalSiblingMetrics.avgSiblingChildren);
		return result;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal sibling metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalSiblingMetrics(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final T node) {
		final Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		final Collection<T> vertexParents = this.genealogy.getAllDependents(node);
		
		final DescriptiveStatistics siblingChildren = new DescriptiveStatistics();
		int num = 0;
		
		for (final T child : this.genealogy.getAllDependents(node)) {
			final Collection<T> childParents = this.genealogy.getAllDependents(child);
			final int size = CollectionUtils.intersection(vertexParents, childParents).size();
			if (size > 0) {
				++num;
			}
			siblingChildren.addValue(size);
		}
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(UniversalSiblingMetrics.numSiblingChildren, nodeId, num));
		result.add(new GenealogyMetricValue(UniversalSiblingMetrics.avgSiblingChildren, nodeId,
		                                    (siblingChildren.getN() < 1)
		                                                                ? 0
		                                                                : siblingChildren.getMean()));
		
		return result;
	}
}
