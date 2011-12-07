package de.unisaarland.cs.st.moskito.genealogies.metrics;

import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;


public class GenealogyMetricMux<K> extends AndamaMultiplexer<GenealogyNode<K>> {
	
	public GenealogyMetricMux(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		
	}
	
}
