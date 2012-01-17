package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class PartitionAuthorMetrics extends GenealogyPartitionMetric {
	
	public static String    numDepAuthors    = "changeSize";
	public static String    numParentAuthors = "avgDepChangeSize";
	
	public PartitionAuthorMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(numDepAuthors);
		metricNames.add(numParentAuthors);
		return metricNames;
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		Collection<JavaChangeOperation> transaction = item.getNode();
		String nodeId = genealogy.getNodeId(transaction);
		
		Set<Long> depAuthors = new HashSet<Long>();
		for (Collection<JavaChangeOperation> dependant : genealogy.getAllDependants(transaction)) {
			for (JavaChangeOperation tmpOp : dependant) {
				depAuthors.add(tmpOp.getRevision().getTransaction().getPersons().getGeneratedId());
			}
		}
		
		metricValues.add(new GenealogyMetricValue(numDepAuthors, nodeId, depAuthors.size()));
		
		Set<Long> parentAuthors = new HashSet<Long>();
		for (Collection<JavaChangeOperation> parent : genealogy.getAllParents(transaction)) {
			for (JavaChangeOperation tmpOp : parent) {
				parentAuthors.add(tmpOp.getRevision().getTransaction().getPersons().getGeneratedId());
			}
		}
		
		metricValues.add(new GenealogyMetricValue(numParentAuthors, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}
