package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalInOutDegreeMetrics<T> {
	
	public static Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(inDegree);
		result.add(avgInDegree);
		result.add(outDegree);
		result.add(avgOutDegree);
		return result;
	}
	
	private ChangeGenealogy<T> genealogy;
	private static String      inDegree     = "InDegree";
	private static String      avgInDegree  = "AvgInDegree";
	private static String      outDegree    = "OutDegree";
	private static String      avgOutDegree = "AvgOutDegree";
	
	public UniversalInOutDegreeMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		String nodeId = genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(inDegree, nodeId, genealogy.inDegree(node)));
		result.add(new GenealogyMetricValue(outDegree, nodeId, genealogy.outDegree(node)));
		
		DescriptiveStatistics outDegreeStat = new DescriptiveStatistics();
		
		for (T parent : genealogy.getAllParents(node)) {
			outDegreeStat.addValue(genealogy.getEdges(node, parent).size());
		}
		result.add(new GenealogyMetricValue(avgOutDegree, nodeId, outDegreeStat.getMean()));
		
		DescriptiveStatistics inDegreeStat = new DescriptiveStatistics();
		for (T child : genealogy.getAllDependants(node)) {
			inDegreeStat.addValue(genealogy.getEdges(child, node).size());
		}
		result.add(new GenealogyMetricValue(avgInDegree, nodeId, inDegreeStat.getMean()));
		
		return result;
	}
}
