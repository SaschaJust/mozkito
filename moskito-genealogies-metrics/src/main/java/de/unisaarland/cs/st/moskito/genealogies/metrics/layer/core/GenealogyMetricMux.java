package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;


public class GenealogyMetricMux extends AndamaMultiplexer<GenealogyCoreNode> {
	
	public GenealogyMetricMux(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		
	}
	
}
