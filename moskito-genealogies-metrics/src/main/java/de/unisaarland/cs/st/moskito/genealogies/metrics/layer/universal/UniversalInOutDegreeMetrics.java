package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalInOutDegreeMetrics<T> {
	
	private ChangeGenealogy<T> genealogy;
	
	private String             inDegree     = "InDegree";
	private String             avgInDegree  = "AvgInDegree";
	private String             outDegree    = "OutDegree";
	private String             avgOutDegree = "AvgOutDegree";
	
	public UniversalInOutDegreeMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(inDegree);
		result.add(avgInDegree);
		result.add(outDegree);
		result.add(avgOutDegree);
		return result;
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
