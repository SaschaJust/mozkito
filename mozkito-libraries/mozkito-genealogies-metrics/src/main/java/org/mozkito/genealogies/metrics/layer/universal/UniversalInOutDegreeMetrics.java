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

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalInOutDegreeMetrics.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalInOutDegreeMetrics<T> {
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(UniversalInOutDegreeMetrics.inDegree);
		result.add(UniversalInOutDegreeMetrics.outDegree);
		return result;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/** The in degree. */
	private static String            inDegree  = "inDegree";
	
	/** The out degree. */
	private static String            outDegree = "outDegree";
	
	/**
	 * Instantiates a new universal in out degree metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalInOutDegreeMetrics(final ChangeGenealogy<T> genealogy) {
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
		final String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(UniversalInOutDegreeMetrics.outDegree, nodeId,
		                                    this.genealogy.outDegree(node)));
		result.add(new GenealogyMetricValue(UniversalInOutDegreeMetrics.inDegree, nodeId, this.genealogy.inDegree(node)));
		
		return result;
	}
}
