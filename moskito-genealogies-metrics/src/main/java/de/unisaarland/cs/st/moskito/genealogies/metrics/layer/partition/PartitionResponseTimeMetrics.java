package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalResponseTimeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class PartitionResponseTimeMetrics extends GenealogyPartitionMetric implements
DayTimeDiff<Collection<JavaChangeOperation>> {
	
	
	private UniversalResponseTimeMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionResponseTimeMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<Collection<JavaChangeOperation>> genealogy) {
		super(threadGroup, settings, genealogy);
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