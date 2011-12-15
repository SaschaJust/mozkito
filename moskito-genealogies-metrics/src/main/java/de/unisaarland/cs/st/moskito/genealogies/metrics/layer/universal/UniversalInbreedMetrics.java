package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalInbreedMetrics<T> {
	
	private static String      numInbreedChildren = "NumInbreedChildren";
	private static String      numInbreedParents  = "NumInbreedParents";
	private static String      avgInbreedChildren = "AvgInbreedChildren";
	private static String      avgInbreedParents  = "AvgInbreedParents";
	
	private ChangeGenealogy<T> genealogy;
	
	public UniversalInbreedMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(numInbreedChildren);
		result.add(numInbreedParents);
		result.add(avgInbreedChildren);
		result.add(avgInbreedParents);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		Collection<T> vertexParents = genealogy.getAllDependants(node);
		Collection<T> vertexChildren = genealogy.getAllDependants(node);
		
		DescriptiveStatistics inbreedChildrenStat = new DescriptiveStatistics();
		DescriptiveStatistics inbreedParentsStat = new DescriptiveStatistics();
		
		Collection<T> inbreedChildren = new HashSet<T>();
		Collection<T> inbreedParents = new HashSet<T>();
		
		for (T child : genealogy.getAllDependants(node)) {
			Collection<T> grandChildren = genealogy.getAllDependants(child);
			@SuppressWarnings("rawtypes") Collection intersection = CollectionUtils.intersection(vertexChildren,
					grandChildren);
			inbreedChildren.addAll(intersection);
			inbreedChildrenStat.addValue(intersection.size());
		}
		
		for (T parent : genealogy.getAllParents(node)) {
			Collection<T> grandParents = genealogy.getAllParents(parent);
			@SuppressWarnings("rawtypes") Collection intersection = CollectionUtils.intersection(vertexParents,
					grandParents);
			inbreedParents.addAll(intersection);
			inbreedParentsStat.addValue(intersection.size());
		}
		
		String nodeId = genealogy.getNodeId(node);
		
		result.add(new GenealogyMetricValue(numInbreedChildren, nodeId, inbreedChildren.size()));
		result.add(new GenealogyMetricValue(numInbreedParents, nodeId, inbreedParents.size()));
		
		result.add(new GenealogyMetricValue(avgInbreedChildren, nodeId, inbreedChildrenStat.getMean()));
		result.add(new GenealogyMetricValue(avgInbreedParents, nodeId, inbreedParentsStat.getMean()));
		
		return result;
	}
}
