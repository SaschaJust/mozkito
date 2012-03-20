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

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalInOutDegreeMetrics<T> {
	
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(inDegree);
		result.add(outDegree);
		return result;
	}
	
	private final ChangeGenealogy<T> genealogy;
	private static String            inDegree  = "inDegree";
	private static String            outDegree = "outDegree";
	
	public UniversalInOutDegreeMetrics(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<GenealogyMetricValue> handle(final T node) {
		final Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		final String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(outDegree, nodeId, this.genealogy.outDegree(node)));
		result.add(new GenealogyMetricValue(inDegree, nodeId, this.genealogy.inDegree(node)));
		
		return result;
	}
}
