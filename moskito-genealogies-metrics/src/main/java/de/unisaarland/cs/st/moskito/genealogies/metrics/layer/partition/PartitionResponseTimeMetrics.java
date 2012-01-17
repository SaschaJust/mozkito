package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalResponseTimeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class PartitionResponseTimeMetrics extends GenealogyPartitionMetric implements
DayTimeDiff<Collection<JavaChangeOperation>> {
	
	
	private UniversalResponseTimeMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionResponseTimeMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalResponseTimeMetrics<Collection<JavaChangeOperation>>(genealogy, this);
	}
	
	@Override
	public int daysDiff(Collection<JavaChangeOperation> p1, Collection<JavaChangeOperation> p2) {
		return DaysBetweenUtils.getDaysBetween(p1, p2);
		
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalResponseTimeMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}