package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;


public class PartitionGenealogyMetricMux extends AndamaMultiplexer<GenealogyPartitionNode> {
	
	public PartitionGenealogyMetricMux(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		
	}
	
}
