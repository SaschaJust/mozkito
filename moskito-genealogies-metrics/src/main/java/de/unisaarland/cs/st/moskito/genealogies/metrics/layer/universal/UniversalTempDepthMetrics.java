package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalTempDepthMetrics<T> {
	
	private static final String maxTempDepth1      = "maxTempDepth_1";
	private static final String maxTempDepth2      = "maxTempDepth_2";
	private static final String maxTempDepth5      = "maxTempDepth_5";
	private static final String maxTempDepth10     = "maxTempDepth_10";
	private static final String maxTempDepth14     = "maxTempDepth_14";
	
	private static final String numTempResponses1  = "numTempResponses_1";
	
	private static final String numTempResponses2  = "numTempResponses_2";
	
	private static final String numTempResponses5  = "numTempResponses_5";
	
	private static final String numTempResponses10 = "numTempResponses_10";
	
	private static final String numTempResponses14 = "numTempResponses_14";
	
	public static String getMaxtempdepth1() {
		return maxTempDepth1;
	}
	
	public static String getMaxtempdepth10() {
		return maxTempDepth10;
	}
	
	public static String getMaxtempdepth14() {
		return maxTempDepth14;
	}
	
	public static String getMaxtempdepth2() {
		return maxTempDepth2;
	}
	
	public static String getMaxtempdepth5() {
		return maxTempDepth5;
	}
	
	public static Collection<String> getMetricNames() {
		final Collection<String> result = new LinkedList<String>();
		result.add(maxTempDepth1);
		result.add(maxTempDepth2);
		result.add(maxTempDepth5);
		result.add(maxTempDepth10);
		result.add(maxTempDepth14);
		result.add(numTempResponses1);
		result.add(numTempResponses2);
		result.add(numTempResponses5);
		result.add(numTempResponses10);
		result.add(numTempResponses14);
		return result;
	}
	
	public static String getNumtempresponses1() {
		return numTempResponses1;
	}
	
	public static String getNumtempresponses10() {
		return numTempResponses10;
	}
	
	public static String getNumtempresponses14() {
		return numTempResponses14;
	}
	
	public static String getNumtempresponses2() {
		return numTempResponses2;
	}
	
	public static String getNumtempresponses5() {
		return numTempResponses5;
	}
	
	private final ChangeGenealogy<T> genealogy;
	private final DayTimeDiff<T>     dayTimeDiff;
	private final Set<String>        responses_1  = new HashSet<String>();
	private final Set<String>        responses_2  = new HashSet<String>();
	private final Set<String>        responses_5  = new HashSet<String>();
	private final Set<String>        responses_10 = new HashSet<String>();
	
	private final Set<String>        responses_14 = new HashSet<String>();
	
	public UniversalTempDepthMetrics(final ChangeGenealogy<T> genealogy, final DayTimeDiff<T> dayTimeDiff) {
		this.genealogy = genealogy;
		this.dayTimeDiff = dayTimeDiff;
	}
	
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
		
		result.add(new GenealogyMetricValue(maxTempDepth1, nodeId, longestPaths[0]));
		result.add(new GenealogyMetricValue(maxTempDepth2, nodeId, longestPaths[1]));
		result.add(new GenealogyMetricValue(maxTempDepth5, nodeId, longestPaths[2]));
		result.add(new GenealogyMetricValue(maxTempDepth10, nodeId, longestPaths[3]));
		result.add(new GenealogyMetricValue(maxTempDepth14, nodeId, longestPaths[4]));
		
		result.add(new GenealogyMetricValue(numTempResponses1, nodeId, this.responses_1.size()));
		result.add(new GenealogyMetricValue(numTempResponses2, nodeId, this.responses_2.size()));
		result.add(new GenealogyMetricValue(numTempResponses5, nodeId, this.responses_5.size()));
		result.add(new GenealogyMetricValue(numTempResponses10, nodeId, this.responses_10.size()));
		result.add(new GenealogyMetricValue(numTempResponses14, nodeId, this.responses_14.size()));
		
		return result;
	}
	
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
		
		for (final T dependant : this.genealogy.getAllDependants(node)) {
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
