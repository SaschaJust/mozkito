package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInOutDegreeMetrics;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionInOutDegreeMetrics extends GenealogyPartitionMetric {
	
	private UniversalInOutDegreeMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionInOutDegreeMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalInOutDegreeMetrics<Collection<JavaChangeOperation>>(
				genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalInOutDegreeMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
	
}
