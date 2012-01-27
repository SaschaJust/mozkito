package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class CoreAuthorMetrics extends GenealogyCoreMetric {
	
	private static final String numDepAuthors    = "changeSize";
	private static final String numParentAuthors = "avgDepChangeSize";
	
	public CoreAuthorMetrics(CoreChangeGenealogy genealogy) {
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
	public Collection<GenealogyMetricValue> handle(GenealogyCoreNode item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		JavaChangeOperation operation = item.getNode();
		String nodeId = genealogy.getNodeId(operation);
		
		Set<Long> depAuthors = new HashSet<Long>();
		for (JavaChangeOperation dependant : genealogy.getAllDependants(operation)) {
			depAuthors.add(dependant.getRevision().getTransaction().getPersons().getGeneratedId());
		}
		
		metricValues.add(new GenealogyMetricValue(numDepAuthors, nodeId, depAuthors.size()));
		
		Set<Long> parentAuthors = new HashSet<Long>();
		for (JavaChangeOperation parent : genealogy.getAllParents(operation)) {
			parentAuthors.add(parent.getRevision().getTransaction().getPersons().getGeneratedId());
		}
		metricValues.add(new GenealogyMetricValue(numParentAuthors, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}
