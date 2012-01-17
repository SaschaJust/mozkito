package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTempParentsMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionTempParentsMetrics extends GenealogyPartitionMetric implements
DayTimeDiff<Collection<JavaChangeOperation>> {
	
	private UniversalTempParentsMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionTempParentsMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalTempParentsMetrics<Collection<JavaChangeOperation>>(genealogy, this);
	}
	
	@Override
	public int daysDiff(Collection<JavaChangeOperation> t1, Collection<JavaChangeOperation> t2) {
		return DaysBetweenUtils.getDaysBetween(t1, t2);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalTempParentsMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}
