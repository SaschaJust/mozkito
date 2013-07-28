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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.mozkito.genealogies.core.ChangeGenealogy;
import org.mozkito.genealogies.metrics.DayTimeDiff;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalTempDepthMetrics.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalTempDepthMetrics<T> {
	
	/** The Constant maxTempDepth1. */
	private static final String MAX_TEMP_DEPTH_1      = "maxTempDepth_1";
	
	/** The Constant maxTempDepth2. */
	private static final String MAX_TEMP_DEPTH_2      = "maxTempDepth_2";
	
	/** The Constant maxTempDepth5. */
	private static final String MAX_TEMP_DEPTH_5      = "maxTempDepth_5";
	
	/** The Constant maxTempDepth10. */
	private static final String MAX_TEMP_DEPTH_10     = "maxTempDepth_10";
	
	/** The Constant maxTempDepth14. */
	private static final String MAX_TEMP_DEPTH_14     = "maxTempDepth_14";
	
	/** The Constant numTempResponses1. */
	private static final String NUM_TEMP_RESPONSES_1  = "numTempResponses_1";
	
	/** The Constant numTempResponses2. */
	private static final String NUM_TEMP_RESPONSES_2  = "numTempResponses_2";
	
	/** The Constant numTempResponses5. */
	private static final String NUM_TEMP_RESPONSES_5  = "numTempResponses_5";
	
	/** The Constant numTempResponses10. */
	private static final String NUM_TEMP_RESPONSES_10 = "numTempResponses_10";
	
	/** The Constant numTempResponses14. */
	private static final String NUM_TEMP_RESPONSES_14 = "numTempResponses_14";
	
	/**
	 * Gets the maxtempdepth1.
	 * 
	 * @return the maxtempdepth1
	 */
	public static String getMaxtempdepth1() {
		return UniversalTempDepthMetrics.MAX_TEMP_DEPTH_1;
	}
	
	/**
	 * Gets the maxtempdepth10.
	 * 
	 * @return the maxtempdepth10
	 */
	public static String getMaxtempdepth10() {
		return UniversalTempDepthMetrics.MAX_TEMP_DEPTH_10;
	}
	
	/**
	 * Gets the maxtempdepth14.
	 * 
	 * @return the maxtempdepth14
	 */
	public static String getMaxtempdepth14() {
		return UniversalTempDepthMetrics.MAX_TEMP_DEPTH_14;
	}
	
	/**
	 * Gets the maxtempdepth2.
	 * 
	 * @return the maxtempdepth2
	 */
	public static String getMaxtempdepth2() {
		return UniversalTempDepthMetrics.MAX_TEMP_DEPTH_2;
	}
	
	/**
	 * Gets the maxtempdepth5.
	 * 
	 * @return the maxtempdepth5
	 */
	public static String getMaxtempdepth5() {
		return UniversalTempDepthMetrics.MAX_TEMP_DEPTH_5;
	}
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_1);
		result.add(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_2);
		result.add(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_5);
		result.add(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_10);
		result.add(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_14);
		result.add(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_1);
		result.add(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_2);
		result.add(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_5);
		result.add(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_10);
		result.add(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_14);
		return result;
	}
	
	/**
	 * Gets the numtempresponses1.
	 * 
	 * @return the numtempresponses1
	 */
	public static String getNumtempresponses1() {
		return UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_1;
	}
	
	/**
	 * Gets the numtempresponses10.
	 * 
	 * @return the numtempresponses10
	 */
	public static String getNumtempresponses10() {
		return UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_10;
	}
	
	/**
	 * Gets the numtempresponses14.
	 * 
	 * @return the numtempresponses14
	 */
	public static String getNumtempresponses14() {
		return UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_14;
	}
	
	/**
	 * Gets the numtempresponses2.
	 * 
	 * @return the numtempresponses2
	 */
	public static String getNumtempresponses2() {
		return UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_2;
	}
	
	/**
	 * Gets the numtempresponses5.
	 * 
	 * @return the numtempresponses5
	 */
	public static String getNumtempresponses5() {
		return UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_5;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	
	/** The day time diff. */
	private final DayTimeDiff<T>     dayTimeDiff;
	
	/** The responses_1. */
	private final Set<String>        responses_1  = new HashSet<String>();
	
	/** The responses_2. */
	private final Set<String>        responses_2  = new HashSet<String>();
	
	/** The responses_5. */
	private final Set<String>        responses_5  = new HashSet<String>();
	
	/** The responses_10. */
	private final Set<String>        responses_10 = new HashSet<String>();
	
	/** The responses_14. */
	private final Set<String>        responses_14 = new HashSet<String>();
	
	/**
	 * Instantiates a new universal temp depth metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 * @param dayTimeDiff
	 *            the day time diff
	 */
	public UniversalTempDepthMetrics(final ChangeGenealogy<T> genealogy, final DayTimeDiff<T> dayTimeDiff) {
		this.genealogy = genealogy;
		this.dayTimeDiff = dayTimeDiff;
	}
	
	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final T node) {
		final Collection<GenealogyMetricValue> result = new ArrayList<GenealogyMetricValue>(10);
		
		this.responses_1.clear();
		this.responses_2.clear();
		this.responses_5.clear();
		this.responses_10.clear();
		this.responses_14.clear();
		
		final int[] longestPaths = longestPath(node, node, new HashSet<T>());
		
		this.responses_2.addAll(this.responses_1);
		this.responses_5.addAll(this.responses_2);
		this.responses_10.addAll(this.responses_5);
		this.responses_14.addAll(this.responses_10);
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_1, nodeId, longestPaths[0]));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_2, nodeId, longestPaths[1]));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_5, nodeId, longestPaths[2]));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_10, nodeId, longestPaths[3]));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.MAX_TEMP_DEPTH_14, nodeId, longestPaths[4]));
		
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_1, nodeId,
		                                    this.responses_1.size()));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_2, nodeId,
		                                    this.responses_2.size()));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_5, nodeId,
		                                    this.responses_5.size()));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_10, nodeId,
		                                    this.responses_10.size()));
		result.add(new GenealogyMetricValue(UniversalTempDepthMetrics.NUM_TEMP_RESPONSES_14, nodeId,
		                                    this.responses_14.size()));
		
		return result;
	}
	
	/**
	 * Longest path.
	 * 
	 * @param originalNode
	 *            the original node
	 * @param node
	 *            the node
	 * @param seen
	 *            the seen
	 * @return the int[]
	 */
	private int[] longestPath(final T originalNode,
	                          final T node,
	                          final Collection<T> seen) {
		
		final int[] result = { 0, 0, 0, 0, 0 };
		
		final int diff = this.dayTimeDiff.daysDiff(originalNode, node);
		if (diff > 14) {
			return result;
		}
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		final int[] toAdd = { 0, 0, 0, 0, 0 };
		
		for (final T dependant : this.genealogy.getAllDependents(node)) {
			if (seen.contains(dependant)) {
				continue;
			}
			final Collection<T> seenCopy = new HashSet<T>(seen);
			seenCopy.add(dependant);
			final int[] tmp = longestPath(originalNode, dependant, seenCopy);
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
			this.responses_1.add(nodeId);
		} else if (diff < 3) {
			j = 1;
			this.responses_2.add(nodeId);
		} else if (diff < 6) {
			j = 2;
			this.responses_5.add(nodeId);
		} else if (diff < 11) {
			j = 3;
			this.responses_10.add(nodeId);
		} else {
			j = 4;
			this.responses_14.add(nodeId);
		}
		
		for (int i = j; i < 5; ++i) {
			result[i] = toAdd[i] + 1;
		}
		
		return result;
	}
}
