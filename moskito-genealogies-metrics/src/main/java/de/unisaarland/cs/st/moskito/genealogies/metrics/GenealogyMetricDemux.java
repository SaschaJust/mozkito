package de.unisaarland.cs.st.moskito.genealogies.metrics;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaGroup;


public class GenealogyMetricDemux extends AndamaDemultiplexer<GenealogyMetricValue> {
	
	public GenealogyMetricDemux(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
	}
	
}
