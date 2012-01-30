package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalSiblingMetrics<T> {
	
	private static String      numSiblingChildren = "NumSiblingChildren";
	private static String      avgSiblingChildren = "AvgSiblingChildren";
	public static  Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(numSiblingChildren);
		result.add(avgSiblingChildren);
		return result;
	}
	
	private ChangeGenealogy<T> genealogy;
	
	public UniversalSiblingMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		Collection<T> vertexParents = genealogy.getAllDependants(node);
		
		DescriptiveStatistics siblingChildren = new DescriptiveStatistics();
		int num = 0;
		
		for (T child : genealogy.getAllDependants(node)) {
			Collection<T> childParents = genealogy.getAllDependants(child);
			int size = CollectionUtils.intersection(vertexParents, childParents).size();
			if (size > 0) {
				++num;
			}
			siblingChildren.addValue(size);
		}
		
		String nodeId = genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(numSiblingChildren, nodeId, num));
		result.add(new GenealogyMetricValue(avgSiblingChildren, nodeId, (siblingChildren.getN() < 1) ? 0 : siblingChildren.getMean()));
		
		
		return result;
	}
}
