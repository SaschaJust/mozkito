package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricThread;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public abstract class GenealogyPartitionMetric extends GenealogyMetricThread<Collection<JavaChangeOperation>> {
	
	protected ChangeGenealogy<Collection<JavaChangeOperation>> genealogy;

	public GenealogyPartitionMetric(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<Collection<JavaChangeOperation>> genealogy) {
		super(threadGroup, settings);
		this.genealogy = genealogy;
	}
	
}
