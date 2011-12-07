package de.unisaarland.cs.st.moskito.genealogies.metrics.partition;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricThread;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public abstract class GenealogyPartitionMetric extends GenealogyMetricThread<Collection<JavaChangeOperation>> {
	
	public GenealogyPartitionMetric(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings);
	}
	
}
