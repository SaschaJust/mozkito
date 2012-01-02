package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalTempParentsMetrics<T> {
	
	//TODO requires intensive testing
	
	private static String      maxTempParentDepth1  = "maxTempParentDepth_1";
	private static String      maxTempParentDepth2  = "maxTempParentDepth_2";
	private static String      maxTempParentDepth5  = "maxTempParentDepth_5";
	private static String      maxTempParentDepth10 = "maxTempParentDepth_10";
	private static String      maxTempParentDepth14 = "maxTempParentDepth_14";
	
	private static String      numTempParents1      = "numTempParents_1";
	private static String      numTempParents2      = "numTempParents_2";
	private static String      numTempParents5      = "numTempParents_5";
	private static String      numTempParents10     = "numTempParents_10";
	private static String      numTempParents14     = "numTempParents_14";
	private ChangeGenealogy<T> genealogy;
	private DayTimeDiff<T>     dayTimeDiff;
	private Set<String>        parents_1            = new HashSet<String>();
	private Set<String>        parents_2            = new HashSet<String>();
	private Set<String>        parents_5            = new HashSet<String>();
	private Set<String>        parents_10           = new HashSet<String>();
	private Set<String>        parents_14           = new HashSet<String>();
	
	public UniversalTempParentsMetrics(ChangeGenealogy<T> genealogy, DayTimeDiff<T> dayTimeDiff) {
		this.genealogy = genealogy;
		this.dayTimeDiff = dayTimeDiff;
	}
	
	public Collection<String> getMetricNames() {
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
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new ArrayList<GenealogyMetricValue>(10);
		
		parents_1.clear();
		parents_2.clear();
		parents_5.clear();
		parents_10.clear();
		parents_14.clear();
		
		int[] longestPaths = longestPath(node, node);
		
		parents_2.addAll(parents_1);
		parents_5.addAll(parents_2);
		parents_10.addAll(parents_5);
		parents_14.addAll(parents_10);
		
		String nodeId = genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(maxTempParentDepth1, nodeId, longestPaths[0]));
		result.add(new GenealogyMetricValue(maxTempParentDepth2, nodeId, longestPaths[1]));
		result.add(new GenealogyMetricValue(maxTempParentDepth5, nodeId, longestPaths[2]));
		result.add(new GenealogyMetricValue(maxTempParentDepth10, nodeId, longestPaths[3]));
		result.add(new GenealogyMetricValue(maxTempParentDepth14, nodeId, longestPaths[4]));
		
		
		result.add(new GenealogyMetricValue(numTempParents1, nodeId, parents_1.size()));
		result.add(new GenealogyMetricValue(numTempParents2, nodeId, parents_2.size()));
		result.add(new GenealogyMetricValue(numTempParents5, nodeId, parents_5.size()));
		result.add(new GenealogyMetricValue(numTempParents10, nodeId, parents_10.size()));
		result.add(new GenealogyMetricValue(numTempParents14, nodeId, parents_14.size()));
		
		return result;
	}
	
	private int[] longestPath(T originalNode, T node) {
		
		int[] longestPath = { 0, 0, 0, 0, 0 };
		int diff = dayTimeDiff.daysDiff(originalNode, node);
		if (diff > 14) {
			return longestPath;
		}
		
		String nodeId = genealogy.getNodeId(node);
		
		for (T dependant : genealogy.getAllParents(node)) {
			int[] tmp = longestPath(originalNode, dependant);
			for (int i = 0; i < 5; ++i) {
				if ((tmp[i] + 1) > longestPath[i]) {
					longestPath[i] = tmp[i] + 1;
				}
			}
		}
		
		if (diff < 2) {
			parents_1.add(nodeId);
		} else if (diff < 3) {
			longestPath[0] = 0;
			parents_2.add(nodeId);
		} else if (diff < 6) {
			longestPath[0] = 0;
			longestPath[1] = 0;
			parents_5.add(nodeId);
		} else if (diff < 11) {
			longestPath[0] = 0;
			longestPath[1] = 0;
			longestPath[2] = 0;
			parents_10.add(nodeId);
		} else {
			longestPath[0] = 0;
			longestPath[1] = 0;
			longestPath[2] = 0;
			longestPath[3] = 0;
			parents_14.add(nodeId);
		}
		return longestPath;
	}
}