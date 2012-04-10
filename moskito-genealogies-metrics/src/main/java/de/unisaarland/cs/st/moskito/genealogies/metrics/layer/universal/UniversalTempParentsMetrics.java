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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalTempParentsMetrics.
 *
 * @param <T> the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalTempParentsMetrics<T> {
	
	/** The max temp parent depth1. */
	private static String maxTempParentDepth1  = "maxTempParentDepth_1";
	
	/** The max temp parent depth2. */
	private static String maxTempParentDepth2  = "maxTempParentDepth_2";
	
	/** The max temp parent depth5. */
	private static String maxTempParentDepth5  = "maxTempParentDepth_5";
	
	/** The max temp parent depth10. */
	private static String maxTempParentDepth10 = "maxTempParentDepth_10";
	
	/** The max temp parent depth14. */
	private static String maxTempParentDepth14 = "maxTempParentDepth_14";
	
	/** The num temp parents1. */
	private static String numTempParents1      = "numTempParents_1";
	
	/** The num temp parents2. */
	private static String numTempParents2      = "numTempParents_2";
	
	/** The num temp parents5. */
	private static String numTempParents5      = "numTempParents_5";
	
	/** The num temp parents10. */
	private static String numTempParents10     = "numTempParents_10";
	
	/** The num temp parents14. */
	private static String numTempParents14     = "numTempParents_14";
	
	/**
	 * Gets the metric names.
	 *
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(maxTempParentDepth1);
		result.add(maxTempParentDepth2);
		result.add(maxTempParentDepth5);
		result.add(maxTempParentDepth10);
		result.add(maxTempParentDepth14);
		result.add(numTempParents1);
		result.add(numTempParents2);
		result.add(numTempParents5);
		result.add(numTempParents10);
		result.add(numTempParents14);
		return result;
	}
	
	/** The genealogy. */
	private ChangeGenealogy<T> genealogy;
	
	/** The day time diff. */
	private DayTimeDiff<T>     dayTimeDiff;
	
	/** The parents_1. */
	private Set<String>        parents_1  = new HashSet<String>();
	
	/** The parents_2. */
	private Set<String>        parents_2  = new HashSet<String>();
	
	/** The parents_5. */
	private Set<String>        parents_5  = new HashSet<String>();
	
	/** The parents_10. */
	private Set<String>        parents_10 = new HashSet<String>();
	
	/** The parents_14. */
	private Set<String>        parents_14 = new HashSet<String>();
	
	/**
	 * Instantiates a new universal temp parents metrics.
	 *
	 * @param genealogy the genealogy
	 * @param dayTimeDiff the day time diff
	 */
	public UniversalTempParentsMetrics(ChangeGenealogy<T> genealogy, DayTimeDiff<T> dayTimeDiff) {
		this.genealogy = genealogy;
		this.dayTimeDiff = dayTimeDiff;
	}
	
	/**
	 * Handle.
	 *
	 * @param node the node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new ArrayList<GenealogyMetricValue>(10);
		
		this.parents_1.clear();
		this.parents_2.clear();
		this.parents_5.clear();
		this.parents_10.clear();
		this.parents_14.clear();
		
		int[] longestPaths = longestPath(node, node, new HashSet<T>());
		
		this.parents_2.addAll(this.parents_1);
		this.parents_5.addAll(this.parents_2);
		this.parents_10.addAll(this.parents_5);
		this.parents_14.addAll(this.parents_10);
		
		String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(maxTempParentDepth1, nodeId, longestPaths[0]));
		result.add(new GenealogyMetricValue(maxTempParentDepth2, nodeId, longestPaths[1]));
		result.add(new GenealogyMetricValue(maxTempParentDepth5, nodeId, longestPaths[2]));
		result.add(new GenealogyMetricValue(maxTempParentDepth10, nodeId, longestPaths[3]));
		result.add(new GenealogyMetricValue(maxTempParentDepth14, nodeId, longestPaths[4]));
		
		result.add(new GenealogyMetricValue(numTempParents1, nodeId, this.parents_1.size()));
		result.add(new GenealogyMetricValue(numTempParents2, nodeId, this.parents_2.size()));
		result.add(new GenealogyMetricValue(numTempParents5, nodeId, this.parents_5.size()));
		result.add(new GenealogyMetricValue(numTempParents10, nodeId, this.parents_10.size()));
		result.add(new GenealogyMetricValue(numTempParents14, nodeId, this.parents_14.size()));
		
		return result;
	}
	
	/**
	 * Longest path.
	 *
	 * @param originalNode the original node
	 * @param node the node
	 * @param seen the seen
	 * @return the int[]
	 */
	private int[] longestPath(T originalNode,
	                          T node,
	                          Collection<T> seen) {
		
		int[] result = { 0, 0, 0, 0, 0 };
		
		int diff = this.dayTimeDiff.daysDiff(originalNode, node);
		if (diff > 14) {
			return result;
		}
		
		String nodeId = this.genealogy.getNodeId(node);
		
		int[] toAdd = { 0, 0, 0, 0, 0 };
		
		for (T dependant : this.genealogy.getAllParents(node)) {
			if (seen.contains(dependant)) {
				continue;
			}
			Collection<T> seenCopy = new HashSet<T>(seen);
			seenCopy.add(dependant);
			int[] tmp = longestPath(originalNode, dependant, seenCopy);
			for (int i = 0; i < 5; ++i) {
				if (tmp[i] > toAdd[i]) {
					toAdd[i] = tmp[i];
				}
			}
		}
		
		if (originalNode.equals(node)) {
			return toAdd;
		}
		
		int j = 0;
		if (diff < 2) {
			this.parents_1.add(nodeId);
		} else if (diff < 3) {
			j = 1;
			this.parents_2.add(nodeId);
		} else if (diff < 6) {
			j = 2;
			this.parents_5.add(nodeId);
		} else if (diff < 11) {
			j = 3;
			this.parents_10.add(nodeId);
		} else {
			j = 4;
			this.parents_14.add(nodeId);
		}
		
		for (int i = j; i < 5; ++i) {
			result[i] = toAdd[i] + 1;
		}
		
		return result;
	}
}
