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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalInOutDegreeMetrics<T> {
	
	public static Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(avgInDegree);
		result.add(avgOutDegree);
		return result;
	}
	
	private ChangeGenealogy<T> genealogy;
	private static String      avgInDegree  = "AvgInDegree";
	private static String      avgOutDegree = "AvgOutDegree";
	
	public UniversalInOutDegreeMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		String nodeId = genealogy.getNodeId(node);
		
		DescriptiveStatistics outDegreeStat = new DescriptiveStatistics();
		
		for (T parent : genealogy.getAllParents(node)) {
			outDegreeStat.addValue(genealogy.getEdges(node, parent).size());
		}
		result.add(new GenealogyMetricValue(avgOutDegree, nodeId, outDegreeStat.getMean()));
		
		DescriptiveStatistics inDegreeStat = new DescriptiveStatistics();
		for (T child : genealogy.getAllDependants(node)) {
			inDegreeStat.addValue(genealogy.getEdges(child, node).size());
		}
		result.add(new GenealogyMetricValue(avgInDegree, nodeId, inDegreeStat.getMean()));
		
		return result;
	}
}
