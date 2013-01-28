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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import edu.uci.ics.jung.algorithms.scoring.PageRank;

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.utils.JungGenealogyGraph;
import org.mozkito.genealogies.utils.JungGenealogyGraph.Edge;

/**
 * The Class UniversalPageRankMetric.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalPageRankMetric<T> {
	
	/** The page rank. */
	private static final String PAGE_RANK_NAME = "pageRank";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(UniversalPageRankMetric.PAGE_RANK_NAME);
		return metricNames;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T>   genealogy;
	
	/** The page rank. */
	private final PageRank<T, Edge<T>> pageRank;
	
	/**
	 * Instantiates a new universal page rank metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalPageRankMetric(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		final JungGenealogyGraph<T> jungGraph = new JungGenealogyGraph<T>(genealogy);
		this.pageRank = new PageRank<T, JungGenealogyGraph.Edge<T>>(jungGraph, 0.1);
	}
	
	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @param finalNode
	 *            the final node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final T node,
	                                               final boolean finalNode) {
		final Double vertexScore = this.pageRank.getVertexScore(node);
		final Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		result.add(new GenealogyMetricValue(UniversalPageRankMetric.PAGE_RANK_NAME, this.genealogy.getNodeId(node),
		                                    vertexScore));
		return result;
	}
}
