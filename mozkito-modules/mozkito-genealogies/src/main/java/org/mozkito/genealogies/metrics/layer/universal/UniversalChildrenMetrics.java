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

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import org.mozkito.genealogies.core.ChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalChildrenMetrics.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalChildrenMetrics<T> {
	
	/** The num children children. */
	private static String numChildrenChildren = "NumChildrenChildren";
	
	/** The num children out. */
	private static String numChildrenOut      = "NumChildrenOut";
	
	/** The avg children children. */
	private static String avgChildrenChildren = "AvgChildrenChildren";
	
	/** The avg children out. */
	private static String avgChildrenOut      = "AvgChildrenOut";
	
	/** The num children parents. */
	private static String numChildrenParents  = "NumChildrenParents";
	
	/** The num children in. */
	private static String numChildrenIn       = "NumChildrenIn";
	
	/** The avg children parents. */
	private static String avgChildrenParents  = "AvgChildrenParents";
	
	/** The avg children in. */
	private static String avgChildrenIn       = "AvgChildrenIn";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(UniversalChildrenMetrics.numChildrenChildren);
		result.add(UniversalChildrenMetrics.numChildrenOut);
		result.add(UniversalChildrenMetrics.avgChildrenChildren);
		result.add(UniversalChildrenMetrics.avgChildrenOut);
		result.add(UniversalChildrenMetrics.numChildrenParents);
		result.add(UniversalChildrenMetrics.numChildrenIn);
		result.add(UniversalChildrenMetrics.avgChildrenParents);
		result.add(UniversalChildrenMetrics.avgChildrenIn);
		return result;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal children metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalChildrenMetrics(final ChangeGenealogy<T> genealogy) {
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
		
		final DescriptiveStatistics childrenChildren = new DescriptiveStatistics();
		final DescriptiveStatistics childrenOut = new DescriptiveStatistics();
		final DescriptiveStatistics childrenParents = new DescriptiveStatistics();
		final DescriptiveStatistics childrenIn = new DescriptiveStatistics();
		
		for (final T child : this.genealogy.getAllDependents(node)) {
			childrenChildren.addValue(this.genealogy.getAllDependents(child).size());
			childrenOut.addValue(this.genealogy.outDegree(node));
			childrenParents.addValue(this.genealogy.getAllParents(child).size());
			childrenIn.addValue(this.genealogy.inDegree(node));
		}
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.numChildrenChildren, nodeId,
		                                    (childrenChildren.getN() < 1)
		                                                                 ? 0
		                                                                 : childrenChildren.getSum()));
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.avgChildrenChildren, nodeId,
		                                    (childrenChildren.getN() < 1)
		                                                                 ? 0
		                                                                 : childrenChildren.getMean()));
		
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.numChildrenOut, nodeId,
		                                    (childrenOut.getN() < 1)
		                                                            ? 0
		                                                            : childrenOut.getSum()));
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.avgChildrenOut, nodeId,
		                                    (childrenOut.getN() < 1)
		                                                            ? 0
		                                                            : childrenOut.getMean()));
		
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.numChildrenParents, nodeId,
		                                    (childrenParents.getN() < 1)
		                                                                ? 0
		                                                                : childrenParents.getSum()));
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.avgChildrenParents, nodeId,
		                                    (childrenParents.getN() < 1)
		                                                                ? 0
		                                                                : childrenParents.getMean()));
		
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.numChildrenIn, nodeId,
		                                    (childrenIn.getN() < 1)
		                                                           ? 0
		                                                           : childrenIn.getSum()));
		result.add(new GenealogyMetricValue(UniversalChildrenMetrics.avgChildrenIn, nodeId,
		                                    (childrenIn.getN() < 1)
		                                                           ? 0
		                                                           : childrenIn.getMean()));
		
		return result;
	}
}
