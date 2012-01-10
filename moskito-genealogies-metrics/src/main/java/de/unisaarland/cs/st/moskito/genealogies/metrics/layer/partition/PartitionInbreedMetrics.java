package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInbreedMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionInbreedMetrics extends GenealogyPartitionMetric {
	
	private UniversalInbreedMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionInbreedMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<Collection<JavaChangeOperation>> genealogy) {
		super(threadGroup, settings, genealogy);
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
