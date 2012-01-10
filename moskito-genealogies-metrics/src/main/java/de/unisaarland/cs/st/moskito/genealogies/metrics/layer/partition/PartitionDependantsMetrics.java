package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalAncestorMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionDependantsMetrics extends GenealogyPartitionMetric {
	
	private UniversalAncestorMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionDependantsMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<Collection<JavaChangeOperation>> genealogy) {
		super(threadGroup, settings, genealogy);
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
