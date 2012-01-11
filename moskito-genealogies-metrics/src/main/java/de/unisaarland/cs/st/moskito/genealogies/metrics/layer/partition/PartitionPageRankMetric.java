package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalPageRankMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class PartitionPageRankMetric extends GenealogyPartitionMetric {
	
	
	private UniversalPageRankMetric<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionPageRankMetric(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalPageRankMetric<Collection<JavaChangeOperation>>(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalPageRankMetric.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode(), item.isLast());
	}
	
}
