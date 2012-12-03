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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalDependencyMetrics. Returns a set of metric values indicating the number of
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalParentsMetrics<T> {
	
	/** The cache. */
	Map<T, Map<Integer, Map<String, Set<T>>>> cache                 = new HashMap<T, Map<Integer, Map<String, Set<T>>>>();
	
	/** The all parents. */
	private static final String               ALL_PARENTS           = "NumParents";
	
	/** The all parents d2. */
	private static final String               ALL_PARENTS_D2        = "NumParents_depth_2";
	
	/** The all parents d3. */
	private static final String               ALL_PARENTS_D3        = "NumParents_depth_3";
	
	/** The definition parents. */
	private static final String               DEFINITION_PARENTS    = "NumDefinitionParents";
	
	/** The definition parents d2. */
	private static final String               DEFINITION_PARENTS_D2 = "NumDefinitionParents_depth_2";
	
	/** The definition parents d3. */
	private static final String               DEFINITION_PARENTS_D3 = "NumDefinitionParents_depth_3";
	
	/** The call parents. */
	private static final String               CALL_PARENTS          = "NumCallParents";
	
	/** The call parents d2. */
	private static final String               CALL_PARENTS_D2       = "NumCallParents_depth_2";
	
	/** The call parents d3. */
	private static final String               CALL_PARENTS_D3       = "NumCallParents_depth_3";
	
	/** The metric names. */
	private static List<String>               metricNames;
	
	/**
	 * Compose metric name.
	 * 
	 * @param eType
	 *            the e type
	 * @param depth
	 *            the depth
	 * @return the string
	 */
	private static String composeMetricName(final GenealogyEdgeType eType,
	                                        final int depth) {
		if (depth > 1) {
			return "Num" + eType.toString() + "ParentsD" + depth;
		} else if (depth < 0) {
			return "NumGlobal" + eType.toString() + "Parents";
		}
		return "Num" + eType.toString() + "Parents";
	}
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		if ((UniversalParentsMetrics.metricNames != null) && (!UniversalParentsMetrics.metricNames.isEmpty())) {
			return UniversalParentsMetrics.metricNames;
		}
		UniversalParentsMetrics.metricNames = new LinkedList<String>();
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.ALL_PARENTS);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.ALL_PARENTS_D2);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.ALL_PARENTS_D3);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.DEFINITION_PARENTS);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.DEFINITION_PARENTS_D2);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.DEFINITION_PARENTS_D3);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.CALL_PARENTS);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.CALL_PARENTS_D2);
		UniversalParentsMetrics.metricNames.add(UniversalParentsMetrics.CALL_PARENTS_D3);
		for (final GenealogyEdgeType eType : GenealogyEdgeType.values()) {
			UniversalParentsMetrics.metricNames.add(composeMetricName(eType, 1));
			UniversalParentsMetrics.metricNames.add(composeMetricName(eType, -1));
			UniversalParentsMetrics.metricNames.add(composeMetricName(eType, 2));
			UniversalParentsMetrics.metricNames.add(composeMetricName(eType, 3));
		}
		return UniversalParentsMetrics.metricNames;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal parents metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalParentsMetrics(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	/**
	 * Gets the num parents.
	 * 
	 * @param t
	 *            the t
	 * @param depth
	 *            the maximal depth used to determine parents. If set to -1, all parents will be used.
	 * @param types
	 *            the types
	 * @return the num parents
	 */
	@SuppressWarnings ("unchecked")
	private int getNumParents(final T t,
	                          final int depth,
	                          final GenealogyEdgeType... types) {
		
		int result = 0;
		final List<T> nodes = new LinkedList<T>();
		final List<T> nextNodes = new LinkedList<T>();
		final Set<T> seenNodes = new HashSet<T>();
		nodes.add(t);
		
		if (depth != -1) {
			for (int i = 0; i < depth; ++i) {
				for (final T node : nodes) {
					final Collection<T> parents = this.genealogy.getParents(node, types);
					result += parents.size();
					nextNodes.addAll(CollectionUtils.subtract(parents, seenNodes));
					seenNodes.addAll(parents);
				}
				nodes.clear();
				nodes.addAll(nextNodes);
				nextNodes.clear();
			}
		} else {
			while (!nodes.isEmpty()) {
				for (final T node : nodes) {
					final Collection<T> parents = this.genealogy.getParents(node, types);
					result += parents.size();
					nextNodes.addAll(CollectionUtils.subtract(parents, seenNodes));
					seenNodes.addAll(parents);
				}
				nodes.clear();
				nodes.addAll(nextNodes);
				nextNodes.clear();
			}
		}
		
		return result;
	}
	
	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final T node) {
		final List<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		// NumDependencies
		int numAllParents = getNumParents(node, 1, GenealogyEdgeType.values());
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.ALL_PARENTS, this.genealogy.getNodeId(node),
		                                    numAllParents));
		
		numAllParents = getNumParents(node, 2, GenealogyEdgeType.values());
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.ALL_PARENTS_D2, this.genealogy.getNodeId(node),
		                                    numAllParents));
		
		numAllParents = getNumParents(node, 3, GenealogyEdgeType.values());
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.ALL_PARENTS_D3, this.genealogy.getNodeId(node),
		                                    numAllParents));
		
		// for each GenealogyEdgeType
		for (final GenealogyEdgeType eType : GenealogyEdgeType.values()) {
			int numParents = getNumParents(node, 1, eType);
			result.add(new GenealogyMetricValue(composeMetricName(eType, 1), this.genealogy.getNodeId(node), numParents));
			numParents = getNumParents(node, -1, eType);
			result.add(new GenealogyMetricValue(composeMetricName(eType, -1), this.genealogy.getNodeId(node),
			                                    numParents));
			numParents = getNumParents(node, 2, eType);
			result.add(new GenealogyMetricValue(composeMetricName(eType, 2), this.genealogy.getNodeId(node), numParents));
			numParents = getNumParents(node, 3, eType);
			result.add(new GenealogyMetricValue(composeMetricName(eType, 3), this.genealogy.getNodeId(node), numParents));
		}
		
		// Definition dependants
		int numDefinitionParents = getNumParents(node, 1, GenealogyEdgeType.DefinitionOnDefinition,
		                                         GenealogyEdgeType.DefinitionOnDeletedDefinition,
		                                         GenealogyEdgeType.DeletedDefinitionOnDefinition);
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.DEFINITION_PARENTS, this.genealogy.getNodeId(node),
		                                    numDefinitionParents));
		numDefinitionParents = getNumParents(node, 2, GenealogyEdgeType.DefinitionOnDefinition,
		                                     GenealogyEdgeType.DefinitionOnDeletedDefinition,
		                                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.DEFINITION_PARENTS_D2,
		                                    this.genealogy.getNodeId(node), numDefinitionParents));
		numDefinitionParents = getNumParents(node, 3, GenealogyEdgeType.DefinitionOnDefinition,
		                                     GenealogyEdgeType.DefinitionOnDeletedDefinition,
		                                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.DEFINITION_PARENTS_D3,
		                                    this.genealogy.getNodeId(node), numDefinitionParents));
		
		// Call dependants
		int numCallParents = getNumParents(node, 1, GenealogyEdgeType.CallOnDefinition,
		                                   GenealogyEdgeType.DeletedCallOnCall,
		                                   GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.CALL_PARENTS, this.genealogy.getNodeId(node),
		                                    numCallParents));
		numCallParents = getNumParents(node, 2, GenealogyEdgeType.CallOnDefinition,
		                               GenealogyEdgeType.DeletedCallOnCall,
		                               GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.CALL_PARENTS_D2, this.genealogy.getNodeId(node),
		                                    numCallParents));
		numCallParents = getNumParents(node, 3, GenealogyEdgeType.CallOnDefinition,
		                               GenealogyEdgeType.DeletedCallOnCall,
		                               GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		result.add(new GenealogyMetricValue(UniversalParentsMetrics.CALL_PARENTS_D3, this.genealogy.getNodeId(node),
		                                    numCallParents));
		
		// check for data integrity
		Condition.check(result.size() == getMetricNames().size(), "The number of "
		                        + "generated dependency metrics differs from the number of metric names. "
		                        + "Num of metric names: %s. Num of metric values: %s. "
		                        + "Please check for miss matches. MetricNames=%s. Generated MetricValue=%s",
		                getMetricNames().size(),
		                result.size(), StringUtils.join(getMetricNames(), ","), StringUtils.join(result, ","));
		
		return result;
	}
	
}
