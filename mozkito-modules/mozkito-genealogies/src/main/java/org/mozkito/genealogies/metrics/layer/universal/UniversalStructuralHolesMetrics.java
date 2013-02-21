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
 * The Class UniversalStructuralHolesMetrics.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalStructuralHolesMetrics<T> {
	
	/** The in eff size. */
	private static String inEffSize     = "InEffSize";
	
	/** The out eff size. */
	private static String outEffSize    = "OutEffSize";
	
	/** The eff size. */
	private static String effSize       = "EffSize";
	
	/** The in efficiency. */
	private static String inEfficiency  = "InEfficiency";
	
	/** The out efficiency. */
	private static String outEfficiency = "OutEfficiency";
	
	/** The efficiency. */
	private static String efficiency    = "Efficiency";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(UniversalStructuralHolesMetrics.inEffSize);
		result.add(UniversalStructuralHolesMetrics.outEffSize);
		result.add(UniversalStructuralHolesMetrics.effSize);
		
		result.add(UniversalStructuralHolesMetrics.inEfficiency);
		result.add(UniversalStructuralHolesMetrics.outEfficiency);
		result.add(UniversalStructuralHolesMetrics.efficiency);
		return result;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal structural holes metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalStructuralHolesMetrics(final ChangeGenealogy<T> genealogy) {
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
		
		final Collection<T> incoming = this.genealogy.getAllDependants(node);
		final Collection<T> outgoing = this.genealogy.getAllParents(node);
		@SuppressWarnings ("unchecked")
		final Collection<T> egoNetwork = CollectionUtils.union(incoming, outgoing);
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		final DescriptiveStatistics inEffStat = new DescriptiveStatistics();
		for (final T in : incoming) {
			// get the number of connections between in and all other incomings
			final Collection<T> inTies = this.genealogy.getAllDependants(in);
			inTies.addAll(this.genealogy.getAllParents(in));
			inEffStat.addValue(CollectionUtils.intersection(incoming, inTies).size());
		}
		final double inEgoSize = incoming.size();
		final double inEffValue = inEgoSize - ((inEffStat.getN() < 1)
		                                                             ? 0
		                                                             : inEffStat.getMean());
		result.add(new GenealogyMetricValue(UniversalStructuralHolesMetrics.inEffSize, nodeId, inEffValue));
		result.add(new GenealogyMetricValue(UniversalStructuralHolesMetrics.inEfficiency, nodeId,
		                                    (inEffValue / (inEgoSize + 1))));
		
		final DescriptiveStatistics outEffStat = new DescriptiveStatistics();
		for (final T out : outgoing) {
			// get the number of connections between in and all other outgoings
			final Collection<T> outTies = this.genealogy.getAllDependants(out);
			outTies.addAll(this.genealogy.getAllParents(out));
			outEffStat.addValue(CollectionUtils.intersection(outgoing, outTies).size());
		}
		final double outEgoSize = outgoing.size();
		final double outEffValue = outEgoSize - ((outEffStat.getN() < 1)
		                                                                ? 0
		                                                                : outEffStat.getMean());
		result.add(new GenealogyMetricValue(UniversalStructuralHolesMetrics.outEffSize, nodeId, outEffValue));
		result.add(new GenealogyMetricValue(UniversalStructuralHolesMetrics.outEfficiency, nodeId,
		                                    (outEffValue / (outEgoSize + 1))));
		
		final DescriptiveStatistics effStat = new DescriptiveStatistics();
		for (final T ego : egoNetwork) {
			// get the number of connections between in and all other ego-network
			final Collection<T> ties = this.genealogy.getAllDependants(ego);
			ties.addAll(this.genealogy.getAllParents(ego));
			effStat.addValue(CollectionUtils.intersection(egoNetwork, ties).size());
		}
		final double egoSize = egoNetwork.size();
		final double effValue = egoSize - ((effStat.getN() < 1)
		                                                       ? 0
		                                                       : effStat.getMean());
		result.add(new GenealogyMetricValue(UniversalStructuralHolesMetrics.effSize, nodeId, effValue));
		result.add(new GenealogyMetricValue(UniversalStructuralHolesMetrics.efficiency, nodeId,
		                                    (effValue / (egoSize + 1))));
		
		return result;
	}
}
