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


package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalChildrenMetrics<T> {
	
	private static String numChildrenChildren = "NumChildrenChildren";
	private static String numChildrenOut      = "NumChildrenOut";
	private static String avgChildrenChildren = "AvgChildrenChildren";
	private static String avgChildrenOut      = "AvgChildrenOut";
	private static String numChildrenParents  = "NumChildrenParents";
	private static String numChildrenIn       = "NumChildrenIn";
	private static String avgChildrenParents  = "AvgChildrenParents";
	private static String avgChildrenIn       = "AvgChildrenIn";
	public static Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
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
	
	private ChangeGenealogy<T> genealogy;
	
	public UniversalChildrenMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		DescriptiveStatistics childrenChildren = new DescriptiveStatistics();
		DescriptiveStatistics childrenOut = new DescriptiveStatistics();
		DescriptiveStatistics childrenParents = new DescriptiveStatistics();
		DescriptiveStatistics childrenIn = new DescriptiveStatistics();
		
		for (T child : genealogy.getAllDependants(node)) {
			childrenChildren.addValue(genealogy.getAllDependants(child).size());
			childrenOut.addValue(genealogy.outDegree(node));
			childrenParents.addValue(genealogy.getAllParents(child).size());
			childrenIn.addValue(genealogy.inDegree(node));
		}
		
		String nodeId = genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(numChildrenChildren, nodeId, (childrenChildren.getN() < 1) ? 0 : childrenChildren.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenChildren, nodeId, (childrenChildren.getN() < 1) ? 0 : childrenChildren.getMean()));
		
		result.add(new GenealogyMetricValue(numChildrenOut, nodeId, (childrenOut.getN() < 1) ? 0 : childrenOut.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenOut, nodeId, (childrenOut.getN() < 1) ? 0 : childrenOut.getMean()));
		
		result.add(new GenealogyMetricValue(numChildrenParents, nodeId, (childrenParents.getN() < 1) ? 0 : childrenParents.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenParents, nodeId, (childrenParents.getN() < 1) ? 0 : childrenParents.getMean()));
		
		result.add(new GenealogyMetricValue(numChildrenIn, nodeId, (childrenIn.getN() < 1) ? 0 : childrenIn.getSum()));
		result.add(new GenealogyMetricValue(avgChildrenIn, nodeId, (childrenIn.getN() < 1) ? 0 : childrenIn.getMean()));
		
		return result;
	}
}
