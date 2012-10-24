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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.mozkito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalSiblingMetrics.
 *
 * @param <T> the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
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
		Collection<String> result = new LinkedList<String>();
		result.add(numSiblingChildren);
		result.add(avgSiblingChildren);
		return result;
	}
	
	/** The genealogy. */
	private ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal sibling metrics.
	 *
	 * @param genealogy the genealogy
	 */
	public UniversalSiblingMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	/**
	 * Handle.
	 *
	 * @param node the node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		Collection<T> vertexParents = this.genealogy.getAllDependants(node);
		
		DescriptiveStatistics siblingChildren = new DescriptiveStatistics();
		int num = 0;
		
		for (T child : this.genealogy.getAllDependants(node)) {
			Collection<T> childParents = this.genealogy.getAllDependants(child);
			int size = CollectionUtils.intersection(vertexParents, childParents).size();
			if (size > 0) {
				++num;
			}
			siblingChildren.addValue(size);
		}
		
		String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(numSiblingChildren, nodeId, num));
		result.add(new GenealogyMetricValue(avgSiblingChildren, nodeId,
		                                    (siblingChildren.getN() < 1)
		                                                                ? 0
		                                                                : siblingChildren.getMean()));
		
		return result;
	}
}
