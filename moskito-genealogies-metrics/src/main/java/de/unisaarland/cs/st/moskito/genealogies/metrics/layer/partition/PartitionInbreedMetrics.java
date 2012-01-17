package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInbreedMetrics;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionInbreedMetrics extends GenealogyPartitionMetric {
	
	private UniversalInbreedMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionInbreedMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalInbreedMetrics<Collection<JavaChangeOperation>>(
				genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalInbreedMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
	
}
