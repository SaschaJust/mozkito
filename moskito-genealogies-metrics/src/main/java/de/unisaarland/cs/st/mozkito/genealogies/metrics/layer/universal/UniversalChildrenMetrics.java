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

package de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.mozkito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalChildrenMetrics.
 *
 * @param <T> the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
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
		result.add(numChildrenChildren);
		result.add(numChildrenOut);
		result.add(avgChildrenChildren);
		result.add(avgChildrenOut);
		result.add(numChildrenParents);
		result.add(numChildrenIn);
		result.add(avgChildrenParents);
		result.add(avgChildrenIn);
		return result;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal children metrics.
	 *
	 * @param genealogy the genealogy
	 */
	public UniversalChildrenMetrics(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	/**
	 * Handle.
	 *
	 * @param node the node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final T node) {
		final Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		final DescriptiveStatistics childrenChildren = new DescriptiveStatistics();
		final DescriptiveStatistics childrenOut = new DescriptiveStatistics();
		final DescriptiveStatistics childrenParents = new DescriptiveStatistics();
		final DescriptiveStatistics childrenIn = new DescriptiveStatistics();
		
		for (final T child : this.genealogy.getAllDependants(node)) {
			childrenChildren.addValue(this.genealogy.getAllDependants(child).size());
			childrenOut.addValue(this.genealogy.outDegree(node));
			childrenParents.addValue(this.genealogy.getAllParents(child).size());
			childrenIn.addValue(this.genealogy.inDegree(node));
		}
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(numChildrenChildren, nodeId,
		                                    (childrenChildren.getN() < 1)
		                                                                 ? 0
		                                                                 : childrenChildren.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenChildren, nodeId,
		                                    (childrenChildren.getN() < 1)
		                                                                 ? 0
		                                                                 : childrenChildren.getMean()));
		
		result.add(new GenealogyMetricValue(numChildrenOut, nodeId, (childrenOut.getN() < 1)
		                                                                                    ? 0
		                                                                                    : childrenOut.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenOut, nodeId, (childrenOut.getN() < 1)
		                                                                                    ? 0
		                                                                                    : childrenOut.getMean()));
		
		result.add(new GenealogyMetricValue(numChildrenParents, nodeId,
		                                    (childrenParents.getN() < 1)
		                                                                ? 0
		                                                                : childrenParents.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenParents, nodeId,
		                                    (childrenParents.getN() < 1)
		                                                                ? 0
		                                                                : childrenParents.getMean()));
		
		result.add(new GenealogyMetricValue(numChildrenIn, nodeId, (childrenIn.getN() < 1)
		                                                                                  ? 0
		                                                                                  : childrenIn.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenIn, nodeId, (childrenIn.getN() < 1)
		                                                                                  ? 0
		                                                                                  : childrenIn.getMean()));
		
		return result;
	}
}
