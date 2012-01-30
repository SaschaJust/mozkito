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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalDependencyMetrics. Returns a set of metric values
 * indicating the number of
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalAncestorMetrics<T> {
	
	/** The all dependants. */
	private static final String allDependants        = "NumDependants";
	
	/** The all dependants d2. */
	private static final String allDependantsD2      = "NumDependants_depth_2";
	
	/** The all dependants d3. */
	private static final String allDependantsD3        = "NumDependants_depth_3";
	
	/** The definition dependants. */
	private static final String definitionDependants = "NumDefinitionDependants";
	
	/** The definition dependants d2. */
	private static final String definitionDependantsD2 = "NumDefinitionDependants_depth_2";
	
	/** The definition dependants d3. */
	private static final String definitionDependantsD3 = "NumDefinitionDependants_depth_3";
	
	/** The call dependants. */
	private static final String callDependants       = "NumCallDependants";
	
	/** The call dependants d2. */
	private static final String callDependantsD2       = "NumCallDependants_depth_2";
	
	/** The call dependants d3. */
	private static final String callDependantsD3       = "NumCallDependants_depth_3";
	
	/** The metric names. */
	private static List<String>       metricNames;
	
	/**
	 * Compose metric name.
	 * 
	 * @param eType
	 *            the e type
	 * @param depth
	 *            the depth
	 * @return the string
	 */
	private static final String composeMetricName(GenealogyEdgeType eType, int depth) {
		if (depth < 2) {
			return "Num" + eType.toString() + "Dependants";
		}
		return "Num" + eType.toString() + "DependantsD" + depth;
	}
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		if ((metricNames != null) && (!metricNames.isEmpty())) {
			return metricNames;
		}
		metricNames = new LinkedList<String>();
		metricNames.add(allDependants);
		metricNames.add(allDependantsD2);
		metricNames.add(allDependantsD3);
		metricNames.add(definitionDependants);
		metricNames.add(definitionDependantsD2);
		metricNames.add(definitionDependantsD3);
		metricNames.add(callDependants);
		metricNames.add(callDependantsD2);
		metricNames.add(callDependantsD3);
		for (GenealogyEdgeType eType : GenealogyEdgeType.values()) {
			metricNames.add(composeMetricName(eType, 1));
			metricNames.add(composeMetricName(eType, 2));
			metricNames.add(composeMetricName(eType, 3));
		}
		return metricNames;
	}
	
	/** The genealogy. */
	private ChangeGenealogy<T> genealogy;
	
	/**
	 * Instantiates a new universal dependants metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalAncestorMetrics(ChangeGenealogy<T> genealogy){
		this.genealogy = genealogy;
	}
	
	/**
	 * Gets the num dependants.
	 * 
	 * @param t
	 *            the t
	 * @param depth
	 *            the depth
	 * @param types
	 *            the types
	 * @return the num dependants
	 */
	@SuppressWarnings("unchecked")
	private int getNumDependants(T t, int depth, GenealogyEdgeType... types) {
		
		int result = 0;
		List<T> nodes = new LinkedList<T>();
		List<T> nextNodes = new LinkedList<T>();
		Set<T> seenNodes = new HashSet<T>();
		nodes.add(t);
		
		
		if (depth != -1) {
			for (int i = 0; i < depth; ++i) {
				for (T node : nodes) {
					Collection<T> children = genealogy.getDependants(node, types);
					result += children.size();
					nextNodes.addAll(CollectionUtils.subtract(children, seenNodes));
					seenNodes.addAll(children);
				}
				nodes.clear();
				nodes.addAll(nextNodes);
				nextNodes.clear();
			}
		} else {
			while (!nodes.isEmpty()) {
				for (T node : nodes) {
					Collection<T> children = genealogy.getDependants(node, types);
					result += children.size();
					nextNodes.addAll(CollectionUtils.subtract(children, seenNodes));
					seenNodes.addAll(children);
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
	public Collection<GenealogyMetricValue> handle(T node) {
		List<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		//NumDependencies
		int numAllDependants = getNumDependants(node, 1, GenealogyEdgeType.values());
		result.add(new GenealogyMetricValue(allDependants, genealogy.getNodeId(node), numAllDependants));
		
		numAllDependants = getNumDependants(node, 2, GenealogyEdgeType.values());
		result.add(new GenealogyMetricValue(allDependantsD2, genealogy.getNodeId(node), numAllDependants));
		
		numAllDependants = getNumDependants(node, 3, GenealogyEdgeType.values());
		result.add(new GenealogyMetricValue(allDependantsD3, genealogy.getNodeId(node), numAllDependants));
		
		//for each GenealogyEdgeType
		for (GenealogyEdgeType eType : GenealogyEdgeType.values()) {
			int numDependants = getNumDependants(node, 1, eType);
			result.add(new GenealogyMetricValue(composeMetricName(eType, 1), genealogy.getNodeId(node), numDependants));
			numDependants = getNumDependants(node, 2, eType);
			result.add(new GenealogyMetricValue(composeMetricName(eType, 2), genealogy.getNodeId(node), numDependants));
			numDependants = getNumDependants(node, 3, eType);
			result.add(new GenealogyMetricValue(composeMetricName(eType, 3), genealogy.getNodeId(node), numDependants));
		}
		
		//Definition dependants
		int numDefinitionDependants = getNumDependants(node, 1, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
		result.add(new GenealogyMetricValue(definitionDependants, genealogy.getNodeId(node), numDefinitionDependants));
		numDefinitionDependants = getNumDependants(node, 2, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
		result.add(new GenealogyMetricValue(definitionDependantsD2, genealogy.getNodeId(node), numDefinitionDependants));
		numDefinitionDependants = getNumDependants(node, 3, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
		result.add(new GenealogyMetricValue(definitionDependantsD3, genealogy.getNodeId(node), numDefinitionDependants));
		
		//Call dependants
		int numCallDependants = getNumDependants(node, 1, GenealogyEdgeType.CallOnDefinition,
				GenealogyEdgeType.DeletedCallOnCall, GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		result.add(new GenealogyMetricValue(callDependants, genealogy.getNodeId(node), numCallDependants));
		numCallDependants = getNumDependants(node, 2, GenealogyEdgeType.CallOnDefinition,
				GenealogyEdgeType.DeletedCallOnCall, GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		result.add(new GenealogyMetricValue(callDependantsD2, genealogy.getNodeId(node), numCallDependants));
		numCallDependants = getNumDependants(node, 3, GenealogyEdgeType.CallOnDefinition,
				GenealogyEdgeType.DeletedCallOnCall, GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		result.add(new GenealogyMetricValue(callDependantsD3, genealogy.getNodeId(node), numCallDependants));
		
		//check for data integrity
		Condition.check(result.size() == getMetricNames().size(), "The number of "
				+ "generated dependency metrics differs from the number of metric names. "
				+ "Num of metric names: %s. Num of metric values: %s. "
				+ "Please check for miss matches. MetricNames=%s. Generated MetricValue=%s", getMetricNames().size(),
				result.size(), StringUtils.join(getMetricNames(), ","), StringUtils.join(result, ","));
		
		return result;
	}
	
}
