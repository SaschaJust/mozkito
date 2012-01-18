package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalAncestorMetrics;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionDependantsMetrics extends GenealogyPartitionMetric {
	
	private UniversalAncestorMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionDependantsMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalAncestorMetrics<Collection<JavaChangeOperation>>(
				genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalAncestorMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
	
}
