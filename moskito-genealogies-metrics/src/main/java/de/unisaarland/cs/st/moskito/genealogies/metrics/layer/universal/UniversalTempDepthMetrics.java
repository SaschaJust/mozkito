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
	
	public static String       maxTempDepth1      = "maxTempDepth_1";
	public static String       maxTempDepth2      = "maxTempDepth_2";
	public static String       maxTempDepth5      = "maxTempDepth_5";
	public static String       maxTempDepth10     = "maxTempDepth_10";
	public static String       maxTempDepth14     = "maxTempDepth_14";
	
	public static String       numTempResponses1  = "numTempResponses_1";
	public static String       numTempResponses2  = "numTempResponses_2";
	public static String       numTempResponses5  = "numTempResponses_5";
	public static String       numTempResponses10 = "numTempResponses_10";
	public static String       numTempResponses14 = "numTempResponses_14";
	
	private ChangeGenealogy<T> genealogy;
	private DayTimeDiff<T>     dayTimeDiff;
	private Set<String>        responses_1        = new HashSet<String>();
	private Set<String>        responses_2        = new HashSet<String>();
	private Set<String>        responses_5        = new HashSet<String>();
	private Set<String>        responses_10       = new HashSet<String>();
	private Set<String>        responses_14       = new HashSet<String>();
	
	public UniversalTempDepthMetrics(ChangeGenealogy<T> genealogy, DayTimeDiff<T> dayTimeDiff) {
		this.genealogy = genealogy;
		this.dayTimeDiff = dayTimeDiff;
	}
	
	public Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
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
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new ArrayList<GenealogyMetricValue>(10);
		
		responses_1.clear();
		responses_2.clear();
		responses_5.clear();
		responses_10.clear();
		responses_14.clear();
		
		int[] longestPaths = longestPath(node, node, new HashSet<T>());
		
		responses_2.addAll(responses_1);
		responses_5.addAll(responses_2);
		responses_10.addAll(responses_5);
		responses_14.addAll(responses_10);
		
		String nodeId = genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(maxTempDepth1, nodeId, longestPaths[0]));
		result.add(new GenealogyMetricValue(maxTempDepth2, nodeId, longestPaths[1]));
		result.add(new GenealogyMetricValue(maxTempDepth5, nodeId, longestPaths[2]));
		result.add(new GenealogyMetricValue(maxTempDepth10, nodeId, longestPaths[3]));
		result.add(new GenealogyMetricValue(maxTempDepth14, nodeId, longestPaths[4]));
		
		
		result.add(new GenealogyMetricValue(numTempResponses1, nodeId, responses_1.size()));
		result.add(new GenealogyMetricValue(numTempResponses2, nodeId, responses_2.size()));
		result.add(new GenealogyMetricValue(numTempResponses5, nodeId, responses_5.size()));
		result.add(new GenealogyMetricValue(numTempResponses10, nodeId, responses_10.size()));
		result.add(new GenealogyMetricValue(numTempResponses14, nodeId, responses_14.size()));
		
		return result;
	}
	
	private int[] longestPath(T originalNode, T node, Collection<T> seen) {
		
		int[] result = { 0, 0, 0, 0, 0 };
		
		int diff = dayTimeDiff.daysDiff(originalNode, node);
		if (diff > 14) {
			return result;
		}
		
		String nodeId = genealogy.getNodeId(node);
		
		int[] toAdd = { 0, 0, 0, 0, 0 };
		
		for (T dependant : genealogy.getAllDependants(node)) {
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
			responses_1.add(nodeId);
		} else if (diff < 3) {
			j = 1;
			responses_2.add(nodeId);
		} else if (diff < 6) {
			j = 2;
			responses_5.add(nodeId);
		} else if (diff < 11) {
			j = 3;
			responses_10.add(nodeId);
		} else {
			j = 4;
			responses_14.add(nodeId);
		}
		
		for (int i = j; i < 5; ++i) {
			result[i] = toAdd[i] + 1;
		}
		
		return result;
	}
}